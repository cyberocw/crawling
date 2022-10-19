package com.hyundai.crawling.service;

import com.hyundai.crawling.util.Parser;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CrawlerService {
    private final RestTemplate restTemplate;

    public CrawlerService() {
        restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .additionalInterceptors(clientHttpRequestInterceptor())
                .build();
    }

    public ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
        return (request, body, execution) -> {
            RetryTemplate retryTemplate = new RetryTemplate();
            retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));
            try {
                return retryTemplate.execute(context -> execution.execute(request, body));
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        };
    }

    public int[] getDistinctStringTableByUrl(String ...urls) throws ExecutionException, InterruptedException {
        int[] table = new int['z' + 1];
        int[] i = {0};

        CompletableFuture<String>[] futures = Arrays.stream(urls).map(url ->
                CompletableFuture.runAsync(() -> {
                    String body = getBody(url);
                    if (i[0]++ % 2 == 1) {
                        Parser.makeTableDesc(table, body);
                    } else {
                        Parser.makeTableAsc(table, body);
                    }
                })
        ).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).get();
        return table;
    }

    public int[] getDistinctStringTableByText(String ...texts) throws ExecutionException, InterruptedException {
        int[] table = new int['z' + 1];

        int[] i = {0};

        CompletableFuture<String>[] futures = Arrays.stream(texts).map(text ->
                CompletableFuture.runAsync(() -> {
                    if (i[0]++ % 2 == 1) {
                        Parser.makeTableDesc(table, text);
                    } else {
                        Parser.makeTableAsc(table, text);
                    }
                })
        ).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).get();
        return table;
    }

    public String getMergedHtmlString(String ...urls) {
        CompletableFuture<String>[] futures = Arrays.stream(urls).map(url ->
                CompletableFuture.supplyAsync(() ->
                     getBody(url)
                )
        ).toArray(CompletableFuture[]::new);

        return Stream.of(futures)
                .map(CompletableFuture::join)
                .collect(Collectors.joining());
    }

    private String getBody(String url) {
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);

        if (entity.getStatusCode() != HttpStatus.OK) {
            throw new HttpServerErrorException(entity.getStatusCode());
        }

        return entity.getBody();
    }

}
