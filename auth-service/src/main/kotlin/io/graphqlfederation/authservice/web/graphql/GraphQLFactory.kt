package io.graphqlfederation.authservice.web.graphql

import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import io.gqljf.federation.FederatedSchemaBuilder
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import javax.inject.Singleton

@Factory
class GraphQLFactory(
    private val signInFetcher: SignInFetcher,
    private val validateTokenFetcher: ValidateTokenFetcher
) {

    @Bean
    @Singleton
    fun graphQL(resourceResolver: ResourceResolver): GraphQL {
        val schemaInputStream = resourceResolver.getResourceAsStream("classpath:schema.graphqls").get()
        val transformedGraphQLSchema = FederatedSchemaBuilder()
            .schemaInputStream(schemaInputStream)
            .runtimeWiring(createRuntimeWiring())
            .build()

        return GraphQL.newGraphQL(transformedGraphQLSchema).build()
    }

    private fun createRuntimeWiring() = RuntimeWiring.newRuntimeWiring()
        .type("Query") { builder ->
            builder.dataFetcher("validateToken", validateTokenFetcher)
        }
        .type("Mutation") { builder ->
            builder.dataFetcher("signIn", signInFetcher)
        }
        .build()
}
