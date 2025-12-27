package com.gustavom.qrcode.generator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavom.qrcode.generator.dto.QRCodeGenerateRequest;
import com.gustavom.qrcode.generator.exception.GlobalExceptionHandler;
import com.gustavom.qrcode.generator.service.QRCodeGenerateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QRCodeController.class)
@Import(GlobalExceptionHandler.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QRCodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QRCodeGenerateService qrCodeGenerateService;

    @Test
    @DisplayName("Deve retornar 200 quando requisição for válida")
    void shouldReturn200WhenRequestIsValid() throws Exception {
        QRCodeGenerateRequest request = new QRCodeGenerateRequest("https://example.com");
        when(qrCodeGenerateService.generateAndUploadQRCode(anyString()))
                .thenReturn(new com.gustavom.qrcode.generator.dto.QRCodeGenerateResponse("https://storage.example.com/qrcode.png"));

        mockMvc.perform(post("/qrcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", notNullValue()));
    }

    @Test
    @DisplayName("Deve retornar 400 quando texto for vazio")
    void shouldReturn400WhenTextIsEmpty() throws Exception {
        QRCodeGenerateRequest request = new QRCodeGenerateRequest("");
        when(qrCodeGenerateService.generateAndUploadQRCode(anyString()))
                .thenThrow(new IllegalArgumentException("O texto não pode ser vazio"));

        mockMvc.perform(post("/qrcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("texto")))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("Deve retornar 500 quando upload falhar")
    void shouldReturn500WhenUploadFails() throws Exception {
        QRCodeGenerateRequest request = new QRCodeGenerateRequest("texto teste");
        when(qrCodeGenerateService.generateAndUploadQRCode(anyString()))
                .thenThrow(new com.gustavom.qrcode.generator.exception.QRCodeUploadException("Falha", null));

        mockMvc.perform(post("/qrcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Erro ao fazer upload do QR Code")))
                .andExpect(jsonPath("$.status", is(500)));
    }
}
