package com.jeeplus.core.ext.service;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.ext.persistence.BaseEntity;
import com.jeeplus.core.ext.persistence.DataEntity;
import com.jeeplus.modules.sys.entity.DataRule;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Service基类
 *
 * @author jeeplus
 * @version 2017-05-16
 */
@Transactional(readOnly = true)
public abstract class CrudService<M extends BaseMapper<T>, T extends DataEntity<T>> extends ServiceImpl<M, T> {

    /**
     * 数据范围过滤
     * @param entity 当前过滤的实体类
     */
    public static void dataRuleFilter(BaseEntity<?> entity) {
        if(UserUtils.getUser() == null ||  StringUtils.isBlank(UserUtils.getUser().getId())){
            return;
        }
        entity.setCurrentUser(UserUtils.getUser());
        List<DataRule> dataRuleList = UserUtils.getDataRuleList();

        // 如果是超级管理员，则不过滤数据
        if (dataRuleList.size() == 0) {
            return;
        }

        // 数据范围
        StringBuilder sqlString = new StringBuilder();


        for(DataRule dataRule : dataRuleList){
            if(entity.getClass().getSimpleName().equals(dataRule.getClassName())){
                sqlString.append(dataRule.getDataScopeSql());
            }

        }

        entity.setDataScope(sqlString.toString());
    }


//    /**
//     * 查询列表数据
//     *
//     * @param entity
//     * @return
//     */
//    public List<T> findList(T entity) {
//        dataRuleFilter(entity);
//        return mapper.findList(entity);
//    }


    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        return super.saveBatch(entityList, batchSize);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean saveOrUpdate(T entity) {
        return super.saveOrUpdate(entity);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        return super.saveOrUpdateBatch(entityList, batchSize);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        return super.updateBatchById(entityList, batchSize);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    protected boolean executeBatch(Consumer<SqlSession> consumer) {
        return super.executeBatch(consumer);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return super.executeBatch(list, batchSize, consumer);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    protected <E> boolean executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
        return super.executeBatch(list, consumer);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean save(T entity) {
        return super.save(entity);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean saveBatch(Collection<T> entityList) {
        return super.saveBatch(entityList);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean removeById(T entity) {
        return super.removeById(entity);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean removeByMap(Map<String, Object> columnMap) {
        return super.removeByMap(columnMap);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean remove(Wrapper<T> queryWrapper) {
        return super.remove(queryWrapper);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return super.removeByIds(idList);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean updateById(T entity) {
        return super.updateById(entity);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean update(Wrapper<T> updateWrapper) {
        return super.update(updateWrapper);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean update(T entity, Wrapper<T> updateWrapper) {
        return super.update(entity, updateWrapper);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean updateBatchById(Collection<T> entityList) {
        return super.updateBatchById(entityList);
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public KtUpdateChainWrapper<T> ktUpdate() {
        return super.ktUpdate();
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public UpdateChainWrapper<T> update() {
        return super.update();
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public LambdaUpdateChainWrapper<T> lambdaUpdate() {
        return super.lambdaUpdate();
    }

    @Transactional(
            readOnly = false,
            rollbackFor = Exception.class
    )
    @Override
    public boolean saveOrUpdate(T entity, Wrapper<T> updateWrapper) {
        return super.saveOrUpdate(entity, updateWrapper);
    }

    /**
     * 数据源切换
     *
     * @param name dataSource name
     * @return i
     */
    public JdbcTemplate getJdbcTemplate(String name) {
        DataSource dataSource = SpringUtil.getBean(DynamicRoutingDataSource.class).getDataSource(name);
        return new JdbcTemplate(dataSource);
    }
}
