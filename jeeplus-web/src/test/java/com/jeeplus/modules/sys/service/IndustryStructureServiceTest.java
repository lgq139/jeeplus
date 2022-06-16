package com.jeeplus.modules.sys.service;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.excel.EasyExcel;
import com.jeeplus.modules.sys.excel.listener.IndustryStructureListenHandler;
import com.jeeplus.modules.sys.excel.model.IndustryStructureImportModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;

@SpringBootTest
public class IndustryStructureServiceTest {

    @Autowired
    private IndustryStructureService industryStructureService;
    @Test
    public void importDataTest() {
        InputStream inputStream = ResourceUtil.getStream("产业结构调整指导目录.xls");
        EasyExcel.read(inputStream,
                IndustryStructureImportModel.class,
                new IndustryStructureListenHandler(industryStructureService))
                .sheet()
                .doRead();
    }

}
