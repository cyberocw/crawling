package com.hyundai.crawling.util.impl;

import com.hyundai.crawling.util.Sorter;

/**
 * - 정렬 및 중복된 문자 제거된 문자열은 조건에 맞추어 교차 출력한다. 대문자 소문자 숫자 대문자 소문자 숫자
 * - 교차 진행시 영문 데이터가 없다면 남아있는 숫자로만 정렬되며, 반대도 마찬가지 AaBCDdefghIlmtv124 -> Aa1B2C4DdefghIilmtv
 */
public class ResponseSortType implements Sorter {
    private int numberIndex = 0;

    public String getSortedString(int[] uppers, int[] lowers, int[] numbers) {
        StringBuilder builder = new StringBuilder(uppers.length + lowers.length + numbers.length);
        int maxLen = Math.max(Math.max(uppers.length, lowers.length), numbers.length);

        int i = 0;

        while (i < maxLen) {
            appendChar(builder, i, uppers);
            appendChar(builder, i, lowers);
            forceAppendChar(builder, numbers);
            i++;
        }

        clear();

        return builder.toString();
    }

    private void forceAppendChar(StringBuilder builder, int[] chars) {
        if (chars.length <= numberIndex) {
            return;
        }

        int val = 0;

        while (chars.length > numberIndex && val <= 0) {
            val = chars[numberIndex++];
        }

        if (val != 0) {
            builder.append((char) val);
        }
    }


    private void clear() {
        numberIndex = 0;
    }

}
