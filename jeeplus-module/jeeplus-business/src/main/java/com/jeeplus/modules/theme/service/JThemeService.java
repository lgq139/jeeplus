package com.jeeplus.modules.theme.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.theme.entity.JTheme;
import com.jeeplus.modules.theme.mapper.JThemeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: jeeplus
 * @description:
 * @author: 李国强
 * @create: 2022-07-02 16:09
 **/
@Service
@Transactional(readOnly = true)
public class JThemeService extends ServiceImpl<JThemeMapper, JTheme> {
}
