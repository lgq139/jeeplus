package com.jeeplus.config.properties;

import cn.hutool.extra.spring.SpringUtil;
import com.jeeplus.common.utils.StringUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.Serializable;

/**
 * 全局配置类
 *
 * @author jeeplus
 * @version 2017-06-25
 */
@Data
@Configuration
public class JeePlusProperites implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(JeePlusProperites.class);

    @Value("${version}")
    private String version;

    @Value("${demoMode}")
    private String demoMode;

    @Value("${spring.cache.type}")
    private String cacheType;

    @Value("${userfiles.basedir}")
    private String userfilesBasedir;

    @Value("${jwt.accessToken.expireTime}")
    public long EXPIRE_TIME;


    /**
     * 上传文件基础虚拟路径
     */
    public static final String USERFILES_BASE_URL = "/userfiles/";

    /**
     * 是/否
     */
    public static final String YES = "1";
    public static final String NO = "0";

    /**
     * 对/错
     */
    public static final String TRUE = "true";
    public static final String FALSE = "false";


    /**
     * 显示/隐藏
     */
    public static final String SHOW = "1";
    public static final String HIDE = "0";




    public static JeePlusProperites newInstance() {

        return SpringUtil.getBean(JeePlusProperites.class);
    }



    /**
     * 获取上传文件的根目录
     *
     * @return
     */
    public String getUserfilesBaseDir() {
        String dir = this.userfilesBasedir;
        if (StringUtils.isBlank(dir)) {
            try {
                return new File(this.getClass().getResource("/").getPath())
                        .getParentFile().getParentFile().getParentFile().getCanonicalPath();
            } catch (Exception e) {
                return "";
            }
        }
        if (!dir.endsWith("/")) {
            dir += "/";
        }

        return dir;
    }


    /**
     * 是否是演示模式，演示模式下不能修改用户、角色、密码、菜单、授权
     */
    public Boolean isDemoMode() {
        String dm = this.demoMode;
        return "true".equals(dm) || "1".equals(dm);
    }



}
