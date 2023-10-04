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
import com.cs.wit.model.IMGroup;
import com.cs.wit.model.IMGroupUser;
import com.cs.wit.model.Organ;
import com.cs.wit.model.RecentUser;
import com.cs.wit.model.StreamingFile;
import com.cs.wit.model.UploadStatus;
import com.cs.wit.model.User;
import com.cs.wit.peer.PeerSyncEntIM;
import com.cs.wit.persistence.blob.JpaBlobHelper;
import com.cs.wit.persistence.repository.ChatMessageRepository;
import com.cs.wit.persistence.repository.IMGroupRepository;
import com.cs.wit.persistence.repository.IMGroupUserRepository;
import com.cs.wit.persistence.repository.OrganRepository;
import com.cs.wit.persistence.repository.RecentUserRepository;
import com.cs.wit.persistence.repository.StreamingFileRepository;
import com.cs.wit.persistence.repository.UserRepository;
import com.cs.wit.proxy.AttachmentProxy;
import com.cs.wit.proxy.UserProxy;
import com.cs.wit.socketio.client.NettyClients;
import com.cs.wit.socketio.message.ChatMessage;
import com.cs.wit.util.Menu;
import com.cs.wit.util.StreamingFileUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/ent/im")
@RequiredArgsConstructor
public class EntIMController extends Handler {
    @NonNull
    private final OrganRepository organRes;
    @NonNull
    private final UserRepository userRes;
    @NonNull
    private final IMGroupRepository imGroupRes;
    @NonNull
    private final IMGroupUserRepository imGroupUserRes;
    @NonNull
    private final ChatMessageRepository chatMessageRes;
    @NonNull
    private final RecentUserRepository recentUserRes;
    @NonNull
    private final StreamingFileRepository streamingFileRepository;
    @NonNull
    private final JpaBlobHelper jpaBlobHelper;
    @NonNull
    private final UserProxy userProxy;
    @NonNull
    private final AttachmentProxy attachmentProxy;
    @NonNull
    private final PeerSyncEntIM peerSyncEntIM;

    @Value("${web.upload-path}")
    private String path;

    private Map<String, Organ> getChatOrgans(User user, String orgi) {
        Map<String, Organ> organs = new HashMap<>();
        user.getOrgans().values().forEach(o -> {
            if (!StringUtils.equals(o.getParent(), "0")) {
                Organ parent = organRes.findByIdAndOrgi(o.getParent(), orgi);
                organs.put(parent.getId(), parent);
            }

            List<Organ> brother = organRes.findByOrgiAndParent(orgi, o.getParent());
            brother.forEach(b -> {
                if (!organs.containsKey(b.getId())) {
                    organs.put(b.getId(), b);
                }
            });
        });

        user.getAffiliates().forEach(p -> {
            if (!organs.containsKey(p)) {
                Organ organ = organRes.findByIdAndOrgi(p, orgi);
                organs.put(p, organ);
            }
        });

        return organs;
    }

    @RequestMapping("/index")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView view = request(super.createEntIMTempletResponse("/apps/entim/index"));

        User logined = super.getUser(request);

        Map<String, Organ> targetOrgans = getChatOrgans(logined, super.getOrgi(request));

        view.addObject("organList", targetOrgans.values());
        List<User> users = userRes.findByOrgiAndDatastatus(super.getOrgi(request), false);

        // TODO: 优化性能
        for (User u : users) {
            userProxy.attachOrgansPropertiesForUser(u);
        }

        view.addObject("userList", users);
        view.addObject("groupList", imGroupRes.findByCreaterAndOrgi(super.getUser(request).getId(), super.getOrgi(request)));
        view.addObject("joinGroupList", imGroupUserRes.findByUserAndOrgi(super.getUser(request), super.getOrgi(request)));
        view.addObject("recentUserList", recentUserRes.findByCreaterAndOrgi(super.getUser(request).getId(), super.getOrgi(request)));

