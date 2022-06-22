package com.jeeplus.modules.convenience.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.jeeplus.common.utils.IdGen;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.convenience.entity.ConCataImport;
import com.jeeplus.modules.convenience.entity.ConCataImportFile;
import com.jeeplus.modules.convenience.service.ConCataImportFileService;
import com.jeeplus.modules.convenience.service.ConCataImportService;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ConCataImportListenHandler extends AnalysisEventListener<ConCataImport> {

    private List<ConCataImport> list = new ArrayList<>();

    protected ConCataImportService conCataImportService;

    @Autowired
    private ConCataImportFileService importFileService;

    public ConCataImportListenHandler(ConCataImportService cataImportService) {
        this.conCataImportService = cataImportService;
    }

    @Override
    public void invoke(ConCataImport data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有[{}]条数据解析完成！", (list != null ? list.size() : 0));
        String fileUuid = IdGen.uuid();
        // 执行数据批量插入
        ListUtils.partition(list, 200).forEach(subList -> {
            subList.forEach(conCataImport -> {
                conCataImport.setImportFileUuid(fileUuid);
                LambdaQueryChainWrapper<ConCataImport> query = conCataImportService.lambdaQuery();
                List<ConCataImport> res = query.eq(ConCataImport::getBaseCode,conCataImport.getBaseCode())
                        .eq(ConCataImport::getCataVersion,conCataImport.getCataVersion()).list();
                if (res.size() > 0) {
                    conCataImport.setCheckResult("错误:导入目录相同版本数据已存在");
                    conCataImport.setImportReport("无效数据导入,导入失败");
                }else {
                    conCataImport.setCheckResult("导入成功");
                    conCataImport.setImportReport("有效数据");
                }
                conCataImportService.save(conCataImport);
            });
        });
        ConCataImportFile conCataImportFile = new ConCataImportFile();
        conCataImportFile.setUuid(fileUuid);
        conCataImportFile.setImportCount(list != null ? list.size() : 0);
        User user = UserUtils.getUser();
        conCataImportFile.setImportUserName(user.getName());
        conCataImportFile.setImportOrgCode(user.getOrgCode());
        conCataImportFile.setImportOrgName(user.getOrgName());
        conCataImportFile.setImportStatus("1");
        conCataImportFile.setImportLevel(user.getRegionCode());
        importFileService.saveOrUpdate(conCataImportFile);
    }
}
