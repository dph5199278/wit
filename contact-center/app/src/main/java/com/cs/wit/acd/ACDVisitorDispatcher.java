/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.cs.wit.acd;

import com.cs.compose4j.Composer;
import com.cs.compose4j.exception.Compose4jRuntimeException;
import com.cs.wit.acd.basic.ACDComposeContext;
import com.cs.wit.acd.basic.IACDDispatcher;
import com.cs.wit.acd.middleware.visitor.ACDVisAllocatorMw;
import com.cs.wit.acd.middleware.visitor.ACDVisBindingMw;
import com.cs.wit.acd.middleware.visitor.ACDVisBodyParserMw;
import com.cs.wit.acd.middleware.visitor.ACDVisServiceMw;
import com.cs.wit.acd.middleware.visitor.ACDVisSessionCfgMw;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 处置访客分配
 */
@Component
public class ACDVisitorDispatcher implements IACDDispatcher {
    private final static Logger logger = LoggerFactory.getLogger(ACDVisitorDispatcher.class);

    /**
     * 为访客安排坐席
     */
    private Composer<ACDComposeContext> pipleline;

    @Autowired
    private ACDVisBodyParserMw acdVisBodyParserMw;

    @Autowired
    private ACDVisBindingMw acdVisBindingMw;

    @Autowired
    private ACDVisSessionCfgMw acdVisSessionCfgMw;

    @Autowired
    private ACDVisServiceMw acdVisServiceMw;

    @Autowired
    private ACDVisAllocatorMw acdVisAllocatorMw;

    @PostConstruct
    private void setup() {
        logger.info("[setup] setup ACD Visitor Dispatch Service ...");
        buildPipeline();
    }

    /**
     * 建立访客处理管道
     */
    private void buildPipeline() {
        pipleline = new Composer<ACDComposeContext>()
        .use(
            /**
             * 1) 设置基本信息
             */
            acdVisBodyParserMw,
            /**
             * 1) 绑定技能组或坐席(包括邀请时的坐席)
             */
            acdVisBindingMw,
            /**
             * 1) 坐席配置:工作时间段，有无就绪在线坐席
             *
             */
            acdVisSessionCfgMw,
            /**
             * 1）选择坐席，确定AgentService
             */
            acdVisServiceMw,
            /**
             * 1）根据策略筛选坐席
             */
            acdVisAllocatorMw
        );
    }

    @Override
    public void enqueue(final ACDComposeContext ctx) {
        try {
            pipleline.handle(ctx);
        } catch (Compose4jRuntimeException e) {
            logger.error("[enqueueVisitor] error", e);
        }
    }

    @Override
    public void dequeue(ACDComposeContext ctx) {

    }
}
