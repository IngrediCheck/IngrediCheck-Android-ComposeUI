$ErrorActionPreference = 'Stop'

function Fix-Header {
  param(
    [string]$Path,
    [string]$ExpectedPackage,
    [string[]]$EnsureImports
  )
  Write-Host "Fixing $Path"
  $lines = Get-Content -Encoding UTF8 $Path

  # Remove stray lines that are just FQCNs without 'import'
  $lines = $lines | Where-Object { $_.Trim() -ne 'lc.fungee.IngrediCheck.model.utils.AppConstants' -and $_.Trim() -ne 'lc.fungee.IngrediCheck.model.entities.AppleAuthConfig' }

  # Ensure package line exists and is the first non-empty line
  $firstNonEmptyIdx = ($lines | ForEach-Object { $_ } | Select-String '.' | Select-Object -First 1).LineNumber
  if (-not $firstNonEmptyIdx) { $firstNonEmptyIdx = 1 }
  if ($lines.Length -eq 0 -or -not ($lines[$firstNonEmptyIdx-1] -match '^\s*package\s+')) {
    $lines = @("package $ExpectedPackage") + $lines
  } else {
    # Overwrite with expected package to be safe
    $lines[$firstNonEmptyIdx-1] = "package $ExpectedPackage"
  }

  # Recompute package index
  $pkgIdx = ($lines | Select-String '^\s*package\s+').LineNumber
  if (-not $pkgIdx) { throw "Package line not found after insertion for $Path" }
  $pkgIdx = $pkgIdx[0] - 1

  # Collect existing import set
  $existingImports = @{}
  for ($i=0; $i -lt $lines.Length; $i++) {
    if ($lines[$i] -match '^\s*import\s+(.+)$') {
      $existingImports[$Matches[1].Trim()] = $true
    }
  }

  # Build import entries to insert
  $toInsert = @()
  foreach ($imp in $EnsureImports) {
    if (-not $existingImports.ContainsKey($imp)) {
      $toInsert += "import $imp"
    }
  }

  if ($toInsert.Count -gt 0) {
    $before = $lines[0..$pkgIdx]
    $after = @()
    if ($pkgIdx + 1 -le $lines.Length - 1) { $after = $lines[($pkgIdx+1)..($lines.Length-1)] }
    ($before + $toInsert + $after) | Set-Content -Encoding UTF8 $Path
  } else {
    $lines | Set-Content -Encoding UTF8 $Path
  }
}

# Files
$repo = 'c:\Users\gaura\OneDrive\Documents\IngrediCheck-Android-ComposeUI\app\src\main\java\lc\fungee\IngrediCheck\model\repository\LoginAuthRepository.kt'
$vm   = 'c:\Users\gaura\OneDrive\Documents\IngrediCheck-Android-ComposeUI\app\src\main\java\lc\fungee\IngrediCheck\viewmodel\LoginAuthViewModel.kt'

Fix-Header -Path $repo -ExpectedPackage 'lc.fungee.IngrediCheck.model.repository' -EnsureImports @(
  'lc.fungee.IngrediCheck.model.utils.AppConstants',
  'lc.fungee.IngrediCheck.model.entities.AppleAuthConfig'
)

Fix-Header -Path $vm -ExpectedPackage 'lc.fungee.IngrediCheck.viewmodel' -EnsureImports @(
  'lc.fungee.IngrediCheck.model.utils.AppConstants'
)

Write-Host 'Header fix script completed.'