        return view;
    }

    @RequestMapping("/skin")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView skin() {

        return request(super.createEntIMTempletResponse("/apps/entim/skin"));
    }

    @RequestMapping("/point")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView point(HttpServletRequest request) {
        ModelAndView view = request(super.createEntIMTempletResponse("/apps/entim/point"));
        view.addObject(
                "recentUserList",
                recentUserRes.findByCreaterAndOrgi(super.getUser(request).getId(), super.getOrgi(request))
        );
        return view;
    }

    @RequestMapping("/expand")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView expand() {
        return request(super.createEntIMTempletResponse("/apps/entim/expand"));
    }

    @RequestMapping("/chat")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView chat(HttpServletRequest request, @Valid String userid) {
        ModelAndView view = request(super.createEntIMTempletResponse("/apps/entim/chat"));
        User entImUser = userRes.findByIdAndOrgi(userid, super.getOrgi(request));

        if (entImUser != null) {
            userProxy.attachOrgansPropertiesForUser(entImUser);
            view.addObject("organs", entImUser.getOrgans().values());
        }

        view.addObject("entimuser", entImUser);
        view.addObject("contextid", MainUtils.genNewID(super.getUser(request).getId(), userid));
        view.addObject("online", NettyClients.getInstance().getEntIMClientsNum(userid) > 0);

        Page<ChatMessage> chatMessageList = chatMessageRes.findByContextidAndUseridAndOrgi(userid,
                super.getUser(request).getId(), super.getOrgi(request),
                PageRequest.of(0, 20, Sort.Direction.DESC, "createtime")
        );

        view.addObject("chatMessageList", chatMessageList);

        RecentUser recentUser = recentUserRes.findByCreaterAndUserAndOrgi(super.getUser(request).getId(),
                new User(userid), super.getOrgi(request)
        ).orElseGet(() -> {
            RecentUser u = new RecentUser();
            u.setOrgi(super.getOrgi(request));
            u.setCreater(super.getUser(request).getId());
            u.setUser(new User(userid));
            return u;
        });
        /*
         * 我的最近联系人
         */
        recentUser.setNewmsg(0);

        recentUserRes.save(recentUser);
        /*
         * 对方的最近联系人
         */
        recentUserRes.findByCreaterAndUserAndOrgi(userid, super.getUser(request), super.getOrgi(request)).orElseGet(() -> {
            RecentUser u = new RecentUser();
            u.setOrgi(super.getOrgi(request));
            u.setCreater(userid);
            u.setUser(super.getUser(request));
            recentUserRes.save(u);
            return u;
        });

        return view;
    }

    @RequestMapping("/chat/more")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView chatMore(
            HttpServletRequest request, @Valid String userid,
            @Valid Date createtime
    ) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/entim/more"));

        Page<ChatMessage> chatMessageList = chatMessageRes.findByContextidAndUseridAndOrgiAndCreatetimeLessThan(userid,
                super.getUser(request).getId(), super.getOrgi(request), createtime,
                PageRequest.of(0, 20, Sort.Direction.DESC, "createtime")
        );
        view.addObject("chatMessageList", chatMessageList);

        return view;
    }

    @RequestMapping("/group")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView groupMore(HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createEntIMTempletResponse("/apps/entim/group/index"));
        IMGroup imGroup = imGroupRes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("IMGroup %s not found", id)));
        view.addObject("imGroup", imGroup);
        view.addObject("imGroupUserList", imGroupUserRes.findByImgroupAndOrgi(imGroup, super.getOrgi(request)));
        view.addObject("contextid", id);
        view.addObject("chatMessageList", chatMessageRes.findByContextidAndOrgi(id, super.getOrgi(request),
                PageRequest.of(0, 20, Sort.Direction.DESC, "createtime")
        ));
        return view;
    }

    @RequestMapping("/group/more")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView group(
            HttpServletRequest request, @Valid String id,
            @Valid Date createtime
    ) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/entim/group/more"));
        view.addObject("chatMessageList", chatMessageRes.findByContextidAndOrgiAndCreatetimeLessThan(id,
                super.getOrgi(request), createtime, PageRequest.of(0, 20, Sort.Direction.DESC, "createtime")
        ));
        return view;
    }

    @RequestMapping("/group/user")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView user(HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createEntIMTempletResponse("/apps/entim/group/user"));
        User logined = super.getUser(request);
        HashSet<String> affiliates = logined.getAffiliates();

        List<User> users = userProxy.findByOrganInAndDatastatus(new ArrayList<>(affiliates), false);
        users.forEach(userProxy::attachOrgansPropertiesForUser);
        view.addObject("userList", users);

        IMGroup imGroup = imGroupRes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("IMGroup %s not found", id)));
        List<Organ> organs = organRes.findAllById(affiliates);

        view.addObject("imGroup", imGroup);
        view.addObject("organList", organs);
        view.addObject("imGroupUserList", imGroupUserRes.findByImgroupAndOrgi(imGroup, super.getOrgi(request)));

        return view;
    }

    @RequestMapping("/group/seluser")
    @Menu(type = "im", subtype = "entim")
    public void seluser(
            HttpServletRequest request, @Valid String id,
            @Valid String user
    ) {
        IMGroup imGroup = new IMGroup();
        imGroup.setId(id);
        User curUser = new User();
        curUser.setId(user);
        IMGroupUser imGroupUser = imGroupUserRes.findByImgroupAndUserAndOrgi(imGroup, curUser, super.getOrgi(request));
        if (imGroupUser == null) {
            imGroupUser = new IMGroupUser();
            imGroupUser.setImgroup(imGroup);
            imGroupUser.setUser(curUser);
            imGroupUser.setOrgi(super.getUser(request).getOrgi());
            imGroupUser.setCreater(super.getUser(request).getId());
            imGroupUserRes.save(imGroupUser);
        }
    }

    @RequestMapping("/group/rmuser")
    @Menu(type = "im", subtype = "entim")
    public void rmluser(
            HttpServletRequest request, @Valid String id,
            @Valid String user
    ) {
        IMGroup imGroup = new IMGroup();
        imGroup.setId(id);
        User curUser = new User();
        curUser.setId(user);
        IMGroupUser imGroupUser = imGroupUserRes.findByImgroupAndUserAndOrgi(imGroup, curUser, super.getOrgi(request));
        if (imGroupUser != null) {
            imGroupUserRes.delete(imGroupUser);
        }
    }

    @RequestMapping("/group/tipmsg")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView tipmsg(
            @Valid String id,
            @Valid String tipmsg
    ) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/entim/group/tipmsg"));
        imGroupRes.findById(id).ifPresent(imGroup -> {
            imGroup.setTipmessage(tipmsg);
            imGroupRes.save(imGroup);
            view.addObject("imGroup", imGroup);
        });
        return view;
    }

    @RequestMapping("/group/save")
    @Menu(type = "im", subtype = "entim")
    public ModelAndView groupsave(HttpServletRequest request, @Valid IMGroup group) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/entim/group/grouplist"));
        if (!StringUtils.isBlank(group.getName())
                && imGroupRes.countByNameAndOrgi(group.getName(), super.getOrgi(request)) == 0) {
            group.setOrgi(super.getUser(request).getOrgi());
            group.setCreater(super.getUser(request).getId());
            imGroupRes.save(group);

            IMGroupUser imGroupUser = new IMGroupUser();
            imGroupUser.setOrgi(super.getUser(request).getOrgi());
            imGroupUser.setUser(super.getUser(request));
            imGroupUser.setImgroup(group);
            imGroupUser.setAdmin(true);
            imGroupUser.setCreater(super.getUser(request).getId());
            imGroupUserRes.save(imGroupUser);
        }
        view.addObject(
                "groupList",
                imGroupRes.findByCreaterAndOrgi(super.getUser(request).getId(), super.getOrgi(request))
        );

        view.addObject(
                "joinGroupList",
                imGroupUserRes.findByUserAndOrgi(super.getUser(request), super.getOrgi(request))
        );

        return view;
    }

    private ChatMessage createFileMessage(String message, int length, String name, String msgtype, String userid, String attachid, String orgi) {
        ChatMessage data = new ChatMessage();
        data.setFilesize(length);
        data.setFilename(name);
        data.setAttachmentid(attachid);
        data.setMessage(message);
        data.setMsgtype(msgtype);
        data.setType(MainContext.MessageType.MESSAGE.toString());
        data.setCalltype(MainContext.CallType.OUT.toString());
        data.setOrgi(orgi);

        data.setTouser(userid);

        return data;
    }

    @RequestMapping("/image/upload")
    @Menu(type = "im", subtype = "image", access = true)
    public ModelAndView upload(
            ModelMap map, HttpServletRequest request,
            @RequestParam(value = "imgFile", required = false) MultipartFile multipart, @Valid String group,
            @Valid String userid, @Valid String orgi, @Valid String paste
    ) throws IOException {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/upload"));
        final User logined = super.getUser(request);

        UploadStatus upload;
        String fileName;

        if (multipart != null && multipart.getOriginalFilename() != null && multipart.getOriginalFilename().lastIndexOf(".") > 0
                && StringUtils.isNotBlank(userid)) {
            File uploadDir = new File(path, "upload");
            if (!uploadDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                uploadDir.mkdirs();
            }

            String fileid = MainUtils.getUUID();
            StreamingFile sf = new StreamingFile();
            sf.setId(fileid);
            sf.setName(multipart.getOriginalFilename());
            sf.setMime(multipart.getContentType());
            if (multipart.getContentType() != null
                    && multipart.getContentType().contains(Constants.ATTACHMENT_TYPE_IMAGE)) {
                String invalid = StreamingFileUtil.getInstance().validate(Constants.ATTACHMENT_TYPE_IMAGE, multipart.getOriginalFilename());
                if (invalid == null) {
                    fileName = "upload/" + fileid + "_original";
                    File imageFile = new File(path, fileName);
                    FileCopyUtils.copy(multipart.getBytes(), imageFile);
                    String thumbnailsFileName = "upload/" + fileid;
                    File thumbnail = new File(path, thumbnailsFileName);
                    MainUtils.processImage(thumbnail, imageFile);

                    sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
                    sf.setThumbnail(jpaBlobHelper.createBlobWithFile(thumbnail));
                    streamingFileRepository.save(sf);
                    String fileUrl = "/res/image?id=" + fileid;
                    upload = new UploadStatus("0", fileUrl);

                    if (paste == null) {
                        ChatMessage fileMessage = createFileMessage(fileUrl, (int) multipart.getSize(), multipart.getName(), MainContext.MediaType.IMAGE.toString(), userid, fileid, super.getOrgi(request));
                        fileMessage.setUsername(logined.getUname());
                        peerSyncEntIM.send(logined.getId(), group, orgi, MainContext.MessageType.MESSAGE, fileMessage);
                    }
                } else {
                    upload = new UploadStatus(invalid);
                }
            } else {
                String invalid = StreamingFileUtil.getInstance().validate(Constants.ATTACHMENT_TYPE_FILE, multipart.getOriginalFilename());
                if (invalid == null) {
                    sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
                    streamingFileRepository.save(sf);

                    String id = attachmentProxy.processAttachmentFile(multipart,
                            fileid, logined.getOrgi(), logined.getId()
                    );
                    upload = new UploadStatus("0", "/res/file?id=" + id);
                    String file = "/res/file?id=" + id;

                    ChatMessage fileMessage = createFileMessage(file, (int) multipart.getSize(), multipart.getOriginalFilename(), MainContext.MediaType.FILE.toString(), userid, fileid, super.getOrgi(request));
                    fileMessage.setUsername(logined.getUname());
                    peerSyncEntIM.send(logined.getId(), group, orgi, MainContext.MessageType.MESSAGE, fileMessage);
                } else {
                    upload = new UploadStatus(invalid);
                }
            }
        } else {
            upload = new UploadStatus("请选择文件");
        }

        map.addAttribute("upload", upload);

        return view;
    }
}
