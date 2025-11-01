# Manual Setup Checklist

This checklist outlines the manual steps required after running the `init.sh` script.

## Firebase

- [ ] **Create a new Firebase project.**
  - Go to the [Firebase Console](https://console.firebase.google.com/).
  - Click "Add project" and follow the on-screen instructions.

- [ ] **Download the `google-services.json` file and place it in the `app/` directory.**
  - In your Firebase project settings, under the "General" tab, select your Android app.
  - Click "google-services.json" to download the file.
  - Move the downloaded file to the `app/` directory of your project.

- [ ] **Create a tester group in Firebase App Tester.**
  - In the Firebase Console, navigate to "App Distribution".
  - Go to the "Testers & Groups" tab.
  - Click "Add group" to create a new group and add testers to it.

## GitHub Repository Secrets

Navigate to your repository's `Settings > Secrets and variables > Actions` and add the following secrets:

- [ ] **`FIREBASE_TOKEN`**: The token obtained from the Firebase CLI.
  - Run the following command in your terminal: `firebase login:ci`
  - This will open a browser window to authenticate. After authentication, a token will be displayed in the terminal. Copy this token.

- [ ] **`SIGNING_KEY`**: The base64 encoded release signing key.
  - If you don't have a signing key, generate one using `keytool`.
  - Then, encode the key file to base64 using the following command: `base64 -w 0 your_key_name.jks`
  - Copy the output.

- [ ] **`KEY_PASSWORD`**: The password for the signing key.
  - This is the password you set when you created the signing key.
