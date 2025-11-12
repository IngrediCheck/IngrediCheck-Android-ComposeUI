# CI/CD & Release Guide

This project enforces guarded changes on `main` and publishes release APKs via GitHub Actions. Use this guide to configure repository settings, maintain secrets, and operate the pipelines.

## Branch Protection

Main must remain fast-forward only and receive all changes through reviewed pull requests.

1. **Enable protection in GitHub**  
   - Go to `Settings → Branches → Branch protection rules → Add rule`.  
   - Target branch name pattern: `main`.  
   - Enable these options:
     - Require a pull request before merging (minimum one approval, dismiss stale reviews).  
     - Require status checks to pass (`Build APK / Assemble Release APK`).  
     - Require branches to be up to date before merging.  
     - Include administrators (prevents accidental direct pushes).  
     - Disallow force pushes and deletions.

## Required GitHub Secrets

| Secret | Purpose | Notes |
| --- | --- | --- |
| `ANDROID_SIGNING_KEYSTORE_BASE64` | Base64-encoded `.jks`/`.keystore` file | Optional; omit to produce unsigned release artifacts. |
| `ANDROID_SIGNING_KEYSTORE_PASSWORD` | Password for the keystore | Required for signing. |
| `ANDROID_SIGNING_KEY_ALIAS` | Alias inside the keystore | Required for signing. |
| `ANDROID_SIGNING_KEY_PASSWORD` | Password for the key alias | Required for signing. |
| `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON` | Google Play service account JSON key | Optional; omit to skip Play Console upload. See setup instructions below. |

If signing secrets are not provided, releases remain unsigned but still build successfully. If the Google Play secret is not provided, the workflow will skip Play Console upload but still produce artifacts.

### Setting up Google Play Service Account

To enable automatic uploads to Google Play Console:

1. **Create a service account in Google Cloud Console:**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select an existing one
   - Navigate to `IAM & Admin → Service Accounts`
   - Click `Create Service Account`
   - Provide a name (e.g., "github-actions-play-upload") and description
   - Click `Create and Continue`

2. **Grant Play Console permissions:**
   - In the service account details, note the email address (format: `name@project-id.iam.gserviceaccount.com`)
   - Go to [Google Play Console](https://play.google.com/console/)
   - Navigate to `Setup → API access`
   - Find your service account email and click `Grant access`
   - Grant the following permissions:
     - `View app information and download bulk reports`
     - `Manage production releases`
     - `Manage testing track releases` (if using internal/alpha/beta tracks)
   - Save the changes

3. **Create and download the JSON key:**
   - Return to Google Cloud Console → Service Accounts
   - Click on your service account
   - Go to the `Keys` tab
   - Click `Add Key → Create new key`
   - Choose `JSON` format
   - Download the JSON file

4. **Add the secret to GitHub:**
   - Open the downloaded JSON file and copy its entire contents
   - Go to your GitHub repository → `Settings → Secrets and variables → Actions`
   - Click `New repository secret`
   - Name: `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON`
   - Value: Paste the entire JSON file contents
   - Click `Add secret`

The workflow will now automatically upload signed APKs to the selected Play Console track when triggered.

## Workflows

### Build APK (`.github/workflows/build-apk.yml`)

- Triggers on pull requests to `main` and pushes directly to `main`.  
- Builds the release variant using JDK 17 and Android SDK.  
- Publishes the generated APK as an artifact (`app-release-apk`) for validation.  
- Use this workflow as a required status check in the branch protection rule.

### Release APK (`.github/workflows/release-apk.yml`)

- Trigger modes:
  - **Tag push (`v*`)**: Builds, optionally signs, uploads to Google Play Console (if configured), and attaches the APK to a GitHub Release created for the tag (requires branch/tag commit on `main`).  
  - **Manual dispatch**: Select `main` as the branch, choose a Play Console track (internal, alpha, beta, production), optionally override the release name, and obtain the artifact from the run (no release created automatically for manual runs).
- Signing is automatic when all four signing secrets are present; otherwise an unsigned artifact is uploaded.
- Google Play Console upload is automatic when `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON` is configured. For manual dispatch, you can select the release track (defaults to `internal`).
- The workflow aborts if the triggering ref/tag does not point to the latest commit on `main`, guaranteeing releases originate from protected history.

## Release Checklist

1. Merge all changes into `main` via reviewed pull requests.  
2. Tag the `main` commit (`git tag vX.Y.Z && git push origin vX.Y.Z`) **or** run the Release workflow manually from `main` (select the Play Console track if using manual dispatch).  
3. Confirm the workflow succeeds:
   - The signed APK is available as a GitHub Actions artifact
   - If `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON` is configured, the APK is automatically uploaded to the selected Play Console track
   - For tag pushes, a GitHub Release is created with the APK attached
4. Monitor the Play Console to confirm the release is processed and available to testers/users.

