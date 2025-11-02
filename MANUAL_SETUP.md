# 手動セットアップ チェックリスト

このチェックリストは、`init.sh` スクリプトを実行した後に必要な手動での設定手順をまとめたものです。

## Firebase

- [ ] **Firebaseプロジェクトを新規作成する**
  - [Firebaseコンソール](https://console.firebase.google.com/) にアクセスします。
  - 「プロジェクトを追加」をクリックし、画面の指示に従います。

- [ ] **`google-services.json` ファイルをダウンロードし、`app/` ディレクトリに配置する**
  - Firebaseプロジェクトの設定画面を開き、「全般」タブで対象のAndroidアプリを選択します。
  - 「google-services.json」をクリックしてファイルをダウンロードします。
  - ダウンロードしたファイルを、プロジェクトの `app/` ディレクトリに移動します。
  - **注意:** このファイルは `.gitignore` によりバージョン管理から除外されています。各開発者はローカル環境にこのファイルを配置する必要があります。

- [ ] **Firebase App Testerでテスターグループを作成する**
  - Firebaseコンソールのメニューから「App Distribution」に移動します。
  - 「テスターとグループ」タブを開きます。
  - 「グループを追加」をクリックして新しいグループを作成し、テスターを追加します。
  - **【重要】** 自動レビューサイクル（CI/CD）が正しく機能するためには、ここで作成するグループの名前（グループのエイリアス）を **`testers`** にする必要があります。

## GitHubリポジトリのシークレット設定

リポジトリの `Settings > Secrets and variables > Actions` を開き、以下のシークレットを追加します。

- [ ] **`FIREBASE_APP_ID`**: Firebaseアプリの一意のID。
  - Firebaseコンソールの `プロジェクト設定 > 全般 > マイアプリ` を開きます。
  - 対象のAndroidアプリを選択し、**`アプリ ID`** (`1:1234...`のような形式) をコピーして、このシークレットの値として貼り付けます。これはテストアプリの自動配布に必要です。 

- [ ] **`FIREBASE_TOKEN`**: Firebase CLIから取得したトークン。
  - ターミナルで次のコマンドを実行します: `firebase login:ci`
  - ブラウザが開き、認証が求められます。認証後、ターミナルにトークンが表示されるので、それをコピーします。

- [ ] **`SIGNING_KEY`**: Base64でエンコードされたリリース署名キー。
  - 署名キーがない場合は、`keytool` を使って以下のコマンドで生成します。
    ```bash
    keytool -genkeypair -v -keystore release-key.jks -alias your-alias -keyalg RSA -keysize 2048 -validity 10000
    ```
  - 次に、生成されたキーファイルをBase64にエンコードします。お使いのOSによってコマンドが異なります。
    - **macOS / Linux の場合:** `base64 -w 0 release-key.jks`
    - **Windows の場合:** `certutil -encode release-key.jks temp_key.txt` して生成されたファイルから `-----BEGIN...` と `-----END...` の間の文字列のみコピーします。
  - コピーした長い文字列を、GitHubのシークレットに設定します。

- [ ] **`KEY_PASSWORD`**: 署名キーのパスワード。

- [ ] **`KEY_STORE_PASSWORD`**: キーストアファイルのパスワード。

- [ ] **`KEY_ALIAS`**: キーストア内にあるキーのエイリアス。

- [ ] **`GOOGLE_SERVICES_JSON`**: Base64でエンコードされた `google-services.json` の内容。
  - **【重要】** CI/CDでのフォーマット崩れを防ぐため、ファイルの内容をBase64形式で設定します。
  - ターミナルで `app` ディレクトリのある階層（プロジェクトルート）に移動し、以下のコマンドでBase64文字列を生成します。
    - **macOS / Linux の場合:** `base64 -w 0 app/google-services.json`
    - **Windows の場合:** `certutil -encode app/google-services.json temp_google_services.txt` して生成されたファイルから `-----BEGIN...` と `-----END...` の間の文字列のみコピーします。
  - コピーしたBase64文字列で、GitHubの `GOOGLE_SERVICES_JSON` シークレットの値を**上書き更新**してください。

## GitHub Issuesの自動作成

このリポジトリでは、セットアップを円滑に進めるため、必要なタスクをまとめたGitHub Issuesが自動的に作成されることがあります。

もし、これらのIssuesが誤って削除されたり、自動作成が失敗した場合は、**GitHubのIssueまたはPull Requestのコメント欄に**以下のコマンドを書き込むことで、手動で再作成をトリガーできます。

**コマンドトリガー (GitHubコメント用):**
```
/create-initial-issues
```

## 最終確認

- [ ] **Firebaseコンソールでパッケージ名を確認・更新する**
  - Firebaseコンソールで `プロジェクト設定 > 全般 > マイアプリ` を開きます。
  - 対象のAndroidアプリを選択し、**パッケージ名**が `app/build.gradle.kts` の `applicationId` と一致していることを確認します。
  - **【重要】** もし一致していなければ、Firebaseコンソールのパッケージ名を更新し、新しい `google-services.json` を再ダウンロードしてローカルに配置し、さらに `GOOGLE_SERVICES_JSON` シークレットも更新してください。

- [ ] **Gradleの同期を実行する**
  - Android Studioで `File > Sync Project with Gradle Files` をクリックし、すべての設定が正しく読み込まれることを確認します。

- [ ] **リリースビルドを生成する**
  - **ローカルPCのターミナルで**以下のコマンドを実行し、署名設定が正しく機能するかを確認します。
    - **macOS / Linux の場合:** `./gradlew assembleRelease`
    - **Windows の場合:** `gradlew.bat assembleRelease`
  - これが成功すれば、CI/CDのための設定が正しく行われていることが確認できます。
