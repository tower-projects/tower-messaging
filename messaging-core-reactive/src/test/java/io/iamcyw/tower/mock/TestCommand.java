package io.iamcyw.tower.mock;

import java.io.Serializable;

public class TestCommand implements Serializable {
    private final String id;

    public TestCommand(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }


}
