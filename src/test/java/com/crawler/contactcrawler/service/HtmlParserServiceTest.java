package com.crawler.contactcrawler.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HtmlParserServiceTest {

    private HtmlParserService parserService;

    @BeforeEach
    void setUp() {
        parserService = new HtmlParserService();
    }

    @Test
    void shouldExtractPhone() {
        String html = "<html><body>Call us: +79991234567</body></html>";
        String phone = parserService.extractPhone(html);
        assertNotNull(phone);
        assertTrue(phone.contains("79991234567"));
    }

    @Test
    void shouldExtractEmail() {
        String html = "<html><body>Contact: test@example.com</body></html>";
        String email = parserService.extractEmail(html);
        assertNotNull(email);
        assertEquals("test@example.com", email);
    }

    @Test
    void shouldExtractCompanyName() {
        String html = "<html><head><title>My Company</title></head><body></body></html>";
        String name = parserService.extractCompanyName(html);
        assertEquals("My Company", name);
    }
}