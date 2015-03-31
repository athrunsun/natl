package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Model;

public class CustomParameterModel extends Model<CustomParameterModel> {
    public static final String TABLE = "custom_parameter";

    public class Fields {
        public static final String ID = "id";
        public static final String KEY = "key";
        public static final String VALUE = "value";
        public static final String DOMAIN_KEY = "domain_key";
        public static final String DOMAIN_VALUE = "domain_value";
        public static final String TYPE = "type";
    }

    public static final CustomParameterModel me = new CustomParameterModel();

    public long getId() {
        return this.getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public String getKey() {
        return this.getStr(Fields.KEY);
    }

    public void setKey(String key) {
        this.set(Fields.KEY, key);
    }

    public String getValue() {
        return this.getStr(Fields.VALUE);
    }

    public void setValue(String value) {
        this.set(Fields.VALUE, value);
    }

    public String getDomainKey() {
        return this.getStr(Fields.DOMAIN_KEY);
    }

    public void setDomainKey(String domainKey) {
        this.set(Fields.DOMAIN_KEY, domainKey);
    }

    public String getDomainValue() {
        return this.getStr(Fields.DOMAIN_VALUE);
    }

    public void setDomainValue(String domainValue) {
        this.set(Fields.DOMAIN_VALUE, domainValue);
    }

    public int getType() {
        return this.getInt(Fields.TYPE);
    }

    public void setType(int type) {
        this.set(Fields.TYPE, type);
    }
}
