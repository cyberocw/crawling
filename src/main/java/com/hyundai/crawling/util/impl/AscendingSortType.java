package com.hyundai.crawling.util.impl;

import com.hyundai.crawling.util.Sorter;

import java.util.Arrays;

/**
 * - 문자열을 오름차순으로 정렬한다. 대소문자 구분
 * - 중복된 문자는 제거하여 하나만 사용한다. html124divABCDefgtaBleImg1 -> AaBCDdefghIlmtv124 (숫자는 마지막에 붙음)
 */
public class AscendingSortType implements Sorter {
    @Override
    public String getSortedString(int[] uppers, int[] lowers, int[] numbers) {
        int i = 0;

        int maxLen = Math.max(Math.max(uppers.length, lowers.length), numbers.length);

        StringBuilder builder = new StringBuilder(uppers.length + lowers.length + numbers.length);

        while (i < maxLen) {
            appendChar(builder, i, uppers);
            appendChar(builder, i, lowers);
            i++;
        }
        Arrays.stream(numbers).filter(val -> val > 0).collect(() -> builder, StringBuilder::appendCodePoint, StringBuilder::append);

        return builder.toString();
    }

}
