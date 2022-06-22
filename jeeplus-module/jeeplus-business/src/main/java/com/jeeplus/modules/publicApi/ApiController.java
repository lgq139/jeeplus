package com.jeeplus.modules.publicApi;

import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.DictValue;
import com.jeeplus.modules.sys.utils.DictUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "公共接口")
@RestController
@RequestMapping(value = "/api")
public class ApiController extends BaseController {

    @GetMapping(value = "getDictdDirectories")
    @ApiOperation(value = "词典查询",notes = "根据词典类型查询词典项")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "dictType", value = "词典类型", required = true, dataType = "String" )
    })
    public AjaxJson getDictdDirectories(@RequestParam(value = "dictType") String dictType){
        AjaxJson j = new AjaxJson();
        List<DictValue> list = DictUtils.getDictList(dictType);
        Map<Object,String> map =new HashMap<>();
        if (list.size() > 0) {
            list.forEach(dictValue -> {
                map.put(dictValue.getValue(),dictValue.getLabel());
            });
        }
        j.setSuccess(true);
        j.put("options", list);
        j.put("keyAndValue", map);
        j.setMsg("成功");
        return j;
    }

}
