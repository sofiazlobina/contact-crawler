package com.crawler.contactcrawler.dto;

import java.util.List;

public class CrawlRequest {
    private List<String> urls;
    private int maxDepth;

    public List<String> getUrls() { return urls; }
    public void setUrls(List<String> urls) { this.urls = urls; }
    public int getMaxDepth() { return maxDepth; }
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }
}