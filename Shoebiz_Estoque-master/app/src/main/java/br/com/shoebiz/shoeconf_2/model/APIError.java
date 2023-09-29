package br.com.shoebiz.shoeconf_2.model;

public class APIError {

    private int errorCode;
    private String errorMessage;
    private String message;

    public APIError() {
    }

    public int status() {
        return errorCode;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public String message() {
        return message;
    }
}