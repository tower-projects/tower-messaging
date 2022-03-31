package io.iamcyw.tower.quarkus.runtime.graphql;


import io.iamcyw.tower.graphql.execution.datafetcher.QueryGatewayLookupService;
import io.iamcyw.tower.queryhandling.gateway.QueryGateway;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.CDI;
import java.util.Set;

public class CdiQueryGatewayLookupService implements QueryGatewayLookupService {
    @Override
    public QueryGateway getQueryGateway() {
        CDI<Object> cdi = CDI.current();
        return cdi.select(QueryGateway.class).get();
    }

    private Bean<?> getExactlyOneObject(Set<Bean<?>> set) {
        if (set.size() > 1) {
            throw new AmbiguousResolutionException();
        }
        if (set.size() == 0) {
            throw new UnsatisfiedResolutionException();
        }
        return set.iterator().next();
    }

}
