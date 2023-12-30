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

import com.cs.wit.acd.ACDPolicyService;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.cache.Cache;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.AdType;
import com.cs.wit.model.BlackEntity;
import com.cs.wit.model.Dict;
import com.cs.wit.model.SessionConfig;
import com.cs.wit.model.SysDic;
import com.cs.wit.model.Tag;
import com.cs.wit.persistence.repository.AdTypeRepository;
import com.cs.wit.persistence.repository.BlackListRepository;
import com.cs.wit.persistence.repository.SessionConfigRepository;
import com.cs.wit.persistence.repository.TagRepository;
import com.cs.wit.persistence.repository.TemplateRepository;
import com.cs.wit.util.Menu;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/setting")
@RequiredArgsConstructor
public class AgentSettingsController extends Handler {

    @NonNull
    private final ACDPolicyService acdPolicyService;

    @NonNull
    private final SessionConfigRepository sessionConfigRes;

    @NonNull
    private final TagRepository tagRes;

    @NonNull
    private final BlackListRepository blackListRes;

    @NonNull
    private final AdTypeRepository adTypeRes;

    @NonNull
    private final TemplateRepository templateRes;

    @NonNull
    private final Cache cache;

    @RequestMapping("/agent/index")
    @Menu(type = "setting", subtype = "sessionconfig", admin = false)
    public ModelAndView index(ModelMap map, HttpServletRequest request) {
        SessionConfig sessionConfig = sessionConfigRes.findByOrgi(super.getOrgi(request));
        if (sessionConfig == null) {
            sessionConfig = new SessionConfig();
        }
        map.put("sessionConfig", sessionConfig);


        List<SysDic> dicList = Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_DIC);
        SysDic inputDic = null, outputDic = null;
        for (SysDic dic : dicList) {
            if (dic.getCode().equals(Constants.CSKEFU_SYSTEM_AI_INPUT)) {
                inputDic = dic;
            }
            if (dic.getCode().equals(Constants.CSKEFU_SYSTEM_AI_OUTPUT)) {
                outputDic = dic;
            }
        }
        if (inputDic != null) {
            map.addAttribute("innputtemlet", templateRes.findByTemplettypeAndOrgi(inputDic.getId(), super.getOrgi(request)));
        }
        if (outputDic != null) {
            map.addAttribute("outputtemlet", templateRes.findByTemplettypeAndOrgi(outputDic.getId(), super.getOrgi(request)));
        }

