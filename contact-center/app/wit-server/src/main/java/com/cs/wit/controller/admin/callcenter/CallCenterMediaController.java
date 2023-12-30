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
package com.cs.wit.controller.admin.callcenter;

import com.cs.wit.basic.MainUtils;
import com.cs.wit.controller.Handler;
import com.cs.wit.model.Media;
import com.cs.wit.persistence.repository.MediaRepository;
import com.cs.wit.persistence.repository.PbxHostRepository;
import com.cs.wit.util.Menu;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import jakarta.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/callcenter")
@RequiredArgsConstructor
public class CallCenterMediaController extends Handler {

    @NonNull
    private final PbxHostRepository pbxHostRes;

    @NonNull
    private final MediaRepository mediaRes;

    @Value("${web.upload-path}")
    private String path;

    @RequestMapping(value = "/media")
    @Menu(type = "callcenter", subtype = "callcentermedia", admin = true)
    public ModelAndView media(ModelMap map, HttpServletRequest request, @Valid String hostid) {
        if (!StringUtils.isBlank(hostid)) {
            map.addAttribute("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
            map.addAttribute("mediaList", mediaRes.findByHostidAndOrgi(hostid, super.getOrgi(request)));
        }
        return request(super.createRequestPageTempletResponse("/admin/callcenter/media/index"));
    }

    @RequestMapping(value = "/media/add")
    @Menu(type = "callcenter", subtype = "media", admin = true)
    public ModelAndView mediaadd(ModelMap map, HttpServletRequest request, @Valid String hostid) {
        map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/admin/callcenter/media/add"));
    }

    @RequestMapping(value = "/media/save")
    @Menu(type = "callcenter", subtype = "media", admin = true)
    public ModelAndView mediasave(HttpServletRequest request, @RequestParam(value = "mediafile", required = false) MultipartFile mediafile) throws IOException {
        Media media = new Media();
        media.setName(request.getParameter("name"));
        media.setHostid(request.getParameter("hostid"));
        if (!StringUtils.isBlank(media.getName())) {
            int count = mediaRes.countByNameAndOrgi(media.getName(), super.getOrgi(request));
            if (count == 0) {
                String fileName = "media/" + MainUtils.getUUID() + Objects.requireNonNull(mediafile.getOriginalFilename())
                        .substring(mediafile.getOriginalFilename().lastIndexOf("."));

                media.setOrgi(super.getOrgi(request));
                media.setCreater(super.getUser(request).getId());
                media.setFilelength((int) mediafile.getSize());
                media.setContent(mediafile.getContentType());
                media.setFilename(fileName);

                if (mediafile.getOriginalFilename().lastIndexOf(".") > 0) {
                    File logoDir = new File(path, "media");
                    if (!logoDir.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        logoDir.mkdirs();
                    }
                    FileCopyUtils.copy(mediafile.getBytes(), new File(path, fileName));
                }

                mediaRes.save(media);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/media?hostid=" + media.getHostid()));
    }

    @RequestMapping(value = "/media/edit")
    @Menu(type = "callcenter", subtype = "media", admin = true)
    public ModelAndView mediaedit(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String hostid) {
        map.addAttribute("media", mediaRes.findByIdAndOrgi(id, super.getOrgi(request)));
        map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/admin/callcenter/media/edit"));
    }

    @RequestMapping(value = "/media/update")
    @Menu(type = "callcenter", subtype = "media", admin = true)
    public ModelAndView pbxhostupdate(HttpServletRequest request, @RequestParam(value = "mediafile", required = false) MultipartFile mediafile) throws IOException {
        Media media = new Media();
        media.setName(request.getParameter("name"));
        media.setHostid(request.getParameter("hostid"));
        media.setId(request.getParameter("id"));
        if (!StringUtils.isBlank(media.getId())) {
            Media oldMedia = mediaRes.findByIdAndOrgi(media.getId(), super.getOrgi(request));
            if (oldMedia != null) {
                if (mediafile != null && mediafile.getSize() > 0) {
                    File wavFile = new File(path, oldMedia.getFilename());
                    if (!wavFile.exists()) {
                        wavFile.deleteOnExit();
                    }

                    String fileName = "media/" + MainUtils.getUUID() + Objects.requireNonNull(mediafile.getOriginalFilename())
                            .substring(mediafile.getOriginalFilename().lastIndexOf("."));
                    oldMedia.setFilename(fileName);

                    if (mediafile.getOriginalFilename().lastIndexOf(".") > 0) {
                        File mediaDir = new File(path, "media");
                        if (!mediaDir.exists()) {
                            //noinspection ResultOfMethodCallIgnored
                            mediaDir.mkdirs();
                        }
                        FileCopyUtils.copy(mediafile.getBytes(), new File(path, fileName));
                    }
                }
                oldMedia.setName(media.getName());
                mediaRes.save(oldMedia);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/media?hostid=" + media.getHostid()));
    }

    @RequestMapping(value = "/media/delete")
    @Menu(type = "callcenter", subtype = "media", admin = true)
    public ModelAndView mediadelete(@Valid String id, @Valid String hostid) {
        if (!StringUtils.isBlank(id)) {
            mediaRes.deleteById(id);
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/media?hostid=" + hostid));
    }

    @RequestMapping(value = "/play")
    @Menu(type = "callcenter", subtype = "play")
    public ModelAndView play(ModelMap map, HttpServletRequest request, @Valid final String id) {
        map.addAttribute("media", mediaRes.findByIdAndOrgi(id, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/admin/callcenter/media/play"));
    }
}
