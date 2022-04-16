package com.cs.wit.peer;

import com.cs.wit.basic.Constants;
import com.cs.wit.socketio.message.ChatMessage;
import org.apache.commons.lang.StringUtils;

public class PeerUtils {

    /**
     * 过滤书写中的消息
     *
     * @param chatMessage
     * @return
     */
    public static boolean isMessageInWritting(final ChatMessage chatMessage) {
        return StringUtils.equals(
                chatMessage.getType(), Constants.IM_MESSAGE_TYPE_WRITING);
    }
}
