package io.iamcyw.tower.commandhandling.model.inspection;

public interface AggregateMetaModelFactory {

    <T> AggregateModel<T> createModel(Class<T> serviceType);
}
