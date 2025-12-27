package com.gustavom.qrcode.generator.service;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.gustavom.qrcode.generator.dto.QRCodeGenerateResponse;
import com.gustavom.qrcode.generator.exception.QRCodeGenerationException;
import com.gustavom.qrcode.generator.exception.QRCodeUploadException;
import com.gustavom.qrcode.generator.ports.StoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class QRCodeGenerateServiceTest {

    @Mock
    private StoragePort storagePort;

    @Mock
    private MatrixToImageWriter matrixToImageWriter;

    @InjectMocks
    private QRCodeGenerateService qrCodeGenerateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve gerar QR Code e fazer upload com sucesso")
    void shouldGenerateAndUploadQRCodeSuccessfully() {
        // Arrange
        String text = "https://example.com";
        String expectedUrl = "https://storage.example.com/qrcode.png";

        when(storagePort.uploadFile(any(byte[].class), anyString(), eq("image/png"))).thenReturn(expectedUrl);

        // Act
        QRCodeGenerateResponse result = qrCodeGenerateService.generateAndUploadQRCode(text);

        // Assert
        assertNotNull(result);
        assertEquals(expectedUrl, result.url());
        verify(storagePort, times(1)).uploadFile(any(byte[].class), anyString(), eq("image/png"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando texto for vazio")
    void shouldThrowExceptionWhenTextIsEmpty() {
        String text = "";


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> qrCodeGenerateService.generateAndUploadQRCode(text)
        );

        assertTrue(exception.getMessage().contains("não pode ser vazio"));
        verify(storagePort, never()).uploadFile(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando texto for null")
    void shouldThrowExceptionWhenTextIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> qrCodeGenerateService.generateAndUploadQRCode(null)
        );

        assertTrue(exception.getMessage().contains("não pode ser vazio"));
        verify(storagePort, never()).uploadFile(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando texto for apenas espaços em branco")
    void shouldThrowExceptionWhenTextIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> qrCodeGenerateService.generateAndUploadQRCode("  ")
        );

        assertTrue(exception.getMessage().contains("não pode ser vazio"));
        verify(storagePort, never()).uploadFile(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando upload falhar")
    void shouldThrowExceptionWhenUploadFails() {
        String text = "https://example.com";

        when(storagePort.uploadFile(any(byte[].class), anyString(), eq("image/png")))
                .thenThrow(new RuntimeException("Erro no S3"));

        QRCodeUploadException exception = assertThrows(
                QRCodeUploadException.class,
                () -> qrCodeGenerateService.generateAndUploadQRCode(text)
        );

        assertTrue(exception.getMessage().contains("Falha ao fazer upload"));
    }


    @Test
    @DisplayName("Deve lançar exceção quando texto exceder limite do QR Code")
    void shouldThrowExceptionWhenTextExceedsLimit() {
        // QR Code tem limite de ~4296 caracteres em modo alfanumérico
        String text = "A".repeat(5000);

        QRCodeGenerationException exception = assertThrows(
                QRCodeGenerationException.class,
                () -> qrCodeGenerateService.generateAndUploadQRCode(text)
        );

        assertTrue(exception.getMessage().contains("Falha ao codificar o QR Code"));
    }
}