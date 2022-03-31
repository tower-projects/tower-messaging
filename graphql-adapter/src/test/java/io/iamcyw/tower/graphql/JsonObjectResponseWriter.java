package io.iamcyw.tower.graphql;

import io.smallrye.graphql.execution.ExecutionResponse;
import io.smallrye.graphql.execution.ExecutionResponseWriter;
import org.jboss.logging.Logger;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * A default implementation for Execution Response Writer
 *
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class JsonObjectResponseWriter implements ExecutionResponseWriter {
    private static final Map<String, Object> JSON_PROPERTIES = new HashMap<>(1);

    protected static final Logger LOG = Logger.getLogger(JsonObjectResponseWriter.class.getName());

    static {
        JSON_PROPERTIES.put(JsonGenerator.PRETTY_PRINTING, true);
    }

    private final JsonObject input;

    private ExecutionResponse executionResponse = null;

    private Throwable throwable = null;

    JsonObjectResponseWriter(String graphQL) {
        this.input = toJsonObject(graphQL);
    }

    JsonObjectResponseWriter(JsonObject input) {
        this.input = input;
    }

    @Override
    public void write(ExecutionResponse er) {
        this.executionResponse = er;
    }

    @Override
    public void fail(Throwable t) {
        this.throwable = t;
    }

    public void logInput() {
        String prettyInput = getPrettyJson(input);
        LOG.info(prettyInput);
    }

    public void logOutput() {
        if (executionResponse != null) {
            String prettyData = getPrettyJson(executionResponse.getExecutionResultAsJsonObject());
            LOG.info(prettyData);
        } else if (throwable != null) {
            LOG.error("ERROR", throwable);
        }
    }

    public JsonObject getInput() {
        return this.input;
    }

    public JsonObject getOutput() {
        if (this.executionResponse != null) {
            return this.executionResponse.getExecutionResultAsJsonObject();
        }
        return JsonObject.EMPTY_JSON_OBJECT;
    }

    public ExecutionResponse getExecutionResponse() {
        return this.executionResponse;
    }

    private String getPrettyJson(JsonObject jsonObject) {

        JsonWriterFactory writerFactory = Json.createWriterFactory(JSON_PROPERTIES);

        try (StringWriter sw = new StringWriter(); JsonWriter jsonWriter = writerFactory.createWriter(sw)) {
            jsonWriter.writeObject(jsonObject);
            return sw.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private JsonObject toJsonObject(String graphQL) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("query", graphQL);
        return builder.build();
    }
}
