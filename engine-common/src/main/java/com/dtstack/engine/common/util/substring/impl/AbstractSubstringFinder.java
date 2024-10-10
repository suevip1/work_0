package com.dtstack.engine.common.util.substring.impl;

import com.dtstack.engine.common.util.substring.Substring;
import com.dtstack.engine.common.util.substring.SubstringFinder;

import java.util.Objects;

public abstract class AbstractSubstringFinder implements SubstringFinder {

    public Substring find(String input) {
        return this.find(input, 0, input.length());
    }

    public Substring find(String input, int start) {
        return this.find(input, start, input.length());
    }

    public Substring find(String input, int start, int end) {
        return this.find(input != null ? input.toCharArray() : null, start, end);
    }

    public Substring find(char[] input, int start, int end) {
        Objects.requireNonNull(input, "The input is null");
        if (start > end) {
            throw new StringIndexOutOfBoundsException("Start is great than end.");
        }
        if (start < 0 || end > input.length) {
            throw new StringIndexOutOfBoundsException("Start or end are invalid indices.");
        }
        return findInternal(input, start, end);
    }

    protected abstract Substring findInternal(char[] input, int start, int end);

}
