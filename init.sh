#!/bin/bash

# --- Configuration: Define the template's original values ---
# These values will be replaced by the user's input.
# Based on project history, the original package name was this.
TEMPLATE_PACKAGE_NAME="com.gws.auto.for.android"
# The app name placeholder is assumed to be the repository name, or a generic name.
TEMPLATE_APP_NAME="GWS-Auto-for-Android"

# --- User Input ---
echo "--- Project Initialization ---"
read -p "Enter New App Name (e.g., My Awesome App): " new_app_name
read -p "Enter New Package Name (e.g., com.example.myapp): " new_package_name
echo "--------------------------------"
echo

# --- Validation (Simple) ---
if [ -z "$new_app_name" ] || [ -z "$new_package_name" ]; then
    echo "❌ Error: App Name and Package Name cannot be empty."
    exit 1
fi

echo "🚀 Starting initialization..."

# --- Step 1: Replace placeholders in files ---
echo "🔄 Replacing package and app name in project files..."

# List of file extensions to search and replace. Excludes the .git directory.
TARGET_FILES=$(find . -type f -not -path "./.git/*" -not -path "./init.sh")

for file in $TARGET_FILES; do
    # Use a temporary file for sed for macOS/Linux compatibility
    sed -i.bak "s/$TEMPLATE_PACKAGE_NAME/$new_package_name/g" "$file"
    sed -i.bak "s/$TEMPLATE_APP_NAME/$new_app_name/g" "$file"
done

# Clean up backup files created by sed
find . -name "*.bak" -type f -delete

echo "✅ File content replaced."
echo

# --- Step 2: Rename package directory structure ---
echo "🔄 Renaming package directory structure..."

# Convert package names to directory paths
OLD_PACKAGE_PATH="app/src/main/java/$(echo $TEMPLATE_PACKAGE_NAME | tr '.' '/')"
NEW_PACKAGE_PATH="app/src/main/java/$(echo $new_package_name | tr '.' '/')"

if [ ! -d "$OLD_PACKAGE_PATH" ]; then
    echo "⚠️ Warning: Template directory '$OLD_PACKAGE_PATH' not found. It might have been renamed already."
else
    # Create the new directory structure
    mkdir -p "$NEW_PACKAGE_PATH"

    # Move the source files from the old directory to the new one
    mv "$OLD_PACKAGE_PATH"/* "$NEW_PACKAGE_PATH/"

    echo "✅ Source files moved to '$NEW_PACKAGE_PATH'."

    # Clean up the old, now-empty directories
    # This command finds all empty directories and deletes them, which is a safe way to clean up.
    find app/src/main/java -type d -empty -delete

    echo "✅ Old package directories cleaned up."
fi

# --- Step 3: Create GitHub issue from MANUAL_SETUP.md ---
echo
echo "🔄 Checking for GitHub CLI to create setup issue..."
if [ -f "MANUAL_SETUP.md" ]; then
  # Check if gh is installed
  if ! command -v gh &> /dev/null
  then
      echo "⚠️ GitHub CLI (gh) could not be found. Please install it to create an issue automatically."
  else
      echo "🤖 Creating GitHub issue for manual setup tasks..."
      gh issue create --title "Project Setup: Manual Tasks" --body "Please complete the tasks in MANUAL_SETUP.md to finalize the project configuration."
      if [ $? -eq 0 ]; then
        echo "✅ Successfully created setup issue on GitHub."
      else
        echo "❌ Failed to create GitHub issue. Please check your 'gh' authentication."
      fi
  fi
else
  echo "⚠️ MANUAL_SETUP.md not found. Skipping issue creation."
fi

echo
echo "🎉 Initialization script finished!"
echo "Next steps:"
echo "  1. Review the changes and commit them to your repository."
echo "  2. Follow the checklist in MANUAL_SETUP.md."
