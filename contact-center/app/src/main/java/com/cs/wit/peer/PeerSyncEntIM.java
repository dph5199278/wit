package com.cs.wit.peer;

import com.cs.wit.basic.MainContext;
import com.cs.wit.basic.MainUtils;
import com.cs.wit.model.User;
import com.cs.wit.persistence.repository.ChatMessageRepository;
import com.cs.wit.persistence.repository.RecentUserRepository;
import com.cs.wit.socketio.client.NettyClients;
import com.cs.wit.socketio.message.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PeerSyncEntIM {
    private final static Logger logger = LoggerFactory.getLogger(
            PeerSyncEntIM.class);
    @NonNull
    private final ChatMessageRepository chatMessageRes;

    @NonNull
    private final RecentUserRepository recentUserRes;

    public void send(
            final String user,
            final String group,
            final String orgi,
            final MainContext.MessageType msgType,
            final ChatMessage data
    ) {
        logger.info(
                "[send] userid {}, group {}, group {}, msgType {}, outMessage {}", user, group, orgi, msgType, data);

        if (data.getType() == null) {
            data.setType("message");
        }

        data.setUserid(user);
//		data.setUsername(name);
        data.setId(MainUtils.getUUID());
        data.setUsession(user);
        data.setCalltype(MainContext.CallType.OUT.toString());

        if (!StringUtils.isBlank(group)) {    //如果是群聊
            data.setContextid(group);
            data.setChatype("group");
            data.setModel("entim");
            chatMessageRes.save(data);
            NettyClients.getInstance().sendEntIMGroupEventMessage(data.getUserid(), group, msgType.toString(), data);
        } else {    //单聊
            data.setContextid(data.getTouser());
            chatMessageRes.save(data);
            ChatMessage outMessage = new ChatMessage();
            BeanUtils.copyProperties(data, outMessage);
            NettyClients.getInstance().sendEntIMEventMessage(data.getUserid(), msgType.toString(), outMessage);    //同时将消息发送给自己
            data.setCalltype(MainContext.CallType.IN.toString());
            data.setContextid(user);
            data.setUserid(data.getTouser());
            data.setId(MainUtils.getUUID());
            chatMessageRes.save(data);    //每条消息存放两条，一个是我的对话记录 ， 另一条是对方的对话历史， 情况当前聊天记录的时候，只清理自己的
            NettyClients.getInstance().sendEntIMEventMessage(data.getTouser(), msgType.toString(), data);    //发送消息给目标用户

            recentUserRes.findByCreaterAndUserAndOrgi(data.getTouser(), new User(user), orgi).ifPresent(u -> {
                u.setNewmsg(u.getNewmsg() + 1);
                if (data.getMessage() != null && data.getMessage().length() > 50) {
                    u.setLastmsg(data.getMessage().substring(0, 50));
                } else {
                    u.setLastmsg(data.getMessage());
                }
                recentUserRes.save(u);
            });

        }
    }
}
