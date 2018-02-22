package com.cts.reny.interview.scenario.one;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SpreadsheetColumnHeadersConverterTest {
    private SpreadsheetColumnHeadersConverter spreadsheetColumnHeadersConverter;

    @Before
    public void setup() {
        spreadsheetColumnHeadersConverter = new SpreadsheetColumnHeadersConverter();
    }

    @Test(expected = IllegalArgumentException.class)
    public void convert_negative_column_index_results_in_exception() {
        spreadsheetColumnHeadersConverter.convert(-13432);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convert_column_index_0_results_in_exception() {
        spreadsheetColumnHeadersConverter.convert(0);
    }

    @Test
    public void convert_column_index_1_results_in_A() {
        assertThat(spreadsheetColumnHeadersConverter.convert(1), is("A"));
    }

    @Test
    public void convert_column_index_26_results_in_Z() {
        assertThat(spreadsheetColumnHeadersConverter.convert(26), is("Z"));
    }

    @Test
    public void convert_column_index_27_results_in_AA() {
        assertThat(spreadsheetColumnHeadersConverter.convert(27), is("AA"));
    }

    @Test
    public void convert_column_index_52_results_in_AZ() {
        assertThat(spreadsheetColumnHeadersConverter.convert(52), is("AZ"));
    }

    @Test
    public void convert_column_index_702_results_in_ZZ() {
        assertThat(spreadsheetColumnHeadersConverter.convert(702), is("ZZ"));
    }

    @Test
    public void convert_column_index_703_results_in_AAA() {
        assertThat(spreadsheetColumnHeadersConverter.convert(703), is("AAA"));
    }

    @Test
    public void convert_column_index_18278_results_in_ZZZ() {
        assertThat(spreadsheetColumnHeadersConverter.convert(18278), is("ZZZ"));
    }
}