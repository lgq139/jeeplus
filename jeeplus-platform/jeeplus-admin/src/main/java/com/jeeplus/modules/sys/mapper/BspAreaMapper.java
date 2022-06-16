package com.jeeplus.modules.sys.mapper;

import com.jeeplus.core.ext.persistence.Node;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Deprecated
@Mapper
@Repository
public interface BspAreaMapper {

    @Select("SELECT code AS id, " +
            "NVL((PRIOR code), '#') AS parent, " +
            "name AS text " +
            "FROM pub_region " +
            "where type = ${type} and grade < ${grade} " +
            "START WITH RPAD(CODE, 12, '0') = ${newRegionCode} " +
            "CONNECT BY PRIOR code = parent_code ")
    List<Node> selectRegion(@Param("type") String type, @Param("grade") int grade, @Param("newRegionCode") String newRegionCode);

}
