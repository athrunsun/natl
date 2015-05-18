package net.nitrogen.ates.daemon.email;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import net.nitrogen.ates.core.enumeration.EmailStatus;
import net.nitrogen.ates.core.model.email.EmailModel;
import net.nitrogen.ates.core.model.email.EmailModel.Fields;
import net.nitrogen.ates.util.DateTimeUtil;
import net.nitrogen.ates.util.HotMail;
import net.nitrogen.ates.util.PropertiesUtil;

import java.util.Properties;

public class EmailMonitorJob implements Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.fetchAndSend();
    }

    private void fetchAndSend() {
        EmailModel entry = EmailModel.me.findUnsentEmail();
        if (entry == null) {
            return;
        }

        updateStatus(entry);
        sendEmail(entry);
    }

    private void updateStatus(EmailModel entry) {
        entry.setStatus(EmailStatus.SENT.getValue());
        entry.set(Fields.UPDATED_DATE, DateTimeUtil.toStringWithDefaultFormat(DateTime.now()));
        entry.update();
    }

    private void sendEmail(EmailModel entry) {
        Properties props = PropertiesUtil.load("config.txt");
        String user = props.getProperty("hotmailUser");
        String password = props.getProperty("hotmailPassword");
        HotMail hotmail = new HotMail(user, password);
        hotmail.setSubject(entry.getSubject());
        hotmail.setBody(entry.getMessage());
        hotmail.setFrom(user);
        hotmail.setTo(entry.getTo().split(";"));
        try {
            hotmail.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
