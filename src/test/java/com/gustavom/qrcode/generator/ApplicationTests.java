package com.gustavom.qrcode.generator;

import com.gustavom.qrcode.generator.ports.StoragePort;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ApplicationTests {

    @MockitoBean
    private StoragePort storagePort;

    @Test
    void contextLoads() {
    }

}