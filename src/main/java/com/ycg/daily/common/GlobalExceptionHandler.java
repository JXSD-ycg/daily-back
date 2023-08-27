package com.ycg.daily.common;

import com.ycg.daily.constants.ExceptionConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;

import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthenticatedException.class)
    public R<String> handlerRuntimeException(UnauthenticatedException e) {
        log.error(ExceptionConstants.SKIP_TO_AUTHORIZED,e);
        return R.error(ExceptionConstants.SKIP_TO_AUTHORIZED);
    }


    @ExceptionHandler(RuntimeException.class)
    public R<String> handlerRuntimeException(RuntimeException e) {
        String message = e.getMessage();
        if (message.contains(ExceptionConstants.TOKEN_MISSING)) {
            log.error(ExceptionConstants.TOKEN_MISSING);
            return R.error(ExceptionConstants.TOKEN_MISSING);
        } else if (message.contains(ExceptionConstants.TOKEN_RESOLVE_FAIL)) {
            log.error(ExceptionConstants.TOKEN_RESOLVE_FAIL);
            return R.error(ExceptionConstants.TOKEN_RESOLVE_FAIL);
        } else if (message.contains(ExceptionConstants.PASSWORD_ERROR)) {
            log.error(ExceptionConstants.PASSWORD_ERROR);
            return R.error(ExceptionConstants.PASSWORD_ERROR);
        } else if (message.contains(ExceptionConstants.UNAUTHORIZED)) {
            log.error(ExceptionConstants.UNAUTHORIZED);
            return R.error(ExceptionConstants.UNAUTHORIZED);
        } else if (message.contains(ExceptionConstants.USER_IS_DISABLE)) {
            log.error(ExceptionConstants.USER_IS_DISABLE);
            return R.error(ExceptionConstants.USER_IS_DISABLE);
        } else if (message.contains(ExceptionConstants.USER_NOT_EXIST)) {
            log.error(ExceptionConstants.USER_NOT_EXIST);
            return R.error(ExceptionConstants.USER_NOT_EXIST);
        }

        log.error(ExceptionConstants.UNKNOWN_ERROR,e);
        return R.error(ExceptionConstants.UNKNOWN_ERROR);
    }

}
