package com.dtstack.engine.common.util.substring.impl;

import com.dtstack.engine.common.util.substring.PositionedParameters;
import com.dtstack.engine.common.util.substring.Substring;

import java.util.Objects;

public class ConsistentSubstring implements Substring {

    private final char[] source;
    private final int start;
    private final int end;

    ConsistentSubstring(String source) {
        this(source, 0, source.length());
    }

    ConsistentSubstring(String source, int start) {
        this(source, start, source.length());
    }

    ConsistentSubstring(String source, int start, int end) {
        this(source != null ? source.toCharArray() : null, start, end);
    }

    ConsistentSubstring(char[] source, int start, int end) {
        Objects.requireNonNull(source, "Source is null");
        if (start > end) {
            throw new StringIndexOutOfBoundsException("Start is great than end.");
        }
        if (start < 0 || end > source.length) {
            throw new StringIndexOutOfBoundsException("Start or end are invalid indices.");
        }
        this.source = source;
        this.start = start;
        this.end = end;
    }

    public String replace(PositionedParameters targets) {
        return targets.consume().toString();
    }

    public final String getSource() {
        return new String(this.source);
    }

    public final int getStart() {
        return start;
    }

    public final int getEnd() {
        return end;
    }

    public final boolean isEmpty() {
        return this.getEnd() == this.getStart();
    }

    public final int length() {
        return this.getEnd() - this.getStart();
    }

    @Override
    public final String toString() {
        return this.getSource().substring(this.getStart(), this.length());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsistentSubstring)) return false;
        ConsistentSubstring substring = (ConsistentSubstring) o;
        return this.getSource().equals(substring.getSource()) &&
                this.getStart() == substring.getStart() &&
                this.getEnd() == substring.getEnd();
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.getSource(), this.getStart(), this.getEnd());
    }

}
