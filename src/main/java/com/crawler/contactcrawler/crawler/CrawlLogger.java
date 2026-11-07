package com.crawler.contactcrawler.crawler;

import org.springframework.stereotype.Component;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class CrawlLogger {
    
    // Блокирующая структура для логов
    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private final Thread daemonThread;

    public CrawlLogger() {
        // Создаем демон-поток
        daemonThread = new Thread(this::processLogs);
        daemonThread.setDaemon(true); // Делаем поток демоном
        daemonThread.setName("Crawler-Daemon-Logger");
        daemonThread.start();
    }

    public void log(String message) {
        try {
            logQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processLogs() {
        // Запись в собственный файл
        try (PrintWriter writer = new PrintWriter(new FileWriter("crawler_logs.txt", true))) {
            while (true) {
                try {
                    String log = logQueue.take(); // Блокирующее ожидание
                    writer.println(log);
                    writer.flush();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}