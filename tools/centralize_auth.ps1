$ErrorActionPreference = 'Stop'

function Update-LoginAuthRepository {
  param([string]$Path)
  Write-Host "Updating $Path"
  $text = Get-Content -Raw -Encoding UTF8 $Path

  # Inject imports after package if missing
  if ($text -notmatch 'import\s+lc\.fungee\.IngrediCheck\.model\.utils\.AppConstants') {
    $text = $text -replace '^(package[^\r\n]*\r?\n)', "$1import lc.fungee.IngrediCheck.model.utils.AppConstants`r`n"
  }
  if ($text -notmatch 'import\s+lc\.fungee\.IngrediCheck\.model\.entities\.AppleAuthConfig') {
    $text = $text -replace '^(package[^\r\n]*\r?\n)', "$1import lc.fungee.IngrediCheck.model.entities.AppleAuthConfig`r`n"
  }

  # Literal replacements
  $text = $text.Replace('getSharedPreferences("supabase_session", Context.MODE_PRIVATE)','getSharedPreferences(AppConstants.Prefs.SUPABASE_SESSION, Context.MODE_PRIVATE)')
  $text = $text.Replace('getSharedPreferences("user_session", Context.MODE_PRIVATE)','getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)')
  $text = $text.Replace('val appRedirect = "io.supabase.ingredicheck://callback"','val appRedirect = "${AppleAuthConfig.APP_SCHEME}://callback"')
  $text = $text.Replace('.authority("wqidjkpfdrvomfkmefqc.supabase.co")','.authority(Uri.parse(supabaseUrl).host ?: AppConstants.Supabase.HOST)')
  $text = $text.Replace('appendQueryParameter("provider", "apple")','appendQueryParameter("provider", AppConstants.Providers.APPLE)')
  $text = $text.Replace('getString("login_provider", null)','getString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, null)')
  $text = $text.Replace('"apple" -> AuthProvider.APPLE','AppConstants.Providers.APPLE -> AuthProvider.APPLE')
  $text = $text.Replace('"google" -> AuthProvider.GOOGLE','AppConstants.Providers.GOOGLE -> AuthProvider.GOOGLE')
  $text = $text.Replace('"anonymous" -> AuthProvider.ANONYMOUS','AppConstants.Providers.ANONYMOUS -> AuthProvider.ANONYMOUS')

  Set-Content -Encoding UTF8 $Path $text
}

function Update-LoginAuthViewModel {
  param([string]$Path)
  Write-Host "Updating $Path"
  $text = Get-Content -Raw -Encoding UTF8 $Path

  # Inject import after package if missing
  if ($text -notmatch 'import\s+lc\.fungee\.IngrediCheck\.model\.utils\.AppConstants') {
    $text = $text -replace '^(package[^\r\n]*\r?\n)', "$1import lc.fungee.IngrediCheck.model.utils.AppConstants`r`n"
  }

  # Literal replacements
  $text = $text.Replace('getSharedPreferences("user_session", Context.MODE_PRIVATE)','getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)')
  $text = $text.Replace('.putString("login_provider", "apple")','.putString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, AppConstants.Providers.APPLE)')
  $text = $text.Replace('.putString("login_provider", "google")','.putString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, AppConstants.Providers.GOOGLE)')
  $text = $text.Replace('.putString("login_provider", "anonymous")','.putString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, AppConstants.Providers.ANONYMOUS)')

  Set-Content -Encoding UTF8 $Path $text
}

$repo = 'c:\Users\gaura\OneDrive\Documents\IngrediCheck-Android-ComposeUI\app\src\main\java\lc\fungee\IngrediCheck\model\repository\LoginAuthRepository.kt'
$vm   = 'c:\Users\gaura\OneDrive\Documents\IngrediCheck-Android-ComposeUI\app\src\main\java\lc\fungee\IngrediCheck\viewmodel\LoginAuthViewModel.kt'

Update-LoginAuthRepository -Path $repo
Update-LoginAuthViewModel  -Path $vm

Write-Host 'Centralization script completed.'
