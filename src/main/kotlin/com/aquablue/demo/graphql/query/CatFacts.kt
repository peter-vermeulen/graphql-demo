package com.aquablue.demo.graphql.query

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import io.ktor.client.HttpClient
import org.springframework.stereotype.Component
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get

@Component
class CatFacts : Query {

    @GraphQLDescription("some cat facts from https://alexwohlbruck.github.io/cat-facts/")
    suspend fun catFacts(): List<CatFact> =
        client.get("https://cat-fact.herokuapp.com/facts")

    private val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                serializeNulls()
                disableHtmlEscaping()
            }
        }
    }
}

data class CatFact(val text:String)