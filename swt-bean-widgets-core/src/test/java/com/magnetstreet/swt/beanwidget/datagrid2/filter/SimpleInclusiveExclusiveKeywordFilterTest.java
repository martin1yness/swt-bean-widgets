package com.magnetstreet.swt.beanwidget.datagrid2.filter;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;

/**
 * SimpleInclusiveExclusiveKeywordFilterTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 9/21/11
 */
public class SimpleInclusiveExclusiveKeywordFilterTest {
    private SimpleInclusiveExclusiveKeywordColumnFilter filter;
    @Before
    public void setUpFilter() {
        filter = new SimpleInclusiveExclusiveKeywordColumnFilter() {
            @Override protected String getFilterText() {
                return "+genericKeyword additionalKeyword -excludedKeyword \"Compound Keyword\" +\"Inclusive Compound Keyword\"";
            }
        };
    }

    @Test public void givenAComplexFilter_validMatchesReturnTrueInvalidMatchesReturnFalse() {
        assertThat(filter.checkModelProperty("Some text with a genericKeyword additionalKeyword Compound Keyword and Inclusive Compound Keyword but nothing else."), is(true));

        assertThat(filter.checkModelProperty("Some text with a Compound Keyword but nothing else."), is(false));
        assertThat(filter.checkModelProperty("Some text with an Inclusive Compound Keyword"), is(false));
        assertThat(filter.checkModelProperty("Some text with a Compound Keyw0rd that doesn't match"), is(false));
        assertThat(filter.checkModelProperty("Some text with a Compound Keyword but also an excludedKeyword"), is(false));
        assertThat(filter.checkModelProperty("Some text with a genericKeyword but also an excludedKeyword"), is(false));
        assertThat(filter.checkModelProperty("Some text with an Inclusive Compound Keyword but also an excludedKeyword"), is(false));
        assertThat(filter.checkModelProperty("Some text with a genericKeyword additionalKeyword Compound Keyword and Inclusive Compound Keyword but also an excludedKeyword."), is(false));
    }

    @Test
    public void givenSimpleInclusive_keywordsBrokenIntoSingleItemList() {
        List<String> keywords = filter.getKeywords("genericKeyword");
        assertThat(keywords.size(), is(1));
        assertThat(keywords.get(0), is("genericKeyword"));
    }

    @Test
    public void givenComplexInclusiveExclusiveAndCompound_keywordsBrokenIntoList() {
        List<String> keywords = filter.getKeywords("+genericKeyword additionalKeyword -excludedKeyword \"Compound Keyword\" +\"Inclusive Compound Keyword\"");
        assertThat(keywords.size(), is(5));
        assertThat(keywords.get(0), is("+genericKeyword"));
        assertThat(keywords.get(1), is("additionalKeyword"));
        assertThat(keywords.get(2), is("-excludedKeyword"));
        assertThat(keywords.get(3), is("Compound Keyword"));
        assertThat(keywords.get(4), is("+Inclusive Compound Keyword"));
    }

    @Test
    public void givenNonEmptyOperatorArraySizeOne_matchesKeywordWithOperatorAsPrefix() {
        assertThat(filter.matchOperator("+Some Keyword", new String[]{"+"}), is("+"));
        assertThat(filter.matchOperator("-Some Keyword", new String[]{"+"}), is(nullValue()));
        assertThat(filter.matchOperator("abcSome Keyword", new String[]{"abc"}), is("abc"));
        assertThat(filter.matchOperator("acSome Keyword", new String[]{"abc"}), is(nullValue()));
    }

    @Test
    public void givenNonEmptyOperatorArraySizeThree_matchesKeywordWithOperatorAsPrefix() {
        assertThat(filter.matchOperator("+Some Keyword", new String[]{"+", "abc", "--"}), is("+"));
        assertThat(filter.matchOperator("abcSome Keyword", new String[]{"+", "abc", "--"}), is("abc"));
        assertThat(filter.matchOperator("--Some Keyword", new String[]{"+", "abc", "--"}), is("--"));

        assertThat(filter.matchOperator("-+Some Keyword", new String[]{"+", "abc", "--"}), is(nullValue()));
        assertThat(filter.matchOperator("abSome Keyword", new String[]{"+", "abc", "--"}), is(nullValue()));
        assertThat(filter.matchOperator("-Some Keyword", new String[]{"+", "abc", "--"}), is(nullValue()));
    }

    @Test
    public void givenASetOfKeywordsWithInclusions_inclusionsReturned() {
        List<String> keywords = new ArrayList<String>();
        keywords.add("+inclusionA");
        keywords.add("inclusionB");
        keywords.add("-exclusion");
        Set<String> inclusiveKeywords = filter.getInclusiveKeywords(keywords);
        assertThat(inclusiveKeywords.size(), is(2));
        assertThat(inclusiveKeywords, hasItem("inclusionA"));
        assertThat(inclusiveKeywords, hasItem("inclusionB"));
    }

    @Test
    public void givenASetOfKeywordsWithExclusions_ExclusionsReturned() {
        List<String> keywords = new ArrayList<String>();
        keywords.add("+inclusionA");
        keywords.add("inclusionB");
        keywords.add("-exclusion");
        Set<String> exclusionKeywords = filter.getExclusionKeywords(keywords);
        assertThat(exclusionKeywords.size(), is(1));
        assertThat(exclusionKeywords, hasItem("exclusion"));
    }
}
