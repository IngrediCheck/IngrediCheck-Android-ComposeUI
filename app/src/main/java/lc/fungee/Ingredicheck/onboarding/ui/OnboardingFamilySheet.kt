package lc.fungee.Ingredicheck.onboarding.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import lc.fungee.Ingredicheck.auth.AuthViewModel
import lc.fungee.Ingredicheck.family.CreateFamilyRequest
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel
import lc.fungee.Ingredicheck.ui.components.NonDraggableBottomSheet

/**
 * UI overlay and bottom sheet for inviting a family member.
 * Invite flow logic is handled by [runInviteFlow]; the host wires callbacks here.
 */
@Composable
internal fun InviteMemberOverlay(
    member: OnboardingViewModel.FamilyOverviewMember,
    onDismiss: () -> Unit,
    onMaybeLater: () -> Unit,
    onInvite: () -> Unit,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NonDraggableBottomSheet(
            onDismissRequest = onDismiss,
            horizontalPaddingEnabled = true
        ) {
            InviteConfirmationSheet(
                memberName = member.name,
                onMayBeLater = onMaybeLater,
                onInvite = onInvite,
                isLoading = isLoading
            )
        }
    }
}

/**
 * Dimmed overlay shown behind the invite sheet. Host should compose this when [memberToInvite != null].
 */
@Composable
internal fun InviteMemberOverlayScrim(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f)
            .background(Color.Black)
            .clickable(onClick = onDismiss)
    )
}

/**
 * Runs the full invite flow: ensure family exists, create if needed, invite member, share code.
 * Handles duplicate memberId retry. Calls [setInviting] and [onDismiss] as appropriate.
 */
internal fun runInviteFlow(
    context: Context,
    authViewModel: AuthViewModel,
    vm: OnboardingViewModel,
    member: OnboardingViewModel.FamilyOverviewMember,
    currentFamily: Any?,
    getMembers: () -> List<OnboardingViewModel.FamilyOverviewMember>,
    buildCreateFamilyRequestFromMembers: (List<OnboardingViewModel.FamilyOverviewMember>) -> CreateFamilyRequest?,
    shareInviteCode: (Context, String) -> Unit,
    setInviting: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    setInviting(true)
    Log.d("OnboardingHost", "Invite confirmed for memberId=${member.id}, name=${member.name}")

    if (currentFamily != null) {
        authViewModel.inviteFamilyMember(member.id) { result ->
            val code = result.getOrNull()
            if (code != null) {
                vm.setInvitePending(member.id, true)
                onDismiss()
                shareInviteCode(context, code)
            } else {
                Toast.makeText(context, "Failed to create invite", Toast.LENGTH_SHORT).show()
            }
            setInviting(false)
        }
        return
    }

    val req = buildCreateFamilyRequestFromMembers(getMembers())
    if (req == null) {
        authViewModel.inviteFamilyMember(member.id) { result ->
            val code = result.getOrNull()
            if (code != null) {
                vm.setInvitePending(member.id, true)
                onDismiss()
                shareInviteCode(context, code)
            } else {
                Toast.makeText(context, "Failed to create invite", Toast.LENGTH_SHORT).show()
            }
            setInviting(false)
        }
        return
    }

    authViewModel.createFamily(req) { createResult ->
        createResult.fold(
            onSuccess = {
                authViewModel.inviteFamilyMember(member.id) { result ->
                    val code = result.getOrNull()
                    if (code != null) {
                        vm.setInvitePending(member.id, true)
                        onDismiss()
                        shareInviteCode(context, code)
                    } else {
                        Toast.makeText(context, "Failed to create invite", Toast.LENGTH_SHORT).show()
                    }
                    setInviting(false)
                }
            },
            onFailure = {
                val msg = it.localizedMessage.orEmpty()
                val isDuplicateMemberId =
                    msg.contains("members_pkey", ignoreCase = true) ||
                        msg.contains("duplicate key", ignoreCase = true)

                if (isDuplicateMemberId) {
                    Log.e(
                        "OnboardingHost",
                        "createFamily failed due to duplicate memberId; regenerating ids + retry",
                        it
                    )
                    val idMap = vm.regenerateFamilyOverviewMemberIds()
                    val regeneratedMemberId = idMap[member.id] ?: member.id
                    val retryReq = buildCreateFamilyRequestFromMembers(getMembers())

                    if (retryReq == null) {
                        setInviting(false)
                        Toast.makeText(context, "Failed to retry createFamily", Toast.LENGTH_SHORT).show()
                        return@createFamily
                    }

                    authViewModel.createFamily(retryReq) { retryResult ->
                        retryResult.fold(
                            onSuccess = {
                                authViewModel.inviteFamilyMember(regeneratedMemberId) { result ->
                                    val code = result.getOrNull()
                                    if (code != null) {
                                        vm.setInvitePending(regeneratedMemberId, true)
                                        onDismiss()
                                        shareInviteCode(context, code)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to create invite",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    setInviting(false)
                                }
                            },
                            onFailure = { retryErr ->
                                Log.e(
                                    "OnboardingHost",
                                    "createFamily retry failed; not inviting memberId=$regeneratedMemberId",
                                    retryErr
                                )
                                setInviting(false)
                                Toast.makeText(
                                    context,
                                    retryErr.localizedMessage ?: "Failed to create family",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                    return@createFamily
                }

                setInviting(false)
                Log.e(
                    "OnboardingHost",
                    "createFamily failed; not inviting memberId=${member.id}",
                    it
                )
                Toast.makeText(
                    context,
                    it.localizedMessage ?: "Failed to create family",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}
