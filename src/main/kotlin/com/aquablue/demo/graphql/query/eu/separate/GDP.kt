package com.aquablue.demo.graphql.query.eu.separate

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import io.ktor.client.HttpClient
import org.springframework.stereotype.Component
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get

@Component
class GrossDomesticProduct : Query {

    @GraphQLDescription("Gross domestic product by country in mllions")
    suspend fun gdp(countryCode: String, year: Int): Int =
        eurostatGrossDomesticProduct(countryCode, year)

    private suspend fun eurostatGrossDomesticProduct(countryCode: String, year: Int): Int {
        val response: EurostatGrossDomesticProductData =
            client.get("http://ec.europa.eu/eurostat/wdds/rest/data/v2.1/json/en/nama_10_gdp?geo=${countryCode}&precision=1&na_item=B1GQ&unit=CP_MEUR&time=${year}")
        return response.value["0"]!!
    }

    private val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                serializeNulls()
                disableHtmlEscaping()
            }
        }
    }
}

private data class EurostatGrossDomesticProductData(val value: Map<String,Int>)