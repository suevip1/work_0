package com.dtstack.engine.api.pojo;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FlinkTableResult implements Serializable {

    private ResultKind resultKind;
    private ResultSchema schema;
    private List<Map<String, Object>> result;
    private String errorMsg;

    public FlinkTableResult() {
    }

    private FlinkTableResult(ResultKind resultKind, ResultSchema schema, List<Map<String, Object>> result, String errorMsg) {
        this.resultKind = resultKind;
        this.schema = schema;
        this.result = result;
        this.errorMsg = errorMsg;
    }

    public static FlinkTableResult success() {
        return new FlinkTableResult(ResultKind.SUCCESS, null, null, "");
    }

    public static FlinkTableResult successWithContext(List<Map<String, Object>> result, ResultSchema schema) throws IllegalAccessException {
        if (result == null || result.isEmpty() || schema == null) {
            throw new IllegalAccessException("When resultKind is SUCCESS_WITH_CONTEXT, result and schema cannot be empty!");
        }
        return new FlinkTableResult(ResultKind.SUCCESS_WITH_CONTEXT, schema, result, "");
    }

    public static FlinkTableResult failed(String errorMsg) {
        return new FlinkTableResult(ResultKind.FAILED, null, null, errorMsg);
    }

    public ResultKind getResultKind() {
        return resultKind;
    }

    public void setResultKind(ResultKind resultKind) {
        this.resultKind = resultKind;
    }

    public ResultSchema getSchema() {
        return schema;
    }

    public void setSchema(ResultSchema schema) {
        this.schema = schema;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public enum ResultKind {
        SUCCESS,
        SUCCESS_WITH_CONTEXT,
        FAILED
    }

    public static class ResultSchema {
        private List<String> columnNameList;
        private List<String> comment;
        private int rowCount;

        public ResultSchema() {
        }

        public ResultSchema(List<String> columnNameList, List<String> comment, int rowCount) {
            this.columnNameList = columnNameList;
            this.comment = comment;
            this.rowCount = rowCount;
        }

        public List<String> getColumnNameList() {
            return columnNameList;
        }

        public void setColumnNameList(List<String> columnNameList) {
            this.columnNameList = columnNameList;
        }

        public List<String> getComment() {
            return comment;
        }

        public void setComment(List<String> comment) {
            this.comment = comment;
        }

        public int getRowCount() {
            return rowCount;
        }

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }
    }

    public static List<FlinkTableResult> exception(String msg) {
        FlinkTableResult checkResult = new FlinkTableResult();
        checkResult.setResult(Lists.newArrayList());
        checkResult.setErrorMsg(msg);

        return Lists.newArrayList(checkResult);
    }
}




