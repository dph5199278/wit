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
package com.cs.wit.controller.resource;

import com.cs.wit.controller.Handler;
import com.cs.wit.proxy.OnlineUserProxy;
import com.cs.wit.util.Menu;
import freemarker.template.TemplateException;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class QuickReplyResourceController extends Handler{
	
	@RequestMapping("/res/quickreply")
    @Menu(type = "res" , subtype = "quickreply")
    public ModelAndView add(ModelMap map , HttpServletRequest request , @Valid String q) throws IOException, TemplateException {
		if(q==null){
			q = "" ;
		}
    	map.addAttribute("quickReplyList", OnlineUserProxy.search(q, super.getOrgi(request), super.getUser(request))) ;
        return request(super.createRequestPageTempletResponse("/public/quickreply"));
    }
}