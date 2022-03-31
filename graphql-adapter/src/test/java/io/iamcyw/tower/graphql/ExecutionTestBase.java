package io.iamcyw.tower.graphql;

import graphql.schema.GraphQLSchema;
import io.iamcyw.tower.graphql.schema.SchemaBuilder;
import io.smallrye.graphql.bootstrap.Bootstrap;
import io.smallrye.graphql.execution.ExecutionService;
import io.smallrye.graphql.execution.SchemaPrinter;
import io.smallrye.graphql.schema.model.Schema;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * Base class for execution tests
 *
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public abstract class ExecutionTestBase {
    private static final String DATA = "data";

    private static final String ERRORS = "errors";

    protected static final Logger LOG = Logger.getLogger(ExecutionTestBase.class.getName());

    protected ExecutionService executionService;

    @BeforeEach
    public void init() {
        IndexView index = getIndex();
        Schema schema = SchemaBuilder.build(index);
        GraphQLSchema graphQLSchema = Bootstrap.bootstrap(schema);

        SchemaPrinter printer = new SchemaPrinter();
        String schemaString = printer.print(graphQLSchema);
        LOG.info("================== Testing against: ====================");
        LOG.info(schemaString);
        LOG.info("========================================================");
        this.executionService = new ExecutionService(graphQLSchema, schema.getBatchOperations(), true);
    }

    protected IndexView getIndex() {
        return Indexer.getAllTestIndex();
    }

    protected JsonObject executeAndGetData(String graphQL) {
        JsonObject result = executeAndGetResult(graphQL);
        JsonValue value = result.get(DATA);
        if (value != null) {
            return result.getJsonObject(DATA);
        }
        return JsonObject.EMPTY_JSON_OBJECT;
    }

    protected JsonArray executeAndGetErrors(String graphQL) {
        JsonObject result = executeAndGetResult(graphQL);
        JsonValue value = result.get(ERRORS);
        if (value != null) {
            return result.getJsonArray(ERRORS);
        }
        return null;
    }

    protected JsonObject executeAndGetResult(String graphQL) {
        JsonObjectResponseWriter jsonObjectResponseWriter = new JsonObjectResponseWriter(graphQL);
        jsonObjectResponseWriter.logInput();
        executionService.executeSync(jsonObjectResponseWriter.getInput(), jsonObjectResponseWriter);
        jsonObjectResponseWriter.logOutput();

        return jsonObjectResponseWriter.getOutput();
    }

}
