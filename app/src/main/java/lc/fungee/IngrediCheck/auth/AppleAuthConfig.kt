package lc.fungee.IngrediCheck.auth

object AppleAuthConfig {
    // Apple Services ID (web client identifier), e.g. "com.yourcompany.service"
    // TODO: replace with your actual Services ID from Apple Developer.
    const val CLIENT_ID: String = "REPLACE_WITH_APPLE_SERVICES_ID"

    // Hosted HTTPS return URL configured on Apple Developer portal.
    // This page must forward the id_token to your app scheme, e.g. io.supabase.ingredicheck://apple?id_token=...
    // TODO: replace with your hosted URL
    const val REDIRECT_URI: String = "https://yourdomain.com/apple/callback"

    // Your app custom scheme deeplink used by the HTTPS bounce page
    const val APP_SCHEME: String = "io.supabase.ingredicheck"
    const val APP_HOST: String = "apple"
}
