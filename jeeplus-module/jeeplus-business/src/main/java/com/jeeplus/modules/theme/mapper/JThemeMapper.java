package com.jeeplus.modules.theme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.theme.entity.JTheme;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @program: jeeplus
 * @description:
 * @author: 李国强
 * @create: 2022-07-02 16:12
 **/
@Mapper
@Repository
public interface JThemeMapper extends BaseMapper<JTheme> {
}
