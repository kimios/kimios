package org.kimios.kernel.ws.pojo.web;

public class StringResponse {

    private String response;

    public StringResponse(String s) {
        this.response = s;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}