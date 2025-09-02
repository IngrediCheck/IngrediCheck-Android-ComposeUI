package lc.fungee.IngrediCheck.data.model
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class SupabaseSession(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String,
    val user: SupabaseUser
)

data class SupabaseUser(
    val id: String,
    val aud: String,
    val email: String,
    @SerializedName("created_at") val createdAt: String,
    val role: String
)

data class AppMetadata(
    val provider: String? = null,
    val providers: List<String>? = null
)

data class UserMetadata(
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val email: String? = null,
    @SerializedName("email_verified") val emailVerified: Boolean? = null,
    @SerializedName("full_name") val fullName: String? = null,
    val iss: String? = null,
    val name: String? = null,
    @SerializedName("phone_verified") val phoneVerified: Boolean? = null,
    val picture: String? = null,
    @SerializedName("provider_id") val providerId: String? = null,
    val sub: String? = null
)

data class Identity(
    @SerializedName("identity_id") val identityId: String,
    val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("identity_data") val identityData: IdentityData? = null,
    val provider: String,
    @SerializedName("last_sign_in_at") val lastSignInAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val email: String? = null
)

data class IdentityData(
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val email: String? = null,
    @SerializedName("email_verified") val emailVerified: Boolean? = null,
    @SerializedName("full_name") val fullName: String? = null,
    val iss: String? = null,
    val name: String? = null,
    @SerializedName("phone_verified") val phoneVerified: Boolean? = null,
    val picture: String? = null,
    @SerializedName("provider_id") val providerId: String? = null,
    val sub: String? = null
)
