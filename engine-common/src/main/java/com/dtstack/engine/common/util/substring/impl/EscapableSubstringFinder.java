package com.dtstack.engine.common.util.substring.impl;

import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.common.util.substring.Substring;
import com.dtstack.engine.common.util.substring.SubstringFinder;

import java.util.function.Function;

public class EscapableSubstringFinder extends AbstractSubstringFinder {

    private static final char DEFAULT_ESCAPE = '\\';
    private final char escape;
    private final SubstringFinder finder;

    public EscapableSubstringFinder(String pattern) {
        this(pattern, DEFAULT_ESCAPE);
    }

    public EscapableSubstringFinder(String pattern, char escape) {
        this(pattern, escape, SundaySubstringFinder::new);
    }

    public EscapableSubstringFinder(String pattern,
                                    Function<String, AbstractSimpleSubstringFinder> finderCreator) {
        this(pattern, DEFAULT_ESCAPE, finderCreator);
    }

    public EscapableSubstringFinder(String pattern, char escape,
                                    Function<String, AbstractSimpleSubstringFinder> finderCreator) {
        if (StringUtils.isEmpty(pattern)) {
            throw new IllegalArgumentException("Pattern is empty or null.");
        }
        if (pattern.indexOf(escape) != -1) {
            throw new IllegalArgumentException("Pattern is contain escape: " + escape);
        }
        if (maybeOverlap(pattern.toCharArray()))
            throw new IllegalArgumentException("This pattern may be ambiguity.");
        this.escape = escape;
        this.finder = finderCreator.apply(pattern);
    }

    private boolean maybeOverlap(char[] pattern) {
        int[] lens = new int[pattern.length + 1];
        for (int i = 2; i < pattern.length + 1; i++) {
            int len = lens[i - 1];
            while (len != 0) {
                if (pattern[i - 1] == pattern[len]) {
                    lens[i] = len + 1;
                    break;
                }
                len = lens[len];
            }
            if (len == 0) {
                lens[i] = pattern[i - 1] == pattern[len] ? 1 : 0;
            }
        }
        return lens[pattern.length] != 0;
    }

    @Override
    public Substring findInternal(char[] input, int start, int end) {
        int firstEscape = start;
        while (firstEscape != -1) {
            int firstUnescape = StringUtils.indexOfNot(this.escape, input, firstEscape, end);
            if (firstUnescape == -1) {
                return null;
            } else {
                int nextFirstEscape = StringUtils.indexOf(this.escape, input, firstUnescape, end);
                Substring substring = this.finder.find(input, firstUnescape, nextFirstEscape == -1 ? end : nextFirstEscape);
                if (substring == null) {
                    firstEscape = nextFirstEscape;
                } else if (substring.getStart() == firstUnescape && firstEscape != firstUnescape) {
                    return new EscapableSubString(input, firstEscape, firstUnescape, substring.getEnd());
                } else {
                    return substring;
                }
            }
        }
        return null;
    }

}