        return request(super.createAppsTempletResponse("/apps/setting/agent/index"));
    }

    @RequestMapping("/agent/sessionconfig/save")
    @Menu(type = "setting", subtype = "sessionconfig", admin = false)
    public ModelAndView sessionconfig(ModelMap map, HttpServletRequest request, @Valid SessionConfig sessionConfig) {
        SessionConfig tempSessionConfig = sessionConfigRes.findByOrgi(super.getOrgi(request));
        String orgi = super.getOrgi(request);
        if (tempSessionConfig == null) {
            tempSessionConfig = sessionConfig;
            tempSessionConfig.setCreater(super.getUser(request).getId());
        } else {
            MainUtils.copyProperties(sessionConfig, tempSessionConfig);
        }
        tempSessionConfig.setOrgi(super.getOrgi(request));
        // 强制开启满意度问卷
        tempSessionConfig.setSatisfaction(true);
        sessionConfigRes.save(tempSessionConfig);

        cache.putSessionConfigByOrgi(tempSessionConfig, orgi);
        cache.deleteSessionConfigListByOrgi(orgi);

        acdPolicyService.initSessionConfigList();
        map.put("sessionConfig", tempSessionConfig);

        return request(super.createRequestPageTempletResponse("redirect:/setting/agent/index"));
    }

    @RequestMapping("/blacklist")
    @Menu(type = "setting", subtype = "blacklist", admin = false)
    public ModelAndView blacklist(ModelMap map, HttpServletRequest request) {
        map.put("blackList", blackListRes.findByOrgi(super.getOrgi(request), PageRequest.of(super.getP(request), super.getPs(request), Sort.Direction.DESC, "endtime")));
        map.put("tagTypeList", Dict.getInstance().getDic("com.dic.tag.type"));
        return request(super.createAppsTempletResponse("/apps/setting/agent/blacklist"));
    }

    @RequestMapping("/blacklist/delete")
    @Menu(type = "setting", subtype = "tag", admin = false)
    public ModelAndView blacklistdelete(HttpServletRequest request, @Valid String id) {
        if (!StringUtils.isBlank(id)) {
            BlackEntity tempBlackEntity = blackListRes.findByIdAndOrgi(id, super.getOrgi(request));
            if (tempBlackEntity != null) {
                blackListRes.delete(tempBlackEntity);
                cache.deleteSystembyIdAndOrgi(tempBlackEntity.getUserid(), MainContext.SYSTEM_ORGI);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/setting/blacklist"));
    }

    @RequestMapping("/tag")
    @Menu(type = "setting", subtype = "tag", admin = false)
    public ModelAndView tag(ModelMap map, HttpServletRequest request, @Valid String code) {
        SysDic tagType = null;
        List<SysDic> tagList = Dict.getInstance().getDic("com.dic.tag.type");
        if (tagList.size() > 0) {

            if (!StringUtils.isBlank(code)) {
                for (SysDic dic : tagList) {
                    if (code.equals(dic.getCode())) {
                        tagType = dic;
                    }
                }
            } else {
                tagType = tagList.get(0);
            }
            map.put("tagType", tagType);
        }
        if (tagType != null) {
            map.put("tagList", tagRes.findByOrgiAndTagtype(super.getOrgi(request), tagType.getCode(), PageRequest.of(super.getP(request), super.getPs(request))));
        }
        map.put("tagTypeList", tagList);
        return request(super.createAppsTempletResponse("/apps/setting/agent/tag"));
    }

    @RequestMapping("/tag/add")
    @Menu(type = "setting", subtype = "tag", admin = false)
    public ModelAndView tagadd(ModelMap map, @Valid String tagtype) {
        map.addAttribute("tagtype", tagtype);
        return request(super.createRequestPageTempletResponse("/apps/setting/agent/tagadd"));
    }

    @RequestMapping("/tag/edit")
    @Menu(type = "setting", subtype = "tag", admin = false)
    public ModelAndView tagedit(ModelMap map, @Valid String id, @Valid String tagtype) {
        map.put("tag", tagRes.findById(id).orElse(null));
        map.addAttribute("tagtype", tagtype);
        return request(super.createRequestPageTempletResponse("/apps/setting/agent/tagedit"));
    }

    @RequestMapping("/tag/update")
    @Menu(type = "setting", subtype = "tag", admin = false)
    public ModelAndView tagupdate(HttpServletRequest request, @Valid Tag tag, @Valid String tagtype) {
        Tag temptag = tagRes.findByOrgiAndTag(super.getOrgi(request), tag.getTag());
        if (temptag == null || tag.getId().equals(temptag.getId())) {
            tag.setOrgi(super.getOrgi(request));
            tag.setCreater(super.getUser(request).getId());
            tagRes.save(tag);
        }
        return request(super.createRequestPageTempletResponse("redirect:/setting/tag?code=" + tagtype));
    }

    @RequestMapping("/tag/save")
    @Menu(type = "setting", subtype = "tag", admin = false)
    public ModelAndView tagsave(HttpServletRequest request, @Valid Tag tag, @Valid String tagtype) {
        if (tagRes.findByOrgiAndTag(super.getOrgi(request), tag.getTag()) == null) {
            tag.setOrgi(super.getOrgi(request));
            tag.setCreater(super.getUser(request).getId());
            tagRes.save(tag);
        }
        return request(super.createRequestPageTempletResponse("redirect:/setting/tag?code=" + tagtype));
    }

    @RequestMapping("/tag/delete")
    @Menu(type = "setting", subtype = "tag", admin = false)
    public ModelAndView tagdelete(@Valid String id, @Valid String tagtype) {
        tagRes.deleteById(id);
        return request(super.createRequestPageTempletResponse("redirect:/setting/tag?code=" + tagtype));
    }


    @RequestMapping("/acd")
    @Menu(type = "setting", subtype = "acd", admin = false)
    public ModelAndView acd(ModelMap map) {
        map.put("tagTypeList", Dict.getInstance().getDic("com.dic.tag.type"));
        return request(super.createAppsTempletResponse("/apps/setting/agent/acd"));
    }


    @RequestMapping("/adv")
    @Menu(type = "setting", subtype = "adv", admin = false)
    public ModelAndView adv(ModelMap map, HttpServletRequest request, @Valid String adpos) {
        SysDic advType = null;
        List<SysDic> tagList = Dict.getInstance().getDic("com.dic.adv.type");
        if (tagList.size() > 0) {
            if (!StringUtils.isBlank(adpos)) {
                for (SysDic dic : tagList) {
                    if (adpos.equals(dic.getId())) {
                        advType = dic;
                    }
                }
            } else {
                advType = tagList.get(0);
            }
            map.put("advType", advType);
        }
        if (advType != null) {
            map.put("adTypeList", adTypeRes.findByAdposAndOrgi(advType.getId(), super.getOrgi(request)));
        }

        map.put("tagTypeList", Dict.getInstance().getDic("com.dic.tag.type"));

        map.put("advTypeList", Dict.getInstance().getDic("com.dic.adv.type"));

        return request(super.createAppsTempletResponse("/apps/setting/agent/adv"));
    }

    @RequestMapping("/adv/add")
    @Menu(type = "setting", subtype = "adv", admin = false)
    public ModelAndView advadd(ModelMap map, @Valid String adpos) {
        map.addAttribute("adpos", adpos);
        return request(super.createRequestPageTempletResponse("/apps/setting/agent/adadd"));
    }

    @RequestMapping("/adv/save")
    @Menu(type = "setting", subtype = "adv", admin = false)
    public ModelAndView advsave(HttpServletRequest request, @Valid AdType adv, @RequestParam(value = "imgfile", required = false) MultipartFile imgfile) throws IOException {
        adv.setOrgi(super.getOrgi(request));
        adv.setCreater(super.getUser(request).getId());
        if (StringUtils.isNotBlank(adv.getContent())) {
            adv.setContent(adv.getContent().replaceAll("\"", "'"));
        }
        adv.setCreatetime(new Date());
        if (imgfile != null && imgfile.getSize() > 0) {
            adv.setImgurl("/res/image?id=" + super.saveImageFileWithMultipart(imgfile));
        }
        adTypeRes.save(adv);

        MainUtils.initAdv(super.getOrgi(request));

        return request(super.createRequestPageTempletResponse("redirect:/setting/adv?adpos=" + adv.getAdpos()));
    }

    @RequestMapping("/adv/edit")
    @Menu(type = "setting", subtype = "adv", admin = false)
    public ModelAndView advedit(ModelMap map, HttpServletRequest request, @Valid String adpos, @Valid String id) {
        map.addAttribute("adpos", adpos);
        map.put("ad", adTypeRes.findByIdAndOrgi(id, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/apps/setting/agent/adedit"));
    }

    @RequestMapping("/adv/update")
    @Menu(type = "setting", subtype = "adv", admin = false)
    public ModelAndView advupdate(HttpServletRequest request, @Valid AdType ad, @Valid String adpos, @RequestParam(value = "imgfile", required = false) MultipartFile imgfile) throws IOException {
        AdType tempad = adTypeRes.findByIdAndOrgi(ad.getId(), super.getOrgi(request));
        if (tempad != null) {
            ad.setOrgi(super.getOrgi(request));
            ad.setCreater(tempad.getCreater());
            ad.setCreatetime(tempad.getCreatetime());
            if (StringUtils.isNotBlank(ad.getContent())) {
                ad.setContent(ad.getContent().replaceAll("\"", "'"));
            }
            if (imgfile != null && imgfile.getSize() > 0) {
                ad.setImgurl("/res/image?id=" + super.saveImageFileWithMultipart(imgfile));
            } else {
                ad.setImgurl(tempad.getImgurl());
            }
            adTypeRes.save(ad);
            MainUtils.initAdv(super.getOrgi(request));
        }
        return request(super.createRequestPageTempletResponse("redirect:/setting/adv?adpos=" + adpos));
    }

    @RequestMapping("/adv/delete")
    @Menu(type = "setting", subtype = "adv", admin = false)
    public ModelAndView advdelete(HttpServletRequest request, @Valid String id, @Valid String adpos) {
        adTypeRes.deleteById(id);
        MainUtils.initAdv(super.getOrgi(request));
        return request(super.createRequestPageTempletResponse("redirect:/setting/adv?adpos=" + adpos));
    }
}
