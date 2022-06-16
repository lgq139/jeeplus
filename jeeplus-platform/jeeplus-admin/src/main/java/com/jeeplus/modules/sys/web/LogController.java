package com.jeeplus.modules.sys.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.Log;
import com.jeeplus.modules.sys.service.LogService;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 日志Controller
 */
@RestController
@RequestMapping("/sys/log")
public class LogController extends BaseController {

	@Autowired
	private LogService logService;

	@RequiresPermissions("sys:log:list")
	@GetMapping("list")
	public AjaxJson data(Log log, HttpServletRequest request, HttpServletResponse response) {
		String pageNo = StringUtils.defaultIfBlank(request.getParameter("pageNo"), "1");
		String pageSize = StringUtils.defaultIfBlank(request.getParameter("pageSize"), "10");
        IPage<Log> page = logService.findPage(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)), log);
		return AjaxJson.success().put("data", page);
	}

	@RequiresPermissions("user")
	@GetMapping("data/mine")
	public AjaxJson mine(Log log, HttpServletRequest request, HttpServletResponse response, Model model) {
		String pageNo = StringUtils.defaultIfBlank(request.getParameter("pageNo"), "1");
		String pageSize = StringUtils.defaultIfBlank(request.getParameter("pageSize"), "10");
		log.setUserId(UserUtils.getUser().getId());
		IPage<Log> page = logService.findPage(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)), log);
		return AjaxJson.success().put("data", page);
	}


	/**
	 * 批量删除
	 */
	@RequiresPermissions("sys:log:del")
	@DeleteMapping("delete")
	public AjaxJson deleteAll(String ids) {
		if (StringUtils.isNotBlank(ids)) {
			logService.removeByIds(Arrays.asList(ids.split(",")));
		}
		return AjaxJson.success("删除日志成功！");
	}

	/**
	 * 批量删除
	 */
	@RequiresPermissions("sys:log:del")
	@DeleteMapping("empty")
	public AjaxJson empty() {
		logService.empty();
		return AjaxJson.success("清空日志成功!");
	}
}
