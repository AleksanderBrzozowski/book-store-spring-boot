package com.brzozowski.presentation.common.exception

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * @author Aleksander Brzozowski
 */

@ControllerAdvice
class ExceptionHandler (private val messageSource: MessageSource){

    @ExceptionHandler
    fun handleRuntimeException(exception: BusinessException) : ResponseEntity<ErrorInfo>{
        val message = messageSource.getMessage(exception.message, emptyArray(), LocaleContextHolder.getLocaleContext().locale)
        return ResponseEntity(ErrorInfo(message), HttpStatus.BAD_REQUEST)
    }
}

class ErrorInfo(val message:String)

open class BusinessException(message: String) : RuntimeException(message)