package io.iamcyw.tower.messaging.test;

import io.iamcyw.tower.messaging.QueryHandle;
import io.iamcyw.tower.messaging.UseCase;

@UseCase
public class TestService {

    @QueryHandle
    public String query(TestQuery query) {
        return "success";
    }

}
