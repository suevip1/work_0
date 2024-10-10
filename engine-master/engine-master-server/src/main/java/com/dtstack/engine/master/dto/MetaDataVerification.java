package com.dtstack.engine.master.dto;

import com.dtstack.dtcenter.loader.dto.Column;

import java.util.List;

public class MetaDataVerification {

    private boolean reader;

    private Long readerSourceId;

    private List<Column> readerColumnInfoList;

    private boolean writer;

    private Long writerSourceId;

    private List<Column> writerColumnInfoList;

    public boolean isReader() {
        return reader;
    }

    public void setReader(boolean reader) {
        this.reader = reader;
    }

    public Long getReaderSourceId() {
        return readerSourceId;
    }

    public void setReaderSourceId(Long readerSourceId) {
        this.readerSourceId = readerSourceId;
    }



    public List<Column> getReaderColumnInfoList() {
        return readerColumnInfoList;
    }

    public void setReaderColumnInfoList(List<Column> readerColumnInfoList) {
        this.readerColumnInfoList = readerColumnInfoList;
    }

    public boolean isWriter() {
        return writer;
    }

    public void setWriter(boolean writer) {
        this.writer = writer;
    }

    public Long getWriterSourceId() {
        return writerSourceId;
    }

    public void setWriterSourceId(Long writerSourceId) {
        this.writerSourceId = writerSourceId;
    }

    public List<Column> getWriterColumnInfoList() {
        return writerColumnInfoList;
    }

    public void setWriterColumnInfoList(List<Column> writerColumnInfoList) {
        this.writerColumnInfoList = writerColumnInfoList;
    }
}

