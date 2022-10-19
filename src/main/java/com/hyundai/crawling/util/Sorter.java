package com.hyundai.crawling.util;

public interface Sorter {
    String getSortedString(int[] uppers, int[] lowers, int[] numbers);

    default void appendChar(StringBuilder builder, int index, int[] chars) {
        char val = popChar(index, chars);

        if (val > 0) {
            builder.append(val);
        }
    }

    default char popChar(int index, int[] chars) {
        if (chars.length > index && chars[index] > 0) {
            return (char) chars[index];
        } else {
            return 0;
        }
    }
}
