package com.jeeplus.modules.sys.web;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.Post;
import com.jeeplus.modules.sys.service.PostService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 岗位Controller
 */
@RestController
@RequestMapping(value = "/sys/post")
public class PostController extends BaseController {

	@Autowired
	private PostService postService;

	@ModelAttribute
	public Post get(@RequestParam(required=false) String id) {
		Post entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = postService.getById(id);
		}
		if (entity == null){
			entity = new Post();
		}
		return entity;
	}

	/**
	 * 岗位列表数据
	 */
	@GetMapping("list")
	public AjaxJson list(Post post, HttpServletRequest request, HttpServletResponse response) {
		LambdaQueryChainWrapper<Post> wrapper = postService.lambdaQuery();
		wrapper.like(StringUtils.isNotBlank(post.getName()), Post::getName, post.getName());
		wrapper.like(StringUtils.isNotBlank(post.getCode()), Post::getCode, post.getCode());
		wrapper.eq(StringUtils.isNotBlank(post.getStatus()), Post::getStatus, post.getStatus());
		wrapper.orderByAsc(Post::getSort);
		return AjaxJson.success().data(wrapper.page(new Page<>(Long.parseLong(getPageNoParameter(request)),
				Long.parseLong(getPageSizeParameter(request)))));
	}

	/**
	 * 根据Id获取岗位数据
	 */
	@RequiresPermissions(value={"sys:post:view","sys:post:add","sys:post:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(Post post) {
		return AjaxJson.success().data(postService.getById(post));
	}

	/**
	 * 保存岗位
	 */
	@RequiresPermissions(value={"sys:post:add","sys:post:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(Post post) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(post);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		postService.saveOrUpdate(post);//保存
		return AjaxJson.success("保存岗位成功");
	}


	/**
	 * 批量删除岗位
	 */
	@RequiresPermissions("sys:post:del")
	@DeleteMapping("delete")
	public AjaxJson delete(String ids) {
		String[] idArray =ids.split(",");
		for(String id : idArray){
			postService.removeById(postService.getById(id));
		}
		return AjaxJson.success("删除岗位成功");
	}

}
