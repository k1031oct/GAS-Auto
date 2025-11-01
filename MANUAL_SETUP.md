# Manual Setup Checklist

This checklist outlines the manual steps required after running the `init.sh` script.

## Firebase

- [ ] Create a new Firebase project.
- [ ] Download the `google-services.json` file and place it in the `app/` directory.
- [ ] Create a tester group in Firebase App Tester.

## GitHub Repository Secrets

Navigate to your repository's `Settings > Secrets and variables > Actions` and add the following secrets:

- [ ] `FIREBASE_TOKEN`: The token obtained from the Firebase CLI.
- [ ] `SIGNING_KEY`: The base64 encoded release signing key.
- [ ] `KEY_PASSWORD`: The password for the signing key.
