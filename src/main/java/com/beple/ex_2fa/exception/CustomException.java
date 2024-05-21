package com.beple.ex_2fa.exception;

import com.beple.ex_2fa.enums.IResponseMessage;

public class CustomException extends RuntimeException {
    public CustomException() {
        super("Custom Exception");
    }
    public CustomException(String message) {
        super(message);
    }

    public CustomException(IResponseMessage resMsgInterface) {
        super(resMsgInterface.getMessage(), new Throwable(resMsgInterface.getCode()));
    }

    public CustomException(String msgCode,String message){
        super(msgCode + " : "+message);
    }

}
