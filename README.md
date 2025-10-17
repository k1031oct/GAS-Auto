🤖 Automater
概要
本プロジェクトは、旧「GAS File Organizer」および「GAS Workflow Builder」の機能を統合し、よりモダンで拡張性の高い「モジュール駆動型のワークフロー自動化プラットフォーム」として再構築することを目的としています。

🚨 現在のフェーズと目標
現在、プロジェクトは以下の状態にあります。
アプリケーション統合: 完了済みです。二つのアプリのロジックが統合されました。
UI/デザイン: 進行中です。Material Design Lite (MDL) から Tailwind CSS への移行を進めています。
モジュール定義: 刷新済みです。モジュール定義は GAS コード内ではなく、Google Drive 上の JSON ファイルから動的にロードされるアーキテクチャになりました。
API管理: オミット済みです。従来の API Key 管理機能は削除されました。
開発環境: AI自律化済みです。Gemini CLI GitHub Actions を導入し、Issue 駆動での自動修正・デプロイを行います。

🛠️ 技術スタック
本プロジェクトの主要な技術要素は以下の通りです。
バックエンド: Google Apps Script (GAS)
フロントエンド: HTML / JavaScript
デザイン: Tailwind CSS (移行中)
データストア: Script Properties (ワークフロー保存), Google Drive (モジュール JSON, ログ), Google Sheets (実行履歴)

🤖 AIエージェント運用ガイドライン (Readiness)
本リポジトリでは、AIエージェントが自律的に Issue を解決し、Pull Request (PR) を作成します。

1. タスクの依頼方法 (AIへの指示)
AIにコード修正を依頼する場合、以下の手順に従ってください。
新しい Issue を作成するか、既存の Issue を再オープンします。
Issueに ready-for-ai ラベルを付与します。
Issueのコメント欄に、具体的なタスク（例: /fix）を指示します。

2. コード修正の強制実行 (最も確実な方法)
トリアージロジックの複雑な判断を避け、直接コード修正を依頼するには、Issueのコメント欄に以下のコマンドを投稿します。
@github-actions /fix [タスクの概要]

3. レビューとデプロイ
AIがPRを作成すると、GitHub Actionsが自動的にあなたのレビュー環境にデプロイを試みます。
レビュー用 URL: PRのコメント欄に投稿された URL で動作を確認してください。
マージ: 動作確認後、承認（Approve）し、マージしてください。

📂 リポジトリ構成
主要なファイルの役割は以下の通りです。
index.html: メインの Web UI (HTML/Tailwind CSS)
javascript.html: クライアント側の主要な JavaScript ロジック
Code.gs: メインのエントリーポイントとGASサービスの呼び出し
ModuleService.gs: モジュール定義（JSON）の Google Drive からの動的ロードロジック (刷新済み)
WorkflowService.gs: ワークフローの実行、保存、ロードロジック
.github/workflows/: GitHub Actions のワークフロー定義 (AI自律修正、デプロイなど)
GEMINI.md: AIエージェントへの詳細なコーディングガイドライン

