package io.iamcyw.tower.graphql.execution;

import io.iamcyw.tower.graphql.execution.datafetcher.DefaultDataFetcher;
import io.smallrye.graphql.execution.datafetcher.PlugableDataFetcher;
import io.smallrye.graphql.schema.model.Operation;
import io.smallrye.graphql.spi.DataFetcherService;

public class DefaultDataFetcherService implements DataFetcherService {
    @Override
    public Integer getPriority() {
        return 1;
    }

    @Override
    public PlugableDataFetcher getCompletionStageDataFetcher(Operation operation) {
        return DataFetcherService.super.getCompletionStageDataFetcher(operation);
    }

    @Override
    public PlugableDataFetcher getUniDataFetcher(Operation operation) {
        return DataFetcherService.super.getUniDataFetcher(operation);
    }

    @Override
    public PlugableDataFetcher getPublisherDataFetcher(Operation operation) {
        return DataFetcherService.super.getPublisherDataFetcher(operation);
    }

    @Override
    public PlugableDataFetcher getMultiDataFetcher(Operation operation) {
        return DataFetcherService.super.getMultiDataFetcher(operation);
    }

    @Override
    public PlugableDataFetcher getOtherWrappedDataFetcher(Operation operation) {
        return getDefaultDataFetcher(operation);
    }

    @Override
    public PlugableDataFetcher getOtherFieldDataFetcher(Operation operation) {
        return getDefaultDataFetcher(operation);
    }

    @Override
    public PlugableDataFetcher getDefaultDataFetcher(Operation operation) {
        return new DefaultDataFetcher(operation);
    }

}
