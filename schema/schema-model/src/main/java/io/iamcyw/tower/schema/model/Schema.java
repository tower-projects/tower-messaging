package io.iamcyw.tower.schema.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Schema implements Serializable {
    private Set<Operation> queries = new HashSet<>();

    private Set<Operation> commands = new HashSet<>();

    private Set<Operation> subscriptions = new HashSet<>();

    private Set<Operation> predicates = new HashSet<>();

    private Map<String, InputType> inputs = new HashMap<>();

    private Map<String, String> wrappedDataFetchers = new HashMap<>();

    private Map<String, String> fieldDataFetchers = new HashMap<>();

    public Schema() {
    }

    public Set<Operation> getQueries() {
        return queries;
    }

    public void setQueries(Set<Operation> queries) {
        this.queries = queries;
    }

    public void addQuery(Operation query) {
        this.queries.add(query);
    }

    public boolean hasOperations() {
        return hasQueries() || hasCommands();
    }

    public Set<Operation> getPredicates() {
        return predicates;
    }

    public void setPredicates(Set<Operation> predicates) {
        this.predicates = predicates;
    }

    public void addPredicate(Operation predicate) {
        this.predicates.add(predicate);
    }

    public boolean hasPredicates() {
        return !this.predicates.isEmpty();
    }

    public boolean hasQueries() {
        return !this.queries.isEmpty();
    }

    public Set<Operation> getCommands() {
        return commands;
    }

    public void setCommands(Set<Operation> commands) {
        this.commands = commands;
    }

    public void addCommand(Operation command) {
        this.commands.add(command);
    }

    public boolean hasCommands() {
        return !this.commands.isEmpty();
    }

    public Set<Operation> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Operation> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public void addSubscription(Operation subscription) {
        this.subscriptions.add(subscription);
    }

    public boolean hasSubscriptions() {
        return !this.subscriptions.isEmpty();
    }

    // public Map<Group, Set<Operation>> getGroupedQueries() {
    //     return groupedQueries;
    // }

    // public void setGroupedQueries(Map<Group, Set<Operation>> groupedQueries) {
    //     this.groupedQueries = groupedQueries;
    // }

    // public void addGroupedQuery(Group group, Operation query) {
    //     addToOperationMap(this.groupedQueries, group, query);
    // }

    // public boolean hasGroupedQueries() {
    //     return !this.groupedQueries.isEmpty();
    // }
    //
    // public Map<Group, Set<Operation>> getGroupedMutations() {
    //     return groupedMutations;
    // }
    //
    // public void setGroupedMutations(Map<Group, Set<Operation>> groupedMutations) {
    //     this.groupedMutations = groupedMutations;
    // }

    // public void addGroupedMutation(Group group, Operation mutation) {
    //     addToOperationMap(this.groupedMutations, group, mutation);
    // }

    // public boolean hasGroupedMutations() {
    //     return !this.groupedMutations.isEmpty();
    // }

    // public Map<Group, Set<Operation>> getGroupedSubscriptions() {
    //     return groupedSubscriptions;
    // }

    // public void setGroupedSubscriptions(Map<Group, Set<Operation>> groupedSubscriptions) {
    //     this.groupedSubscriptions = groupedSubscriptions;
    // }

    // public void addGroupedSubscription(Group group, Operation subscription) {
    //     addToOperationMap(this.groupedSubscriptions, group, subscription);
    // }

    // public boolean hasGroupedSubscriptions() {
    //     return !this.groupedSubscriptions.isEmpty();
    // }

    public Map<String, InputType> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, InputType> inputs) {
        this.inputs = inputs;
    }

    public void addInput(InputType input) {
        this.inputs.put(input.getName(), input);
    }

    public boolean containsInput(String name) {
        return this.inputs.containsKey(name);
    }

    public boolean hasInputs() {
        return !this.inputs.isEmpty();
    }

    public Map<String, String> getWrappedDataFetchers() {
        return this.wrappedDataFetchers;
    }

    public void setWrappedDataFetchers(Map<String, String> wrappedDataFetchers) {
        this.wrappedDataFetchers = wrappedDataFetchers;
    }

    public void addWrappedDataFetcher(String forReturn, String className) {
        this.wrappedDataFetchers.put(forReturn, className);
    }

    public boolean hasWrappedDataFetchers() {
        return !this.wrappedDataFetchers.isEmpty();
    }

    public Map<String, String> getFieldDataFetchers() {
        return this.fieldDataFetchers;
    }

    public void setFieldDataFetchers(Map<String, String> fieldDataFetchers) {
        this.fieldDataFetchers = fieldDataFetchers;
    }

    public void addFieldDataFetcher(String forReturn, String className) {
        this.fieldDataFetchers.put(forReturn, className);
    }

    public boolean hasFieldDataFetchers() {
        return !this.fieldDataFetchers.isEmpty();
    }

    @Override
    public String toString() {
        return "Schema{" + "queries=" + queries + ", mutations=" + commands + ", subscriptions=" + subscriptions +
                ", wrappedDataFetchers=" + wrappedDataFetchers + ", fieldDataFetchers=" + fieldDataFetchers + '}';
    }

}
