package com.cs.wit.basic;

import com.cs.wit.model.SystemConfig;
import com.cs.wit.model.SystemMessage;
import com.cs.wit.persistence.repository.SystemMessageRepository;
import com.cs.wit.util.mail.MailSender;
import lombok.RequiredArgsConstructor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TextEncryptor {

    @Value("${application.security.password}")
    private String securityPass;

    private BasicTextEncryptor textEncryptor = new BasicTextEncryptor();

    @NonNull
    private final SystemMessageRepository systemMessageRepository;

    @PostConstruct
    public void setup() {
        textEncryptor.setPassword(securityPass);
    }

    /**
     * 加密字符串
     * @param str
     * @return
     */
    public String encryption(final String str) {
        return textEncryptor.encrypt(str);
    }

    /**
     * 解密字符串
     * @param str
     * @return
     */
    public String decryption(final String str) {
        return textEncryptor.decrypt(str);
    }

    /**
     * 发送邮件
     */
    public void sendMail(String email, String cc, String subject, String content, List<String> filenames) throws Exception {
        SystemConfig config = MainUtils.getSystemConfig();
        if (config != null && config.isEnablemail() && config.getEmailid() != null) {
            SystemMessage systemMessage = systemMessageRepository.findByIdAndOrgi(config.getEmailid(), config.getOrgi());
            MailSender sender = new MailSender(
                    systemMessage.getSmtpserver(), systemMessage.getMailfrom(), systemMessage.getSmtpuser(),
                    decryption(systemMessage.getSmtppassword()), systemMessage.getSeclev(), systemMessage.getSslport());
            if (email != null) {
                sender.send(email, cc, subject, content, filenames);
            }
        }
    }
}
