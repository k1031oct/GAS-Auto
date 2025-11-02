$files = Get-Content "filelist.txt"
foreach ($file in $files) {
    git update-index --no-skip-worktree $file
}