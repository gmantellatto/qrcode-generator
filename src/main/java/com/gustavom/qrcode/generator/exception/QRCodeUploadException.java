package com.gustavom.qrcode.generator.exception;

public class QRCodeUploadException extends RuntimeException {
    public QRCodeUploadException(String message) {
        super(message);
    }

    public QRCodeUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
