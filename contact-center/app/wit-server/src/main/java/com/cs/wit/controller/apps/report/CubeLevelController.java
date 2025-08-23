/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cs.wit.controller.apps.report;

import com.cs.wit.controller.Handler;
import com.cs.wit.model.CubeLevel;
import com.cs.wit.model.CubeMetadata;
import com.cs.wit.model.Dimension;
import com.cs.wit.model.TableProperties;
import com.cs.wit.persistence.repository.CubeLevelRepository;
import com.cs.wit.persistence.repository.CubeMetadataRepository;
import com.cs.wit.persistence.repository.DimensionRepository;
import com.cs.wit.persistence.repository.TablePropertiesRepository;
import com.cs.wit.util.Menu;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/apps/report/cubelevel")
@RequiredArgsConstructor
public class CubeLevelController extends Handler {

    @NonNull
    private final CubeLevelRepository cubeLevelRes;

    @NonNull
    private final DimensionRepository dimensionRes;

    @NonNull
    private final TablePropertiesRepository tablePropertiesRes;

    @NonNull
    private final CubeMetadataRepository cubeMetadataRes;

    @RequestMapping("/add")
    @Menu(type = "report", subtype = "cubelevel")
    public ModelAndView cubeLeveladd(ModelMap map, HttpServletRequest request, @Valid String cubeid, @Valid String dimid) {
        map.addAttribute("cubeid", cubeid);
        map.addAttribute("dimid", dimid);
        //map.addAttribute("fktableList",cubeMetadataRes.findByCubeid(cubeid));
        Dimension dim = dimensionRes.findByIdAndOrgi(dimid, super.getOrgi(request));

        if (dim != null) {
            if (!StringUtils.isBlank(dim.getFktable())) {
                map.put("fktableidList", tablePropertiesRes.findByDbtableid(dim.getFktable()));
            } else {
                List<CubeMetadata> cmList = cubeMetadataRes.findByCubeidAndMtype(cubeid, "0");
                if (!cmList.isEmpty() && cmList.get(0) != null) {
                    map.put("fktableidList", tablePropertiesRes.findByDbtableid(cmList.get(0).getTb().getId()));
                }
            }

        }
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/cubelevel/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "report", subtype = "cubelevel")
    public ModelAndView cubeLevelsave(HttpServletRequest request, @Valid CubeLevel cubeLevel, @Valid String tableid) {
        if (!StringUtils.isBlank(cubeLevel.getName())) {
            cubeLevel.setOrgi(super.getOrgi(request));
            cubeLevel.setCreater(super.getUser(request).getId());
            cubeLevel.setCode(cubeLevel.getColumname());
            if (!StringUtils.isBlank(tableid)) {
                TableProperties tb = new TableProperties();
                tb.setId(tableid);
                TableProperties t = tablePropertiesRes.findById(tableid)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Table properties %s not found", tableid)));
                cubeLevel.setTablename(t.getTablename());
                cubeLevel.setCode(t.getFieldname());
                cubeLevel.setColumname(t.getFieldname());
                cubeLevel.setTableproperty(tb);
            }
            cubeLevelRes.save(cubeLevel);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail?id=" + cubeLevel.getCubeid() + "&dimensionId=" + cubeLevel.getDimid()));
    }

    @RequestMapping("/delete")
    @Menu(type = "report", subtype = "cubelevel")
    public ModelAndView quickreplydelete(@Valid String id) {
        CubeLevel cubeLevel = cubeLevelRes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cube level %s not found", id)));
        cubeLevelRes.delete(cubeLevel);
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail?id=" + cubeLevel.getCubeid() + "&dimensionId=" + cubeLevel.getDimid()));
    }

    @RequestMapping("/edit")
    @Menu(type = "report", subtype = "cubelevel", admin = true)
    public ModelAndView quickreplyedit(ModelMap map, HttpServletRequest request, @Valid String id) {
        CubeLevel cubeLevel = cubeLevelRes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cube level %s not found", id)));
        map.put("cubeLevel", cubeLevel);
        Dimension dim = dimensionRes.findByIdAndOrgi(cubeLevel.getDimid(), super.getOrgi(request));
        if (dim != null) {
            if (!StringUtils.isBlank(dim.getFktable())) {
                map.put("fktableidList", tablePropertiesRes.findByDbtableid(dim.getFktable()));
                map.addAttribute("tableid", dim.getFktable());
            } else {
                List<CubeMetadata> cmList = cubeMetadataRes.findByCubeidAndMtype(cubeLevel.getCubeid(), "0");
                if (!cmList.isEmpty() && cmList.get(0) != null) {
                    map.put("fktableidList", tablePropertiesRes.findByDbtableid(cmList.get(0).getTb().getId()));
                    map.addAttribute("tableid", cmList.get(0).getId());
                }
            }

        }
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/cubelevel/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "report", subtype = "cubelevel", admin = true)
    public ModelAndView quickreplyupdate(HttpServletRequest request, @Valid CubeLevel cubeLevel, @Valid String tableid) {
        if (!StringUtils.isBlank(cubeLevel.getId())) {
            cubeLevel.setOrgi(super.getOrgi(request));
            cubeLevel.setCreater(super.getUser(request).getId());
            cubeLevelRes.findById(cubeLevel.getId())
                    .ifPresent(it -> cubeLevel.setCreatetime(it.getCreatetime()));
            if (!StringUtils.isBlank(tableid)) {
                TableProperties tb = new TableProperties();
                tb.setId(tableid);
                TableProperties t = tablePropertiesRes.findById(tableid)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Table properties %s not found", tableid)));
                cubeLevel.setTablename(t.getTablename());
                cubeLevel.setCode(t.getFieldname());
                cubeLevel.setColumname(t.getFieldname());
                cubeLevel.setTableproperty(tb);
            }
            cubeLevelRes.save(cubeLevel);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/report/cube/detail?id=" + cubeLevel.getCubeid() + "&dimensionId=" + cubeLevel.getDimid()));
    }

    @RequestMapping("/fktableid")
    @Menu(type = "report", subtype = "cubelevel", admin = true)
    public ModelAndView fktableid(ModelMap map, @Valid String tableid) {
        if (!StringUtils.isBlank(tableid)) {
            map.put("fktableidList", tablePropertiesRes.findByDbtableid(tableid));
        }
        return request(super.createRequestPageTempletResponse("/apps/business/report/cube/cubelevel/fktableiddiv"));
    }
}
