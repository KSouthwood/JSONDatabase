package model;

public class ServerResponse {
    String response = null;
    String reason = null;
    String value = null;

    public ServerResponse() {
    }

    public ServerResponse response(String response) {
        this.response = response;
        return this;
    }

    public ServerResponse reason(String reason) {
        this.reason = reason;
        return this;
    }

    public ServerResponse value(String value) {
        this.value = value;
        return this;
    }
}
