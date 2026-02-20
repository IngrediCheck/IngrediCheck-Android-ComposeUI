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

Filter logcat by `DietaryPreference`, `OnboardingAllergies`, or `AuthDebug` to confirm it’s working.
