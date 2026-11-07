package com.crawler.contactcrawler.scheduler;

import com.crawler.contactcrawler.repository.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CrawlerScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerScheduler.class);

    @Autowired
    private ContactRepository contactRepository;

    // Запускается каждые 60 секунд (60000 миллисекунд)
    @Scheduled(fixedRate = 60000)
    public void logContactCount() {
        long count = contactRepository.count();
        logger.info("=== SCHEDULER: Всего контактов в базе: {} ===", count);
    }

    // Запускается раз в 5 минут (300000 миллисекунд)
    @Scheduled(fixedDelay = 300000)
    public void periodicCleanup() {
        logger.info("=== SCHEDULER: Периодическая проверка системы ===");
    }
}