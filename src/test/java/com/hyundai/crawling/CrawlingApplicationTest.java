package com.hyundai.crawling;

import com.hyundai.crawling.service.CrawlerService;
import com.hyundai.crawling.util.Parser;
import com.hyundai.crawling.util.impl.AscendingSortType;
import com.hyundai.crawling.util.impl.ResponseSortType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

/*
    - 머지된 문자열중, 문자, 숫자만 추출
    - 문자열을 오름차순으로 정렬한다. 대소문자 구분
    - 중복된 문자는 제거하여 하나만 사용한다. html124divABCDefgtaBleImg1 -> AaBCDdefghIlmtv124
    - 정렬 및 중복된 문자 제거된 문자열은 조건에 맞추어 교차 출력한다. 대문자 소문자 숫자 대문자 소문자 숫자
    - 교차 진행시 영문 데이터가 없다면 남아있는 숫자로만 정렬되며, 반대도 마찬가지 AaBCDdefghIlmtv124 -> Aa1B2C4DdefghIilmtv
    - 결과가 JSON 형태로 Response되도록 한다. {"status":200, "merge":"Aa1B2C4DdefghIilmtv"}
    - 추가 고려 사항
        3개의 크롤링을 동시에
        크롤링하는 경우 timeout 운영 및 처리 방안
        머지된 문자열 오름 차순 및 파싱시 성능 고려
        Cache 적용 유무
 */

class CrawlingApplicationTest {
    private final String[] URLS = {"https://shop.hyundai.com", "https://www.kia.com", "https://www.genesis.com"};

    @Test
    void test_정규식으로추출() {
        Parser parser = new Parser();

        String stringAndNumber = parser.extractStringAndNumber("<div style=\"width:55px\">wefkfwe</div> <br />A는 52등 E는 55등 B는 3등");
        Assertions.assertTrue(!stringAndNumber.matches("[^0-9a-zA-Z]+"));
    }

    @Test
    void test_오름차순_영어대소_숫자순() {
        Parser parser = new Parser();

        parser.makeTable("html124divABCDefgtaBleImg1");
        String sortedValue = parser.getSortedString(new AscendingSortType());
        Assertions.assertEquals("AaBCDdefghIilmtv124", sortedValue);
    }

    @Test
    void test_오름차순_영어_대_소_숫자() {
        Parser parser = new Parser();

        parser.makeTable("html124divABCDefgtaBleImg1");
        String sortedValue = parser.getSortedString(new ResponseSortType());
        Assertions.assertEquals("Aa1B2C4DdefghIilmtv", sortedValue);
    }

    @Test
    void test_사이트크롤링() {
        CrawlerService crawlerService = new CrawlerService();

        String mergedHtmlString = crawlerService.getMergedHtmlString(URLS);
        System.out.println("mergedHTML ==========================");
        System.out.println(mergedHtmlString);
    }

    @Test
    void test_replaceAll로_문자열_추출() {
        long nTime = System.nanoTime();

        CrawlerService crawlerService = new CrawlerService();
        Parser parser = new Parser();

        String mergedHtmlString = crawlerService.getMergedHtmlString(URLS);
        String extractedStringAndNumber = parser.extractStringAndNumber(mergedHtmlString);
        parser.makeTable(extractedStringAndNumber);
        String sortedValue = parser.getSortedString(new ResponseSortType());
        System.out.println(sortedValue);

        long eTime = System.nanoTime();

        System.out.println((eTime-nTime) / 1_000_000 + "초/1000 걸림"); //780
    }

    @Test
    void test_ascii코드로_문자열_탐색하여_추출() throws ExecutionException, InterruptedException {
        long nTime = System.nanoTime();

        CrawlerService crawlerService = new CrawlerService();
        Parser parser = new Parser();

        int[] distinctStringTable = crawlerService.getDistinctStringTableByUrl(URLS);
        parser.makeTypeTable(distinctStringTable);
        String sortedValue = parser.getSortedString(new ResponseSortType());
        System.out.println(sortedValue);

        long eTime = System.nanoTime();

        System.out.println((eTime-nTime) / 1_000_000 + "초/1000 걸림"); //738 -> 더 빠름
    }
}