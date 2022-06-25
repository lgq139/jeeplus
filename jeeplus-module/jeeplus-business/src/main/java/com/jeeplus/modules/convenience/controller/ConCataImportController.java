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
import com.jeeplus.modules.convenience.entity.ConCataMaintain;
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
        query.eq(ConCataImport::getImportFileUuid,conCataImport.getImportFileUuid())
                .eq(ConCataImport::getDelFlag,"0");
        query.orderByDesc(ConCataImport::getUpdateDate);
        IPage<ConCataImport> page = query.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        return AjaxJson.success().data(page);
    }

    @RequestMapping("import")
    @CrossOrigin
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
                    conCataImport.setImportFileUuid(fileUuid);
                    if (StringUtils.isBlank(errMsg)) {
                        boolean flag = cataMaintainService.lambdaQuery().eq(ConCataMaintain::getBaseCode, conCataImport.getBaseCode())
                                .eq(ConCataMaintain::getCataVersion,conCataImport.getCataVersion())
                                .exists();
                        if (flag) {
                            conCataImport.setCheckResult("错误:导入目录相同版本数据已存在");
                            conCataImport.setImportReport("无效数据");
                            conCataImport.setIsValid("0");
                        }else {
                            conCataImport.setCheckResult("导入成功");
                            conCataImport.setImportReport("有效数据");
                            conCataImport.setIsValid("1");
                        }
                     //   conCataImport.setUuid(IdGen.uuid());
                    }else {
                        conCataImport.setCheckResult(errMsg);
                        conCataImport.setImportReport("无效数据");
                        conCataImport.setIsValid("0");
                    }
                    cataImportService.save(conCataImport);
                    successNum++;
                }catch(ConstraintViolationException ex){
                    failureNum++;
                }catch (Exception ex) {
                    failureNum++;
                }
            }
            if (successNum != 0) {
                ConCataImportFile conCataImportFile = new ConCataImportFile();
                conCataImportFile.setUuid(fileUuid);
                conCataImportFile.setImportStatus("0");
                importFileService.saveOrUpdate(conCataImportFile);
            }
            if (failureNum>0){
                failureMsg.insert(0, "，失败 "+failureNum+" 条记录。");
            }
            return AjaxJson.success("已成功导入 "+successNum+" 条记录"+failureMsg).data(fileUuid);
        } catch (Exception e) {
            return AjaxJson.error("导入失败！失败信息："+e.getMessage());
        }
    }

    @RequestMapping("saveImport")
    public AjaxJson saveImport(ConCataImport conCataImport) {
        List<ConCataImport> cataList = cataImportService.lambdaQuery()
                .eq(ConCataImport::getImportFileUuid,conCataImport.getImportFileUuid())
                .eq(ConCataImport::getDelFlag,"0").list();
        if (cataList.size() > 0) {
            cataList.forEach(cataImport -> {
                if ("1".equals(cataImport.getIsValid())) {
                    cataMaintainService.saveImportCata(cataImport);
                }
            });
        }
        ConCataImportFile cataImportFile = importFileService.lambdaQuery()
                .eq(ConCataImportFile::getUuid,conCataImport.getImportFileUuid())
                .one();
        User user = UserUtils.getUser();
        cataImportFile.setImportCount(cataList.size());
        cataImportFile.setImportUserName(user.getName());
        cataImportFile.setImportOrgCode(user.getOrgCode());
        cataImportFile.setImportOrgName(user.getOrgName());
        cataImportFile.setImportLevel(UserUtils.getGradeByRegionCode(user.getRegionCode()));
        cataImportFile.setImportStatus("1");
        importFileService.updateById(cataImportFile);
        return AjaxJson.success("保存成功");
    }


    @PostMapping("delete")
    public AjaxJson delete( ConCataImport conCataImport) {
        cataImportService.removeById(conCataImport);
        return AjaxJson.success("删除成功!");
    }

}
