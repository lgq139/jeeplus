package com.jeeplus.modules.sys.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.sys.entity.IndustryStructure;
import com.jeeplus.modules.sys.excel.model.IndustryStructureImportModel;
import com.jeeplus.modules.sys.service.IndustryStructureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class IndustryStructureListenHandler extends AnalysisEventListener<IndustryStructureImportModel> {

    private List<IndustryStructure> list = new ArrayList<>();

    protected IndustryStructureService industryStructureService;

    public IndustryStructureListenHandler(IndustryStructureService industryStructureService) {
        this.industryStructureService = industryStructureService;
    }


    @Override
    public void invoke(IndustryStructureImportModel data, AnalysisContext context) {
        IndustryStructure industryStructure = new IndustryStructure();
        industryStructure.setId(data.getValue());
        industryStructure.setName(data.getLabel());
        if (StringUtils.isBlank(data.getParentId()) || "null".equals(data.getParentId())) {
            industryStructure.setParentId("0");
            if (data.getValue().startsWith("X")) {
                industryStructure.setType("A00003");
            } else if (data.getValue().startsWith("Y")) {
                industryStructure.setType("A00002");
            } else {
                industryStructure.setType("A00001");
            }
        } else  {
            industryStructure.setParentId(data.getParentId());
        }
        list.add(industryStructure);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有[{}]条数据解析完成！", (list != null ? list.size() : 0));
        // 执行数据批量插入
        ListUtils.partition(list, 200).forEach(subList -> {
            industryStructureService.saveBatch(subList);
        });
    }
}
