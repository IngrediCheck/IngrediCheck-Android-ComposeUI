package lc.fungee.Ingredicheck.network

enum class ApiBase {
    FLY,
    SUPABASE_FUNCTIONS
}

enum class SafeEatsEndpoint(
    val template: String,
    val base: ApiBase
) {
    MEMOJI("memoji", ApiBase.SUPABASE_FUNCTIONS),

    PREFERENCE_LISTS_DEFAULT("preferencelists/default", ApiBase.SUPABASE_FUNCTIONS),
    PREFERENCE_LISTS_DEFAULT_ITEM("preferencelists/default/%s", ApiBase.SUPABASE_FUNCTIONS),

    FAMILY("family", ApiBase.SUPABASE_FUNCTIONS),
    FAMILY_JOIN("family/join", ApiBase.SUPABASE_FUNCTIONS),
    FAMILY_LEAVE("family/leave", ApiBase.SUPABASE_FUNCTIONS),
    FAMILY_MEMBERS("family/members", ApiBase.SUPABASE_FUNCTIONS),
    FAMILY_MEMBER("family/members/%s", ApiBase.SUPABASE_FUNCTIONS),
    FAMILY_INVITE("family/invite", ApiBase.SUPABASE_FUNCTIONS),

    FAMILY_FOOD_NOTES("family/food-notes", ApiBase.SUPABASE_FUNCTIONS),
    FAMILY_FOOD_NOTES_ALL("family/food-notes/all", ApiBase.SUPABASE_FUNCTIONS),
    FAMILY_MEMBER_FOOD_NOTES("family/members/%s/food-notes", ApiBase.SUPABASE_FUNCTIONS),

    FAMILY_FOOD_NOTES_SUMMARY("family/food-notes/summary", ApiBase.FLY),

    SCAN_BARCODE("v2/scan/barcode", ApiBase.FLY)
}
