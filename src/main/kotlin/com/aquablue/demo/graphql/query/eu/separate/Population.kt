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
class Population : Query {

    @GraphQLDescription("population by country")
    suspend fun population(countryCode: String, year: Int): Int =
        eurostatPopulation(countryCode, year)

    private suspend fun eurostatPopulation(countryCode: String, year: Int): Int {
        val response: EurostatData =
            client.get("http://ec.europa.eu/eurostat/wdds/rest/data/v2.1/json/en/demo_pjan?geo=${countryCode}&time=${year}&age=TOTAL&sex=T")
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

private data class EurostatData(val value: Map<String,Int>)