package com.yifu.emos.exception;

import lombok.Data;

/**
 * @auther YIFU GAO
 * @date 2022/12/23/21:33
 * TODO
 */
@Data
public class EmosException extends RuntimeException {
    private String msg;
    private int code = 500;

    public EmosException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public EmosException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }

    public EmosException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public EmosException(String msg, int code, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
        this.code = code;
    }


}
