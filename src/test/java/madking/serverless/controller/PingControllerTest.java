package madking.serverless.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
class PingControllerTest {

    @Autowired
    private PingController pingController;

    @Test
    void processMimeMessage() throws Exception {


    }

}