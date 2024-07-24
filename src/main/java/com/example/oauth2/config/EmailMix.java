package com.example.oauth2.config;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailMix {
    private String myEmail;
    private String emailPass;
    private int count = 1;
    public EmailMix(String myEmail, String emailPass16Words, int con) {
        this.myEmail = myEmail;
        this.emailPass = emailPass16Words;
    }

    public boolean sendContentToVer2(String toEmail, String headerEmail, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailMix.this.myEmail, EmailMix.this.emailPass);
            }
        };
        Session session = Session.getInstance(props, auth);
        MimeMessage msg = new MimeMessage(session);

        try {
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(toEmail, "NoReply-JD"));
            msg.setReplyTo(InternetAddress.parse(toEmail, false));
            msg.setSubject(headerEmail, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(RecipientType.TO, InternetAddress.parse(toEmail, false));
            Transport.send(msg);
            return true;
        } catch (MessagingException var9) {
            System.out.println("Yêu cầu mật khẩu 16 ký tự từ xác minh 2 bước Google.");
            return false;
        } catch (UnsupportedEncodingException var10) {
            System.out.println("Yêu cầu mật khẩu 16 ký tự từ xác minh 2 bước Google.");
            return false;
        }
    }

    public boolean sendContentAndMultipartToVer2(String toEmail, String headerEmail, String body, List<String> dataSource) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailMix.this.myEmail, EmailMix.this.emailPass);
            }
        });
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(this.myEmail));
            message.setRecipients(RecipientType.TO, InternetAddress.parse(toEmail, false));
            message.setSubject(headerEmail);
            BodyPart headerEmailPart = new MimeBodyPart();
            headerEmailPart.setText(body);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(headerEmailPart);

            for(Iterator var10 = dataSource.iterator(); var10.hasNext(); ++this.count) {
                String source = (String)var10.next();
                MimeBodyPart bodyPart = new MimeBodyPart();
                DataSource dataSourceDetail = new FileDataSource(source);
                bodyPart.setDataHandler(new DataHandler(dataSourceDetail));
                bodyPart.setFileName("file" + this.count);
                multipart.addBodyPart(bodyPart);
            }

            message.setContent(multipart);
            Transport.send(message);
            return true;
        } catch (AddressException var14) {
            System.out.println("Yêu cầu mật khẩu 16 ký tự từ xác minh 2 bước Google.");
            return false;
        } catch (MessagingException var15) {
            System.out.println("Yêu cầu mật khẩu 16 ký tự từ xác minh 2 bước Google.");
            return false;
        }
    }

}