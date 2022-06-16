/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.sys.entity.DictValue;
import com.jeeplus.modules.sys.vo.DictValueVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 数据字典MAPPER接口
 * @author FEAR
 */
@Mapper
@Repository
public interface DictValueMapper extends BaseMapper<DictValue> {

    List<DictValue> getDictValueByType();

    List<DictValueVO> getDictValueList(@Param("types") List<String> types);

}
