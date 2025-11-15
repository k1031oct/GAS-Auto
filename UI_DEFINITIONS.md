# UI関連の変数・定義一覧

このドキュメントは、アプリケーションのUIに関連する主要な変数、スタイル、テーマ、レイアウトの定義をまとめたものです。

---

## 1. テーマ (Themes)

アプリケーション全体のデザインテーマとスタイルを定義します。

### ファイルパス
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values-night/themes.xml`
- `app/src/main/java/com/gws/auto/mobile/android/ui/theme/Theme.kt`

### 主要な定義

| 定義名 | 説明 |
| --- | --- |
| `Theme.GWSAutoForAndroid` | アプリケーションのベースとなるXMLテーマ。`Base.Theme.GWSAutoForAndroid`を継承します。 |
| `Base.Theme.GWSAutoForAndroid` | Material 3を親テーマとし、アプリの基本的な配色（`colorPrimary`, `colorSurface`など）を定義します。ライトテーマとダークテーマでそれぞれ定義されています。 |
| `GWSAutoForAndroidTheme` (Composable) | Jetpack Compose用のテーマ。`ThemeViewModel`からユーザー設定（テーマ、ハイライトカラー）を読み取り、動的に配色を切り替えるロジックを含みます。 |
| `SettingsHeader`, `SettingsItem` | 設定画面で使用される見出しや項目のためのカスタムスタイル。 |

---

## 2. 配色 (Colors)

アプリケーションで使用されるカラーパレットを定義します。

### ファイルパス
- `app/src/main/res/values/colors.xml`
- `app/src/main/java/com/gws/auto/mobile/android/ui/theme/Color.kt`

### 主要な定義
| 定義 | 説明 |
| --- | --- |
| `colors.xml` | アプリケーションのMaterial 3カラーパレットを16進数カラーコードで定義します。ライトテーマ用 (`md_theme_light_*`) とダークテーマ用 (`md_theme_dark_*`) が含まれます。 |
| `Color.kt` | `colors.xml`で定義されたカラーコードを、Jetpack Composeで使用するための`Color`オブジェクトとして定義します。デフォルトの配色に加え、`forest`テーマや`ocean`テーマのハイライトカラーもここで定義されています。 |

---

## 3. タイポグラフィ (Typography)

アプリケーション全体のテキストスタイル（フォントサイズ、ウェイトなど）を定義します。

### ファイルパス
- `app/src/main/java/com/gws/auto/mobile/android/ui/theme/Typography.kt`

### 主要な定義
| 定義名 | 説明 |
| --- | --- |
| `AppTypography` | `MaterialTheme`で使用されるタイポグラフィスケーリング（`headlineSmall`, `bodyLarge`など）を定義した`Typography`オブジェクト。 |

---

## 4. 主要なレイアウト (Layouts)

画面やリストアイテムの基本的な骨格を定義するXMLファイルです。

### ファイルパス
- `app/src/main/res/layout/`

### 主要な定義
| ファイル名 | 説明 |
| --- | --- |
| `activity_main.xml` | メイン画面のレイアウト。`Toolbar`（検索バーを含む）、`ViewPager2`、`BottomNavigationView`で構成されます。 |
| `list_item_workflow.xml` | ワークフロー一覧画面の各アイテムのレイアウト。ワークフロー名、説明、各種操作ボタン（お気に入り、実行、編集、削除）を配置します。 |
| `list_item_history_header.xml` | 実行履歴画面の各アイテム（ヘッダー）のレイアウト。ブックマークボタンを含みます。 |
| `fragment_search.xml` | 検索時に表示されるボトムシートのレイアウト。タグと検索履歴の`RecyclerView`を配置します。 |
| `fragment_app_settings.xml` | 「アプリケーションの設定」画面のレイアウト。テーマや言語などを変更するための`Spinner`を配置します。 |
| `fragment_user_info.xml` | ユーザー情報画面のレイアウト。プロフィール画像やGoogleサインインボタンを配置します。 |
| `spinner_item_right_aligned.xml` | 設定画面の`Spinner`で、選択された項目を右寄せで表示するためのカスタムレイアウト。 |

---

## 5. 主要なDrawable

UIコンポーネントの背景やアイコンとして使用されるXMLファイルです。

### ファイルパス
- `app/src/main/res/drawable/`

### 主要な定義
| ファイル名 | 説明 |
| --- | --- |
| `search_view_background.xml` | 検索バーの背景として使用される角丸の図形（Shape Drawable）。 |
