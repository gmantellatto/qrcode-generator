package com.gustavom.qrcode.generator.exception;

import com.gustavom.qrcode.generator.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar QRCodeGenerationException corretamente")
    void shouldHandleQRCodeGenerationException() {
        // Arrange
        QRCodeGenerationException exception = new QRCodeGenerationException(
                "Erro ao gerar",
                "texto teste",
                new RuntimeException()
        );

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleQRCodeGenerationException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro ao gerar QR Code", response.getBody().message());
        assertEquals(500, response.getBody().status());
    }

    @Test
    @DisplayName("Deve tratar QRCodeUploadException corretamente")
    void shouldHandleQRCodeUploadException() {
        // Arrange
        QRCodeUploadException exception = new QRCodeUploadException(
                "Erro ao fazer upload",
                new RuntimeException()
        );

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleQRCodeUploadException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro ao fazer upload do QR Code", response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar IllegalArgumentException corretamente")
    void shouldHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Texto inválido");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Texto inválido", response.getBody().message());
        assertEquals(400, response.getBody().status());
    }

    @Test
    @DisplayName("Deve tratar Exception genérica corretamente")
    void shouldHandleGenericException() {
        // Arrange
        Exception exception = new Exception("Erro desconhecido");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro interno do servidor", response.getBody().message());
    }
}
