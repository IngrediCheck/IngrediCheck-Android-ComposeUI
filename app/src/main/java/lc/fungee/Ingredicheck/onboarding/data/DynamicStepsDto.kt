package lc.fungee.Ingredicheck.onboarding.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Root payload from dynamicJsonData.json (same structure as iOS). */
@Serializable
data class DynamicStepsPayload(
    val steps: List<DynamicStep>
)

@Serializable
data class DynamicStep(
    val id: String,
    val type: String,
    val header: DynamicStepHeader,
    val content: DynamicStepContent
)

@Serializable
data class DynamicStepHeader(
    val iconUrl: String,
    val name: String,
    val individual: DynamicHeaderVariant,
    val family: DynamicHeaderVariant,
    val singleMember: DynamicHeaderVariant? = null
)

@Serializable
data class DynamicHeaderVariant(
    val question: String,
    val description: String? = null
)

@Serializable
data class DynamicStepContent(
    val options: List<DynamicOption>? = null,
    val subSteps: List<DynamicSubStep>? = null,
    val regions: List<DynamicRegion>? = null
)

@Serializable
data class DynamicOption(
    val name: String,
    val icon: String
)

@Serializable
data class DynamicSubStep(
    val id: String,
    val title: String,
    val description: String,
    val color: String,
    @SerialName("bgImageUrl") val bgImageUrl: String,
    val options: List<DynamicOption>? = null
)

@Serializable
data class DynamicRegion(
    val name: String,
    val subRegions: List<DynamicOption>
)
