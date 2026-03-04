package lc.fungee.Ingredicheck.foodnotes

import android.util.Log
import com.russhwolf.settings.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lc.fungee.Ingredicheck.auth.AuthRepository
import lc.fungee.Ingredicheck.family.FamilyDto
import lc.fungee.Ingredicheck.onboarding.data.EVERYONE_MEMBER_ID
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData

/**
 * Food-notes specific use-case used by onboarding and other flows.
 *
 * Extracted from AuthViewModel so that auth/memoji/family responsibilities are separated
 * from food-notes sync and AI summary loading.
 */
class FoodNotesUseCase(
    private val scope: CoroutineScope,
    private val authRepository: AuthRepository,
    private val foodNotesRepository: FoodNotesRepository,
    private val currentFamilyProvider: () -> FamilyDto?,
    private val debugLogger: (String) -> Unit,
) {

    // AI-generated summary of food notes (text), matching iOS FoodNotesStore.foodNotesSummary.
    private val _foodNotesSummary = MutableStateFlow<String?>(null)
    val foodNotesSummary: StateFlow<String?> = _foodNotesSummary.asStateFlow()

    /**
     * Sync onboarding chip selections to food-notes API (per-member and Everyone), matching iOS.
     * Called when user taps "All Set!" or completes the last preference step.
     * - EVERYONE_MEMBER_ID ("ALL") -> PUT family/food-notes
     * - Each member id -> PUT family/members/{id}/food-notes
     */
    fun syncFoodNotesFromOnboarding(selectedAllergiesByMember: Map<String, Set<String>>) {
        val keys = selectedAllergiesByMember.keys.toList()
        if (BuildConfig.DEBUG) {
            Log.d(
                "FoodNotesAPI",
                "FoodNotes API implementation: sync started, keys=$keys, size=${selectedAllergiesByMember.size}"
            )
        }
        if (selectedAllergiesByMember.isEmpty()) {
            if (BuildConfig.DEBUG) {
                Log.d("FoodNotesAPI", "FoodNotes API: skip sync (empty selections)")
            }
            return
        }
        scope.launch {
            val accessToken = authRepository.accessTokenOrNull()
            if (accessToken.isNullOrBlank()) {
                Log.w("FoodNotesAPI", "FoodNotes API: skip sync (no access token)")
                return@launch
            }
            val familySnapshot = currentFamilyProvider()
            if (BuildConfig.DEBUG) {
                val familyMemberIds = familySnapshot?.let { family ->
                    buildList {
                        add(family.selfMember.id)
                        addAll(family.otherMembers.map { it.id })
                    }.joinToString(",")
                } ?: "none"
                Log.d(
                    "FoodNotesAPI",
                    "FoodNotes API implementation: currentFamily memberIds=[$familyMemberIds]"
                )
            }
            // Only sync for concrete backend members that actually exist in the current family,
            // plus the synthetic EVERYONE_MEMBER_ID key. This mirrors iOS behavior where
            // FoodNotesStore drives sync from the server-backed family model instead of any
            // local-only onboarding member ids.
            val validMemberIds: Set<String> = familySnapshot?.let { family ->
                buildSet {
                    add(family.selfMember.id.lowercase())
                    addAll(family.otherMembers.map { it.id.lowercase() })
                }
            } ?: emptySet()
            var successCount = 0
            var failCount = 0
            for ((memberKey, chipIds) in selectedAllergiesByMember) {
                if (chipIds.isEmpty()) continue
                val content = OnboardingChipData.buildFoodNotesContentFromChipIds(chipIds)
                if (content.isEmpty()) continue
                val isEveryone = memberKey == EVERYONE_MEMBER_ID || memberKey.isBlank()
                // Skip any non-Everyone keys that do not correspond to a real backend member.
                // This prevents 400 "Member does not exist in your family" errors when local-only
                // onboarding members (e.g. drafts that were never created on the server) still
                // have chip selections in selectedAllergiesByMember.
                if (!isEveryone && memberKey.lowercase() !in validMemberIds) {
                    if (BuildConfig.DEBUG) {
                        Log.w(
                            "FoodNotesAPI",
                            "FoodNotes API: skipping sync for unknown memberId=$memberKey (not in current family)"
                        )
                    }
                    continue
                }
                if (BuildConfig.DEBUG) {
                    Log.d(
                        "FoodNotesAPI",
                        "FoodNotes API implementation: syncing key=$memberKey " +
                                "isEveryone=$isEveryone lowerMemberId=${
                                    if (isEveryone) "EVERYONE" else memberKey.lowercase()
                                } " +
                                "chipCount=${chipIds.size}"
                    )
                }
                val result = if (isEveryone) {
                    foodNotesRepository.updateFamilyFoodNotes(
                        accessToken = accessToken,
                        content = content,
                        version = 0
                    )
                } else {
                    foodNotesRepository.updateMemberFoodNotes(
                        accessToken = accessToken,
                        memberId = memberKey.lowercase(),
                        content = content,
                        version = 0
                    )
                }
                result.fold(
                    onSuccess = {
                        successCount++
                        debugLogger("FoodNotes: sync success ${if (isEveryone) "Everyone" else memberKey}")
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "FoodNotesAPI",
                                "FoodNotes API implementation: sync success ${
                                    if (isEveryone) "Everyone" else "member=$memberKey"
                                } — working"
                            )
                        }
                    },
                    onFailure = { e ->
                        failCount++
                        debugLogger(
                            "FoodNotes: sync error ${
                                if (isEveryone) "Everyone" else memberKey
                            } ${e.message}"
                        )
                        Log.e(
                            "FoodNotesAPI",
                            "FoodNotes API: sync error ${if (isEveryone) "Everyone" else memberKey}",
                            e
                        )
                    }
                )
            }
            if (BuildConfig.DEBUG) {
                Log.d(
                    "FoodNotesAPI",
                    "FoodNotes API implementation: sync completed — success=$successCount fail=$failCount (check logs above for details)"
                )
            }
            // Kick off AI summary load immediately after sync so it completes
            // while the user is seeing the robot / chat, reducing wait in summary screen.
            loadFoodNotesSummary(force = true)
        }
    }

    /**
     * Load AI-generated food-notes summary text from backend (GET family/food-notes/summary).
     * When [force] is false and we already have a summary, this is a no-op. When [force] is true,
     * we always hit the backend (used after food-notes sync to match iOS refreshSummary behavior).
     */
    fun loadFoodNotesSummary(force: Boolean = false) {
        if (!force && _foodNotesSummary.value != null) {
            if (BuildConfig.DEBUG) {
                Log.d("FoodNotesAPI", "FoodNotesSummary: already loaded, skipping fetch")
            }
            return
        }
        scope.launch {
            val accessToken = authRepository.accessTokenOrNull()
            if (accessToken.isNullOrBlank()) {
                Log.w("FoodNotesAPI", "FoodNotesSummary: skip load (no access token)")
                return@launch
            }
            if (BuildConfig.DEBUG) {
                Log.d("FoodNotesAPI", "FoodNotesSummary: starting load")
            }
            val result = foodNotesRepository.fetchFoodNotesSummary(accessToken)
            result.fold(
                onSuccess = { response ->
                    _foodNotesSummary.value = response?.summary
                    if (BuildConfig.DEBUG) {
                        Log.d(
                            "FoodNotesAPI",
                            "FoodNotesSummary: loaded summary length=${
                                response?.summary?.length ?: 0
                            } textPreview=${response?.summary?.take(120)}"
                        )
                    }
                },
                onFailure = { e ->
                    Log.e("FoodNotesAPI", "FoodNotesSummary: load error", e)
                }
            )
        }
    }
}

