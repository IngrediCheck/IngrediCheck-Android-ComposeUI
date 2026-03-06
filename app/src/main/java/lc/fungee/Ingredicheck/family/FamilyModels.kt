package lc.fungee.Ingredicheck.family

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FamilyMemberDto(
    val id: String,
    val name: String,
    val color: String,
    val joined: Boolean? = null,
    val invitePending: Boolean? = null,
    @SerialName("imageFileHash") val imageFileHash: String? = null
)

@Serializable
data class FamilyDto(
    val name: String,
    val selfMember: FamilyMemberDto,
    val otherMembers: List<FamilyMemberDto> = emptyList(),
    val version: Long = 0L
)

@Serializable
data class CreateFamilyRequest(
    val name: String,
    val selfMember: FamilyMemberDto,
    val otherMembers: List<FamilyMemberDto>? = null
)

@Serializable
data class InviteRequest(
    @SerialName("memberID") val memberId: String
)

@Serializable
data class InviteResponse(
    val inviteCode: String
)

@Serializable
data class JoinFamilyRequest(
    val inviteCode: String
)

@Serializable
data class UpdateFamilyRequest(
    val name: String
)

@Serializable
data class UpdateMemberRequest(
    val name: String,
    val color: String,
    @SerialName("imageFileHash") val imageFileHash: String? = null
)

@Serializable
data class ApiErrorResponse(
    val error: String? = null,
    val message: String? = null
)
