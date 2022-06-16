package com.jeeplus.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.common.utils.FileUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.config.properties.FileProperties;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.DictValue;
import com.jeeplus.modules.sys.entity.Menu;
import com.jeeplus.modules.sys.entity.Role;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.security.util.JWTUtil;
import com.jeeplus.modules.sys.service.RoleService;
import com.jeeplus.modules.sys.service.UserService;
import com.jeeplus.modules.sys.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户Controller
 */
@RestController
@RequestMapping("/sys/user")
@Api(tags ="用户管理")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private FileProperties fileProperties;

    @ModelAttribute
    public User get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return userService.get(id);
        } else {
            return new User();
        }
    }

    @GetMapping("queryById")
    @ApiOperation(value = "查询用户")
    public AjaxJson queryById(User user) {
        return AjaxJson.success().put("user", get(user.getId()));
    }

    @RequiresPermissions("sys:user:list")
    @GetMapping("list")
    public AjaxJson list(User user, HttpServletRequest request, HttpServletResponse response) {
        // Page<User> page = userService.findPage(new Page<User>(request, response), user);
        // return AjaxJson.success().put("page", page);
        return AjaxJson.success();
    }

    @RequiresPermissions(value = {"sys:user:add", "sys:user:edit"}, logical = Logical.OR)
    @PostMapping("save")
    @ApiOperation(value = "保存用户")
    public AjaxJson save(User user) {
        if (jeePlusProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作!");
        }
        // 如果新密码为空，则不更换密码
        if (StringUtils.isNotBlank(user.getNewPassword())) {
            user.setPassword(UserService.entryptPassword(user.getNewPassword()));
        }
        /**
         * 后台hibernate-validation插件校验
         */
        String errMsg = beanValidator(user);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }
        if (!isCheckLoginName(user.getOldLoginName(), user.getLoginName())) {
            return AjaxJson.error("保存用户'" + user.getLoginName() + "'失败，登录名已存在!");
        }
        // 角色数据有效性验证，过滤不在授权内的角色
        List<Role> roleList = Lists.newArrayList();
        List<String> roleIdList = user.getRoleIdList();
        for (String roleId : roleIdList) {
            roleList.add(roleService.get(roleId));
        }
        user.setRoleList(roleList);

        // 保存用户信息
        userService.saveUser(user);
        // 清除当前用户缓存
        if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
            UserUtils.clearCache();
        }
        return AjaxJson.success("保存用户'" + user.getLoginName() + "'成功!");
    }

    @RequiresPermissions(value = {"sys:user:add", "sys:user:edit"}, logical = Logical.OR)
    @PostMapping("resetPassword")
    @ApiOperation(value = "重置密码")
    public AjaxJson resetPassword(User user) {
        if (jeePlusProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作!");
        }
        if (StringUtils.isBlank(user.getId())) {
            return AjaxJson.error("参数错误！");
        }
        // 保存用户信息
        User targetUser = userService.getById(user);
        userService.updatePasswordById(targetUser.getId(), targetUser.getLoginName(), "123456");
        // 清除当前用户缓存
        if (targetUser.getLoginName().equals(UserUtils.getUser().getLoginName())) {
            UserUtils.clearCache();
        }
        return AjaxJson.success("密码重置成功!");
    }

    /**
     * 批量删除用户
     */
    @ApiOperation(value = "删除用户")
    @RequiresPermissions("sys:user:del")
    @DeleteMapping("delete")
    public AjaxJson delete(String ids) {
        String idArray[] = ids.split(",");
        AjaxJson j = new AjaxJson();
        if (jeePlusProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作!");
        }
        StringBuffer msg = new StringBuffer();
        boolean success = true;
        for (String id : idArray) {
            User user = userService.get(id);
            if (UserUtils.getUser().getId().equals(user.getId())) {
                success = false;
                msg.append("["+user.getLoginName()+"]删除失败，不允许删除当前用户!<br/>");
            } else if (User.isAdmin(user.getId())) {
                success = false;
                msg.append("["+user.getLoginName()+"]删除失败，不允许删除超级管理员!<br/>");//删除用户失败, 不允许删除超级管理员用户
            } else {
                msg.append("["+user.getLoginName()+"]删除成功!<br/>");
                userService.deleteUser(user);//删除用户成功
            }
        }
        if(success){
            return AjaxJson.success(msg.toString());
        }else {
            return AjaxJson.error(msg.toString());
        }
    }


    private boolean isCheckLoginName(String oldLoginName, String loginName) {
        if (loginName != null && loginName.equals(oldLoginName)) {
           return true;
        } else if (loginName != null && userService.getUserByLoginName(loginName) == null) {
            return true;
        }
      return false;
    }


    /**
     * 用户信息显示编辑保存
     *
     * @param user
     * @return
     */
    @RequiresPermissions("user")
    @PostMapping("saveInfo")
    @ApiOperation(value = "修改个人资料")
    public AjaxJson infoEdit(User user) {
        User currentUser = UserUtils.getUser();
        if (jeePlusProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作！");
        }
        if (user.getName() != null) {
            currentUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            currentUser.setEmail(user.getEmail());
        }
        if (user.getPhone() != null) {
            currentUser.setPhone(user.getPhone());
        }
        if (user.getMobile() != null) {
            currentUser.setMobile(user.getMobile());
        }
        if (user.getRemarks() != null) {
            currentUser.setRemarks(user.getRemarks());
        }
        if (user.getSign() != null) {
            currentUser.setSign(user.getSign());
        }
        userService.updateUserInfo(currentUser);
        return AjaxJson.success("修改个人资料成功!");
    }


    /**
     * 用户头像显示编辑保存
     *
     * @return
     * @throws IOException
     * @throws IllegalStateException
     */
    @RequiresPermissions("user")
    @PostMapping("imageUpload")
    @ApiOperation(value = "上传头像")
    public AjaxJson imageUpload(HttpServletRequest request, HttpServletResponse response, MultipartFile file) throws IllegalStateException, IOException {
        User currentUser = UserUtils.getUser();
        // 判断文件是否为空
        if (!file.isEmpty()) {
            if(fileProperties.isImage (file.getOriginalFilename ())){
                // 文件保存路径
                String realPath = FileKit.getAttachmentDir() + "sys/user/images/";
                // 转存文件
                FileUtils.createDirectory(realPath);
                file.transferTo(new File(realPath + file.getOriginalFilename()));
                currentUser.setPhoto(FileKit.getAttachmentUrl() + "sys/user/images/" + file.getOriginalFilename());
                userService.updateUserInfo(currentUser);
                return AjaxJson.success("上传成功!").put("path", FileKit.getAttachmentUrl() + "sys/user/images/" + file.getOriginalFilename());
            }else{
                return AjaxJson.error ("请上传图片!");
            }

        }else{
            return AjaxJson.error ("文件不存在!");
        }


    }

    /**
     * 返回用户信息
     *
     * @return
     */
    @RequiresPermissions("user")
    @GetMapping("info")
    @ApiOperation(value = "获取当前用户信息")
    public AjaxJson infoData() {
        return AjaxJson.success("获取个人信息成功!")
                .put("role", UserUtils.getRoleList())
                .put("user", UserUtils.getUser());
    }


    @RequiresPermissions("user")
    @PostMapping("savePwd")
    @ApiOperation(value = "修改密码")
    public AjaxJson savePwd(String oldPassword, String newPassword, Model model) {
        User user = UserUtils.getUser();
        if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {
            if (jeePlusProperites.isDemoMode()) {
                return AjaxJson.error("演示模式，不允许操作！");
            }
            if (UserService.validatePassword(oldPassword, user.getPassword())) {
                userService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
                return AjaxJson.success("修改密码成功！");
            } else {
                return AjaxJson.error("修改密码失败，旧密码错误！");
            }
        }
        return AjaxJson.error("参数错误！");
    }


    /**
     * 保存签名
     */
    @ApiOperation(value = "用户签名")
    @PostMapping("saveSign")
    public AjaxJson saveSign(User user, boolean __ajax, HttpServletResponse response, Model model) throws Exception {
        AjaxJson j = new AjaxJson();
        User currentUser = UserUtils.getUser();
        currentUser.setSign(user.getSign());
        userService.updateUserInfo(currentUser);
        return AjaxJson.success("设置签名成功!");
    }

    @RequiresPermissions("user")
    @GetMapping("treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String officeId, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<User> list = userService.findUserByOfficeId(officeId);
        for (int i = 0; i < list.size(); i++) {
            User e = list.get(i);
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", "u_" + e.getId());
            map.put("pId", officeId);
            map.put("name", StringUtils.replace(e.getName(), " ", ""));
            mapList.add(map);
        }
        return mapList;
    }


    /**
     * web端ajax验证手机号是否可以注册（数据库中不存在）
     */
    @ApiOperation("验证手机号")
    @GetMapping("validateMobile")
    public AjaxJson validateMobile(String mobile, HttpServletResponse response, Model model) {
        User user = userService.lambdaQuery().eq(User::getMobile, mobile).one();
        if (user == null) {
            return AjaxJson.success().put("noExist", true);
        } else {
            return AjaxJson.success().put("noExist", false);
        }
    }



    @ApiOperation(value = "用户权限")
    @RequiresPermissions("user")
    @GetMapping("getPermissions")
    public Set<String> getPermissions() {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        String token = UserUtils.getToken();
        String loginName = JWTUtil.getLoginName(token);
        User user = UserUtils.getByLoginName(loginName);
        if (user != null) {

            List<Menu> list = UserUtils.getMenuList();
            for (Menu menu : list) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(menu.getPermission())) {
                    // 添加基于Permission的权限信息
                    for (String permission : org.apache.commons.lang3.StringUtils.split(menu.getPermission(), ",")) {
                        info.addStringPermission(permission);
                    }
                }
            }
            // 添加用户权限
            info.addStringPermission("user");
            // 添加用户角色信息
            for (Role role : user.getRoleList()) {
                info.addRole(role.getEnname());
            }
        }
        return info.getStringPermissions();
    }


    @GetMapping("getMenus")
    @RequiresPermissions("user")
    public AjaxJson getMenus() {
        AjaxJson j = new AjaxJson();
        j.put("dictList", this.getDicts());
        j.put("permissions", this.getPermissions());
        j.put("menuList", MenuUtils.getMenus());
        j.put("routerList", RouterUtils.getRoutersByMenu());
        return j;
    }

    private Map<String, List<DictValue>> getDicts() {
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
        return DictUtils.getDictMap();
    }


}
