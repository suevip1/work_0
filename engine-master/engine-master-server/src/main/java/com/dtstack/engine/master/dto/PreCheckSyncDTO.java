package com.dtstack.engine.master.dto;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-25 20:25
 */
public class PreCheckSyncDTO {
    /**
     * 数据来源
     */
    private Reader reader;

    /**
     * 数据同步目标
     */
    private Writer writer;

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PreCheckSyncDTO{");
        sb.append("reader=").append(reader);
        sb.append(", writer=").append(writer);
        sb.append('}');
        return sb.toString();
    }

    public static class Reader {
        /**
         * sql 文本
         */
        private String sqlText;

        public void setSqlText(String sqlText) {
            this.sqlText = sqlText;
        }

        public String getSqlText() {
            return this.sqlText;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Reader{");
            sb.append("sqlText='").append(sqlText).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static class Writer {
        /**
         * sql 文本
         */
        private String sqlText;

        public void setSqlText(String sqlText) {
            this.sqlText = sqlText;
        }

        public String getSqlText() {
            return this.sqlText;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Writer{");
            sb.append("sqlText='").append(sqlText).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}