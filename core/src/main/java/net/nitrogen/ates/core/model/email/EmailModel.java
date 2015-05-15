package net.nitrogen.ates.core.model.email;

import org.joda.time.DateTime;

import com.jfinal.plugin.activerecord.Model;

import net.nitrogen.ates.core.enumeration.EmailStatus;
import net.nitrogen.ates.core.enumeration.EmailType;
import net.nitrogen.ates.core.model.custom_parameter.CustomParameterModel;
import net.nitrogen.ates.core.model.custom_parameter.ProjectEmailSetting;
import net.nitrogen.ates.util.DateTimeUtil;

import java.sql.Timestamp;
import java.util.List;

public class EmailModel extends Model<EmailModel> {
    public static final String TABLE = "email";

    public class Fields {
        public static final String ID = "id";
        public static final String FROM = "from";
        public static final String TO = "to";
        public static final String CC = "cc";
        public static final String SUBJECT = "subject";
        public static final String MESSAGE = "message";
        public static final String STATUS = "status";
        public static final String UPDATED_DATE = "updated_date";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
    }

    public static final EmailModel me = new EmailModel();

    public void insertEmailForSending(long executionId) {
        ProjectEmailSetting settings = CustomParameterModel.me.getExecutionEmailSettings(executionId);
        if (settings.isEmailEnabled()) {
            if (settings.isSendWhenExecutionStarted()) {
                EmailModel model = new EmailModel();
                model.set(Fields.TO, settings.getDefaultRecipients()).set(Fields.STATUS, EmailStatus.READY.getValue())
                        .set(Fields.TYPE, EmailType.EXECUTION.getValue()).set(Fields.VALUE, executionId).setUpdatedDate(DateTime.now());
                model.setSubject("[NATL] Execution started: " + executionId);
                model.setMessage("Hi,\n\nThe execution is started.\n\nRegards,\nNitrogen Automation Test Lab");
                model.save();
            }
            if (settings.isSendWhenExecutionFinished()) {
                EmailModel model = new EmailModel();
                model.set(Fields.TO, settings.getDefaultRecipients()).set(Fields.STATUS, EmailStatus.STARTED.getValue())
                        .set(Fields.TYPE, EmailType.EXECUTION.getValue()).set(Fields.VALUE, executionId).setUpdatedDate(DateTime.now());
                model.save();
            }
        }
    }

    public EmailModel findUnsentEmail() {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?",
                Fields.ID,
                Fields.FROM,
                Fields.TO,
                Fields.CC,
                Fields.SUBJECT,
                Fields.MESSAGE,
                Fields.STATUS,
                Fields.UPDATED_DATE,
                Fields.TYPE,
                Fields.VALUE,
                TABLE,
                Fields.STATUS);

        return findFirst(sql, EmailStatus.READY.getValue());
    }

    public List<EmailModel> findEmails(int limit) {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` ORDER BY 1 DESC LIMIT %s",
                Fields.ID,
                Fields.FROM,
                Fields.TO,
                Fields.CC,
                Fields.SUBJECT,
                Fields.MESSAGE,
                Fields.STATUS,
                Fields.UPDATED_DATE,
                Fields.TYPE,
                Fields.VALUE,
                TABLE,
                limit);

        return find(sql);
    }

    public long getId() {
        return this.getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public String getFrom() {
        return this.getStr(Fields.FROM);
    }

    public void setFrom(String from) {
        this.set(Fields.FROM, from);
    }

    public String getTo() {
        return this.getStr(Fields.TO);
    }

    public void setTo(String to) {
        this.set(Fields.TO, to);
    }

    public String getCc() {
        return this.getStr(Fields.CC);
    }

    public void setCc(String cc) {
        this.set(Fields.CC, cc);
    }

    public String getSubject() {
        return this.getStr(Fields.SUBJECT);
    }

    public void setSubject(String subject) {
        this.set(Fields.SUBJECT, subject);
    }

    public String getMessage() {
        return this.getStr(Fields.MESSAGE);
    }

    public void setMessage(String message) {
        this.set(Fields.MESSAGE, message);
    }

    public int getStatus() {
        return this.getInt(Fields.STATUS);
    }

    public void setStatus(int status) {
        this.set(Fields.STATUS, status);
    }

    public Timestamp getUpdatedDateTimestamp() {
        return this.getTimestamp(Fields.UPDATED_DATE);
    }

    public DateTime getUpdatedDate() {
        Timestamp timestamp = this.getUpdatedDateTimestamp();
        return timestamp == null ? null : DateTimeUtil.fromSqlTimestamp(timestamp);
    }

    public void setUpdatedDateTimestamp(Timestamp t) {
        this.set(Fields.UPDATED_DATE, t);
    }

    public void setUpdatedDate(DateTime createDate) {
        this.setUpdatedDateTimestamp(DateTimeUtil.toSqlTimestamp(createDate));
    }

    public int getType() {
        return this.getInt(Fields.TYPE);
    }

    public void setType(int type) {
        this.set(Fields.TYPE, type);
    }

    public long getValue() {
        return this.getLong(Fields.VALUE);
    }

    public void setValue(long value) {
        this.set(Fields.VALUE, value);
    }
}
