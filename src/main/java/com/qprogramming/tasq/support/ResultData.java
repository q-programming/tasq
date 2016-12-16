package com.qprogramming.tasq.support;

/**
 * Helper class to produce json readable result to all post events
 *
 * @author Khobar
 */
public class ResultData {
    public Code code;
    public String message;

    public ResultData() {

    }

//    public ResultData(String code, String message) {
//        this.code = code;
//        this.message = message;
//    }

    public ResultData(Code code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return this.code + " " + this.message;
    }

    public enum Code {
        OK, WARNING, ERROR
    }

}
