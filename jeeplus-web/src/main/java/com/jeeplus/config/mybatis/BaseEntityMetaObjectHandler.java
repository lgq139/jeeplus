package com.jeeplus.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatis-plus 字段自动填充处理器
 *
 * @author FEAR
 * @since 1.0
 */
@Component
public class BaseEntityMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Date date = new Date();
        this.strictInsertFill(metaObject, "createDate", Date.class, date);
        this.strictInsertFill(metaObject, "updateDate", Date.class, date);
        User user = UserUtils.getUser();
        this.strictInsertFill(metaObject, "createBy", String.class, user != null ? user.getId() : "");
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateDate", Date.class, new Date());
        User user = UserUtils.getUser();
        this.strictUpdateFill(metaObject, "updateBy", String.class, user != null ? user.getId() : "");
    }

}
