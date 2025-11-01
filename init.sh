#!/bin/bash

# Prompt for app name and package name
read -p "Enter App Name: " app_name
read -p "Enter Package Name (e.g., com.example.myapp): " package_name

# Replace placeholders
# This is a simplified script. A more robust solution would be needed for a real project.
echo "Replacing __APP_NAME__ with $app_name"
echo "Replacing __PACKAGE_NAME__ with $package_name"

# In a real scenario, you would use find and sed to replace these values in your project files.
# For example:
# find . -type f -name "*.xml" -o -name "*.gradle.kts" -o -name "*.md" | while read file; do
#     sed -i "s/__APP_NAME__/$app_name/g" "$file"
#     sed -i "s/__PACKAGE_NAME__/$package_name/g" "$file"
# done

echo "TODO: Implement the logic to rename the package directory structure."

echo "Initialization script finished."

# Create GitHub issue from MANUAL_SETUP.md
if [ -f "MANUAL_SETUP.md" ]; then
  issue_title="Manual Setup Tasks"
  issue_body=$(cat MANUAL_SETUP.md)
  
  # Check if gh is installed
  if ! command -v gh &> /dev/null
  then
      echo "GitHub CLI (gh) could not be found. Please install it to create an issue automatically."
  else
      echo "Creating GitHub issue..."
      gh issue create --title "$issue_title" --body "$issue_body"
      if [ $? -eq 0 ]; then
        echo "Successfully created issue: '$issue_title'"
      else
        echo "Failed to create GitHub issue. Please check your authentication and permissions."
      fi
  fi
else
  echo "MANUAL_SETUP.md not found. Skipping issue creation."
fi
