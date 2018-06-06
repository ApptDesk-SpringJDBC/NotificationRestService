package com.telappoint.notification.common.components;

import java.io.StringWriter;
import java.util.Properties;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.telappoint.notification.common.clientdb.dao.NotifyResvDAO;
import com.telappoint.notification.common.constants.PropertiesConstants;
import com.telappoint.notification.common.model.EmailRequest;
import com.telappoint.notification.common.model.EmailTemplate;
import com.telappoint.notification.common.utils.PropertyUtils;

/**
 * 
 * @author Balaji N
 *
 */

@Component
public class EmailComponent extends CommonComponent {

	@Autowired
	private NotifyResvDAO notifyResvDAO;

	//@Async("mailExecutor") - we will enable it later. 
	public void sendEmail(Logger logger, com.telappoint.notification.common.model.EmailRequest emailRequest) throws Exception {
		sendEmail(logger, emailRequest, null);
	}

	private void sendEmail(Logger logger, com.telappoint.notification.common.model.EmailRequest emailRequest, Multipart multipart) throws Exception {
		String mailHost = com.telappoint.notification.common.utils.PropertyUtils.getValueFromProperties("internal.mail.hostname", PropertiesConstants.NOTIFY_PHONE_REST_WS_PROP.getPropertyFileName());

		if (multipart == null) {
			// email body part
			multipart = new MimeMultipart("alternative");
		}
		MimeBodyPart emailBodyPart = new MimeBodyPart();
		System.out.println("EmailBody:" + emailRequest.getEmailBody());
		emailBodyPart.setContent(emailRequest.getEmailBody(), "text/html");
		multipart.addBodyPart(emailBodyPart);

		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(mailHost);
		if (emailRequest.isEmailThroughInternalServer() == false) {
			sender.setHost(PropertyUtils.getValueFromProperties("mail.smtp.hostname", PropertiesConstants.NOTIFY_PHONE_REST_WS_PROP.getPropertyFileName()));
			sender.setPort(Integer.valueOf(PropertyUtils.getValueFromProperties("mail.smtp.port", PropertiesConstants.NOTIFY_PHONE_REST_WS_PROP.getPropertyFileName())));
			sender.setUsername(PropertyUtils.getValueFromProperties("mail.smtp.user", PropertiesConstants.NOTIFY_PHONE_REST_WS_PROP.getPropertyFileName()));
			sender.setPassword(PropertyUtils.getValueFromProperties("mail.smtp.password", PropertiesConstants.NOTIFY_PHONE_REST_WS_PROP.getPropertyFileName()));
			sender.setJavaMailProperties(getSMTPMailProperties());
		}
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(emailRequest.getToAddress());
		if (emailRequest.getCcAddresses() != null && emailRequest.getCcAddresses().length > 0) {
			helper.setCc(emailRequest.getCcAddresses());
		}
		helper.setFrom(emailRequest.getFromAddress());
		helper.setSubject(emailRequest.getSubject());
		helper.setText(emailRequest.getEmailBody());
		message.setContent(multipart);
		sender.send(message);
	}

	public void setMailServerPreference(Logger logger, EmailRequest emailRequest) throws Exception {
		try {
			boolean isEmailThroughInternalServer = "true".equals(PropertyUtils.getValueFromProperties("EMAIL_THROUGH_INTERNAL_SERVER", PropertiesConstants.NOTIFY_PHONE_REST_WS_PROP.getPropertyFileName())) ? true
					: false;
			if (isEmailThroughInternalServer) {
				emailRequest.setFromAddress(PropertyUtils.getValueFromProperties("internal.mail.fromaddress", PropertiesConstants.NOTIFY_PHONE_REST_WS_PROP.getPropertyFileName()));
				emailRequest.setReplyAddress(PropertyUtils.getValueFromProperties("internal.mail.replyaddress", PropertiesConstants.NOTIFY_PHONE_REST_WS_PROP.getPropertyFileName()));
			} else {
				emailRequest.setFromAddress(PropertyUtils.getValueFromProperties("mail.fromaddress", PropertiesConstants.NOTIFY_PHONE_REST_WS_PROP.getPropertyFileName()));
				emailRequest.setReplyAddress(PropertyUtils.getValueFromProperties("mail.replyaddress", PropertiesConstants.NOTIFY_PHONE_REST_WS_PROP.getPropertyFileName()));
			}
			emailRequest.setEmailThroughInternalServer(isEmailThroughInternalServer);
		} catch (Exception e) {
			logger.error("Error in getEmailRequest method. ", e);
		}
	}

	private Properties getSMTPMailProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.auth", "false");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.debug", "false");
		return properties;
	}

	public String getEmailBody(Logger logger, EmailTemplate emailTemplate, Object data) {
		StringWriter sw = processTemplate(emailTemplate.getName(), emailTemplate.getBody(), data);
		return (sw != null) ? sw.toString() : "";
	}

	public String getEmailSubject(Logger logger, EmailTemplate emailTemplate, Object data) {
		StringWriter sw = processTemplate(emailTemplate.getName(), emailTemplate.getSubject(), data);
		return (sw != null) ? sw.toString() : "";
	}
}
