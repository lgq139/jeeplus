package com.jeeplus.modules.sys.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.Notice;
import com.jeeplus.modules.sys.service.NoticeService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/sys/notice")
public class NoticeController extends BaseController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/getById")
    public AjaxJson get(String id) {
        if (StringUtils.isBlank(id)) {
            return AjaxJson.error("参数错误");
        }
        return AjaxJson.success().put("data", noticeService.getById(id));
    }

    @RequiresPermissions("sys:notice:list")
    @GetMapping("list")
    public AjaxJson list(Notice notice, HttpServletRequest request, HttpServletResponse response) {
        String pageNo = StringUtils.defaultIfBlank(request.getParameter("pageNo"), "1");
        String pageSize = StringUtils.defaultIfBlank(request.getParameter("pageSize"), "10");
        LambdaQueryChainWrapper<Notice> wrapper = noticeService.lambdaQuery();
        if (StringUtils.isNotBlank(notice.getTitle())) {
            wrapper.likeRight(Notice::getTitle, notice.getTitle());
        }
        wrapper.orderByDesc(Notice::getCreateDate);
        IPage<Notice> page = wrapper.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        return AjaxJson.success().put("data", page);
    }

    @RequiresPermissions(value = {"sys:notice:add", "sys:notice:edit"}, logical = Logical.OR)
    @PostMapping("save")
    public AjaxJson save(Notice notice) {
        String beanValidator = beanValidator(notice);
        if (StringUtils.isNotBlank(beanValidator)) {
            return AjaxJson.success(beanValidator);
        }
        if (StringUtils.isBlank(notice.getId())) {
            notice.setPublishTime(new Date());
        }
        noticeService.saveOrUpdate(notice);
        return AjaxJson.success("通知公告保存成功");
    }

    @RequiresPermissions("sys:notice:del")
    @PostMapping("delete")
    public AjaxJson delete(String ids) {
        if (StringUtils.isNotBlank(ids)) {
            noticeService.removeByIds(Arrays.asList(ids.split(",")));
        }
        return AjaxJson.success("通知公告删除成功");
    }

}

