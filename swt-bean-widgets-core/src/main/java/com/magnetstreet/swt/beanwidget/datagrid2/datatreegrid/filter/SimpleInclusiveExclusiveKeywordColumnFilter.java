package com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SimpleInclusiveExclusiveKeywordColumnFilter
 *
 * A partially designed filter to allow plus (+) and minus (-) operators to include and exclude
 * keywords respectively. This filter is intended for use on grids with long form text boxes that
 * contain many words as it uses word spacing to breakup keywords.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 9/21/11
 */
public abstract class SimpleInclusiveExclusiveKeywordColumnFilter extends AbstractColumnFilter<String> {
    private String keywordSeparator = " ";
    private String compoundKeywordPrefix = "\"";
    private String compoundKeywordSuffix = "\"";
    private String[] inclusionOperators = new String[]{"+", ""};
    private String[] exclusionOperators = new String[]{"-"};
    private boolean caseSensitive = false;

    public SimpleInclusiveExclusiveKeywordColumnFilter() { }
    public SimpleInclusiveExclusiveKeywordColumnFilter(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    /**
     * Allows on creation overriding the default configuration of the filter.
     * @param customKeywordSeparator The set of characters to use to break up the text body into keywords (Default: ' ')
     * @param customInclusionOperators The a set of collections of characters to match as an inclusion prefix operators to a keyword (Default: '+' and '')
     * @param customExclusionOperators
     */
    public SimpleInclusiveExclusiveKeywordColumnFilter(String customKeywordSeparator, String[] customInclusionOperators, String[] customExclusionOperators) {
        this.keywordSeparator = keywordSeparator;
        this.inclusionOperators = customInclusionOperators;
        this.exclusionOperators = customExclusionOperators;
    }
    protected SimpleInclusiveExclusiveKeywordColumnFilter(String keywordSeparator, String compoundKeywordPrefix, String compoundKeywordSuffix, String[] inclusionOperators, String[] exclusionOperators) {
        this.keywordSeparator = keywordSeparator;
        this.compoundKeywordPrefix = compoundKeywordPrefix;
        this.compoundKeywordSuffix = compoundKeywordSuffix;
        this.inclusionOperators = inclusionOperators;
        this.exclusionOperators = exclusionOperators;
    }
    protected SimpleInclusiveExclusiveKeywordColumnFilter(String keywordSeparator, String compoundKeywordPrefix, String compoundKeywordSuffix, String[] inclusionOperators, String[] exclusionOperators, boolean caseSensitive) {
        this.keywordSeparator = keywordSeparator;
        this.compoundKeywordPrefix = compoundKeywordPrefix;
        this.compoundKeywordSuffix = compoundKeywordSuffix;
        this.inclusionOperators = inclusionOperators;
        this.exclusionOperators = exclusionOperators;
        this.caseSensitive = caseSensitive;
    }

    /**
     * Intended to be implemented to return the value of a text box filter from which a user enters filters like
     * 'return myTextBoxFilter.getText();'
     * @return
     */
    protected abstract String getFilterText();

    @Override public boolean checkModelProperty(String modelObjectProperty) {
        List<String> allKeywordsWithOperators = getKeywords(getFilterText());
        Set<String> exclusionKeywords = getExclusionKeywords(allKeywordsWithOperators);
        Set<String> inclusionKeywords = getInclusiveKeywords(allKeywordsWithOperators);

        for(String exclusion: exclusionKeywords) {
            if(contains(modelObjectProperty, exclusion))
                return false;
        }

        for(String inclusion: inclusionKeywords) {
            if(!contains(modelObjectProperty, inclusion))
                return false;
        }

        return true;
    }

    protected boolean contains(String a, String b) {
        if(caseSensitive)
            return a.contains(b);
        else
            return a.toLowerCase().contains(b.toLowerCase());
    }

    protected List<String> getKeywords(String text) {
        List<String> keywords = new ArrayList<String>();

        boolean compoundKeyword = false;
        StringBuilder buff = new StringBuilder();
        for(int i=0; i<text.length(); i++) {
            if(!compoundKeyword) {
                if(text.indexOf(compoundKeywordPrefix, i)==i) { // Entering a Compound Keyword
                    compoundKeyword = true;
                } else if(text.indexOf(keywordSeparator, i)==i) { // Keyword separator
                    if(buff.length()>0) {
                        keywords.add(buff.toString());
                        buff = new StringBuilder();
                    }
                } else { // Normal character
                    buff.append(text.charAt(i));
                }
            } else { // In middle of compound keyword
                if(text.indexOf(compoundKeywordSuffix, i)!=i)
                    buff.append(text.charAt(i));
                else { // End of compound keyword
                    keywords.add(buff.toString());
                    buff = new StringBuilder();
                    compoundKeyword = false;
                }
            }
        }

        if(buff.length()!=0)
            keywords.add(buff.toString());

        return keywords;
    }

    protected Set<String> getExclusionKeywords(List<String> keywords) {
        Set<String> exclusionKeywords = new HashSet<String>();
        for(String keyword: keywords) {
            String matchedOp = matchOperator(keyword, exclusionOperators);
            if(matchedOp!=null) {
                exclusionKeywords.add(keyword.substring(matchedOp.length()));
            }
        }

        return exclusionKeywords;
    }

    protected Set<String> getInclusiveKeywords(List<String> keywords) {
        Set<String> inclusiveKeywords = new HashSet<String> ();
        for(String keyword: keywords) {
            for(String op: inclusionOperators) {
                if("".equals(op)) { // The Any Prefix except Exclusion
                    if(matchOperator(keyword, exclusionOperators)==null) {
                        inclusiveKeywords.add(keyword);
                        break;
                    }
                } else {
                    String matchedOp = matchOperator(keyword, inclusionOperators);
                    if(matchedOp!=null) {
                        inclusiveKeywords.add(keyword.substring(matchedOp.length()));
                        break;
                    }
                }
            }
        }

        return inclusiveKeywords;
    }

    /**
     * @param keyword Word to match
     * @param operators Array of operators to try
     * @return The matched operator
     */
    protected String matchOperator(String keyword, String[] operators) {
        for(String op : operators) {
            if(!"".equals(op)) {
                if(keyword.startsWith(op)) {
                    return op;
                }
            }
        }
        return null;
    }
}
