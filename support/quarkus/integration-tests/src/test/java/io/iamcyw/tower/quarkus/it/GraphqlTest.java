package io.iamcyw.tower.quarkus.it;

import graphql.GraphQLError;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.graphql.execution.ExecutionResponse;
import io.smallrye.graphql.execution.ExecutionService;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

@QuarkusTest
public class GraphqlTest {

    private static String TEST_G = "{ basicTestQuery(query:{payload: \"aa\"}) }";

    @Inject
    ExecutionService executionService;

    @Test
    void basicTest() {
        JsonObjectResponseWriter jsonObjectResponseWriter = new JsonObjectResponseWriter(TEST_G);
        jsonObjectResponseWriter.logInput();
        executionService.executeSync(jsonObjectResponseWriter.getInput(), jsonObjectResponseWriter);
        jsonObjectResponseWriter.logOutput();

        ExecutionResponse executionResponse = jsonObjectResponseWriter.getExecutionResponse();

        if (!executionResponse.getExecutionResult().getErrors().isEmpty()) {
            List<GraphQLError> graphQLErrors = executionResponse.getExecutionResult().getErrors();
            for (GraphQLError graphQLError : graphQLErrors) {
                throw new RuntimeException(graphQLError.getMessage());
            }
        }

    }

}
