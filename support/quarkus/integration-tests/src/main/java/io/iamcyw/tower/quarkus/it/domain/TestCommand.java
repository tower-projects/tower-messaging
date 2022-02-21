package io.iamcyw.tower.quarkus.it.domain;

public class TestCommand {
    public String payload;

    public TestCommand() {
    }

    public TestCommand(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
