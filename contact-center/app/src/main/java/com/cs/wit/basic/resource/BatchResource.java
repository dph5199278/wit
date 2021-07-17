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
package com.cs.wit.basic.resource;

import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.model.JobDetail;
import com.cs.wit.model.MetadataTable;
import com.cs.wit.persistence.impl.BatchDataProcess;
import com.cs.wit.persistence.impl.ESDataExchangeImpl;
import com.cs.wit.persistence.repository.MetadataRepository;
import com.cs.wit.persistence.repository.ReporterRepository;
import com.cs.wit.util.dsdata.DSData;
import com.cs.wit.util.dsdata.DSDataEvent;
import com.cs.wit.util.dsdata.ExcelImportProecess;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

public class BatchResource extends Resource {

    private final JobDetail jobDetail;
    private final MetadataRepository metadataRes;
    private final ReporterRepository reporterRes;
    private final ESDataExchangeImpl esDataExchange;
    private MetadataTable metadataTable;

    public BatchResource(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
        this.metadataRes = MainContext.getContext().getBean(MetadataRepository.class);
        this.reporterRes = MainContext.getContext().getBean(ReporterRepository.class);
        this.esDataExchange = MainContext.getContext().getBean(ESDataExchangeImpl.class);
    }

    @Override
    public void begin() throws Exception {
        if (!StringUtils.isBlank(jobDetail.getActid())) {
            metadataTable = metadataRes.findByTablename(jobDetail.getActid());
        }
        DSDataEvent event = new DSDataEvent();
        String path = MainContext.getContext().getEnvironment().getProperty("web.upload-path");
        File tempFile = null;
        if (metadataTable != null && !StringUtils.isBlank(this.jobDetail.getBatchtype()) && this.jobDetail.getBatchtype().equals("plan")) {
            if (!StringUtils.isBlank(this.jobDetail.getImptype())) {
                if (this.jobDetail.getImptype().equals("local")) {
                    tempFile = new File(MainUtils.getTemplet(this.jobDetail.getImpurl(), new HashMap<String, Object>()));
                } else if (this.jobDetail.getImptype().equals("remote")) {
                    FileUtils.copyURLToFile(new URL(MainUtils.getTemplet(this.jobDetail.getImpurl(), new HashMap<String, Object>())), tempFile = File.createTempFile("UKeFu-CallOut-Temp", ".xls"));
                }
            }
            if (tempFile != null && tempFile.exists()) {
                String fileName = "callout/batch/" + MainUtils.getUUID() + tempFile.getName().substring(tempFile.getName().lastIndexOf("."));
                File excelFile = new File(path, fileName);
                if (!excelFile.getParentFile().exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    excelFile.getParentFile().mkdirs();
                }

                event.setTablename(metadataTable.getTablename());
                event.setDSData(new DSData(null, excelFile, tempFile.getName(), null));
                event.setOrgi(this.jobDetail.getOrgi());
                event.getValues().put("creater", this.jobDetail.getCreater());

                FileUtils.copyFile(tempFile, new File(path, fileName));

                event.getDSData().setTask(metadataTable);
                event.getDSData().setProcess(new BatchDataProcess(metadataTable, esDataExchange));
                event.setOrgi(this.jobDetail.getOrgi());
                event.setBatid(this.jobDetail.getId());
                event.getDSData().setJobDetail(this.jobDetail);

                event.getDSData().getReport().setOrgi(this.jobDetail.getOrgi());
                event.getDSData().getReport().setDataid(this.jobDetail.getId());
                event.getDSData().getReport().setTitle(this.jobDetail.getName() + "_" + MainUtils.dateFormate.get().format(new Date()));
            } else {
                event.getDSData().getReport().setError(true);
                if (tempFile != null) {
                    event.getDSData().getReport().setErrormsg(tempFile.getAbsolutePath() + " Not Exist!");
                }
            }
            reporterRes.save(event.getDSData().getReport());
            new ExcelImportProecess(event).process();        //启动导入任务
        }
    }

    @Override
    public void end(boolean clear) {

    }

    @Override
    public JobDetail getJob() {
        return this.jobDetail;
    }

    @Override
    public void process(@NonNull OutputTextFormat meta, JobDetail job) {

    }

    @Override
    public OutputTextFormat next() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public OutputTextFormat getText(OutputTextFormat object) {
        return object;
    }

    @Override
    public void rmResource() {
        /*
         * 啥也不做
         */
    }

    @Override
    public void updateTask() {

    }
}
