//package lc.fungee.IngrediCheck.data.model
////contains User, Session, AuthResult
//data class User(
//    val id: String,
//    val email: String?,
//    val displayName: String?,
//    val photoUrl: String?
//)
//
//data class Session(
//    val accessToken: String,
//    val refreshToken: String?,
//    val expiresAt: Long
//)
//
//sealed class AuthResult {
//    data class Success(val user: User) : AuthResult()
//    data class Error(val message: String) : AuthResult()
//    object Loading : AuthResult()
//}
