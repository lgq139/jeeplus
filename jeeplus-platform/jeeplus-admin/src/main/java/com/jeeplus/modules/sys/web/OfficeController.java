package com.jeeplus.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.config.properties.JeePlusProperites;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.service.OfficeService;
import com.jeeplus.modules.sys.utils.DictUtils;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/office")
public class OfficeController extends BaseController {

    @Autowired
    private OfficeService officeService;

    @Autowired
    private JeePlusProperites jeePlusProperites;

    @ModelAttribute("office")
    public Office get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return officeService.get(id);
        } else {
            return new Office();
        }
    }

    @GetMapping(value = "list")
    public AjaxJson list(Office office, Model model) {
        if (office == null || office.getParentIds() == null) {
            return AjaxJson.success().put("list", officeService.findList(false));
        } else {
            return AjaxJson.success().put("list", officeService.findList(office));
        }
    }

    @RequiresPermissions(value = {"sys:office:view", "sys:office:add", "sys:office:edit"}, logical = Logical.OR)
    @GetMapping("queryById")
    public AjaxJson queryById(Office office) {
        User user = UserUtils.getUser();
        if (office.getParent() == null || office.getParent().getId() == null) {
            office.setParent(user.getOffice());
        }
        office.setParent(officeService.get(office.getParent().getId()));
        // ?????????????????????
        if (StringUtils.isBlank(office.getId()) && office.getParent() != null) {
            int size = 0;
            List<Office> list = officeService.findAll();
            for (int i = 0; i < list.size(); i++) {
                Office e = list.get(i);
                if (e.getParent() != null && e.getParent().getId() != null
                        && e.getParent().getId().equals(office.getParent().getId())) {
                    size++;
                }
            }
            office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size + 1 : 1), 3, "0"));
        }
        return AjaxJson.success().put("office", office);
    }

    @RequiresPermissions(value = {"sys:office:add", "sys:office:edit"}, logical = Logical.OR)
    @PostMapping("save")
    public AjaxJson save(Office office, Model model) {
        if (jeePlusProperites.isDemoMode()) {
            return AjaxJson.error("?????????????????????????????????");
        }
        /**
         * ??????hibernate-validation????????????
         */
        String errMsg = beanValidator(office);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }
        officeService.save(office);

        if (office.getChildDeptList() != null) {
            Office childOffice = null;
            for (String id : office.getChildDeptList()) {
                childOffice = new Office();
                childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "??????"));
                childOffice.setParent(office);
                childOffice.setType("2");
                childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade()) + 1));
                childOffice.setUseable(JeePlusProperites.YES);
                officeService.save(childOffice);
            }
        }
        return AjaxJson.success("????????????'" + office.getName() + "'??????");
    }

    @RequiresPermissions("sys:office:del")
    @DeleteMapping("delete")
    public AjaxJson delete(Office office) {
        if (jeePlusProperites.isDemoMode()) {
            return AjaxJson.error("?????????????????????????????????");
        }
        officeService.delete(office);
        return AjaxJson.success("???????????????");
    }


    /**
     * ????????????JSON?????????
     *
     * @param extId ?????????ID
     * @param type  ?????????1????????????2?????????/??????/?????????
     * @return
     */
    @RequiresPermissions("user")
    @GetMapping("treeData")
    public AjaxJson treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String type) {
        List<Office> list = officeService.findList(false);
        List<Office> rootTree = getRootTree(list, extId, type);
        return AjaxJson.success().put("treeData", rootTree);
    }

    private List<Office> getRootTree(List<Office> list, String extId, String type) {
        List<Office> offices = Lists.newArrayList();
        List<Office> rootTrees = officeService.getChildren("0");
        for (Office root : rootTrees) {
            if ((StringUtils.isBlank(extId) || (extId != null && !extId.equals(root.getId()) && root.getParentIds().indexOf("," + extId + ",") == -1))
                    && (type == null || (type != null && (type.equals("1") ? type.equals(root.getType()) : true)))
                    && JeePlusProperites.YES.equals(root.getUseable())) {
                // ?????????????????????????????????
                List<Office> officeList = formatListToTree(root, list, extId, type);
                offices.addAll(officeList);
            }
        }
        return offices;
    }


    public List<Office> formatListToTree(Office root, List<Office> allList, String extId, String type) {
        String rootId = root.getId();

        // type???2????????????????????????????????????type???1???????????????
        if ("2".equals(type) && root.getType().equals("1")) {
            root.setDisabled(true);
        } else {
            root.setDisabled(false);
        }
        // ??????????????????
        List<Office> trees = Lists.newArrayList();

        // ?????????????????????????????????, ????????????id??????key, ???????????????
        Map<String, List<Office>> treeMap = Maps.newHashMap();
        for (Office entity : allList) {
            List<Office> offices = treeMap.get(entity.getParent().getId());
            if (offices == null) {
                offices = Lists.newLinkedList();
            }

            if ((StringUtils.isBlank(extId) || (extId != null && !extId.equals(entity.getId()) && entity.getParentIds().indexOf("," + extId + ",") == -1))
                    && (type == null || (type != null && (type.equals("1") ? type.equals(entity.getType()) : true)))
                    && JeePlusProperites.YES.equals(entity.getUseable())) {
                // type???2????????????????????????????????????type???1???????????????
                if ("2".equals(type) && entity.getType().equals("1")) {
                    entity.setDisabled(true);
                } else {
                    entity.setDisabled(false);
                }
                offices.add(entity);
                treeMap.put(entity.getParent().getId(), offices);
            }
        }

        // ?????????????????????
        List<Office> children = treeMap.get(rootId);
        if (children != null) {
            for (Office parent : children) {
                formatFillChildren(parent, treeMap);
                trees.add(parent);
            }
        }

        root.setChildren(trees);
        return Lists.newArrayList(root);
    }

    /**
     * ???treeMap????????????????????????parent, ??????????????????
     **/
    private void formatFillChildren(Office parent, Map<String, List<Office>> treeMap) {
        List<Office> children = treeMap.get(parent.getId());
        parent.setChildren(children);
        if (children != null && !children.isEmpty()) {
            for (Office child : children) {
                formatFillChildren(child, treeMap);
            }
        }
    }


}
