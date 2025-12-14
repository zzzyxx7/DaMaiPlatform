package com.fuzhou.common.exception;

public class AccountUnExistException extends BaseException {
    public AccountUnExistException() {
    }
    public AccountUnExistException(String msg) {
        super(msg);
    }
}
