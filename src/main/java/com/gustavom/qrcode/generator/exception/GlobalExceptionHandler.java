package com.gustavom.qrcode.generator.exception;

import com.gustavom.qrcode.generator.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(QRCodeGenerationException.class)
    public ResponseEntity<ErrorResponse> handleQRCodeGenerationException(QRCodeGenerationException ex) {
        log.error("Erro ao gerar QR Code: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("Erro ao gerar QR Code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(QRCodeUploadException.class)
    public ResponseEntity<ErrorResponse> handleQRCodeUploadException(QRCodeUploadException ex) {
        log.error("Erro ao fazer upload do QR Code: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("Erro ao fazer upload do QR Code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro inesperado: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("Erro interno do servidor", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
