package com.dtstack.engine.common.util.substring.impl;

import com.dtstack.engine.common.util.StringUtils;

public class SundaySubstringFinder extends AbstractSimpleSubstringFinder {

    private static class Pattern {

        private final char[] pattern;

        private Pattern(String pattern) {
            if (StringUtils.isEmpty(pattern)) {
                throw new IllegalArgumentException("Pattern is empty or null.");
            }
            this.pattern = pattern.toCharArray();
        }

        private boolean match(char[] input, int start) {
            for (int j = 0; j < this.length(); j++) {
                if (this.pattern[j] != input[start + j]) {
                    return false;
                }
            }
            return true;
        }

        private int distanceToBeMove(char c) {
            int lastIdx = this.length() - 1;
            for (int i = lastIdx; i >= 0; i--) {
                if (this.pattern[i] == c) {
                    return this.length() - i;
                }
            }
            return this.length();
        }

        private int length() {
            return this.pattern.length;
        }

        @Override
        public String toString() {
            return String.valueOf(this.pattern);
        }

    }

    private final Pattern pattern;

    public SundaySubstringFinder(String pattern) {
        this.pattern = new Pattern(pattern);
    }

    @Override
    protected int match(char[] input, int start, int end) {
        int prospectiveMatchStart = start;
        int prospectiveMatchEnd = prospectiveMatchStart + this.pattern.length();

        while (prospectiveMatchEnd <= end) {

            if (this.pattern.match(input, prospectiveMatchStart)) {
                return prospectiveMatchStart;
            }

            if (prospectiveMatchEnd == end) {
                return -1;
            }

            int distanceToBeMove = this.pattern.distanceToBeMove(input[prospectiveMatchEnd]);

            prospectiveMatchStart += distanceToBeMove;
            prospectiveMatchEnd += distanceToBeMove;
        }

        return -1;
    }

    @Override
    protected int length() {
        return this.pattern.length();
    }


}
