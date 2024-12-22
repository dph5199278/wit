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

import com.cs.wit.basic.MainUtils;
import com.cs.wit.controller.Handler;
import com.cs.wit.exception.CSKefuException;
import com.cs.wit.model.Contacts;
import com.cs.wit.model.MetadataTable;
import com.cs.wit.model.PropertiesEvent;
import com.cs.wit.model.User;
import com.cs.wit.persistence.es.ContactsRepository;
import com.cs.wit.persistence.repository.AgentUserContactsRepository;
import com.cs.wit.persistence.repository.MetadataRepository;
import com.cs.wit.persistence.repository.OrganRepository;
import com.cs.wit.persistence.repository.PropertiesEventRepository;
import com.cs.wit.persistence.repository.ReporterRepository;
import com.cs.wit.proxy.ContactsProxy;
import com.cs.wit.util.Menu;
import com.cs.wit.util.PinYinTools;
import com.cs.wit.util.PropertiesEventUtil;
import com.cs.wit.util.dsdata.DSData;
import com.cs.wit.util.dsdata.DSDataEvent;
import com.cs.wit.util.dsdata.ExcelImportProecess;
import com.cs.wit.util.dsdata.export.ExcelExporterProcess;
import com.cs.wit.util.dsdata.process.ContactsProcess;
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
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/apps/contacts")
public class ContactsController extends Handler {

    private final static Logger logger = LoggerFactory.getLogger(ContactsController.class);

    @NonNull
    private final ContactsRepository contactsRes;

    @NonNull
    private final PropertiesEventRepository propertiesEventRes;

    @NonNull
    private final ReporterRepository reporterRes;

    @NonNull
    private final MetadataRepository metadataRes;

    @NonNull
    private final ContactsProxy contactsProxy;

    @NonNull
    private final OrganRepository organRes;

    @NonNull
    private final AgentUserContactsRepository agentUserContactsRes;

    @Value("${web.upload-path}")
    private String path;

