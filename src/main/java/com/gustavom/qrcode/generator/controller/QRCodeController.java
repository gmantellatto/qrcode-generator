package com.gustavom.qrcode.generator.controller;

import com.gustavom.qrcode.generator.dto.QRCodeGenerateRequest;
import com.gustavom.qrcode.generator.dto.QRCodeGenerateResponse;
import com.gustavom.qrcode.generator.service.QRCodeGenerateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qrcode")
@AllArgsConstructor
public class QRCodeController {

    private final QRCodeGenerateService qrCodeGenerateService;

    @PostMapping
    public ResponseEntity<QRCodeGenerateResponse> generateQRCode(@RequestBody QRCodeGenerateRequest request) {
        try {
            QRCodeGenerateResponse response = this.qrCodeGenerateService.generateAndUploadQRCode(request.text());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
