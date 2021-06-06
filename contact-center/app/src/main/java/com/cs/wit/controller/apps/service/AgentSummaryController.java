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
package com.cs.wit.controller.apps.service;

import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.AgentService;
import com.cs.wit.model.AgentServiceSummary;
import com.cs.wit.model.MetadataTable;
import com.cs.wit.persistence.es.ContactsRepository;
import com.cs.wit.persistence.repository.AgentServiceRepository;
import com.cs.wit.persistence.repository.MetadataRepository;
import com.cs.wit.persistence.repository.ServiceSummaryRepository;
import com.cs.wit.persistence.repository.TagRepository;
import com.cs.wit.util.Menu;
import com.cs.wit.util.dsdata.export.ExcelExporterProcess;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/apps/agent/summary")
@RequiredArgsConstructor
public class AgentSummaryController extends Handler {

    @NonNull
    private final ServiceSummaryRepository serviceSummaryRes;

    @NonNull
    private final MetadataRepository metadataRes;

    @NonNull
    private final AgentServiceRepository agentServiceRes;

    @NonNull
    private final TagRepository tagRes;

    @NonNull
    private final ContactsRepository contactsRes;

    /**
     * 按条件查询
     */
    @RequestMapping(value = "/index")
    @Menu(type = "agent", subtype = "agentsummary")
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid final String begin, @Valid final String end) {
        final String orgi = super.getOrgi(request);
        Page<AgentServiceSummary> page = serviceSummaryRes.findAll((Specification<AgentServiceSummary>) (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("orgi").as(String.class), orgi));
            list.add(cb.equal(root.get("process").as(boolean.class), 0));
            list.add(cb.notEqual(root.get("channel").as(String.class), MainContext.ChannelType.PHONE.toString()));
            try {
                if (!StringUtils.isBlank(begin) && begin.matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                    list.add(cb.greaterThanOrEqualTo(root.get("createtime").as(Date.class), MainUtils.simpleDateFormat.parse(begin)));
                }
                if (!StringUtils.isBlank(end) && end.matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                    list.add(cb.lessThanOrEqualTo(root.get("createtime").as(Date.class), MainUtils.dateFormate.parse(end + " 23:59:59")));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        }, PageRequest.of(super.getP(request), super.getPs(request), Sort.Direction.DESC, "createtime"));
        map.addAttribute("summaryList", page);
        map.addAttribute("begin", begin);
        map.addAttribute("end", end);

        map.addAttribute("tags", tagRes.findByOrgiAndTagtype(super.getOrgi(request), MainContext.ModelType.SUMMARY.toString()));

        return request(super.createAppsTempletResponse("/apps/service/summary/index"));
    }

    @RequestMapping(value = "/process")
    @Menu(type = "agent", subtype = "agentsummary")
    public ModelAndView process(ModelMap map, HttpServletRequest request, @Valid final String id) {
        AgentServiceSummary summary = serviceSummaryRes.findByIdAndOrgi(id, super.getOrgi(request));
        map.addAttribute("summary", summary);
        map.put("summaryTags", tagRes.findByOrgiAndTagtype(super.getOrgi(request), MainContext.ModelType.SUMMARY.toString()));
        if (summary != null && !StringUtils.isBlank(summary.getAgentserviceid())) {
            AgentService service = agentServiceRes.findByIdAndOrgi(summary.getAgentserviceid(), super.getOrgi(request));
            map.addAttribute("service", service);
            if (!StringUtils.isBlank(summary.getContactsid())) {
                contactsRes.findById(summary.getContactsid())
                        .ifPresent(it -> map.addAttribute("contacts", it));
            }
        }

        return request(super.createRequestPageTempletResponse("/apps/service/summary/process"));
    }

    @RequestMapping(value = "/save")
    @Menu(type = "agent", subtype = "agentsummary")
    public ModelAndView save(HttpServletRequest request, @Valid final AgentServiceSummary summary) {
        AgentServiceSummary oldSummary = serviceSummaryRes.findByIdAndOrgi(summary.getId(), super.getOrgi(request));
        if (oldSummary != null) {
            oldSummary.setProcess(true);
            oldSummary.setUpdatetime(new Date());
            oldSummary.setUpdateuser(super.getUser(request).getId());
            oldSummary.setProcessmemo(summary.getProcessmemo());
            serviceSummaryRes.save(oldSummary);
        }

        return request(super.createRequestPageTempletResponse("redirect:/apps/agent/summary/index.html"));
    }

    @RequestMapping("/expids")
    @Menu(type = "agent", subtype = "agentsummary")
    public void expids(HttpServletResponse response, @Valid String[] ids) throws IOException {
        if (ids != null && ids.length > 0) {
            Iterable<AgentServiceSummary> statusEventList = serviceSummaryRes.findAllById(Arrays.asList(ids));
            MetadataTable table = metadataRes.findByTablename("uk_servicesummary");
            List<Map<String, Object>> values = new ArrayList<>();
            for (AgentServiceSummary event : statusEventList) {
                values.add(MainUtils.transBean2Map(event));
            }

            response.setHeader("content-disposition", "attachment;filename=UCKeFu-Summary-History-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

            ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
            excelProcess.process();
        }
    }

    @RequestMapping("/expall")
    @Menu(type = "agent", subtype = "agentsummary")
    public void expall(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Iterable<AgentServiceSummary> statusEventList = serviceSummaryRes.findByChannelNotAndOrgi(
                MainContext.ChannelType.PHONE.toString(), super.getOrgi(request), PageRequest.of(0, 10000));

        MetadataTable table = metadataRes.findByTablename("uk_servicesummary");
        List<Map<String, Object>> values = new ArrayList<>();
        for (AgentServiceSummary statusEvent : statusEventList) {
            values.add(MainUtils.transBean2Map(statusEvent));
        }

        response.setHeader("content-disposition", "attachment;filename=UCKeFu-Summary-History-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

        ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
        excelProcess.process();
    }

    @RequestMapping("/expsearch")
    @Menu(type = "agent", subtype = "agentsummary")
    public void expall(HttpServletRequest request, HttpServletResponse response, @Valid final String begin, @Valid final String end) throws IOException {
        final String orgi = super.getOrgi(request);
        Page<AgentServiceSummary> page = serviceSummaryRes.findAll((Specification<AgentServiceSummary>) (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.and(cb.equal(root.get("process").as(boolean.class), 0)));
            list.add(cb.equal(root.get("orgi").as(String.class), orgi));
            list.add(cb.and(cb.notEqual(root.get("channel").as(String.class), MainContext.ChannelType.PHONE.toString())));
            try {
                if (!StringUtils.isBlank(begin) && begin.matches("[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}")) {
                    list.add(cb.and(cb.greaterThanOrEqualTo(root.get("createtime").as(Date.class), MainUtils.dateFormate.parse(begin))));
                }
                if (!StringUtils.isBlank(end) && end.matches("[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}")) {
                    list.add(cb.and(cb.lessThanOrEqualTo(root.get("createtime").as(Date.class), MainUtils.dateFormate.parse(end))));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        }, PageRequest.of(0, 10000, Sort.Direction.DESC, "createtime"));

        List<Map<String, Object>> values = new ArrayList<>();
        for (AgentServiceSummary summary : page) {
            values.add(MainUtils.transBean2Map(summary));
        }

        response.setHeader("content-disposition", "attachment;filename=UCKeFu-Summary-History-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls");

        MetadataTable table = metadataRes.findByTablename("uk_servicesummary");

        ExcelExporterProcess excelProcess = new ExcelExporterProcess(values, table, response.getOutputStream());
        excelProcess.process();

    }
}
