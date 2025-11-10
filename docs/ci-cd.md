# CI/CD & Release Guide

This project enforces guarded changes on `main` and publishes release APKs via GitHub Actions. Use this guide to configure repository settings, maintain secrets, and operate the pipelines.

## Branch Protection

Main must remain fast-forward only and receive all changes through reviewed pull requests.

1. **Enable protection in GitHub**  
   - Go to `Settings → Branches → Branch protection rules → Add rule`.  
   - Target branch name pattern: `main`.  
   - Enable these options:
     - Require a pull request before merging (minimum one approval, dismiss stale reviews).  
     - Require status checks to pass (`Build APK`).  
     - Require branches to be up to date before merging.  
     - Include administrators (prevents accidental direct pushes).  
     - Disallow force pushes and deletions.
2. **Automate with GitHub CLI (optional)**  
   Run `./tools/configure_branch_protection.sh` after authenticating with `gh auth login`. The script applies the settings above and can be re-run to enforce updates.

## Required GitHub Secrets

| Secret | Purpose | Notes |
| --- | --- | --- |
| `ANDROID_SIGNING_KEYSTORE_BASE64` | Base64-encoded `.jks`/`.keystore` file | Optional; omit to produce unsigned release artifacts. |
| `ANDROID_SIGNING_KEYSTORE_PASSWORD` | Password for the keystore | Required for signing. |
| `ANDROID_SIGNING_KEY_ALIAS` | Alias inside the keystore | Required for signing. |
| `ANDROID_SIGNING_KEY_PASSWORD` | Password for the key alias | Required for signing. |

If signing secrets are not provided, releases remain unsigned but still build successfully.

## Workflows

### Build APK (`.github/workflows/build-apk.yml`)

- Triggers on pull requests to `main` and pushes directly to `main`.  
- Builds the release variant using JDK 17 and Android SDK.  
- Publishes the generated APK as an artifact (`app-release-apk`) for validation.  
- Use this workflow as a required status check in the branch protection rule.

### Release APK (`.github/workflows/release-apk.yml`)

- Trigger modes:
  - **Tag push (`v*`)**: Builds, optionally signs, and attaches the APK to a GitHub Release created for the tag (requires branch/tag commit on `main`).  
  - **Manual dispatch**: Select `main` as the branch, optionally override the release name, and obtain the artifact from the run (no release created automatically).
- Signing is automatic when all four secrets are present; otherwise an unsigned artifact is uploaded.
- The workflow aborts if the triggering ref/tag does not point to the latest commit on `main`, guaranteeing releases originate from protected history.

## Release Checklist

1. Merge all changes into `main` via reviewed pull requests.  
2. Tag the `main` commit (`git tag vX.Y.Z && git push origin vX.Y.Z`) **or** run the Release workflow manually from `main`.  
3. Confirm the workflow succeeds and download the signed APK from the run or resulting GitHub Release.  
4. Distribute the APK or upload it to the Play Console as needed.

