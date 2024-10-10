package com.dtstack.engine.common.client.bean;
import java.util.List;
import java.util.Map;

public class FlinkTableResultDTO {

    private ResultKind resultKind;
    private ResultSchema schema;
    private List<Map<String, Object>> result;
    private String errorMsg;

    public FlinkTableResultDTO() {
    }

    private FlinkTableResultDTO(ResultKind resultKind, ResultSchema schema, List<Map<String, Object>> result, String errorMsg) {
        this.resultKind = resultKind;
        this.schema = schema;
        this.result = result;
        this.errorMsg = errorMsg;
    }

    public static FlinkTableResultDTO success() {
        return new FlinkTableResultDTO(ResultKind.SUCCESS, null, null, "");
    }

    public static FlinkTableResultDTO successWithContext(List<Map<String, Object>> result, ResultSchema schema) throws IllegalAccessException {
        if (result == null || result.isEmpty() || schema == null) {
            throw new IllegalAccessException("When resultKind is SUCCESS_WITH_CONTEXT, result and schema cannot be empty!");
        }
        return new FlinkTableResultDTO(ResultKind.SUCCESS_WITH_CONTEXT, schema, result, "");
    }

    public static FlinkTableResultDTO failed(String errorMsg) {
        return new FlinkTableResultDTO(ResultKind.FAILED, null, null, errorMsg);
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
}




