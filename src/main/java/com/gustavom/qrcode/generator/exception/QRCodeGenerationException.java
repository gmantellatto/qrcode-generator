package com.gustavom.qrcode.generator.exception;

import lombok.Getter;

@Getter
public class QRCodeGenerationException extends RuntimeException {
    private final String text;

    public QRCodeGenerationException(String message, String text, Throwable cause) {
        super(message, cause);
        this.text = text;
    }

}
