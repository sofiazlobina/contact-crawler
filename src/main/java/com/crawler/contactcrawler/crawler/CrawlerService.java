package com.crawler.contactcrawler.crawler;

import com.crawler.contactcrawler.repository.ContactRepository;
import com.crawler.contactcrawler.service.HtmlParserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service
public class CrawlerService {

    private final ContactRepository repository;
    private final HtmlParserService parserService;
    private final CrawlLogger logger;

    // Кастомный ExecutorService (Критерий 3 и 4)
    private ExecutorService executorService;
    private BlockingQueue<String> urlQueue;
    private Semaphore semaphore;

    public CrawlerService(ContactRepository repository, HtmlParserService parserService, CrawlLogger logger) {
        this.repository = repository;
        this.parserService = parserService;
        this.logger = logger;
    }

    public void startCrawling(List<String> startUrls, int maxThreads) {
    urlQueue = new LinkedBlockingQueue<>(startUrls);
    semaphore = new Semaphore(maxThreads);
    
    executorService = Executors.newFixedThreadPool(maxThreads);
    
    System.out.println("=== КРАУЛЕР ЗАПУЩЕН ===");
    System.out.println("Количество потоков: " + maxThreads);
    System.out.println("URL для обработки: " + startUrls.size());
    logger.log("Запуск краулера с " + maxThreads + " потоками.");

    for (String url : startUrls) {
        System.out.println("Добавление задачи для URL: " + url);
        CrawlerTask task = new CrawlerTask(url, urlQueue, repository, parserService, logger, semaphore);
        executorService.submit(task);
    }

    executorService.shutdown();
    try {
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
    } catch (InterruptedException e) {
        executorService.shutdownNow();
    }
    System.out.println("=== КРАУЛЕР ЗАВЕРШЕН ===");
    logger.log("Краулер завершил работу.");
    }
}