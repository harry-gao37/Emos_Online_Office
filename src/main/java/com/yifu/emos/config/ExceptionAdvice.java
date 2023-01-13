package com.yifu.emos.config;

import com.yifu.emos.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @auther YIFU GAO
 * @date 2022/12/28/16:07
 * File Info: dealing with global exception
 */
@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e) {
        //print out logging
        log.error("Implementation exception",e);
        if (e instanceof MethodArgumentNotValidException){
            //backend not pass verifying
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            return exception.getBindingResult().getFieldError().getDefaultMessage();
        }
        else if (e instanceof EmosException){
            EmosException exception = (EmosException) e;
            return ((EmosException) e).getMsg();
        }
        else if (e instanceof UnauthorizedException){
            return "no related authorization";
        }
        else{
            return "backend implementation error";
        }
    }
}
