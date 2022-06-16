package com.jeeplus.modules.sys.mapper;

import com.jeeplus.modules.sys.entity.Office;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BspOfficeMapper {

    @Select("SELECT code AS id, name as name " +
            "FROM pub_organ " +
            "WHERE region_code = ${regionCode} " +
            "AND status = '1' ")
    List<Office> selectOffice(@Param("regionCode") String regionCode);

    // field_name like '%'||#{field_name}||'%'
    @Select("SELECT code AS id, name as name " +
            "FROM pub_organ " +
            "WHERE region_code = ${regionCode} " +
            "AND status = '1' " +
            "AND name LIKE '%' || '${orgName}' || '%'")
    List<Office> selectOfficeByQuery(@Param("regionCode") String regionCode, @Param("orgName") String orgName);
}
