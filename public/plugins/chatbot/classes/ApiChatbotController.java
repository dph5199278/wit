/*
 * Copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.plugins.chatbot;

import com.chatopera.bot.exception.ChatbotException;
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.controller.api.request.RestUtils;
import com.chatopera.cc.model.Chatbot;
import com.chatopera.cc.model.CousultInvite;
import com.chatopera.cc.model.SNSAccount;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.ChatbotRepository;
import com.chatopera.cc.persistence.repository.ConsultInviteRepository;
import com.chatopera.cc.persistence.repository.SNSAccountRepository;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.chatopera.cc.proxy.OnlineUserProxy;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.SystemEnvHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;


/**
 * ???????????????
 * ???????????????????????????
 */
@RestController
@RequestMapping("/api/chatbot")
public class ApiChatbotController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiChatbotController.class);

    @Autowired
    private ChatbotRepository chatbotRes;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private ConsultInviteRepository consultInviteRes;

    private final static String botServiecProvider = SystemEnvHelper.getenv(
            ChatbotConstants.BOT_PROVIDER, ChatbotConstants.DEFAULT_BOT_PROVIDER);

    /**
     * ???????????????
     *
     * @param request
     * @param body
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "chatbot", access = true)
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body) throws Exception {
        final JsonObject j = (new JsonParser()).parse(body).getAsJsonObject();
        logger.info("[chatbot] operations payload {}", j.toString());
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "???????????????????????????");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "create":
                    // TODO ???????????????????????????????????????????????????????????????Payload???????????????ID ,??? organ
                    // 2019-10-10 Wang Hai Liang
                    json = create(j, logined.getId(), logined.getOrgi());
                    break;
                case "delete":
                    json = delete(j, logined.getId(), logined.getOrgi());
                    break;
                case "fetch":
                    json = fetch(
                            j, logined.getId(), logined.isAdmin(), orgi, super.getP(request), super.getPs(request));
                    break;
                case "update":
                    json = update(j);
                    break;
                case "enable":
                    json = enable(j, true);
                    break;
                case "disable":
                    json = enable(j, false);
                    break;
                case "enableaisuggest":
                    json = enableAiSuggest(j, true);
                    break;
                case "disableaisuggest":
                    json = enableAiSuggest(j, false);
                    break;
                case "vacant":
                    json = vacant(j, orgi, logined.isAdmin());
                    break;
                case "faq":
                    json = faq(j, orgi);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????");
            }
        }
        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param j
     * @param orgi
     * @return
     */
    private JsonObject vacant(final JsonObject j, String orgi, boolean isSuperuser) {
        JsonObject resp = new JsonObject();
        if (!isSuperuser) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "???????????????????????????????????????");
            return resp;
        }

        List<SNSAccount> records = snsAccountRes.findBySnstypeAndOrgi(Constants.CHANNEL_TYPE_WEBIM, orgi);
        JsonArray ja = new JsonArray();

        for (SNSAccount r : records) {
            if (!chatbotRes.existsBySnsAccountIdentifierAndOrgi(r.getSnsid(), orgi)) {
                JsonObject o = new JsonObject();
                o.addProperty("id", r.getId());
                o.addProperty("snsid", r.getSnsid());
                o.addProperty("snsType", r.getSnstype());
                o.addProperty("snsurl", r.getBaseURL());
                ja.add(o);
            }
        }

        resp.add("data", ja);
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        return resp;
    }

    /**
     * Enable Chatbot
     *
     * @param j
     * @return
     */
    private JsonObject enable(JsonObject j, boolean isEnabled) {
        JsonObject resp = new JsonObject();
        if ((!j.has("id")) || StringUtils.isBlank(j.get("id").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????id???????????????");
            return resp;
        }

        final String id = j.get("id").getAsString();
        Chatbot c = chatbotRes.findOne(id);

        if (c == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "??????????????????????????????");
            return resp;
        }

        try {
            com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(
                    c.getClientId(), c.getSecret(), botServiecProvider);
            if (bot.exists()) {
                c.setEnabled(isEnabled);
                chatbotRes.save(c);

                // ????????????????????????
                CousultInvite invite = OnlineUserProxy.consult(c.getSnsAccountIdentifier(), c.getOrgi());
                invite.setAi(isEnabled);
                consultInviteRes.save(invite);
                OnlineUserProxy.cacheConsult(invite);

                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.addProperty(RestUtils.RESP_KEY_DATA, "?????????");
            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_7);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????????????????????????????????????????????????????");
            }
        } catch (MalformedURLException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
            resp.addProperty(RestUtils.RESP_KEY_DATA, "??????????????????????????????????????????????????????");
        } catch (ChatbotException e) {

            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(RestUtils.RESP_KEY_DATA, "???????????????????????????????????????????????????");
        }
        return resp;
    }

    /**
     * Enable Chatbot ????????????
     *
     * @param j
     * @return
     */
    private JsonObject enableAiSuggest(JsonObject j, boolean isEnabled) {
        JsonObject resp = new JsonObject();
        if ((!j.has("id")) || StringUtils.isBlank(j.get("id").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????id???????????????");
            return resp;
        }

        final String id = j.get("id").getAsString();
        Chatbot c = chatbotRes.findOne(id);

        if (c == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "??????????????????????????????");
            return resp;
        }

        c.setAisuggest(isEnabled);
        chatbotRes.save(c);

        // ????????????????????????
        CousultInvite invite = OnlineUserProxy.consult(c.getSnsAccountIdentifier(), c.getOrgi());
        invite.setAisuggest(isEnabled);
        consultInviteRes.save(invite);
        OnlineUserProxy.cacheConsult(invite);

        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.addProperty(RestUtils.RESP_KEY_DATA, "?????????");

        return resp;
    }

    /**
     * ?????????????????????
     *
     * @param j
     * @return
     */
    private JsonObject update(JsonObject j) throws Exception {
        JsonObject resp = new JsonObject();
        if (!j.has("id")) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "???????????????id???????????????");
            return resp;
        }
        final String id = j.get("id").getAsString();

        Chatbot c = chatbotRes.findOne(id);

        if (c == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "??????????????????????????????");
            return resp;
        }

        // update clientId and secret
        if (j.has("clientId")) {
            c.setClientId(j.get("clientId").getAsString());
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????????????????clientId??????");
            return resp;
        }

        if (j.has("secret")) {
            c.setSecret(j.get("secret").getAsString());
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????????????????secret??????");
            return resp;
        }

        // ????????????????????????
        CousultInvite invite = OnlineUserProxy.consult(c.getSnsAccountIdentifier(), c.getOrgi());

        if (j.has("workmode") && Constants.CHATBOT_VALID_WORKMODELS.contains(j.get("workmode").getAsString())) {
            c.setWorkmode(j.get("workmode").getAsString());
            invite.setAifirst(!StringUtils.equals(Constants.CHATBOT_HUMAN_FIRST, c.getWorkmode()));
        }

        if (j.has("enabled")) {
            boolean enabled = j.get("enabled").getAsBoolean();
            c.setEnabled(enabled);
            invite.setAi(enabled);
        }

        try {
            com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(
                    c.getClientId(), c.getSecret(), botServiecProvider);
            if (bot.exists()) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                JsonObject data = new JsonObject();
                data.addProperty("id", c.getId());
                resp.add(RestUtils.RESP_KEY_DATA, data);
                resp.addProperty(RestUtils.RESP_KEY_MSG, "???????????????");
                JSONObject botDetails = bot.details();
                c.setDescription(botDetails.getJSONObject("data").getString("description"));
                c.setFallback(botDetails.getJSONObject("data").getString("fallback"));
                c.setWelcome(botDetails.getJSONObject("data").getString("welcome"));
                invite.setAisuccesstip(botDetails.getJSONObject("data").getString("welcome"));
                c.setName(botDetails.getJSONObject("data").getString("name"));
                invite.setAiname(c.getName());
            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                resp.addProperty(
                        RestUtils.RESP_KEY_ERROR,
                        "Chatopera???????????????????????????????????????????????????1??????????????????????????????????????????2???????????????????????????????????????3???clientId???Secret?????????????????????????????????????????????????????????????????????, ?????? https://bot.chatopera.com");
                return resp;
            }
        } catch (ChatbotException e) {
            logger.error("bot create error", e);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(
                    RestUtils.RESP_KEY_ERROR,
                    "Chatopera???????????????????????????????????????????????????1??????????????????????????????????????????2???????????????????????????????????????3???clientId???Secret???????????????");
            return resp;
        } catch (MalformedURLException e) {
            logger.error("bot request error", e);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_7);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????????????????" + e.toString());
            return resp;
        }

        c.setUpdatetime(new Date());
        chatbotRes.save(c);
        consultInviteRes.save(invite);
        OnlineUserProxy.cacheConsult(invite);

        return resp;
    }

    /**
     * ???????????????????????????
     *
     * @param j
     * @param id
     * @param orgi
     * @param p
     * @param ps
     * @return
     */
    private JsonObject fetch(JsonObject j, String id, boolean isSuperuser, String orgi, int p, int ps) {
        JsonObject resp = new JsonObject();
        if (!isSuperuser) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "????????????????????????????????????????????????????????????????????????");
            return resp;
        }

        Page<Chatbot> records = chatbotRes.findWithPagination(
                PageRequest.of(p, ps, Sort.Direction.DESC, "createtime"));

        JsonArray ja = new JsonArray();
        for (Chatbot c : records) {
            JsonObject o = new JsonObject();
            o.addProperty("id", c.getId());
            o.addProperty("name", c.getName());
            o.addProperty("primaryLanguage", c.getPrimaryLanguage());
            o.addProperty("description", c.getDescription());
            o.addProperty("fallback", c.getFallback());
            o.addProperty("welcome", c.getWelcome());
            o.addProperty("workmode", c.getWorkmode());
            o.addProperty("channel", c.getChannel());
            o.addProperty("snsid", c.getSnsAccountIdentifier());
            o.addProperty("enabled", c.isEnabled());

            // SNSAccount
            SNSAccount snsAccount = snsAccountRes.findBySnsidAndOrgi(c.getSnsAccountIdentifier(), orgi);
            if (snsAccount == null) {
                chatbotRes.delete(c); // ???????????????snsAccount????????????
                continue; // ???????????????snsAccount????????????
            }

            o.addProperty("snsurl", snsAccount.getBaseURL());

            // ?????????
            User user = userRes.findById(c.getCreater());
            if (user != null) {
                o.addProperty("creater", c.getCreater());
                o.addProperty("creatername", user.getUname());
            }

            ja.add(o);
        }

        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.add("data", ja);
        resp.addProperty("size", records.getSize()); // ????????????
        resp.addProperty("number", records.getNumber()); // ?????????
        resp.addProperty("totalPage", records.getTotalPages()); // ?????????
        resp.addProperty("totalElements", records.getTotalElements()); // ????????????????????????

        return resp;
    }

    /**
     * ?????????????????????
     *
     * @param j
     * @param uid
     * @param orgi
     * @return
     */
    private JsonObject delete(final JsonObject j, final String uid, final String orgi) {
        JsonObject resp = new JsonObject();
        if ((!j.has("id")) || StringUtils.isBlank(j.get("id").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "??????????????????????????????id???");
            return resp;
        }
        final String id = j.get("id").getAsString();

        Chatbot c = chatbotRes.findOne(id);
        if (c == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "???????????????????????????????????????????????????");
            return resp;
        }

        // ????????????????????????
        CousultInvite invite = OnlineUserProxy.consult(c.getSnsAccountIdentifier(), c.getOrgi());
        if (invite != null) {
            invite.setAi(false);
            invite.setAiname(null);
            invite.setAisuccesstip(null);
            invite.setAifirst(false);
            invite.setAiid(null);
            invite.setAisuggest(false);
            consultInviteRes.save(invite);
            OnlineUserProxy.cacheConsult(invite);
        }
        chatbotRes.delete(c);
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.addProperty(RestUtils.RESP_KEY_DATA, "???????????????");
        return resp;
    }

    /**
     * ?????????????????????
     *
     * @param j
     * @param creater
     * @param orgi
     * @return
     */
    private JsonObject create(final JsonObject j, final String creater, final String orgi) throws Exception {
        JsonObject resp = new JsonObject();
        String snsid = null;
        String workmode = null;
        String clientId = null;
        String secret = null;

        if ((!j.has("clientId")) || StringUtils.isBlank(j.get("clientId").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????????????????clientId??????");
            return resp;
        } else {
            clientId = j.get("clientId").getAsString();
        }

        if ((!j.has("secret")) || StringUtils.isBlank(j.get("secret").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????????????????secret??????");
            return resp;
        } else {
            secret = j.get("secret").getAsString();
        }

        if (!(j.has("workmode") && Constants.CHATBOT_VALID_WORKMODELS.contains(j.get("workmode").getAsString()))) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "???????????????????????????????????????workmode??????");
            return resp;
        } else {
            workmode = j.get("workmode").getAsString();
        }

        if ((!j.has("snsid")) || StringUtils.isBlank(j.get("snsid").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????????????????snsid??????");
            return resp;
        } else {
            snsid = j.get("snsid").getAsString();
            // #TODO ?????????webim
            if (!snsAccountRes.existsBySnsidAndSnstypeAndOrgi(snsid, Constants.CHANNEL_TYPE_WEBIM, orgi)) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????????????????snsid???????????????????????????");
                return resp;
            }

            if (chatbotRes.existsBySnsAccountIdentifierAndOrgi(snsid, orgi)) {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????????????????snsid?????????????????????????????????");
                return resp;
            }
        }

        if (chatbotRes.existsByClientIdAndOrgi(clientId, orgi)) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "????????????????????????????????????????????????????????????");
            return resp;
        }

        try {
            logger.info("create bot with url {}", botServiecProvider);
            com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(clientId, secret, botServiecProvider);

            if (bot.exists()) { // ?????????????????????clientId ??? Secret????????????
                // ????????????
                Chatbot c = new Chatbot();
                JSONObject botDetails = bot.details();
                c.setId(MainUtils.getUUID());
                c.setClientId(clientId);
                c.setSecret(secret);
                c.setBaseUrl(botServiecProvider);
                c.setDescription(botDetails.getJSONObject("data").getString("description"));
                c.setFallback(botDetails.getJSONObject("data").getString("fallback"));
                c.setPrimaryLanguage(botDetails.getJSONObject("data").getString("primaryLanguage"));
                c.setName(botDetails.getJSONObject("data").getString("name"));
                c.setWelcome(botDetails.getJSONObject("data").getString("welcome"));
                c.setCreater(creater);
                c.setOrgi(orgi);
                c.setChannel(Constants.CHANNEL_TYPE_WEBIM);
                c.setSnsAccountIdentifier(snsid);
                Date dt = new Date();
                c.setCreatetime(dt);
                c.setUpdatetime(dt);
                c.setWorkmode(workmode);

                // ???????????????
                boolean enabled = false;
                c.setEnabled(enabled);

                // ????????????????????????
                CousultInvite invite = OnlineUserProxy.consult(c.getSnsAccountIdentifier(), c.getOrgi());
                invite.setAi(enabled);
                invite.setAifirst(StringUtils.equals(Constants.CHATBOT_CHATBOT_FIRST, workmode));
                invite.setAiid(c.getId());
                invite.setAiname(c.getName());
                invite.setAisuccesstip(c.getWelcome());
                consultInviteRes.save(invite);
                OnlineUserProxy.cacheConsult(invite);
                chatbotRes.save(c);

                JsonObject data = new JsonObject();
                data.addProperty("id", c.getId());
                resp.add(RestUtils.RESP_KEY_DATA, data);
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                return resp;
            } else {
                // ????????????
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
                resp.addProperty(
                        RestUtils.RESP_KEY_ERROR, "Chatopera?????????????????????????????????????????????????????????, ?????? https://bot.chatopera.com");
                return resp;
            }
        } catch (ChatbotException e) {
            logger.error("bot create error", e);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(
                    RestUtils.RESP_KEY_ERROR,
                    "Chatopera???????????????????????????????????????????????????1??????????????????????????????????????????2???????????????????????????????????????3???clientId???Secret???????????????");
            return resp;
        } catch (MalformedURLException e) {
            logger.error("bot request error", e);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "Chatopera?????????????????????????????????????????????URL???");
            return resp;
        }
    }

    private JsonObject faq(final JsonObject j, String orgi) {
        JsonObject resp = new JsonObject();
        if ((!j.has("snsaccountid")) || StringUtils.isBlank(j.get("snsaccountid").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "?????????????????????snsaccountid???????????????");
            return resp;
        }

        final String snsaccountid = j.get("snsaccountid").getAsString();
        Chatbot c = chatbotRes.findBySnsAccountIdentifierAndOrgi(snsaccountid, orgi);

        if (c == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "??????????????????????????????");
            return resp;
        }

        String userId = j.get("userId").getAsString();
        String textMessage = j.get("textMessage").getAsString();

        try {
            com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(
                    c.getClientId(), c.getSecret(), botServiecProvider);

            JSONObject result = bot.faq(
                    userId,
                    textMessage,
                    Double.parseDouble(SystemEnvHelper.getenv(ChatbotConstants.THRESHOLD_FAQ_BEST_REPLY, "0.8")),
                    Double.parseDouble(SystemEnvHelper.getenv(ChatbotConstants.THRESHOLD_FAQ_SUGG_REPLY, "0.6"))
            );
            if (result.getInt("rc") == 0) {
                JsonParser jsonParser = new JsonParser();
                JsonElement data = jsonParser.parse(result.getJSONArray("data").toString());

                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.add(RestUtils.RESP_KEY_DATA, data);
            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
                resp.addProperty(RestUtils.RESP_KEY_DATA, "???????????????????????????????????????????????????");
            }
        } catch (
                MalformedURLException e) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_6);
            resp.addProperty(RestUtils.RESP_KEY_DATA, "??????????????????????????????????????????????????????");
        } catch (
                ChatbotException e) {

            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_5);
            resp.addProperty(RestUtils.RESP_KEY_DATA, "???????????????????????????????????????????????????");
        }
        return resp;
    }
}
