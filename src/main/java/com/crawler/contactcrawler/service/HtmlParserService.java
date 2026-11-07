package com.crawler.contactcrawler.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HtmlParserService {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "(?:\\+7|8)[\\s\\-]?\\(?\\d{3}\\)?[\\s\\-]?\\d{3}[\\s\\-]?\\d{2}[\\s\\-]?\\d{2}"
    );
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}"
    );
    
    public List<String> extractLinks(String html, String baseUrl) {
        List<String> links = new ArrayList<>();
        Document doc = Jsoup.parse(html, baseUrl);
        Elements elements = doc.select("a[href]");
        
        for (Element element : elements) {
            String link = element.absUrl("href");
            if (!link.isEmpty() && isSameDomain(link, baseUrl)) {
                links.add(link);
            }
        }
        return links;
    }
    
    public String extractPhone(String html) {
        Matcher matcher = PHONE_PATTERN.matcher(html);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
    
    public String extractEmail(String html) {
        Matcher matcher = EMAIL_PATTERN.matcher(html);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
    
    public String extractCompanyName(String html) {
        Document doc = Jsoup.parse(html);
        Element title = doc.selectFirst("title");
        if (title != null) {
            return title.text();
        }
        return null;
    }
    
    private boolean isSameDomain(String url1, String url2) {
        try {
            String domain1 = Jsoup.connect(url1).get().location();
            String domain2 = Jsoup.connect(url2).get().location();
            return domain1.equals(domain2);
        } catch (Exception e) {
            return false;
        }
    }
}