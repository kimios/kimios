package org.kimios.webservices;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 2/11/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionMessageWrapper {



    private String message;

    private int code;


    @JsonIgnore
    private String name;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ExceptionMessageWrapper(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public ExceptionMessageWrapper(String message, int code, String name) {
        this.message = message;
        this.code = code;
        this.name = name;
    }

    public ExceptionMessageWrapper(String message, String name) {
        this.message = message;
        this.name = name;
    }
}
