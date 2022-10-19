package com.hyundai.crawling.service;

import com.hyundai.crawling.util.Parser;
import com.hyundai.crawling.util.Sorter;
import com.hyundai.crawling.util.impl.AscendingSortType;
import com.hyundai.crawling.util.impl.ResponseSortType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class CrawlingWrapperService {
    private final CrawlerService crawlerService;

    public CrawlingWrapperService(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    /**
     * 1.병렬로 크롤링하면서 동시에 영문/숫자 문자열 탐색하여 존재하는 ascii 코드 기반 array 생성
     * 2.출력용으로 정렬
     * @param urls
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Cacheable(value = "crawlingFastResult", key = "T(java.util.Arrays).asList(#urls).hashCode()")
    public String getCrawlingResultByFastProcess(String ...urls) throws ExecutionException, InterruptedException {
        Parser parser = new Parser();

        int[] distinctAsciiTable = crawlerService.getDistinctStringTableByUrl(urls);
        parser.makeTypeTable(distinctAsciiTable);
        return parser.getSortedString(new ResponseSortType());
    }

    /**
     * 1. 크롤링한 html 모두 합침
     * 2. 영문/숫자 중복 제거 및 정렬
     * 3. 정렬된 텍스트 기반으로 다시 출력용으로 교차 정렬
     * @param urls
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Cacheable(value = "crawlingSequentialProcessResult", key = "T(java.util.Arrays).asList(#urls).hashCode()")
    public String getCrawlingResultBySequentialProcess(String ...urls) throws ExecutionException, InterruptedException {
        Parser parser = new Parser();

        String mergedString = crawlerService.getMergedHtmlString(urls);
        String sortedText1 = makeText(crawlerService, parser, mergedString, new AscendingSortType());
        return makeText(crawlerService, parser, sortedText1, new ResponseSortType());
    }

    private String makeText(CrawlerService crawlerService, Parser parser, String mergedString, Sorter sorter) throws ExecutionException, InterruptedException {
        int count = mergedString.length() / 3 + 1;
        int[] distinctAsciiTable = crawlerService.getDistinctStringTableByText(mergedString.substring(0, count), mergedString.substring(count, count * 2), mergedString.substring(count * 2));
        parser.makeTypeTable(distinctAsciiTable);
        return parser.getSortedString(sorter);
    }
}
