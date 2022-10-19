package com.hyundai.crawling.util;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Parser {
    private int[] mTable;
    private int[] mUppers;
    private int[] mLowers;
    private int[] mNumbers;

    public String extractStringAndNumber(final String html) {
        return html.replaceAll("[^a-zA-Z0-9]+", "");
    }

    public static void makeTableDesc(int[] table, String body) {
        IntStream ascOrderLower = IntStream.rangeClosed('a', 'z');
        IntStream ascOrderUpper = IntStream.rangeClosed('A', 'Z');
        IntStream ascOrderNumber = IntStream.rangeClosed('0', '9');

        makeTableParallelByStream(ascOrderLower, table, body);
        makeTableParallelByStream(ascOrderUpper, table, body);
        makeTableParallelByStream(ascOrderNumber, table, body);
    }

    public static void makeTableAsc(int[] table, String body) {
        IntStream descOrderLower = IntStream.rangeClosed('a', 'z').map(i -> 'z' + 1 - i + 'a' - 1);
        IntStream descOrderUpper = IntStream.rangeClosed('A', 'Z').map(i -> 'Z' + 1 - i + 'A' - 1);
        IntStream descOrderNumber = IntStream.rangeClosed('0', '9').map(i -> '9' + 1 - i + '0' - 1);

        makeTableParallelByStream(descOrderLower, table, body);
        makeTableParallelByStream(descOrderUpper, table, body);
        makeTableParallelByStream(descOrderNumber, table, body);
    }

    public static void makeTableParallelByStream(IntStream stream, int[] table, String body) {
        stream.parallel().forEach(i -> {
            if (i > 'z' || table[i] > 0) {
                return;
            }

            if (body.indexOf((char) i) > -1) {
                table[i] = i;
            }
        });
    }

    public void clearTable() {
        mTable = new int['z' + 1];
        mUppers = null;
        mLowers = null;
        mNumbers = null;
    }

    public void makeTable(String str) {
        clearTable();
        makeRootTable(str);
        makeTypeTable(mTable);
    }


    private void makeRootTable(String str) {
        str.chars().parallel().forEach(code ->
            mTable[code] = code
        );
    }

    public void makeTypeTable(int[] charTable) {
        mUppers = Arrays.copyOfRange(charTable, 'A', 'Z' + 1);
        mLowers = Arrays.copyOfRange(charTable, 'a', 'z' + 1);
        mNumbers = Arrays.copyOfRange(charTable, '0', '9' + 1);
    }

    public String getSortedString(Sorter sorter) {
        return sorter.getSortedString(mUppers, mLowers, mNumbers);
    }
}
