package com.jeeplus.modules.convenience.controller;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.IdGen;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.convenience.entity.ConCataImport;
import com.jeeplus.modules.convenience.entity.ConCataImportFile;
import com.jeeplus.modules.convenience.excel.ConCataImportListenHandler;
import com.jeeplus.modules.convenience.service.ConCataImportFileService;
import com.jeeplus.modules.convenience.service.ConCataImportService;
import com.jeeplus.modules.convenience.service.ConCataMaintainService;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.InputStream;
import java.util.List;

/**
 * 便民目录导入：目录信息
 */
@RestController
@RequestMapping(value = "/convience/catalog/import")
public class ConCataImportController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ConCataImportFileController.class);

    @Autowired
    private ConCataImportService cataImportService;

    @Autowired
    private ConCataImportFileService importFileService;

    @Autowired
    private ConCataMaintainService cataMaintainService;

    public ConCataImport get(@RequestParam(required = false)String id) {
        ConCataImport entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = cataImportService.getById(id);
        }
        if (entity == null) {
            entity = new ConCataImport();
        }
        return entity;
    }

    /**
     * 便民目录导入查看列表数据
     * @param conCataImport
     * @param request
     * @param response
     * @param model
     * @return
     */
    @GetMapping(value = "list")
    public AjaxJson list(ConCataImport conCataImport, HttpServletRequest request, HttpServletResponse response, Model model) {
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        LambdaQueryChainWrapper<ConCataImport> query = cataImportService.lambdaQuery();
        query.eq(ConCataImport::getImportFileUuid,conCataImport.getImportFileUuid());
        query.orderByDesc(ConCataImport::getUpdateDate);
        IPage<ConCataImport> page = query.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        return AjaxJson.success().data(page);
    }

/*    @PostMapping("import")
    public AjaxJson importFile(@RequestParam("file") MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
        InputStream inputStream = ResourceUtil.getStream("事项目录清单导入文件.xls");
        EasyExcel.read(inputStream,
                ConCataImport.class,
                new ConCataImportListenHandler(cataImportService))
                .sheet()
                .doRead();
        return AjaxJson.success();
    }*/

    @RequestMapping("import")
    public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
        try {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<ConCataImport> list = ei.getDataList(ConCataImport.class);
            String fileUuid = IdGen.uuid();
            for (ConCataImport conCataImport : list){
                try{
                    LambdaQueryChainWrapper<ConCataImport> query = cataImportService.lambdaQuery();
                    String errMsg = beanValidator(conCataImport);
                    List<ConCataImport> res = query.eq(ConCataImport::getBaseCode,conCataImport.getBaseCode())
                            .eq(ConCataImport::getCataVersion,conCataImport.getCataVersion()).list();
                    if (res.size() > 0) {
                        conCataImport.setCheckResult("错误:导入目录相同版本数据已存在");
                        conCataImport.setImportReport("无效数据");
                        conCataImport.setIsValid("0");
                    }else {
                        conCataImport.setCheckResult("导入成功");
                        conCataImport.setImportReport("有效数据");
                        conCataImport.setIsValid("1");
                    }
                    cataImportService.save(conCataImport);
                    successNum++;
                }catch(ConstraintViolationException ex){
                    failureNum++;
                }catch (Exception ex) {
                    failureNum++;
                }
            }
            ConCataImportFile conCataImportFile = new ConCataImportFile();
            conCataImportFile.setUuid(fileUuid);
            conCataImportFile.setImportCount(successNum);
            User user = UserUtils.getUser();
            conCataImportFile.setImportUserName(user.getName());
            conCataImportFile.setImportOrgCode(user.getOrgCode());
            conCataImportFile.setImportOrgName(user.getOrgName());
            conCataImportFile.setImportStatus("0");
            conCataImportFile.setImportLevel(user.getRegionCode());
            importFileService.saveOrUpdate(conCataImportFile);
            if (failureNum>0){
                failureMsg.insert(0, "，失败 "+failureNum+" 条记录。");
            }
            return AjaxJson.success("已成功导入 "+successNum+" 条记录"+failureMsg).data(conCataImportFile);
        } catch (Exception e) {
            return AjaxJson.error("导入失败！失败信息："+e.getMessage());
        }
    }


    @PostMapping("delete")
    public AjaxJson delete( ConCataImport conCataImport) {
        cataImportService.removeById(conCataImport);
        return AjaxJson.success("删除成功!");
    }

}
