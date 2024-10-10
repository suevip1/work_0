package com.dtstack.engine.common.util.substring;

public interface SubstringFinder {
    Substring find(String input);
    Substring find(String input, int start);
    Substring find(String input, int start, int end);
    Substring find(char[] input, int start, int end);
}
