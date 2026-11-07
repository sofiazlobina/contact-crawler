package com.crawler.contactcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // ← ДОБАВИТЬ ЭТУ СТРОКУ

@SpringBootApplication
@EnableScheduling // ← И ЭТУ
public class ContactCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContactCrawlerApplication.class, args);
    }
}