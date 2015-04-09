package net.nitrogen.ates.core.model;

import java.sql.Timestamp;

import net.nitrogen.ates.util.DateTimeUtil;

import org.joda.time.DateTime;

import com.jfinal.plugin.activerecord.Model;

public class FeedbackModel extends Model<FeedbackModel> {
    public static final String TABLE = "feedback";

    public class Fields {
        public static final String ID = "id";
        public static final String MESSAGE = "message";
        public static final String CONTACT_INFO = "contact_info";
        public static final String CREATE_DATE = "create_date";
        public static final String TYPE = "type";
    }

    public static final FeedbackModel me = new FeedbackModel();

    public long getId() {
        return this.getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public String getMessage() {
        return this.getStr(Fields.MESSAGE);
    }

    public void setMessage(String message) {
        this.set(Fields.MESSAGE, message);
    }

    public String getContactInfo() {
        return this.getStr(Fields.CONTACT_INFO);
    }

    public void setContactInfo(String contactInfo) {
        this.set(Fields.CONTACT_INFO, contactInfo);
    }

    public Timestamp getCreateDateTimestamp() {
        return this.getTimestamp(Fields.CREATE_DATE);
    }

    public DateTime getCreateDate() {
        Timestamp timestamp = this.getCreateDateTimestamp();
        return timestamp == null ? null : DateTimeUtil.fromSqlTimestamp(timestamp);
    }

    public void setCreateDateTimestamp(Timestamp t) {
        this.set(Fields.CREATE_DATE, t);
    }

    public void setCreateDate(DateTime createDate) {
        this.setCreateDateTimestamp(DateTimeUtil.toSqlTimestamp(createDate));
    }

    public int getType() {
        return this.getInt(Fields.TYPE);
    }

    public void setType(int type) {
        this.set(Fields.TYPE, type);
    }
}
