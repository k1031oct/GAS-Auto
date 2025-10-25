🤖 Automater

## 概要
本プロジェクトは、旧「GAS File Organizer」および「GAS Workflow Builder」の機能を統合し、よりモダンで拡張性の高い「モジュール駆動型のワークフロー自動化プラットフォーム」として再構築したものです。

## ✨ 主な機能
- **直感的なUI**: モジュールをドラッグ＆ドロップするだけで、複雑なワークフローを視覚的に構築できます。
- **多彩なモジュール**: Google Drive、Spreadsheet、Gmailなど、様々なサービスを操作するためのモジュールが用意されています。
- **関連ファイル管理**:
    - ワークフローごとに、関連するドキュメントやスプレッドシートをまとめて管理できます。
    - 新規ファイルの作成や、テンプレートからの作成が可能です。
- **テンプレート機能**:
    - 繰り返し使うドキュメントの雛形をテンプレートとして登録できます。
    - テンプレートは専用フォルダ (`GAS_Workflow_Automator_AppData/Template`) で一元管理されます。
- **トリガー実行**: 作成したワークフローを、時間ベースのトリガー（毎日、毎週など）で自動実行できます。
- **実行ログ**: ワークフローの実行結果は、専用のログシートに記録され、いつでも確認できます。

## 🛠️ 技術スタック
- **バックエンド:** Google Apps Script (GAS)
- **フロントエンド:** HTML, JavaScript, Tailwind CSS
- **実行エンジン:** `logic`ベースのカスタム実行エンジン
- **データストア:** Script Properties, Google Drive, Google Sheets

---

## 🚀 新モジュールアーキテクチャ (`logic`ベース)
本プロジェクトは、従来の `handler` ベースの実行方式から、より柔軟で宣言的な `logic` ベースのアーキテクチャに移行しました。

### 実行の仕組み
1.  **`WorkflowService`** がワークフローの実行を開始します。
2.  モジュール定義に `logic` キーが存在する場合、**`ExecutionService`** に処理が委譲されます。
3.  **`ExecutionService`** は `logic` 配列内のステップを順に実行します。
4.  各ステップは、`func` キーで指定された関数を **`FunctionRegistry`** を通じて呼び出します。
5.  `args` で指定された引数内のプレースホルダー（例: `{{setting_value}}` や `{{outputs.previous_step.result}}`）が、実行時の値に解決されます。
6.  `resultTo` キーが存在する場合、そのステップの実行結果が後続のステップから参照できるようになります。

### `logic`ベースのモジュール例 (`drive_move_file.json`)
```json
{
  "id": "drive_move_file",
  "name": "ドライブファイルを移動",
  "settings": [
    { "id": "sourceFileUrl", "name": "移動元ファイルURL", "type": "text" },
    { "id": "destinationFolderUrl", "name": "移動先フォルダURL", "type": "text" }
  ],
  "logic": [
    {
      "step_id": "extract_source_id",
      "func": "core.extractIdFromUrl",
      "args": { "url": "{{sourceFileUrl}}" },
      "resultTo": "sourceFileId"
    },
    {
      "step_id": "get_file",
      "func": "core.driveGetFileById",
      "args": { "fileId": "{{outputs.sourceFileId}}" },
      "resultTo": "fileToMove"
    },
    // ... more steps
  ]
}
```

---

📂 リポジトリ構成
主要なファイルの役割は以下の通りです。
- `index.html` / `javascript.html`: フロントエンドのUIとロジック
- `Code.gs`: メインのエントリーポイントとGASサービスの呼び出し
- `modules/`: `logic`ベースの新しいモジュール定義（JSON）を格納するディレクトリ
- `ExecutionService.gs`: `logic`ベースのJSONを解釈・実行する新しい実行エンジン（インタープリタ）
- `CoreFunctions.gs`: `ExecutionService`から呼び出される、再利用可能なコア関数群
- `WorkflowService.gs`: ワークフローの実行、保存、ロードロジック（新旧両方のエンジンに対応）
- `ModuleService.gs`: モジュール定義の動的ロードロジック
- `.github/workflows/`: GitHub Actions のワークフロー定義
- `GEMINI.md`: AIエージェントへの詳細なコーディングガイドライン

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