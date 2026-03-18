package lc.fungee.Ingredicheck.network

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import lc.fungee.Ingredicheck.AppConfig

object ApiUrlBuilder {
    fun url(
        endpoint: SafeEatsEndpoint,
        vararg args: String,
        query: Map<String, String> = emptyMap()
    ): String {
        val base = when (endpoint.base) {
            ApiBase.FLY -> AppConfig.flyIOBaseURL
            ApiBase.SUPABASE_FUNCTIONS -> AppConfig.supabaseFunctionsURLBase
        }

        val baseTrimmed = base.trimEnd('/')
        val path = String.format(endpoint.template, *args).trimStart('/')
        val rawUrl = "$baseTrimmed/$path"

        if (query.isEmpty()) return rawUrl

        val queryString = query.entries.joinToString("&") { (k, v) ->
            val ek = URLEncoder.encode(k, StandardCharsets.UTF_8.toString())
            val ev = URLEncoder.encode(v, StandardCharsets.UTF_8.toString())
            "$ek=$ev"
        }

        return "$rawUrl?$queryString"
    }
}
