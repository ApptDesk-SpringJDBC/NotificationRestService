package com.telappoint.notification.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.telappoint.notification.common.constants.EmailNotifyStatusConstants;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.EmailSMSStatusResponse;
import com.telappoint.notification.service.NotifyService;

/**
 * 
 * @author Balaji N
 *
 */
@Controller
@RequestMapping("/onlinenotify")
public class FrontEndEmailNotifyController extends CommonController {
	@Autowired
	private NotifyService notifyService;
	
	private static final String JSP_MAIL_CONFIRMATION = "mail-confirmation";
	private static final String JSP_MAIL_CANCELLATION = "mail-cancellation";
    private static final String CLIENT_CODE = "clientCode";

	@RequestMapping(value = "/confirm", method = RequestMethod.GET)
	public ModelAndView confirmUserAppt(HttpServletRequest request) {
		String clientCode = request.getParameter("clientCode");
		String token = request.getParameter("token");
		String notifyId = request.getParameter("notifyId");
		Logger logger = null;
		try {
			logger = notifyService.getLogger(clientCode, "INFO");

			EmailSMSStatusResponse response = notifyService.updateEmailNotifyStatus(clientCode, logger, token,
					EmailNotifyStatusConstants.NOTIFY_EMAIL_CONFIRED.getNotifyStatus(), Long.valueOf(notifyId));

			ModelMap modelMap = new ModelMap();
			setStatus(modelMap, response);
			isClientLogoExists(modelMap, request.getContextPath(), clientCode);
			modelMap.addAttribute(CLIENT_CODE, clientCode);
			modelMap.addAttribute("emailSMSStatusResponse", response);
			return new ModelAndView(JSP_MAIL_CONFIRMATION, modelMap);
		} catch (TelAppointException tae) {
			logger.error("Error in confirmAppt request:" + tae, tae);
		}
		return null;
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.GET)
	public ModelAndView cancelUserAppt(HttpServletRequest request) {
		String clientCode = request.getParameter("clientCode");
		String token = request.getParameter("token");
		String notifyId = request.getParameter("notifyId");
		Logger logger = null;
		try {
			logger = notifyService.getLogger(clientCode, "INFO");
			EmailSMSStatusResponse response = notifyService.updateEmailNotifyStatus(clientCode, logger, token,
					EmailNotifyStatusConstants.NOTIFY_EMAIL_CANCELLED.getNotifyStatus(), Long.valueOf(notifyId));
			ModelMap modelMap = new ModelMap();
			setStatus(modelMap, response);
			isClientLogoExists(modelMap, request.getContextPath(), clientCode);
			modelMap.addAttribute(CLIENT_CODE, clientCode);
			modelMap.addAttribute("emailSMSStatusResponse", response);
			return new ModelAndView(JSP_MAIL_CANCELLATION, modelMap);
		} catch (TelAppointException tae) {
			logger.error("Error in confirmAppt request:" + tae, tae);
		}
		return null;
	}

	private void setStatus(ModelMap modelMap, EmailSMSStatusResponse response) {
		if (response.isResponseStatus())
			modelMap.addAttribute("status", "Success");
		else
			modelMap.addAttribute("status", "failure");
	}
	
	private void isClientLogoExists(ModelMap modelMap,String contextPath,String clietnCode){
		File file = new File(contextPath+"//images//"+clietnCode+".jpg");
		 if(file.exists())
			modelMap.addAttribute("imagExists", true);
		 else 		
			 modelMap.addAttribute("imagExists", false);
	}
}