    @RequestMapping("/index")
    @Menu(type = "customer", subtype = "index")
    public ModelAndView index(
            ModelMap map,
            HttpServletRequest request,
            @Valid String q,
            @Valid String ckind
    ) throws CSKefuException {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

        if (!super.esOrganFilter(request)) {
            return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
        }

        if (StringUtils.isNotBlank(q)) {
            map.put("q", q);
        }


        if (StringUtils.isNotBlank(ckind)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("ckind").value(ckind)));
            map.put("ckind", ckind);
        }

        Page<Contacts> contacts = contactsRes.findByCreaterAndSharesAndOrgi(
                logined.getId(),
                logined.getId(),
                orgi,
                null,
                null,
                false,
                boolQueryBuilder,
                q,
                PageRequest.of(super.getP(request), super.getPs(request)));

        map.addAttribute("contactsList", contacts);

        contactsProxy.bindContactsApproachableData(contacts, map, logined);

        return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
    }

    @RequestMapping("/today")
    @Menu(type = "customer", subtype = "today")
    public ModelAndView today(ModelMap map, HttpServletRequest request, @Valid String q, @Valid String ckind) throws CSKefuException {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        if (!super.esOrganFilter(request)) {
            return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
        }

        if (StringUtils.isNotBlank(q)) {
            map.put("q", q);
        }
        if (StringUtils.isNotBlank(ckind)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("ckind").value(ckind)));
            map.put("ckind", ckind);
        }

        Page<Contacts> contacts = contactsRes.findByCreaterAndSharesAndOrgi(logined.getId(),
                logined.getId(),
                orgi,
                MainUtils.getStartTime(), null, false,
                boolQueryBuilder, q,
                PageRequest.of(
                        super.getP(request),
                        super.getPs(request)));

        map.addAttribute(
                "contactsList", contacts);

        contactsProxy.bindContactsApproachableData(contacts, map, logined);

        return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
    }

    @RequestMapping("/week")
    @Menu(type = "customer", subtype = "week")
    public ModelAndView week(ModelMap map, HttpServletRequest request, @Valid String q, @Valid String ckind) throws CSKefuException {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        if (!super.esOrganFilter(request)) {
            return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
        }

        if (StringUtils.isNotBlank(q)) {
            map.put("q", q);
        }
        if (StringUtils.isNotBlank(ckind)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("ckind").value(ckind)));
            map.put("ckind", ckind);
        }

        Page<Contacts> contacts = contactsRes.findByCreaterAndSharesAndOrgi(logined.getId(),
                logined.getId(),
                orgi,
                MainUtils.getWeekStartTime(), null, false,
                boolQueryBuilder, q,
                PageRequest.of(
                        super.getP(request),
                        super.getPs(request)));
        map.addAttribute(
                "contactsList", contacts);
        contactsProxy.bindContactsApproachableData(contacts, map, logined);


        return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
    }

    @RequestMapping("/creater")
    @Menu(type = "customer", subtype = "creater")
    public ModelAndView creater(ModelMap map, HttpServletRequest request, @Valid String q, @Valid String ckind) throws CSKefuException {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        if (!super.esOrganFilter(request)) {
            return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
        }

        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("creater").value(logined.getId())));

        if (StringUtils.isNotBlank(ckind)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("ckind").value(ckind)));
            map.put("ckind", ckind);
        }
        if (StringUtils.isNotBlank(q)) {
            map.put("q", q);
        }

        Page<Contacts> contacts = contactsRes.findByCreaterAndSharesAndOrgi(logined.getId(),
                logined.getId(),
                orgi, null, null, false,
                boolQueryBuilder, q,
                PageRequest.of(super.getP(request), super.getPs(request)));

        map.addAttribute(
                "contactsList", contacts);
        contactsProxy.bindContactsApproachableData(contacts, map, logined);
        return request(super.createAppsTempletResponse("/apps/business/contacts/index"));
    }

    @RequestMapping("/delete")
    @Menu(type = "contacts", subtype = "contacts")
    public ModelAndView delete(@Valid Contacts contacts, @Valid String p, @Valid String ckind) {
        if (contacts != null) {
            String contactsId = contacts.getId();
            contacts = contactsRes.findById(contactsId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Contacts %s not found", contactsId)));
            //客户和联系人都是 逻辑删除
            contacts.setDatastatus(true);
            contactsRes.save(contacts);
        }
        return request(super.createRequestPageTempletResponse(
                "redirect:/apps/contacts/index?p=" + p + "&ckind=" + ckind));
    }

    @RequestMapping("/add")
    @Menu(type = "contacts", subtype = "add")
    public ModelAndView add(ModelMap map, @Valid String ckind) {
        map.addAttribute("ckind", ckind);
        return request(super.createRequestPageTempletResponse("/apps/business/contacts/add"));
    }


    @RequestMapping("/save")
    @Menu(type = "contacts", subtype = "save")
    public ModelAndView save(
            HttpServletRequest request,
            @Valid Contacts contacts,
            @RequestParam(name = "savefrom", required = false) String savefrom,
            @RequestParam(name = "idselflocation", required = false) String selflocation) {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        String skypeIDReplace = contactsProxy.sanitizeSkypeId(contacts.getSkypeid());
        String msg;
        List<Contacts> contact = contactsRes.findByskypeidAndDatastatus(skypeIDReplace, false);

        // 添加数据
        if (contacts.getSkypeid() != null && contact.size() == 0) {
            logger.info("[save] 数据库没有相同skypeid");
            contacts.setCreater(logined.getId());
            contacts.setOrgi(orgi);
            contacts.setPinyin(PinYinTools.getInstance().getFirstPinYin(contacts.getName()));
            if (StringUtils.isBlank(contacts.getCusbirthday())) {
                contacts.setCusbirthday(null);
            }
            contactsRes.save(contacts);
            msg = "new_contacts_success";

            return request(super.createRequestPageTempletResponse(
                    "redirect:/apps/contacts/index?ckind=" + contacts.getCkind() + "&msg=" + msg));
        }
        msg = "new_contacts_fail";
        return request(super.createRequestPageTempletResponse(
                "redirect:/apps/contacts/index?ckind=" + contacts.getCkind() + "&msg=" + msg));
    }

    @RequestMapping("/edit")
    @Menu(type = "contacts", subtype = "contacts")
    public ModelAndView edit(ModelMap map, @Valid String id, @Valid String ckind) {
        map.addAttribute("contacts", contactsRes.findById(id).orElse(null));
        map.addAttribute("ckindId", ckind);
        return request(super.createRequestPageTempletResponse("/apps/business/contacts/edit"));
    }

    @RequestMapping("/detail")
    @Menu(type = "customer", subtype = "index")
    public ModelAndView detail(ModelMap map, @Valid String id) {
        if (id == null) {
            // id is required. Block strange requst anyway with g2.min, https://github.com/alibaba/BizCharts/issues/143
            return null;
        }
        map.addAttribute("contacts", contactsRes.findById(id).orElse(null));
        return request(super.createAppsTempletResponse("/apps/business/contacts/detail"));

    }

    // FIXME 待重构
//    @RequestMapping("mass")
//    @Menu(type = "contacts", subtype = "contacts")
//    public ModelAndView mass(
//            ModelMap map,
//            HttpServletRequest request,
//            @RequestParam(name = "ids", required = false) String ids,
//            @RequestParam(name = "massMessageToOnlineUserText", required = false) String massMessageToOnlineUserText,
//            @RequestParam(value = "massMessageToOnlineUserPic", required = false) MultipartFile multipart,
//            @RequestParam(value = "massMessageToOnlineUserFile", required = false) MultipartFile massMessageToOnlineUserFile,
//            @RequestParam(name = "paste", required = false) String paste,
//            @Valid String msg) throws IOException {
//        map.put("msg", msg);
//        final User logined = super.getUser(request);
//        final String orgi = logined.getOrgi();
//        boolean massStatus = false;
//
//        String massFileStyle = Constants.SKYPE_MESSAGE_TEXT;
//        if (StringUtils.isNotBlank(multipart.getOriginalFilename())) {
//            massFileStyle = Constants.SKYPE_MESSAGE_PIC;
//            contactsProxy.sendMessageToContacts(
//                    logined, ids, massMessageToOnlineUserText, multipart, massFileStyle, paste);
//        } else if (StringUtils.isNotBlank(massMessageToOnlineUserFile.getOriginalFilename())) {
//            massFileStyle = Constants.SKYPE_MESSAGE_FILE;
//            contactsProxy.sendMessageToContacts(
//                    logined, ids, massMessageToOnlineUserText, massMessageToOnlineUserFile, massFileStyle, paste);
//        } else {
//            contactsProxy.sendMessageToContacts(
//                    logined, ids, massMessageToOnlineUserText, massMessageToOnlineUserFile, massFileStyle, paste);
//        }
//
//        return request(
//                super.createRequestPageTempletResponse("redirect:/apps/contacts/index?massStatus=" + massStatus));
//    }


    @RequestMapping("/update")
    @Menu(type = "contacts", subtype = "contacts")
    public ModelAndView update(HttpServletRequest request, @Valid Contacts contacts, @Valid String ckindId) {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        String contactsId = contacts.getId();
        Contacts data = contactsRes.findById(contactsId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Contacts %s not found", contactsId)));
        String msg;

        String skypeIDReplace = contactsProxy.sanitizeSkypeId(contacts.getSkypeid());
        Contacts theOnlyContact = contactsRes.findByskypeidAndOrgiAndDatastatus(
                skypeIDReplace, logined.getOrgi(), false);
        Contacts oldContact = contactsRes.findByidAndOrgiAndDatastatus(contactsId, logined.getOrgi(), false);

        boolean determineChange = contactsProxy.determineChange(contacts, oldContact);
        /*
         * 验证skype唯一性验证
         */
        if (theOnlyContact == null || theOnlyContact.getId().equals(oldContact.getId())) {
            if (determineChange) {
                logger.info("[contacts edit] success :The contact has been modified successfully.");
                msg = "edit_contacts_success";
            } else {
                //无修改，直接点击确定
                return request(super.createRequestPageTempletResponse(
                        "redirect:/apps/contacts/index?ckind=" + ckindId));
            }
        } else {
            logger.info("[contacts edit] errer :The same skypeid exists");
            msg = "edit_contacts_fail";
            return request(super.createRequestPageTempletResponse(
                    "redirect:/apps/contacts/index?ckind=" + ckindId + "&msg=" + msg));
        }


        //记录 数据变更 历史
        List<PropertiesEvent> events = PropertiesEventUtil.processPropertiesModify(
                request, contacts, data, "id", "orgi", "creater", "createtime", "updatetime");
        if (events.size() > 0) {
            String modifyid = MainUtils.getUUID();
            Date modifytime = new Date();
            for (PropertiesEvent event : events) {
                event.setDataid(contactsId);
                event.setCreater(logined.getId());
                event.setOrgi(orgi);
                event.setModifyid(modifyid);
                event.setCreatetime(modifytime);
                propertiesEventRes.save(event);
            }
        }

        contacts.setSkypeid(contacts.getSkypeid());
        contacts.setCreater(data.getCreater());
        contacts.setCreatetime(data.getCreatetime());
        contacts.setOrgi(logined.getOrgi());
        contacts.setPinyin(PinYinTools.getInstance().getFirstPinYin(contacts.getName()));

        if (StringUtils.isBlank(contacts.getCusbirthday())) {
            contacts.setCusbirthday(null);
        }

        contactsRes.save(contacts);
        return request(super.createRequestPageTempletResponse(
                "redirect:/apps/contacts/index?ckind=" + ckindId + "&msg=" + msg));
    }


    @RequestMapping("/imp")
    @Menu(type = "contacts", subtype = "contacts")
    public ModelAndView imp(ModelMap map, @Valid String ckind) {
        map.addAttribute("ckind", ckind);
        return request(super.createRequestPageTempletResponse("/apps/business/contacts/imp"));
    }

    @RequestMapping("/impsave")
    @Menu(type = "contacts", subtype = "contacts")
    public ModelAndView impsave(HttpServletRequest request, @RequestParam(value = "cusfile", required = false) MultipartFile cusfile) throws IOException {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        DSDataEvent event = new DSDataEvent();
        String originalFilename = Objects.requireNonNull(cusfile.getOriginalFilename());
        String fileName = "contacts/" + MainUtils.getUUID() + originalFilename.substring(
                originalFilename.lastIndexOf("."));
        File excelFile = new File(path, fileName);
        if (!excelFile.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            excelFile.getParentFile().mkdirs();
        }
        MetadataTable table = metadataRes.findByTablename("uk_contacts");
        if (table != null) {
            FileUtils.writeByteArrayToFile(new File(path, fileName), cusfile.getBytes());
            event.setDSData(new DSData(table, excelFile, cusfile.getContentType(), logined));
            event.getDSData().setClazz(Contacts.class);
            event.getDSData().setProcess(new ContactsProcess(contactsRes));
            event.setOrgi(orgi);
            event.getValues().put("creater", logined.getId());
            reporterRes.save(event.getDSData().getReport());
            //启动导入任务
            new ExcelImportProecess(event).process();
        }
        return request(super.createRequestPageTempletResponse("redirect:/apps/contacts/index"));
    }

    @RequestMapping("/startmass")
    @Menu(type = "contacts", subtype = "contacts")
    public ModelAndView mass(ModelMap map, HttpServletRequest request, @Valid String ids) {
        map.addAttribute("organList", organRes.findByOrgiAndSkill(super.getOrgi(request), true));

        map.addAttribute("ids", ids);
        return request(super.createRequestPageTempletResponse("/apps/business/contacts/mass"));
    }

    @RequestMapping("/expids")
    @Menu(type = "contacts", subtype = "contacts")
    public void expids(HttpServletResponse response, @Valid String[] ids) throws IOException {
        if (ids != null && ids.length > 0) {
            Iterable<Contacts> contactsList = contactsRes.findAllById(Arrays.asList(ids));
            MetadataTable table = metadataRes.findByTablename("uk_contacts");
            List<Map<String, Object>> values = new ArrayList<>();
            for (Contacts contacts : contactsList) {
                values.add(MainUtils.transBean2Map(contacts));
            }

            response.setHeader(
                    "content-disposition",
                    "attachment;filename=CSKefu-Contacts-" + new SimpleDateFormat("yyyy-MM-dd").format(
                            new Date()) + ".xls");

            ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
            excelProcess.process();
        }

    }


    @RequestMapping("/expall")
    @Menu(type = "contacts", subtype = "contacts")
    public void expall(HttpServletRequest request, HttpServletResponse response) throws IOException, CSKefuException {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        if (!super.esOrganFilter(request)) {
            return;
        }
        //只导出 数据删除状态 为 未删除的 数据
        boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("datastatus").value(Boolean.FALSE.toString())));
        Iterable<Contacts> contactsList = contactsRes.findByCreaterAndSharesAndOrgi(
                logined.getId(), logined.getId(), orgi, null, null,
                false, boolQueryBuilder, null, PageRequest.of(super.getP(request), super.getPs(request)));

        MetadataTable table = metadataRes.findByTablename("uk_contacts");
        List<Map<String, Object>> values = new ArrayList<>();
        for (Contacts contacts : contactsList) {
            values.add(MainUtils.transBean2Map(contacts));
        }

        response.setHeader(
                "content-disposition",
                "attachment;filename=CSKefu-Contacts-" + new SimpleDateFormat("yyyy-MM-dd").format(
                        new Date()) + ".xls");

        ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
        excelProcess.process();
    }

    @RequestMapping("/expsearch")
    @Menu(type = "contacts", subtype = "contacts")
    public void expall(ModelMap map, HttpServletRequest request, HttpServletResponse response, @Valid String q, @Valid String ekind) throws IOException {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        if (StringUtils.isNotBlank(q)) {
            map.put("q", q);
        }
        if (StringUtils.isNotBlank(ekind)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("ekind").value(ekind)));
            map.put("ekind", ekind);
        }

        Iterable<Contacts> contactsList = contactsRes.findByCreaterAndSharesAndOrgi(
                logined.getId(), logined.getId(), orgi, null, null,
                false, boolQueryBuilder, q, PageRequest.of(super.getP(request), super.getPs(request)));
        MetadataTable table = metadataRes.findByTablename("uk_contacts");
        List<Map<String, Object>> values = new ArrayList<>();
        for (Contacts contacts : contactsList) {
            values.add(MainUtils.transBean2Map(contacts));
        }

        response.setHeader(
                "content-disposition",
                "attachment;filename=CSKefu-Contacts-" + new SimpleDateFormat("yyyy-MM-dd").format(
                        new Date()) + ".xls");

        ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
        excelProcess.process();

    }


    @RequestMapping("/embed/index")
    @Menu(type = "customer", subtype = "embed")
    public ModelAndView embed(ModelMap map, HttpServletRequest request, @Valid String q, @Valid String ckind, @Valid String msg, @Valid String userid) throws CSKefuException {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        map.put("msg", msg);
        if (!super.esOrganFilter(request)) {
            return request(super.createAppsTempletResponse("/apps/business/contacts/embed/index"));
        }
        if (StringUtils.isNotBlank(q)) {
            map.put("q", q);
        }
        if (StringUtils.isNotBlank(ckind)) {
            boolQueryBuilder.must(QueryBuilders.term(builder -> builder.field("ckind").value(ckind)));
            map.put("ckind", ckind);
        }
        Page<Contacts> contactsList = contactsRes.findByCreaterAndSharesAndOrgi(
                logined.getId(), logined.getId(), orgi, null, null, false, boolQueryBuilder, q,
                PageRequest.of(super.getP(request), super.getPs(request)));

        map.addAttribute("contactsList", contactsList);

        if (StringUtils.isNotBlank(userid)) {
            agentUserContactsRes.findOneByUseridAndOrgi(userid, orgi).ifPresent(p -> {
                map.addAttribute("currentAgentUserContactsId", p.getId());
                map.addAttribute("currentContacsId", p.getContactsid());
            });
        }

        return request(super.createRequestPageTempletResponse("/apps/business/contacts/embed/index"));
    }

    @RequestMapping("/embed/add")
    @Menu(type = "contacts", subtype = "embedadd")
    public ModelAndView embedadd() {
        return request(super.createRequestPageTempletResponse("/apps/business/contacts/embed/add"));
    }

    @RequestMapping("/embed/save")
    @Menu(type = "contacts", subtype = "embedsave")
    public ModelAndView embedsave(HttpServletRequest request, @Valid Contacts contacts) {
        final User logined = super.getUser(request);
        String skypeIDReplace = contactsProxy.sanitizeSkypeId(contacts.getSkypeid());
        String msg;
        List<Contacts> contact = contactsRes.findByskypeidAndDatastatus(skypeIDReplace, false);

        //添加数据
        if (contacts.getSkypeid() != null && contact.size() == 0) {
            contacts.setCreater(logined.getId());
            contacts.setOrgi(logined.getOrgi());
            contacts.setPinyin(PinYinTools.getInstance().getFirstPinYin(contacts.getName()));
            if (StringUtils.isBlank(contacts.getCusbirthday())) {
                contacts.setCusbirthday(null);
            }
            contactsRes.save(contacts);
            msg = "new_contacts_success";
            return request(
                    super.createRequestPageTempletResponse("redirect:/apps/contacts/embed/index?msg=" + msg));
        }
        msg = "new_contacts_fail";
        return request(super.createRequestPageTempletResponse("redirect:/apps/contacts/embed/index?msg=" + msg));
    }

    @RequestMapping("/embed/edit")
    @Menu(type = "contacts", subtype = "embededit")
    public ModelAndView embededit(ModelMap map, @Valid String id) {
        map.addAttribute("contacts", contactsRes.findById(id).orElse(null));
        return request(super.createRequestPageTempletResponse("/apps/business/contacts/embed/edit"));
    }

    @RequestMapping("/embed/update")
    @Menu(type = "contacts", subtype = "embedupdate")
    public ModelAndView embedupdate(HttpServletRequest request, @Valid Contacts contacts) {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        String contactsId = contacts.getId();
        Contacts data = contactsRes.findById(contactsId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Contacts %s not found", contactsId)));
        String msg;
        String skypeIDReplace = contactsProxy.sanitizeSkypeId(contacts.getSkypeid());
        Contacts theOnlyContact = contactsRes.findByskypeidAndOrgiAndDatastatus(
                skypeIDReplace, logined.getOrgi(), false);
        Contacts oldContact = contactsRes.findByidAndOrgiAndDatastatus(contactsId, logined.getOrgi(), false);

        boolean determineChange = contactsProxy.determineChange(contacts, oldContact);

        /*
         * 验证skype唯一性验证
         */
        if (theOnlyContact == null || theOnlyContact.getId().equals(oldContact.getId())) {
            if (determineChange) {
                logger.info("[contacts edit] success :The contact has been modified successfully.");
                msg = "edit_contacts_success";
            } else {
                //无修改，直接点击确定
                return request(super.createRequestPageTempletResponse("redirect:/apps/contacts/embed/index"));
            }
        } else {
            logger.info("[contacts edit] errer :The same skypeid exists");
            msg = "edit_contacts_fail";
            return request(
                    super.createRequestPageTempletResponse("redirect:/apps/contacts/embed/index?msg=" + msg));
        }

        //记录 数据变更 历史
        List<PropertiesEvent> events = PropertiesEventUtil.processPropertiesModify(
                request, contacts, data, "id", "orgi", "creater", "createtime", "updatetime");
        if (events.size() > 0) {
            String modifyid = MainUtils.getUUID();
            Date modifytime = new Date();
            for (PropertiesEvent event : events) {
                event.setDataid(contactsId);
                event.setCreater(logined.getId());
                event.setOrgi(orgi);
                event.setModifyid(modifyid);
                event.setCreatetime(modifytime);
                propertiesEventRes.save(event);
            }
        }

        contacts.setSkypeid(contacts.getSkypeid());
        contacts.setCreater(data.getCreater());
        contacts.setCreatetime(data.getCreatetime());
        contacts.setOrgi(logined.getOrgi());
        contacts.setPinyin(PinYinTools.getInstance().getFirstPinYin(contacts.getName()));


        if (StringUtils.isBlank(contacts.getCusbirthday())) {
            contacts.setCusbirthday(null);
        }

        contactsRes.save(contacts);
        return request(super.createRequestPageTempletResponse("redirect:/apps/contacts/embed/index?msg=" + msg));
    }
}
