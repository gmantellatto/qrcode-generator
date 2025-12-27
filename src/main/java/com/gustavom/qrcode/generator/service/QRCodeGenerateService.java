package com.gustavom.qrcode.generator.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.gustavom.qrcode.generator.dto.QRCodeGenerateResponse;
import com.gustavom.qrcode.generator.exception.QRCodeGenerationException;
import com.gustavom.qrcode.generator.exception.QRCodeUploadException;
import com.gustavom.qrcode.generator.ports.StoragePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QRCodeGenerateService {

    private final StoragePort storageService;

    public QRCodeGenerateResponse generateAndUploadQRCode(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("O texto não pode ser vazio");
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            String url = storageService.uploadFile(pngData, UUID.randomUUID().toString(), "image/png");

            return new QRCodeGenerateResponse(url);
        } catch (WriterException e) {
            throw new QRCodeGenerationException("Falha ao codificar o QR Code", text, e);
        } catch (IOException e) {
            throw new QRCodeGenerationException("Falha ao processar a imagem", text, e);
        } catch (Exception e) {
            throw new QRCodeUploadException("Falha ao fazer upload", e);
        }
    }


}
