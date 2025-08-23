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

package com.cs.wit.controller.apps;

import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.Dict;
import com.cs.wit.model.KnowledgeType;
import com.cs.wit.model.MetadataTable;
import com.cs.wit.model.SysDic;
import com.cs.wit.model.Topic;
import com.cs.wit.model.TopicItem;
import com.cs.wit.persistence.es.TopicRepository;
import com.cs.wit.persistence.interfaces.DataExchangeInterface;
import com.cs.wit.persistence.repository.AreaTypeRepository;
import com.cs.wit.persistence.repository.KnowledgeTypeRepository;
import com.cs.wit.persistence.repository.MetadataRepository;
import com.cs.wit.persistence.repository.ReporterRepository;
import com.cs.wit.persistence.repository.SysDicRepository;
import com.cs.wit.persistence.repository.TopicItemRepository;
import com.cs.wit.proxy.OnlineUserProxy;
import com.cs.wit.util.Menu;
import com.cs.wit.util.dsdata.DSData;
import com.cs.wit.util.dsdata.DSDataEvent;
import com.cs.wit.util.dsdata.ExcelImportProecess;
import com.cs.wit.util.dsdata.export.ExcelExporterProcess;
import com.cs.wit.util.dsdata.process.TopicProcess;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/apps")
@RequiredArgsConstructor
public class TopicController extends Handler {

    @NonNull
    private final KnowledgeTypeRepository knowledgeTypeRes;
    @NonNull
    private final AreaTypeRepository areaRepository;
    @NonNull
    private final SysDicRepository sysDicRepository;
    @NonNull
    private final TopicRepository topicRes;
    @NonNull
    private final MetadataRepository metadataRes;
    @NonNull
    private final TopicItemRepository topicItemRes;
    @NonNull
    private final ReporterRepository reporterRes;
    @Value("${web.upload-path}")
    private String path;

