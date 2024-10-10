package com.dtstack.engine.common.util.substring;

/**
 * This substring is subsequence of the source from start to end. Where
 * 1. Start and end are indices of the source.
 * 2. Start is not greater than end.
 *
 * The length of this substring is the difference between end and start. Empty if length is zero.
 * Substring sub1 is equals to sub2 if sub1 and sub2 is from the same source and the start and
 * the end of sub1 are the same with them of b correspondingly.
 *
 * You can replace this substring by calling replace method. The final result is defined by
 * concrete implements using the targets specified.
 *
 * Warn: Please don't implement Substring directly. Because the new implements may be incompatible with ours.
 * If you want to implement substring for yourself, the best way is to extend ConsistentSubstring.
 */
public interface Substring {
    String replace(PositionedParameters targets);
    String getSource();
    int getStart();
    int getEnd();
    boolean isEmpty();
    int length();
}
