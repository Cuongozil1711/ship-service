package vn.clmart.manager_service.service;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import vn.clmart.manager_service.config.mail.Mail;
import vn.clmart.manager_service.config.mail.MailFormNew;
import vn.clmart.manager_service.dto.request.MailSendExport;
import vn.clmart.manager_service.utils.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;


public interface MailService {
    JavaMailSender getJavaMailSender();

    boolean sendEmailStatusExport(MailSendExport mailSendExport, File file);
}
