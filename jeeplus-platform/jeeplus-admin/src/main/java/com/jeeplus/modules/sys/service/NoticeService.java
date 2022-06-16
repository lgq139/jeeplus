package com.jeeplus.modules.sys.service;

import com.jeeplus.core.ext.service.CrudService;
import com.jeeplus.modules.sys.entity.Notice;
import com.jeeplus.modules.sys.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

@Service
public class NoticeService extends CrudService<NoticeMapper, Notice> {
}
