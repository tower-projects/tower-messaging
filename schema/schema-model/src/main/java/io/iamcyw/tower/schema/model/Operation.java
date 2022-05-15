package io.iamcyw.tower.schema.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 表示对 Query,Command 或 Predicate 的操作
 */
public final class Operation extends Field {
    /**
     * Java class this is on
     */
    private String className;

    private String invoke;

    /**
     * The arguments (if any)
     */
    private List<Argument> arguments = new LinkedList<>();

    private InputType source;

    /**
     * Operation Type (Query/Mutation)
     */
    private OperationType operationType;

    private Map<String, String> parameter = new HashMap<>();

    /**
     * If this should be executed blocking. By default all normal object returns will be blocking, except if marked
     * with @NonBlocking
     * And all Uni and CompletionStage will be non blocking by default, except if marked with @Blocking
     */
    private Execute execute;

    public Operation() {
    }

    public Operation(String className, String methodName, String propertyName, String name, Reference reference,
                     final OperationType operationType, Execute execute) {
        super(methodName, propertyName, name, reference);
        this.className = className;
        this.operationType = operationType;
        this.execute = execute;
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Argument> getArguments() {
        return this.arguments;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public void addArgument(Argument argument) {
        this.arguments.add(argument);
    }

    public String getParameter(String name) {
        return parameter.get(name);
    }

    public Map<String, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }

    public void addParameter(String name, String value) {
        parameter.put(name, value);
    }

    public boolean hasArguments() {
        return !this.arguments.isEmpty();
    }

    public String getInvoke() {
        return invoke;
    }

    public void setInvoke(String invoke) {
        this.invoke = invoke;
    }

    public boolean hasInvoke() {
        return this.invoke != null;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(final OperationType operationType) {
        this.operationType = operationType;
    }

    public Execute getExecute() {
        return execute;
    }

    public void setExecute(Execute execute) {
        this.execute = execute;
    }


}
