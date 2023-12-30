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

package com.cs.wit.peer;

import com.cs.compose4j.Composer;
import com.cs.compose4j.exception.Compose4jRuntimeException;
import com.cs.wit.basic.MainContext.ChannelType;
import com.cs.wit.basic.MainContext.MessageType;
import com.cs.wit.basic.MainContext.ReceiverType;
import com.cs.wit.peer.im.BasePeerContextMw;
import com.cs.wit.socketio.message.Message;
import java.util.List;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 客服与访客间消息处理类
 * 1. 路由消息
 * 2. 保存并业务处理
 */
@Component
public class PeerSyncIM {

    private final static Logger logger = LoggerFactory.getLogger(
            PeerSyncIM.class);

    private Composer<PeerContext> composer;

    @Resource
    private List<BasePeerContextMw> peerContextMwList;

    @PostConstruct
    public void postConstruct() {
        composer = new Composer<>();

        // 加载中间件
        // 1000) 做发送前的准备工作
        // 2000) 向访客发送WebIM消息
        // 3000) 发送后的工作
        composer.use(peerContextMwList);
    }

    /**
     * 坐席与访客之间的对话
     *
     * @param receiverType 接收人类型
     * @param channelType  渠道类型
     * @param appid        渠道AppId
     * @param msgType      消息类型
     * @param touser       接收人ID
     * @param outMessage   发送内容
     * @param distribute   本机不存在连接，是否分发到其它机器
     */
    public void send(
            final ReceiverType receiverType,
            final ChannelType channelType,
            final String appid,
            final MessageType msgType,
            final String touser,
            final Message outMessage,
            final boolean distribute
                    ) {
        logger.info(
                "[send] instant message \n msgType {}, \n channelType {}, \n appId {}, \n receiverType {}, \n touser {}, \n distribute {} \n",
                msgType.toString(), channelType.toString(), appid, receiverType.toString(), touser,
                distribute);

        PeerContext ctx = new PeerContext();
        ctx.setReceiverType(receiverType);
        ctx.setAppid(appid);
        ctx.setMsgType(msgType);
        ctx.setChannel(channelType);
        ctx.setTouser(touser);
        ctx.setDist(distribute);
        ctx.setMessage(outMessage);

        try {
            composer.handle(ctx);
        } catch (Compose4jRuntimeException e) {
            logger.info("[send] error", e);
        }
    }
}
