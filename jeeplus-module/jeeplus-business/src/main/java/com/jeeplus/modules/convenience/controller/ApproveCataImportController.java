package com.jeeplus.modules.convenience.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.IdGen;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.convenience.entity.ApproveCataImport;
import com.jeeplus.modules.convenience.entity.ConCataImport;
import com.jeeplus.modules.convenience.entity.ConCataImportFile;
import com.jeeplus.modules.convenience.entity.ConCataMaintain;
import com.jeeplus.modules.convenience.service.*;
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
import java.util.List;

/**
 * 便民目录导入：目录信息
 */
@RestController
@RequestMapping(value = "/approve/catalog/import")
public class ApproveCataImportController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ConCataImportFileController.class);

    @Autowired
    private ApproveCataImportService approveCataImportService;

    @Autowired
    private ConCataInventoryService cataInventoryService;


    public ApproveCataImport get(@RequestParam(required = false)String id) {
        ApproveCataImport entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = approveCataImportService.getById(id);
        }
        if (entity == null) {
            entity = new ApproveCataImport();
        }
        return entity;
    }

    /**
     * 便民目录导入查看列表数据
     * @param approveCataImport
     * @param request
     * @param response
     * @param model
     * @return
     */
    @GetMapping(value = "list")
    public AjaxJson list(ApproveCataImport approveCataImport, HttpServletRequest request, HttpServletResponse response, Model model) {
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        LambdaQueryChainWrapper<ApproveCataImport> query = approveCataImportService.lambdaQuery();
        query.eq(ApproveCataImport::getCreateBy,UserUtils.getUser().getId());
        IPage<ApproveCataImport> page = query.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
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
            List<ApproveCataImport> list = ei.getDataList(ApproveCataImport.class);
            String fileUuid = IdGen.uuid();
            for (ApproveCataImport approveCataImport : list){
                try{
                    LambdaQueryChainWrapper<ApproveCataImport> query = approveCataImportService.lambdaQuery();
                    String errMsg = beanValidator(approveCataImport);
                    if (StringUtils.isBlank(errMsg)) {
                        boolean flag = approveCataImportService.lambdaQuery().eq(ApproveCataImport::getBaseCode, approveCataImport.getBaseCode())
                                .eq(ApproveCataImport::getCataVersion,approveCataImport.getCataVersion())
                                .eq(ApproveCataImport::getOrgCode,UserUtils.getUser().getOrgCode())
                                .exists();
                        if (flag) {
                            approveCataImport.setCheckResult("错误:导入目录相同版本数据已存在");
                            approveCataImport.setImportReport("无效数据");
                            approveCataImport.setIsValid("0");
                        }else {
                            approveCataImport.setCheckResult("导入成功");
                            approveCataImport.setImportReport("有效数据");
                            approveCataImport.setIsValid("1");
                        }
                     //   conCataImport.setUuid(IdGen.uuid());
                    }else {
                        approveCataImport.setCheckResult(errMsg);
                        approveCataImport.setImportReport("无效数据");
                        approveCataImport.setIsValid("0");
                    }
                    approveCataImportService.save(approveCataImport);
                    successNum++;
                }catch(ConstraintViolationException ex){
                    failureNum++;
                }catch (Exception ex) {
                    failureNum++;
                }
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
    public AjaxJson saveImport(ApproveCataImport approveCataImport) {
        List<ApproveCataImport> cataList = approveCataImportService.lambdaQuery()
                .eq(ApproveCataImport::getDelFlag,"0")
                .eq(ApproveCataImport::getIsValid,"1")
                .eq(ApproveCataImport::getCreateBy,UserUtils.getUser().getId()).list();
        if (cataList.size() > 0) {
            cataList.forEach(cataImport -> {
                if ("1".equals(cataImport.getIsValid())) {
                    cataInventoryService.saveApproveCata(cataImport);
                }
            });
        }
        return AjaxJson.success("成功保存" + cataList.size() +"条目录");
    }


    @PostMapping("delete")
    public AjaxJson delete( ConCataImport conCataImport) {
        approveCataImportService.removeById(conCataImport);
        return AjaxJson.success("删除成功!");
    }

}
