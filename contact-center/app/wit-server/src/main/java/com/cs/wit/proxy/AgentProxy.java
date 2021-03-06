package com.cs.wit.proxy;

import com.cs.wit.acd.ACDPolicyService;
import com.cs.wit.basic.Constants;
import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.cache.Cache;
import com.cs.wit.exception.CSKefuException;
import com.cs.wit.model.AgentStatus;
import com.cs.wit.model.AgentUser;
import com.cs.wit.model.AttachmentFile;
import com.cs.wit.model.SessionConfig;
import com.cs.wit.model.StreamingFile;
import com.cs.wit.model.User;
import com.cs.wit.peer.PeerSyncIM;
import com.cs.wit.persistence.blob.JpaBlobHelper;
import com.cs.wit.persistence.repository.AgentStatusRepository;
import com.cs.wit.persistence.repository.AttachmentRepository;
import com.cs.wit.persistence.repository.SNSAccountRepository;
import com.cs.wit.persistence.repository.StreamingFileRepository;
import com.cs.wit.socketio.message.ChatMessage;
import com.cs.wit.socketio.message.Message;
import com.cs.wit.util.HashMapUtils;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class AgentProxy {
    private final static Logger logger = LoggerFactory.getLogger(AgentProxy.class);
    @NonNull
    private final ACDPolicyService acdPolicyService;
    @NonNull
    private final AttachmentRepository attachementRes;
    @NonNull
    private final JpaBlobHelper jpaBlobHelper;
    @NonNull
    private final StreamingFileRepository streamingFileRepository;
    @NonNull
    private final PeerSyncIM peerSyncIM;
    @NonNull
    private final SNSAccountRepository snsAccountRes;
    @NonNull
    private final Cache cache;
    @NonNull
    private final AgentStatusRepository agentStatusRes;
    @Value("${web.upload-path}")
    private String webUploadPath;

    /**
     * ?????????????????????????????????
     * ?????????ACD
     */
    public void ready(final User user, final AgentStatus agentStatus, final boolean busy) {
        agentStatus.setOrgi(user.getOrgi());
        agentStatus.setUserid(user.getId());
        agentStatus.setUsername(user.getUname());
        agentStatus.setAgentno(user.getId());
        agentStatus.setLogindate(new Date());
        agentStatus.setOrgi(agentStatus.getOrgi());
        agentStatus.setUpdatetime(new Date());
        agentStatus.setSkills(user.getSkills());
        // TODO ??????busy???????????????????????????AgentStatus maxuser??????users??????
        // ????????????????????????????????????????????????????????????????????????
        agentStatus.setBusy(busy);
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(agentStatus.getOrgi());
        agentStatus.setMaxusers(sessionConfig.getMaxuser());

        /*
         * ????????????????????????
         */
        agentStatus.setUsers(
                cache.getInservAgentUsersSizeByAgentnoAndOrgi(agentStatus.getAgentno(), agentStatus.getOrgi()));
        agentStatus.setStatus(MainContext.AgentStatusEnum.READY.toString());

        logger.info(
                "[ready] set agent {}, status {}", agentStatus.getAgentno(),
                MainContext.AgentStatusEnum.READY.toString());

        // ???????????????
        agentStatusRes.save(agentStatus);
    }


    /**
     * ???????????????????????????
     */
    public void sendChatMessageByAgent(final ChatMessage chatMessage, final AgentUser agentUser) {
        Message outMessage = new Message();
        outMessage.setMessage(chatMessage.getMessage());
        outMessage.setCalltype(chatMessage.getCalltype());
        outMessage.setAgentUser(agentUser);

        // ??????SNSAccount??????
        if (StringUtils.isNotBlank(agentUser.getAppid())) {
            snsAccountRes.findOneBySnsTypeAndSnsIdAndOrgi(
                    agentUser.getChannel(), agentUser.getAppid(), agentUser.getOrgi()).ifPresent(
                    outMessage::setSnsAccount);
        }

        outMessage.setContextid(chatMessage.getContextid());
        outMessage.setAttachmentid(chatMessage.getAttachmentid());
        outMessage.setMessageType(chatMessage.getMsgtype());
        outMessage.setCreatetime(Constants.DISPLAY_DATE_FORMATTER.format(chatMessage.getCreatetime()));
        outMessage.setChannelMessage(chatMessage);

        // ???????????????????????????(?????????????????????????????????????????????????????????)
        peerSyncIM.send(
                MainContext.ReceiverType.VISITOR,
                MainContext.ChannelType.toValue(agentUser.getChannel()),
                agentUser.getAppid(),
                MainContext.MessageType.MESSAGE,
                chatMessage.getTouser(),
                outMessage,
                true);

        // ??????????????????????????????????????????????????????
        peerSyncIM.send(
                MainContext.ReceiverType.AGENT,
                MainContext.ChannelType.WEBIM,
                agentUser.getAppid(),
                MainContext.MessageType.MESSAGE,
                agentUser.getAgentno(),
                outMessage,
                true);
    }

    /**
     * ???????????????????????????????????????????????????
     */
    public void sendFileMessageByAgent(final User creator, @Nullable final AgentUser agentUser, final MultipartFile multipart, final StreamingFile sf) {
        // ?????????
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFilename(multipart.getOriginalFilename());
        chatMessage.setFilesize((int) multipart.getSize());
        chatMessage.setAttachmentid(sf.getId());
        chatMessage.setMessage(sf.getFileUrl());
        chatMessage.setId(MainUtils.getUUID());
        if (agentUser != null) {
            chatMessage.setContextid(agentUser.getContextid());
            chatMessage.setAgentserviceid(agentUser.getAgentserviceid());
            chatMessage.setChannel(agentUser.getChannel());
            chatMessage.setUsession(agentUser.getUserid());
            chatMessage.setAppid(agentUser.getAppid());
        }
        chatMessage.setUserid(creator.getId());
        chatMessage.setOrgi(creator.getOrgi());
        chatMessage.setCreater(creator.getId());
        chatMessage.setUsername(creator.getUname());

        chatMessage.setCalltype(MainContext.CallType.OUT.toString());
        if (agentUser != null && StringUtils.isNotBlank(agentUser.getAgentno())) {
            chatMessage.setTouser(agentUser.getUserid());
        }

        if (multipart.getContentType() != null && multipart.getContentType().contains(Constants.ATTACHMENT_TYPE_IMAGE)) {
            chatMessage.setMsgtype(MainContext.MediaType.IMAGE.toString());
        } else {
            chatMessage.setMsgtype(MainContext.MediaType.FILE.toString());
        }

        Message outMessage = new Message();
        outMessage.setCalltype(chatMessage.getCalltype());
        outMessage.setMessage(sf.getFileUrl());

        if (agentUser != null && !StringUtils.equals(
                agentUser.getStatus(), MainContext.AgentUserStatusEnum.END.toString())) {
            // ????????????
            outMessage.setFilename(multipart.getOriginalFilename());
            outMessage.setFilesize((int) multipart.getSize());
            outMessage.setChannelMessage(chatMessage);
            outMessage.setAgentUser(agentUser);
            outMessage.setCreatetime(Constants.DISPLAY_DATE_FORMATTER.format(new Date()));
            outMessage.setMessageType(chatMessage.getMsgtype());

            /*
             * ????????????????????????
             */
            // ?????????????????????
            peerSyncIM.send(MainContext.ReceiverType.VISITOR,
                    MainContext.ChannelType.toValue(agentUser.getChannel()),
                    agentUser.getAppid(), MainContext.MessageType.MESSAGE,
                    agentUser.getUserid(),
                    outMessage,
                    true);

            // ?????????????????????
            peerSyncIM.send(MainContext.ReceiverType.AGENT,
                    MainContext.ChannelType.WEBIM,
                    agentUser.getAppid(),
                    MainContext.MessageType.MESSAGE,
                    agentUser.getAgentno(), outMessage, true);

        } else {
            logger.warn("[sendFileMessageByAgent] agent user chat is end, disable forward files.");
        }
    }


    /**
     * ???http???multipart??????????????????
     */
    public StreamingFile saveFileIntoMySQLBlob(final User creator, final MultipartFile multipart) throws
            IOException, CSKefuException {
        /*
         * ???????????????
         */
        File uploadDir = new File(webUploadPath, "upload");
        if (!uploadDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            uploadDir.mkdirs();
        }

        String fileid = MainUtils.getUUID();
        StreamingFile sf = new StreamingFile();

        /*
         * ???????????????
         */
        if (multipart.getContentType() != null && multipart.getContentType().contains(Constants.ATTACHMENT_TYPE_IMAGE)) {
            // ??????
            // process thumbnail
            File original = new File(webUploadPath, "upload/" + fileid + "_original");
            File thumbnail = new File(webUploadPath, "upload/" + fileid);
            FileCopyUtils.copy(multipart.getBytes(), original);
            MainUtils.processImage(thumbnail, original);
            sf.setThumbnail(jpaBlobHelper.createBlobWithFile(thumbnail));
            sf.setFileUrl("/res/image.html?id=" + fileid);
        } else {
            // ?????????????????????
            AttachmentFile attachmentFile = processAttachmentFile(creator, multipart, fileid);
            sf.setFileUrl("/res/file.html?id=" + attachmentFile.getId());
        }

        /*
         * ???????????????MySQL?????????
         */
        sf.setId(fileid);
        sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
        sf.setName(multipart.getOriginalFilename());
        sf.setMime(multipart.getContentType());

        streamingFileRepository.save(sf);

        return sf;
    }


    /**
     * ??????multi part???????????????
     */
    public AttachmentFile processAttachmentFile(
            final User owner, final MultipartFile multipart,
            final String fileid) throws IOException, CSKefuException {
        if (multipart.getSize() == 0) {
            throw new CSKefuException("Empty upload file size.");
        }

        // ???????????? ?????? ?????? ?????? ????????? ?????? ???????????????????????????????????????
        AttachmentFile attachmentFile = new AttachmentFile();
        attachmentFile.setCreater(owner.getId());
        attachmentFile.setOrgi(owner.getOrgi());
        attachmentFile.setModel(MainContext.ModelType.WEBIM.toString());
        attachmentFile.setFilelength((int) multipart.getSize());
        if (multipart.getContentType() != null && multipart.getContentType().length() > 255) {
            attachmentFile.setFiletype(multipart.getContentType().substring(0, 255));
        } else {
            attachmentFile.setFiletype(multipart.getContentType());
        }
        File uploadFile = new File(Objects.requireNonNull(multipart.getOriginalFilename()));
        if (uploadFile.getName().length() > 255) {
            attachmentFile.setTitle(uploadFile.getName().substring(0, 255));
        } else {
            attachmentFile.setTitle(uploadFile.getName());
        }
        if (StringUtils.isNotBlank(attachmentFile.getFiletype()) && attachmentFile.getFiletype().contains(Constants.ATTACHMENT_TYPE_IMAGE)) {
            attachmentFile.setImage(true);
        }
        attachmentFile.setFileid(fileid);
        attachementRes.save(attachmentFile);
        FileUtils.writeByteArrayToFile(new File(webUploadPath, "upload/" + fileid), multipart.getBytes());
        return attachmentFile;
    }

    /**
     * ????????????User???AgentStatus
     * ?????????????????????????????????????????????????????????
     */
    public AgentStatus resolveAgentStatusByAgentnoAndOrgi(final String agentno, final String orgi, final HashMap<String, String> skills) {
        logger.info(
                "[resolveAgentStatusByAgentnoAndOrgi] agentno {}, skills {}", agentno,
                HashMapUtils.concatKeys(skills, "|"));
        AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(agentno, orgi);

        if (agentStatus == null) {
            agentStatus = agentStatusRes.findOneByAgentnoAndOrgi(agentno, orgi).orElseGet(AgentStatus::new);
        }

        agentStatus.setSkills(skills);

        return agentStatus;
    }

}
