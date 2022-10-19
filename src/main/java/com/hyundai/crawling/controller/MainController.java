package com.hyundai.crawling.controller;

import com.hyundai.crawling.service.CrawlingWrapperService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class MainController {
    private final CrawlingWrapperService crawlingWrapperService;

    private static final String[] URLS = {"https://shop.hyundai.com", "https://www.kia.com", "https://www.genesis.com"};

    public MainController(CrawlingWrapperService crawlingWrapperService) {
        this.crawlingWrapperService = crawlingWrapperService;
    }

    @GetMapping("/fast")
    public Map<String, Object> fast() throws ExecutionException, InterruptedException {
        String result = crawlingWrapperService.getCrawlingResultByFastProcess(URLS);

        Map<String, Object> response = new HashMap();
        response.put("Status", 200);
        response.put("Merge", result);
        return response;
    }

    @GetMapping("/sequential")
    public Map<String, Object> sequential() throws ExecutionException, InterruptedException {
        String result = crawlingWrapperService.getCrawlingResultBySequentialProcess(URLS);

        Map<String, Object> response = new HashMap();
        response.put("Status", 200);
        response.put("Merge", result);
        return response;
    }
}
