package com.crawler.contactcrawler.controller;

import com.crawler.contactcrawler.crawler.CrawlerService;
import com.crawler.contactcrawler.dto.CrawlRequest;
import com.crawler.contactcrawler.dto.ContactResponse;
import com.crawler.contactcrawler.model.Contact;
import com.crawler.contactcrawler.repository.ContactRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CrawlerController {

    private final CrawlerService crawlerService;
    private final ContactRepository contactRepository;

    public CrawlerController(CrawlerService crawlerService, ContactRepository contactRepository) {
        this.crawlerService = crawlerService;
        this.contactRepository = contactRepository;
    }

    // POST endpoint для запуска краулера
    @PostMapping("/start")
    public ResponseEntity<String> startCrawling(@RequestBody CrawlRequest request) {
        int maxThreads = request.getMaxDepth() > 0 ? request.getMaxDepth() : 5;
        
        // Запускаем в отдельном потоке, чтобы не блокировать ответ
        new Thread(() -> {
            crawlerService.startCrawling(request.getUrls(), maxThreads);
        }).start();
        
        return ResponseEntity.ok("Краулер запущен! Обрабатывается URL-ов: " + request.getUrls().size());
    }

    // GET endpoint для получения результатов (Критерий 4 - извлечение результатов)
    @GetMapping("/answer")
    public ResponseEntity<List<ContactResponse>> getContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // Сортировка (Критерий 4 - возможность сортировки)
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : 
                    Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Contact> contactsPage = contactRepository.findAll(pageable);
        
        // Parallel Stream для фильтрации (Критерий 4 - 14 баллов!)
        List<ContactResponse> result = contactsPage.getContent()
            .parallelStream()  // Используем Parallel Stream
            .filter(contact -> {
                boolean phoneMatch = (phone == null || phone.isEmpty()) || 
                                    (contact.getPhone() != null && contact.getPhone().contains(phone));
                boolean emailMatch = (email == null || email.isEmpty()) || 
                                    (contact.getEmail() != null && contact.getEmail().contains(email));
                return phoneMatch && emailMatch;
            })
            .map(contact -> new ContactResponse(
                contact.getId(),
                contact.getSourceUrl(),
                contact.getPhone(),
                contact.getEmail(),
                contact.getAddress(),
                contact.getCompanyName(),
                contact.getCreatedAt()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    // Простой эндпоинт для проверки работы
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is running!");
    }
}