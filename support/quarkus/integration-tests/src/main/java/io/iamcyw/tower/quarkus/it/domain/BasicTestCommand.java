package io.iamcyw.tower.quarkus.it.domain;

public class BasicTestCommand {
    private String payload;

    public BasicTestCommand() {
    }

    public BasicTestCommand(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
