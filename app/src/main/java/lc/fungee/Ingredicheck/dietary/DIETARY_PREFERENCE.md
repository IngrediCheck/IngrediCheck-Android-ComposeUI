# Dietary Preference (Android) – What it is and how it’s implemented

## What it is

**Dietary preference** is the user’s list of food-related preferences (allergies, intolerances, lifestyle, etc.) stored on the backend. It is used for “add family” and “just me” in the same way: one list per signed-in user.

## How it’s implemented

1. **Backend API** (aligned with iOS)
   - `GET /preferencelists/default` → list of saved preferences
   - `POST /preferencelists/default` (form: `clientActivityId`, `preference`) → add one
   - `PUT /preferencelists/default/{id}` (form: `clientActivityId`, `preference`) → edit one
   - `DELETE /preferencelists/default/{id}` (form: `clientActivityId`) → delete one

2. **When we sync**
   - When the user finishes the fine-tune flow:
     - Taps **“All Set!”** on the decision screen, or
     - Completes the **last step** (e.g. Taste) and taps next.
   - We build one string from all selected chips (e.g. `"Peanuts, Tree nuts, Lactose"`) and send it as a single new preference via POST.

3. **Where it runs**
   - **OnboardingHost**: Builds the preference text from `selectedAllergiesByMember` and calls `authViewModel.syncDietaryPreferencesFromOnboarding(preferenceText)` in both exit paths.
   - **AuthViewModel**: Gets the access token, calls `DietaryPreferenceRepository.addOrEditDietaryPreference(..., preferenceText, id = null)`.
   - **DietaryPreferenceRepository**: Sends the request to the backend and logs success/failure.

4. **Logs**
   - `OnboardingAllergies`: `[DietaryPreference] onSkipPreferences: syncing textLength=...` / `onNext complete: syncing textLength=...`
   - `DietaryPreference`: GET/POST/PUT/DELETE and status/body length
   - `AuthDebug`: `DietaryPreference: syncing...`, `sync success id=...`, `sync failure...`, or `sync error...`

Filter logcat by `DietaryPreference`, `OnboardingAllergies`, or `AuthDebug` to confirm it's working.

---

## Food-notes API (per-member / Everyone) – matches iOS

Onboarding chip selections are also synced to the **food-notes** API so that each family member (and "Everyone") has their own note on the backend, matching iOS behavior.

1. **Endpoints**
   - `PUT family/food-notes` (body: `{ "content": {...}, "version": 0 }`) → "Everyone" / family note
   - `PUT family/members/{memberId}/food-notes` → one member's note
   - `GET family/food-notes/all` → load all (family + members) for versions/cache

2. **When we sync**
   - Same as above: when the user taps "All Set!" or completes the last preference step.
   - For each key in `selectedAllergiesByMember`: **ALL** → `updateFamilyFoodNotes`; member UUID → `updateMemberFoodNotes(memberId.lowercase(), ...)`.

3. **Where it runs**
   - **OnboardingHost**: Calls `authViewModel.syncFoodNotesFromOnboarding(...)` in both exit paths (with dietary preference sync).
   - **AuthViewModel**: `syncFoodNotesFromOnboarding` builds content via `OnboardingChipData.buildFoodNotesContentFromChipIds(chipIds)` and calls `FoodNotesRepository` for each member/Everyone.
   - **FoodNotesRepository**: PUT requests; on 409 retries once with `currentNote.version` or `version = 0`.

4. **Logs**
   - `FoodNotes`: updateFamilyFoodNotes, updateMemberFoodNotes, status and retries.
   - `AuthDebug`: `FoodNotes: sync success Everyone` / `FoodNotes: sync success <memberId>`.