    @RequestMapping("/topic")
    @Menu(type = "xiaoe", subtype = "knowledge")
    public ModelAndView knowledge(ModelMap map, HttpServletRequest request, @Valid String q, @Valid String type) {
        List<KnowledgeType> knowledgeTypeList = knowledgeTypeRes.findByOrgi(super.getOrgi(request));
        map.put("knowledgeTypeList", knowledgeTypeList);
        KnowledgeType ktype = null;
        if (!StringUtils.isBlank(type)) {
            for (KnowledgeType knowledgeType : knowledgeTypeList) {
                if (knowledgeType.getId().equals(type)) {
                    ktype = knowledgeType;
                    break;
                }
            }
        }
        if (!StringUtils.isBlank(q)) {
            map.put("q", q);
        }
        if (ktype != null) {
            map.put("curtype", ktype);
            map.put("topicList", topicRes.getTopicByCateAndOrgi(ktype.getId(), super.getOrgi(request), q, super.getP(request), super.getPs(request)));
        } else {
            map.put("topicList", topicRes.getTopicByCateAndOrgi(Constants.DEFAULT_TYPE, super.getOrgi(request), q, super.getP(request), super.getPs(request)));
        }
        map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgi(request)));
        return request(super.createAppsTempletResponse("/apps/business/topic/index"));
    }

    @RequestMapping("/topic/add")
    @Menu(type = "xiaoe", subtype = "knowledgeadd")
    public ModelAndView knowledgeadd(ModelMap map, @Valid String type, @Valid String aiid) {
        map.put("type", type);
        map.put("aiid", aiid);
        return request(super.createRequestPageTempletResponse("/apps/business/topic/add"));
    }

    @RequestMapping("/topic/save")
    @Menu(type = "xiaoe", subtype = "knowledgesave")
    public ModelAndView knowledgesave(HttpServletRequest request, @Valid Topic topic, @Valid String type, @Valid String aiid) {
        if (!StringUtils.isBlank(topic.getTitle())) {
            if (!StringUtils.isBlank(type)) {
                topic.setCate(type);
            } else {
                topic.setCate(Constants.DEFAULT_TYPE);
            }
            topic.setOrgi(super.getOrgi(request));
            topicRes.save(topic);
            List<TopicItem> topicItemList = new ArrayList<>();
            for (String item : topic.getSilimar()) {
                TopicItem topicItem = new TopicItem();
                topicItem.setTitle(item);
                topicItem.setTopicid(topic.getId());
                topicItem.setOrgi(topic.getOrgi());
                topicItem.setCreater(topic.getCreater());
                topicItem.setCreatetime(new Date());
                topicItemList.add(topicItem);
            }
            if (topicItemList.size() > 0) {
                topicItemRes.saveAll(topicItemList);
            }
            /*
             * 重新缓存
             */
            DataExchangeInterface dataExchangeInterface = (DataExchangeInterface) MainContext.getContext().getBean("topic");
            OnlineUserProxy.resetHotTopic(dataExchangeInterface, super.getOrgi(request), aiid);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/topic" + (!StringUtils.isBlank(type) ? "?type=" + type : "")));
    }

    @RequestMapping("/topic/edit")
    @Menu(type = "xiaoe", subtype = "knowledgeedit")
    public ModelAndView knowledgeedit(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String type) {
        map.put("type", type);
        if (!StringUtils.isBlank(id)) {
            map.put("topic", topicRes.findById(id).orElse(null));
        }
        List<KnowledgeType> knowledgeTypeList = knowledgeTypeRes.findByOrgi(super.getOrgi(request));
        map.put("knowledgeTypeList", knowledgeTypeList);
        return request(super.createRequestPageTempletResponse("/apps/business/topic/edit"));
    }

    @RequestMapping("/topic/update")
    @Menu(type = "xiaoe", subtype = "knowledgeupdate")
    public ModelAndView knowledgeupdate(HttpServletRequest request, @Valid Topic topic, @Valid String type, @Valid String aiid) {
        if (!StringUtils.isBlank(topic.getTitle())) {
            if (!StringUtils.isBlank(type)) {
                topic.setCate(type);
            } else {
                topic.setCate(Constants.DEFAULT_TYPE);
            }
            topicRes.findById(topic.getId())
                    .ifPresent(temp -> {
                        topic.setCreater(temp.getCreater());
                        topic.setCreatetime(temp.getCreatetime());
                    });
            topic.setOrgi(super.getOrgi(request));

            topicRes.save(topic);
            topicItemRes.deleteAll(topicItemRes.findByTopicid(topic.getId()));
            List<TopicItem> topicItemList = new ArrayList<>();
            for (String item : topic.getSilimar()) {
                TopicItem topicItem = new TopicItem();
                topicItem.setTitle(item);
                topicItem.setTopicid(topic.getId());
                topicItem.setOrgi(topic.getOrgi());
                topicItem.setCreater(topic.getCreater());
                topicItem.setCreatetime(new Date());
                topicItemList.add(topicItem);
            }
            if (topicItemList.size() > 0) {
                topicItemRes.saveAll(topicItemList);
            }

            /*
             * 重新缓存
             */
            DataExchangeInterface dataExchangeInterface = (DataExchangeInterface) MainContext.getContext().getBean("topic");
            OnlineUserProxy.resetHotTopic(dataExchangeInterface, super.getOrgi(request), aiid);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/topic" + (!StringUtils.isBlank(type) ? "?type=" + type : "")));
    }

    @RequestMapping("/topic/delete")
    @Menu(type = "xiaoe", subtype = "knowledgedelete")
    public ModelAndView knowledgedelete(HttpServletRequest request, @Valid String id, @Valid String type, @Valid String aiid) {
        if (!StringUtils.isBlank(id)) {
            topicRes.deleteById(id);
            /*
             * 重新缓存
             */
            topicItemRes.deleteAll(topicItemRes.findByTopicid(id));

            DataExchangeInterface dataExchangeInterface = (DataExchangeInterface) MainContext.getContext().getBean("topic");
            OnlineUserProxy.resetHotTopic(dataExchangeInterface, super.getOrgi(request), aiid);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/topic" + (!StringUtils.isBlank(type) ? "?type=" + type : "")));
    }

    @RequestMapping("/topic/type/add")
    @Menu(type = "xiaoe", subtype = "knowledgetypeadd")
    public ModelAndView knowledgetypeadd(ModelMap map, HttpServletRequest request, @Valid String type, @Valid String aiid) {
        map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgi(request)));

        List<KnowledgeType> knowledgeTypeList = knowledgeTypeRes.findByOrgi(super.getOrgi(request));
        map.put("knowledgeTypeList", knowledgeTypeList);
        map.put("aiid", aiid);
        if (!StringUtils.isBlank(type)) {
            map.put("type", type);
        }

        return request(super.createRequestPageTempletResponse("/apps/business/topic/addtype"));
    }

    @RequestMapping("/topic/type/save")
    @Menu(type = "xiaoe", subtype = "knowledgetypesave")
    public ModelAndView knowledgetypesave(HttpServletRequest request, @Valid KnowledgeType type, @Valid String aiid) {
        //int tempTypeCount = knowledgeTypeRes.countByNameAndOrgiAndParentidNot(type.getName(), super.getOrgi(request) , !StringUtils.isBlank(type.getParentid()) ? type.getParentid() : "0") ;
        KnowledgeType knowledgeType = knowledgeTypeRes.findByNameAndOrgi(type.getName(), super.getOrgi(request));
        if (knowledgeType == null) {
            type.setOrgi(super.getOrgi(request));
            type.setCreatetime(new Date());
            type.setId(MainUtils.getUUID());
            type.setTypeid(type.getId());
            type.setUpdatetime(new Date());
            if (StringUtils.isBlank(type.getParentid())) {
                type.setParentid("0");
            } else {
                type.setTypeid(type.getParentid());
            }
            type.setCreater(super.getUser(request).getId());
            knowledgeTypeRes.save(type);
            DataExchangeInterface dataExchangeInterface = (DataExchangeInterface) MainContext.getContext().getBean("topictype");
            OnlineUserProxy.resetHotTopicType(dataExchangeInterface, super.getOrgi(request), aiid);
        } else {
            return request(super.createRequestPageTempletResponse("redirect:/apps/topic?aiid=" + aiid + "&msg=k_type_exist"));
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/topic?aiid=" + aiid));
    }

    @RequestMapping("/topic/type/edit")
    @Menu(type = "xiaoe", subtype = "knowledgetypeedit")
    public ModelAndView knowledgetypeedit(ModelMap map, HttpServletRequest request, @Valid String type, @Valid String aiid) {
        map.put("knowledgeType", knowledgeTypeRes.findById(type).orElse(null));
        map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgi(request)));

        map.put("aiid", aiid);

        List<KnowledgeType> knowledgeTypeList = knowledgeTypeRes.findByOrgi(super.getOrgi(request));
        map.put("knowledgeTypeList", knowledgeTypeList);
        return request(super.createRequestPageTempletResponse("/apps/business/topic/edittype"));
    }

    @RequestMapping("/topic/type/update")
    @Menu(type = "xiaoe", subtype = "knowledgetypeupdate")
    public ModelAndView knowledgetypeupdate(HttpServletRequest request, @Valid KnowledgeType type, @Valid String aiid) {
        //int tempTypeCount = knowledgeTypeRes.countByNameAndOrgiAndIdNot(type.getName(), super.getOrgi(request) , type.getId()) ;
        KnowledgeType knowledgeType = knowledgeTypeRes.findByNameAndOrgiAndIdNot(type.getName(), super.getOrgi(request), type.getId());
        if (knowledgeType == null) {
            KnowledgeType temp = knowledgeTypeRes.findByIdAndOrgi(type.getId(), super.getOrgi(request));
            temp.setName(type.getName());
            temp.setParentid(type.getParentid());
            if (StringUtils.isBlank(type.getParentid()) || type.getParentid().equals("0")) {
                temp.setParentid("0");
                temp.setTypeid(temp.getId());
            } else {
                temp.setParentid(type.getParentid());
                temp.setTypeid(type.getParentid());
            }
            knowledgeTypeRes.save(temp);
            DataExchangeInterface dataExchangeInterface = (DataExchangeInterface) MainContext.getContext().getBean("topictype");
            OnlineUserProxy.resetHotTopicType(dataExchangeInterface, super.getOrgi(request), aiid);
        } else {
            return request(super.createRequestPageTempletResponse("redirect:/apps/topic?aiid=" + aiid + "&msg=k_type_exist&type=" + type.getId()));
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/topic?aiid=" + aiid + "type=" + type.getId()));
    }

    @RequestMapping("/topic/type/delete")
    @Menu(type = "xiaoe", subtype = "knowledgedelete")
    public ModelAndView knowledgetypedelete(HttpServletRequest request, @Valid String id, @Valid String type, @Valid String aiid) {
        Page<Topic> page = topicRes.getTopicByCateAndOrgi(type, super.getOrgi(request), null, super.getP(request), super.getPs(request));
        String msg = null;
        if (page.getTotalElements() == 0) {
            if (!StringUtils.isBlank(id)) {
                knowledgeTypeRes.deleteById(id);
                OnlineUserProxy.resetHotTopicType((DataExchangeInterface) MainContext.getContext().getBean("topictype"), super.getOrgi(request), aiid);
            }
        } else {
            msg = "notempty";
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/topic" + (msg != null ? "?msg=notempty" : "")));
    }

    @RequestMapping("/topic/area")
    @Menu(type = "admin", subtype = "area")
    public ModelAndView area(ModelMap map, @Valid String id) {

        SysDic sysDic = sysDicRepository.findByCode(Constants.CSKEFU_SYSTEM_AREA_DIC);
        if (sysDic != null) {
            map.addAttribute("sysarea", sysDic);
            map.addAttribute("areaList", sysDicRepository.findByDicid(sysDic.getId()));
        }
        map.addAttribute("cacheList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_AREA_DIC));

        map.put("knowledgeType", knowledgeTypeRes.findById(id).orElse(null));
        return request(super.createRequestPageTempletResponse("/apps/business/topic/area"));
    }


    @RequestMapping("/topic/area/update")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView areaupdate(HttpServletRequest request, @Valid KnowledgeType type, @Valid String aiid) {
        KnowledgeType temp = knowledgeTypeRes.findByIdAndOrgi(type.getId(), super.getOrgi(request));
        if (temp != null) {
            temp.setArea(type.getArea());
            knowledgeTypeRes.save(temp);
            OnlineUserProxy.resetHotTopicType((DataExchangeInterface) MainContext.getContext().getBean("topictype"), super.getOrgi(request), aiid);
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/topic?type=" + type.getId()));
    }


    @RequestMapping("/topic/imp")
    @Menu(type = "xiaoe", subtype = "knowledge")
    public ModelAndView imp(ModelMap map, @Valid String type) {
        map.addAttribute("type", type);
        return request(super.createRequestPageTempletResponse("/apps/business/topic/imp"));
    }

    @RequestMapping("/topic/impsave")
    @Menu(type = "xiaoe", subtype = "knowledge")
    public ModelAndView impsave(HttpServletRequest request, @RequestParam(value = "cusfile", required = false) MultipartFile cusfile, @Valid String type) throws IOException {
        DSDataEvent event = new DSDataEvent();
        String originalFilename = Objects.requireNonNull(cusfile.getOriginalFilename());
        String fileName = "xiaoe/" + MainUtils.getUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        File excelFile = new File(path, fileName);
        if (!excelFile.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            excelFile.getParentFile().mkdirs();
        }
        MetadataTable table = metadataRes.findByTablename("uk_xiaoe_topic");
        if (table != null) {
            FileUtils.writeByteArrayToFile(new File(path, fileName), cusfile.getBytes());
            event.setDSData(new DSData(table, excelFile, cusfile.getContentType(), super.getUser(request)));
            event.getDSData().setClazz(Topic.class);
            event.setOrgi(super.getOrgi(request));
            if (!StringUtils.isBlank(type)) {
                event.getValues().put("cate", type);
            } else {
                event.getValues().put("cate", Constants.DEFAULT_TYPE);
            }
            event.getValues().put("creater", super.getUser(request).getId());
            event.getDSData().setProcess(new TopicProcess(topicRes));
            reporterRes.save(event.getDSData().getReport());
            //启动导入任务
            new ExcelImportProecess(event).process();
        }

        return request(super.createRequestPageTempletResponse("redirect:/apps/topic?type=" + type));
    }

    @RequestMapping("/topic/batdelete")
    @Menu(type = "xiaoe", subtype = "knowledge")
    public ModelAndView batdelete(@Valid String[] ids, @Valid String type) {
        if (ids != null && ids.length > 0) {
            Iterable<Topic> topicList = topicRes.findAllById(Arrays.asList(ids));
            topicRes.deleteAll(topicList);
            for (Topic topic : topicList) {
                topicItemRes.deleteAll(topicItemRes.findByTopicid(topic.getId()));
            }
        }

        return request(super.createRequestPageTempletResponse("redirect:/apps/topic" + (!StringUtils.isBlank(type) ? "?type=" + type : "")));
    }

    @RequestMapping("/topic/expids")
    @Menu(type = "xiaoe", subtype = "knowledge")
    public void expids(HttpServletResponse response, @Valid String[] ids) throws IOException {
        if (ids != null && ids.length > 0) {
            Iterable<Topic> topicList = topicRes.findAllById(Arrays.asList(ids));
            MetadataTable table = metadataRes.findByTablename("uk_xiaoe_topic");
            List<Map<String, Object>> values = new ArrayList<>();
            for (Topic topic : topicList) {
                values.add(MainUtils.transBean2Map(topic));
            }

            response.setHeader("content-disposition", "attachment;filename=CSKefu-Contacts-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");
            if (table != null) {
                ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
                excelProcess.process();
            }
        }

    }

    @RequestMapping("/topic/expall")
    @Menu(type = "xiaoe", subtype = "knowledge")
    public void expall(HttpServletRequest request, HttpServletResponse response, @Valid String type) throws IOException {
        Iterable<Topic> topicList = topicRes.getTopicByOrgi(super.getOrgi(request), type, null);

        MetadataTable table = metadataRes.findByTablename("uk_xiaoe_topic");
        List<Map<String, Object>> values = new ArrayList<>();
        for (Topic topic : topicList) {
            values.add(MainUtils.transBean2Map(topic));
        }

        response.setHeader("content-disposition", "attachment;filename=UCKeFu-XiaoE-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

        if (table != null) {
            ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
            excelProcess.process();
        }
    }

    @RequestMapping("/topic/expsearch")
    @Menu(type = "xiaoe", subtype = "knowledge")
    public void expall(HttpServletRequest request, HttpServletResponse response, @Valid String q, @Valid String type) throws IOException {

        Iterable<Topic> topicList = topicRes.getTopicByOrgi(super.getOrgi(request), type, q);

        MetadataTable table = metadataRes.findByTablename("uk_xiaoe_topic");
        List<Map<String, Object>> values = new ArrayList<>();
        for (Topic topic : topicList) {
            values.add(MainUtils.transBean2Map(topic));
        }

        response.setHeader("content-disposition", "attachment;filename=UCKeFu-XiaoE-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

        if (table != null) {
            ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
            excelProcess.process();
        }
    }
}
