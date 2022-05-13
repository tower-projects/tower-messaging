package io.iamcyw.tower.schema.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 结果对象
 */
public final class Type extends Reference {

    private String description;

    private Map<String, Field> fields = new LinkedHashMap<>();

    private Map<String, Operation> operations = new LinkedHashMap<>();

    private Map<String, Operation> batchOperations = new LinkedHashMap<>();

    private Set<Reference> interfaces = new LinkedHashSet<>();

    public Type() {
    }

    public Type(String className, String name, String description) {
        super(className, name, ReferenceType.TYPE);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public void setFields(Map<String, Field> fields) {
        this.fields = fields;
    }

    public void addField(Field field) {
        this.fields.put(field.getName(), field);
    }

    public boolean hasFields() {
        return !this.fields.isEmpty();
    }

    public boolean hasField(String fieldName) {
        return this.fields.containsKey(fieldName);
    }

    public Map<String, Operation> getOperations() {
        return operations;
    }

    public void setOperations(Map<String, Operation> operations) {
        this.operations = operations;
    }

    public void addOperation(Operation operation) {
        this.operations.put(operation.getName(), operation);
    }

    public boolean hasOperations() {
        return !this.operations.isEmpty();
    }

    public boolean hasOperation(String operationName) {
        return this.operations.containsKey(operationName);
    }

    public Map<String, Operation> getBatchOperations() {
        return batchOperations;
    }

    public void setBatchOperations(Map<String, Operation> operations) {
        this.batchOperations = operations;
    }

    public void addBatchOperation(Operation operation) {
        this.batchOperations.put(operation.getName(), operation);
    }

    public boolean hasBatchOperations() {
        return !this.batchOperations.isEmpty();
    }

    public boolean hasBatchOperation(String operationName) {
        return this.batchOperations.containsKey(operationName);
    }

    public Set<Reference> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Set<Reference> interfaces) {
        this.interfaces = interfaces;
    }

    public void addInterface(Reference interfaceType) {
        this.interfaces.add(interfaceType);
    }

    @Override
    public String toString() {
        return "Type{" + "description='" + description + '\'' + ", fields=" + fields + ", operations=" + operations +
                ", batchOperations=" + batchOperations + ", interfaces=" + interfaces + '}';
    }

}
