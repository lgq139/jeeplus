package com.jeeplus.modules.convenience.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.IdGen;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.convenience.entity.ConCataImport;
import com.jeeplus.modules.convenience.entity.ConCataImportFile;
import com.jeeplus.modules.convenience.service.ConCataImportFileService;
import com.jeeplus.modules.convenience.service.ConCataImportService;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 便民目录导入：文件信息
 */
@RestController
@RequestMapping(value = "/convience/catalog/file")
public class ConCataImportFileController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ConCataImportFileController.class);

    @Autowired
    private ConCataImportFileService importFileService;

    @Autowired
    private ConCataImportService cataImportService;

    @ModelAttribute
    public ConCataImportFile get(@RequestParam(required=false) String id) {
        ConCataImportFile entity = null;
        if (StringUtils.isNotBlank(id)){
            entity = importFileService.getById(id);
        }
        if (entity == null){
            entity = new ConCataImportFile();
        }
        return entity;
    }

    @GetMapping(value = "queryById")
    public AjaxJson queryById(@RequestParam(required = true)String id, HttpServletRequest request) {
        ConCataImportFile entity = importFileService.getById(id);;
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        LambdaQueryChainWrapper<ConCataImport> query = cataImportService.lambdaQuery();
        query.eq(ConCataImport::getImportFileUuid,entity.getUuid());
        IPage<ConCataImport> page = query.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        return AjaxJson.success().data(page);
    }

    /**
     * 便民目录导入文件列表数据
     * @param conCataImportFile
     * @param request
     * @param response
     * @param model
     * @return
     */
    @GetMapping(value = "list")
    public AjaxJson list(ConCataImportFile conCataImportFile, HttpServletRequest request, HttpServletResponse response, Model model) {
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        LambdaQueryChainWrapper<ConCataImportFile> query = importFileService.lambdaQuery();
        if (StringUtils.isNotBlank(conCataImportFile.getCreateBy())) {
            query.like(ConCataImportFile::getCreateBy,conCataImportFile.getCreateBy());
        }
        if (StringUtils.isNotBlank(conCataImportFile.getImportOrgName())) {
            query.like(ConCataImportFile::getImportOrgName,conCataImportFile.getImportOrgName());
        }
        IPage<ConCataImportFile> page = query.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        return AjaxJson.success().data(page);
    }


    /**
     * 保存目录导入文件数据
     * @param conCataImportFile
     * @return
     */
    /*@PostMapping(value = "save")
    public AjaxJson save(ConCataImportFile conCataImportFile) {
        String errMsg = beanValidator(conCataImportFile);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }
        conCataImportFile.setUuid(IdGen.uuid());
        User user = UserUtils.getUser();
        conCataImportFile.setImportUserName(user.getName());
        conCataImportFile.setImportOrgCode(user.getOrgCode());
        conCataImportFile.setImportOrgName(user.getOrgName());
        conCataImportFile.setImportStatus("1");
        importFileService.saveOrUpdate(conCataImportFile);
        return AjaxJson.success("保存成功").data(conCataImportFile);
    }*/



}
