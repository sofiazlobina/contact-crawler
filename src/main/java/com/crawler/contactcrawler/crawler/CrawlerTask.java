package com.crawler.contactcrawler.crawler;

import com.crawler.contactcrawler.model.Contact;
import com.crawler.contactcrawler.repository.ContactRepository;
import com.crawler.contactcrawler.service.HtmlParserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

// Реализуем Runnable (Критерий 2)
public class CrawlerTask implements Runnable {
    
    private final String url;
    private final BlockingQueue<String> urlQueue;
    private final ContactRepository repository;
    private final HtmlParserService parserService;
    private final CrawlLogger logger;
    private final Semaphore semaphore; // Примитив синхронизации (Критерий 3)

    public CrawlerTask(String url, BlockingQueue<String> urlQueue, ContactRepository repository,
                       HtmlParserService parserService, CrawlLogger logger, Semaphore semaphore) {
        this.url = url;
        this.urlQueue = urlQueue;
        this.repository = repository;
        this.parserService = parserService;
        this.logger = logger;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            // Захватываем семафор, чтобы ограничить кол-во одновременных запросов (Критерий 3)
            semaphore.acquire();
            logger.log("Начало обработки URL: " + url + " | Поток: " + Thread.currentThread().getName());

            // Скачиваем HTML
            Document doc = Jsoup.connect(url).get();
            String html = doc.html();

            // Парсим данные
            String phone = parserService.extractPhone(html);
            String email = parserService.extractEmail(html);
            String companyName = parserService.extractCompanyName(html);

            // Сохраняем в H2 (Встроенное хранилище - Критерий 4)
            if (phone != null || email != null) {
                Contact contact = new Contact();
                contact.setSourceUrl(url);
                contact.setPhone(phone);
                contact.setEmail(email);
                contact.setCompanyName(companyName);
                repository.save(contact);
                logger.log("Успешно сохранен контакт для: " + url);
            }

            // Извлекаем новые ссылки и добавляем в очередь
            List<String> links = parserService.extractLinks(html, url);
            for (String link : links) {
                // Простая проверка, чтобы не дублировать задачи
                if (!urlQueue.contains(link)) {
                    urlQueue.put(link);
                }
            }

        } catch (Exception e) {
            logger.log("Ошибка при обработке " + url + ": " + e.getMessage());
        } finally {
            semaphore.release(); // Освобождаем семафор
        }
    }
}