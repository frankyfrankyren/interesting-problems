package com.cts.reny.interview.scenario.one;

public class SpreadsheetColumnHeadersConverter {

    public static final int BASE = 26;

    public String convert(int columnNumber) throws IllegalArgumentException {
        if (columnNumber <= 0) {
            throw new IllegalArgumentException("Unable to convert non-positive column " + columnNumber + " to characters format");
        }

        final StringBuilder columnNameBuilder = new StringBuilder();

        while (columnNumber > 0) {
            final int position = columnNumber % BASE;
            columnNameBuilder.append(toChar(position));
            columnNumber = (columnNumber - 1) / BASE;
        }

        return columnNameBuilder.reverse().toString();
    }

    private char toChar(int position) {
        if (position == 0) {
            return 'Z';
        }

        return (char) (((int) 'A') + (position - 1));
    }
}
