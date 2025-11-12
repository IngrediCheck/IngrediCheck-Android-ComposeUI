#!/usr/bin/env python3
"""
Auto-increment version code by querying Google Play Console.

This script queries Google Play Console API to find the highest version code
across all release tracks (internal, alpha, beta, production), increments it,
and updates the version code in app/build.gradle.kts.
"""

import json
import os
import re
import sys
import traceback
from google.oauth2 import service_account
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError

PACKAGE_NAME = "llc.fungee.IngrediCheck"
SERVICE_ACCOUNT_FILE = "/tmp/service_account.json"
GRADLE_FILE = "app/build.gradle.kts"


def get_latest_version_code_from_play():
    """Query Google Play API to get the highest version code across all tracks."""
    try:
        credentials = service_account.Credentials.from_service_account_file(
            SERVICE_ACCOUNT_FILE,
            scopes=['https://www.googleapis.com/auth/androidpublisher']
        )
        
        service = build('androidpublisher', 'v3', credentials=credentials)
        
        # Create a single edit to query all tracks
        edit = service.edits().insert(body={}, packageName=PACKAGE_NAME).execute()
        edit_id = edit['id']
        
        max_version_code = 0
        tracks = ['internal', 'alpha', 'beta', 'production']
        
        # List all tracks at once (more efficient)
        try:
            tracks_list = service.edits().tracks().list(
                packageName=PACKAGE_NAME,
                editId=edit_id
            ).execute()
            
            if 'tracks' in tracks_list:
                for track in tracks_list['tracks']:
                    if 'releases' in track:
                        for release in track['releases']:
                            if 'versionCodes' in release:
                                for vc in release['versionCodes']:
                                    max_version_code = max(max_version_code, int(vc))
        except HttpError as e:
            print(f"Error listing tracks: {e}")
            # Fallback: query each track individually
            for track_name in tracks:
                try:
                    track_response = service.edits().tracks().get(
                        packageName=PACKAGE_NAME,
                        editId=edit_id,
                        track=track_name
                    ).execute()
                    
                    if 'releases' in track_response:
                        for release in track_response['releases']:
                            if 'versionCodes' in release:
                                for vc in release['versionCodes']:
                                    max_version_code = max(max_version_code, int(vc))
                except HttpError:
                    continue
        
        # Delete the edit (cleanup)
        try:
            service.edits().delete(packageName=PACKAGE_NAME, editId=edit_id).execute()
        except:
            pass
        
        return max_version_code
        
    except Exception as e:
        print(f"Error querying Google Play API: {e}")
        print(traceback.format_exc())
        return None


def update_version_code(new_version_code):
    """Update version code in build.gradle.kts."""
    with open(GRADLE_FILE, 'r') as f:
        content = f.read()
    
    # Find and replace versionCode
    pattern = r'versionCode\s*=\s*\d+'
    replacement = f'versionCode = {new_version_code}'
    new_content = re.sub(pattern, replacement, content)
    
    with open(GRADLE_FILE, 'w') as f:
        f.write(new_content)
    
    print(f"✓ Updated {GRADLE_FILE} with version code {new_version_code}")


def main():
    """Main entry point."""
    # Check if service account file exists
    if not os.path.exists(SERVICE_ACCOUNT_FILE):
        print("Google Play service account not configured, skipping version code check")
        return
    
    # Main logic
    print("Querying Google Play Console for latest version code...")
    max_version_code = get_latest_version_code_from_play()
    
    if max_version_code is not None and max_version_code > 0:
        new_version_code = max_version_code + 1
        print(f"Latest version code found: {max_version_code}")
        print(f"New version code will be: {new_version_code}")
        update_version_code(new_version_code)
    else:
        print("Could not retrieve version code from Play Console, using fallback...")
        # Fallback: increment current version code by 1
        with open(GRADLE_FILE, 'r') as f:
            content = f.read()
        
        match = re.search(r'versionCode\s*=\s*(\d+)', content)
        if match:
            current_version = int(match.group(1))
            new_version = current_version + 1
            update_version_code(new_version)
            print(f"Fallback: Incremented from {current_version} to {new_version}")
        else:
            print("ERROR: Could not find versionCode in build.gradle.kts")
            sys.exit(1)


if __name__ == "__main__":
    main()

