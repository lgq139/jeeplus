package com.jeeplus.modules.sys.web;

import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.SysConfig;
import com.jeeplus.modules.sys.service.SysConfigService;
import com.jeeplus.modules.sys.vo.SysConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 系统配置Controller
 */
@RestController
@RequestMapping("/sys/sysConfig")
public class SysConfigController extends BaseController {

	@Autowired
	private SysConfigService sysConfigService;


	/**
	 * 系统配置
	 */
	@GetMapping("queryById")
	public AjaxJson queryById(HttpServletRequest request, HttpServletResponse response, Model model) {
		SysConfig config = sysConfigService.get("1");
		return AjaxJson.success().put("config", config);
	}

	/**
	 * 系统配置
	 */
	@GetMapping("getConfig")
	public AjaxJson getConfig() {
		SysConfig config = sysConfigService.get("1");
		SysConfigVO vo = new SysConfigVO();
		vo.setDefaultLayout (config.getDefaultLayout ());
		vo.setDefaultTheme (config.getDefaultTheme ());
		vo.setLogo (config.getLogo ());
		vo.setProductName (config.getProductName ());
		return AjaxJson.success().put("config", vo);
	}

	/**
	 * 保存系统配置
	 */
	@PostMapping("save")
	public AjaxJson save(SysConfig config)throws Exception {
		AjaxJson j = new AjaxJson();
		if(jeePlusProperites.isDemoMode()){
			return AjaxJson.error("演示模式，禁止修改!");
		}
		if(config.getMultiAccountLogin() == null){
			config.setMultiAccountLogin("0");
		}
		SysConfig target = sysConfigService.get("1");
		MyBeanUtils.copyBeanNotNull2Bean(config, target);
		sysConfigService.save(target);
		return AjaxJson.success("保存系统配置成功").put("config", target);
	}

}
