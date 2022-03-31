package io.iamcyw.tower.graphql;

import io.iamcyw.tower.messaging.MessageApi;
import io.iamcyw.tower.queryhandling.QueryHandle;

import java.util.List;

@MessageApi
public class TestQueryHandler {

    @QueryHandle
    public TestDomain testDomain(TestDomain query) {
        return query;
    }

    @QueryHandle
    public List<TestDomain> testDomains(TestDomain query) {
        return List.of(query);
    }

}
