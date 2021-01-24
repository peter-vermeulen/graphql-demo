package com.aquablue.demo.graphql.query.eu.combined

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CountryQuery : Query  {

    @GraphQLDescription("data by country and year")
    suspend fun country(countryCode: String, year: Int) =
        Country(countryCode, year)

}

data class Country(val countryCode: String, val year: Int) {
    suspend fun population() = euroStatPopulation(countryCode, year)
    suspend fun gdp() = euroStatGrossDomesticProduct(countryCode, year)
}


private suspend fun euroStatPopulation(countryCode: String, year: Int): Int {
    LoggerFactory.getLogger("eurostat population").info("Calling eurostat population for ${countryCode} and ${year}")
    val response: EuroStatData =
        client.get("http://ec.europa.eu/eurostat/wdds/rest/data/v2.1/json/en/demo_pjan?geo=${countryCode}&time=${year}&age=TOTAL&sex=T")
    return response.value["0"]!!
}

private suspend fun euroStatGrossDomesticProduct(countryCode: String, year: Int): Int {
    LoggerFactory.getLogger("eurostat GDP").info("Calling eurostat GDP for ${countryCode} and ${year}")
    val response: EuroStatData =
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
private data class EuroStatData(val value: Map<String,Int>)