package com.jeeplus.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.jeeplus.modules.sys.entity.Post;

/**
 * 岗位MAPPER接口
 */
@Mapper
@Repository
public interface PostMapper extends BaseMapper<Post> {

}
