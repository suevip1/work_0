package com.dtstack.engine.master.utils;

import com.dtstack.engine.api.pojo.GrammarCheckParam;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.catalog.Catalog;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JobClientUtilTest {

    @Test
    public void conversionJobClient() throws IOException {
        ParamAction paramAction = new ParamAction();
        paramAction.setTaskType(1);
        paramAction.setComputeType(1);
        JobClientUtil.conversionJobClient(paramAction);
    }

    @Test
    public void getParamAction() {
        JobClient jobClient = new JobClient();
        jobClient.setJobType(EJobType.SYNC);
        jobClient.setComputeType(ComputeType.STREAM);
        JobClientUtil.getParamAction(jobClient);
    }

    @Test
    public void convert2JobClient() {
        GrammarCheckParam grammarCheckParam = new GrammarCheckParam();
        grammarCheckParam.setTaskType(1);
        grammarCheckParam.setComputeType(1);
        JobClientUtil.convert2JobClient(grammarCheckParam);
    }

    @Test
    public void convertCatalogList() {
        List<Catalog> catalogList = new ArrayList<>();
        Catalog catalog = new Catalog();
        catalogList.add(catalog);
        JobClient jobClient = new JobClient();
        JobClientUtil.convertCatalogList(catalogList,jobClient);
    }

    @Test
    public void convertCatalog() {
    }
}