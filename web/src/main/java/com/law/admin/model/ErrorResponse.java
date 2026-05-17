package com.law.admin.model;

import java.util.List;

/**
 * 錯誤回應格式
 */
public class ErrorResponse {
    private String code;
    private Object msg;
    private List<String> errorList;

    @SuppressWarnings("unchecked")
    public ErrorResponse(String code, Object message) {
        this.code = code;
        this.msg = message;
        if (message instanceof List) {
            try {
                this.errorList = (List<String>) message;
            } catch (ClassCastException e) {
                this.errorList = null;
            }
        }
    }

    public ErrorResponse(String code, List<String> errorList) {
        this.code = code;
        this.msg = errorList;
        this.errorList = errorList;
    }

    public String getCode() { return code; }
    public Object getMsg() { return msg; }
    public List<String> getErrorList() { return errorList; }
}