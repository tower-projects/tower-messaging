package io.iamcyw.tower.graphql;

import org.junit.jupiter.api.Test;

import javax.json.JsonObject;

import static io.iamcyw.tower.utils.Assert.assertNotNull;

class DefaultDataFetcherTest extends ExecutionTestBase {

    private static final String TEST_QUERY1 =
            "{\n" + "  testDomain(query:{name:\"Phillip\",size:2}) {\n" + "    name\n" + "  }\n" + "}";

    // @Test
    void test() {

        // Schema schema = SchemaBuilder.build(Indexer.getAllTestIndex());
        //
        // GraphQLSchema graphQLSchema = Bootstrap.bootstrap(schema);
        // GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();
        // ExecutionResult result = graphQL.execute(TEST_QUERY1);
        //
        // if (!result.getErrors().isEmpty()) {
        //     for (GraphQLError error : result.getErrors()) {
        //         System.out.println(error);
        //     }
        // }
        JsonObject data = executeAndGetData(TEST_QUERY1);

        JsonObject testObject = data.getJsonObject("testCommand");

        assertNotNull(testObject);

        // assertFalse(testObject.isNull("name"), "name should not be null");
        // assertEquals("Phillip", testObject.getString("name"));
        //
        // assertFalse(testObject.isNull("id"), "id should not be null");
        //
        // // Testing source
        // assertFalse(testObject.isNull("timestamp"), "timestamp should not be null");
        // assertFalse(testObject.get("timestamp").asJsonObject().isNull("value"), "timestamp value should not be
        // null");

    }

}