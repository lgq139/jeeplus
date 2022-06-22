package com.jeeplus.modules.convenience.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.convenience.entity.ConCataMaintain;
import com.jeeplus.modules.convenience.mapper.ConCataMaintainMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConCataMaintainService extends ServiceImpl<ConCataMaintainMapper, ConCataMaintain>  {
}
