package com.dtstack.engine.common.util.substring.impl;

import com.dtstack.engine.common.util.substring.PositionedParameters;

final class EscapableSubString extends ConsistentSubstring {

    private final int firstUnescape;

    EscapableSubString(String source) {
        this(source, 0);
    }

    EscapableSubString(String source, int start) {
        this(source, start, source.length());
    }

    EscapableSubString(String source, int start, int end) {
        this(source, start, start, end);
    }

    EscapableSubString(char[] source, int start, int end) {
        this(source, start, start, end);
    }

    EscapableSubString(char[] source, int start, int firstUnescape, int end) {
        super(source, start, end);
        if (start > firstUnescape) {
            throw new StringIndexOutOfBoundsException("FirstUnescape is less than start.");
        }
        this.firstUnescape = firstUnescape;
    }

    EscapableSubString(String source, int start, int firstUnescape, int end) {
        this(source != null ? source.toCharArray() : null, start, firstUnescape, end);
    }

    @Override
    public String replace(PositionedParameters targets) {
        StringBuilder res = new StringBuilder();
        String source = this.getSource();
        if (lenEscape() % 2 == 1) {
            res.append(source, getStart() + 1, this.getEnd());
        } else {
            res.append(source, getStart(), getStart() + lenEscape());
            res.append(targets.consume());
        }
        return res.toString();
    }

    private int lenEscape() {
        return this.firstUnescape - this.getStart();
    }

}
