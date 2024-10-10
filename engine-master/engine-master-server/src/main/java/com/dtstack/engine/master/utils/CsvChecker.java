package com.dtstack.engine.master.utils;

import java.util.List;

public interface CsvChecker {
    void checkData(String[] data, List<List<String>> result);
}
