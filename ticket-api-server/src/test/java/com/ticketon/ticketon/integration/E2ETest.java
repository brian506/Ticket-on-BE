package com.ticketon.ticketon.integration;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;



@IntegrationTest
@ActiveProfiles("test")
public class E2ETest {

    static MySQLContainer container = new MySQLContainer("mysql:8.0.44-debian")
            .withDatabaseName("ticket_on")
            .withUsername("test")
            .withPassword("test");


    static {
        container.start();
    }

    // 별도 경로 설정없이 컨테이너 내에서 자바 코드로 DB 연결
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }


}
