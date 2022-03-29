package io.iamcyw.tower.quarkus.it.domain;

public class BasicTestQuery {
    private String payload;

    public BasicTestQuery() {
    }

    public BasicTestQuery(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
