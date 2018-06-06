
-- 17th oct
DROP TABLE IF EXISTS `dynamic_template_placeholder`;

CREATE TABLE `dynamic_template_placeholder` (
id  int(4) NOT NULL AUTO_INCREMENT,
category varchar(200) COLLATE utf8_unicode_ci NOT NULL,
table_name varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
table_column varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
`alias_name` varchar(600) COLLATE utf8_unicode_ci DEFAULT NULL,
`type` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
place_holder varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

commit;


INSERT INTO `dynamic_template_placeholder` (`category`,`table_name`, `table_column`,`alias_name`,`type`, `place_holder`) VALUES
	('notify_result', 'notify', 'include_audio_1','includeAudio1','varchar', 'includeAudio1'),
	('notify_result', 'notify', 'include_audio_2','includeAudio2','varchar', 'includeAudio2'),
	('notify_result', 'notify', 'due_date_time','dueDateTime','varchar', 'dueDateTime'),
	('notify_result', 'notify', 'include_audio_3','includeAudio3','varchar', 'includeAudio3'),
	('notify_result', 'notify', 'email','email','varchar','email'),
	('location_result', 'location', 'location_name_online','locationName','varchar','locationName'),
	('location_result', 'location', 'location_name_ivr_tts','locationNameIvrTts','varchar', 'locationNameIvrTts'),
	('location_result', 'location', 'location_name_ivr_audio','locationNameIvrAudio','varchar', 'locationNameIvrAudio'),
	('resource_result', 'resource', 'first_name','firstName','varchar','resourceFirstName'),
	('resource_result', 'resource', 'last_name','lastName','varchar', 'resourceLastName'),
	('resource_result', 'resource', 'email','email','varchar', 'resourceEmail'),
	('resource_result', 'resource', 'title','title','varchar', 'resourceTitle');
	
	

insert into `campaign_email_response_action` (`campaign_id`, `action_page`, `page_header`, `page_content`) values('1','confirm','Thank you for confirming your appointment','Your confirmation has been saved on our system and we appreciate your response. Thank you, again, for submitting your confirmation.<br><br>\r\nSincerely,<br><strong>AREVA</strong>');
insert into `campaign_email_response_action` (`campaign_id`, `action_page`, `page_header`, `page_content`) values('1','cancel','We are sorry that you have a conflict of schedule','Your report of conflict has been saved on our system and we appreciate your response. We will follow back up with you shortly to reschedule.<br><br>Thank you,<br><strong>AREVA</strong>');
	
INSERT INTO `campaign_sms_response_action` (`campaign_id`, `input_value`, `action`) VALUES (1, '1', 'CONFIRM');
INSERT INTO `campaign_sms_response_action` (`campaign_id`, `input_value`, `action`) VALUES (1, '2', 'CANCEL');


DROP TABLE IF EXISTS `sms_history`;
CREATE TABLE `sms_history` (
id  int(4) NOT NULL AUTO_INCREMENT,
sid varchar(500) COLLATE utf8_unicode_ci NOT NULL,
date_created timestamp COLLATE utf8_unicode_ci DEFAULT CURRENT_TIMESTAMP,
date_updated timestamp COLLATE utf8_unicode_ci DEFAULT CURRENT_TIMESTAMP,
date_sent timestamp COLLATE utf8_unicode_ci DEFAULT CURRENT_TIMESTAMP,
account_sid varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
`to` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
`from` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
`body` text COLLATE utf8_unicode_ci DEFAULT NULL,
`status` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
`price` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
`direction` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
`api_version` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;