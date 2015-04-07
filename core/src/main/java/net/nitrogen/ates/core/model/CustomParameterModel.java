package net.nitrogen.ates.core.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.nitrogen.ates.core.enumeration.CustomParameterDomainKey;
import net.nitrogen.ates.core.enumeration.CustomParameterType;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
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

    public String getJvmParametersAsString(CustomParameterDomainKey domainKey, long domainValue) {
        StringBuffer params = new StringBuffer();
        for (CustomParameterModel model : findParameters(domainKey, domainValue, CustomParameterType.JVM)) {
            params.append("-D");
            params.append(model.getKey());
            params.append("=");
            params.append(model.getValue());
            params.append(" ");
        }
        return params.toString().trim();
    }

    public List<CustomParameterModel> findParameters(CustomParameterDomainKey key, long domainValue, CustomParameterType type) {
        return find(String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=%s AND `%s`=%s AND `%s`=?",
                Fields.ID,
                Fields.KEY,
                Fields.VALUE,
                Fields.DOMAIN_KEY,
                Fields.DOMAIN_VALUE,
                Fields.TYPE,
                TABLE,
                Fields.DOMAIN_KEY,
                key.getValue(),
                Fields.TYPE,
                type.getValue(),
                Fields.DOMAIN_VALUE), domainValue);
    }

    public List<CustomParameterModel> findParameters(CustomParameterDomainKey key, long domainValue) {
        return find(String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` WHERE `%s`=%s AND `%s`=?",
                Fields.ID,
                Fields.KEY,
                Fields.VALUE,
                Fields.DOMAIN_KEY,
                Fields.DOMAIN_VALUE,
                Fields.TYPE,
                TABLE,
                Fields.DOMAIN_KEY,
                key.getValue(),
                Fields.DOMAIN_VALUE), domainValue);
    }

    public void cloneParameters(CustomParameterDomainKey sourceDomainKey, long sourceDomainValue, long targetExecutionId) {
        List<CustomParameterModel> paramModels = findParameters(sourceDomainKey, sourceDomainValue);
        for (CustomParameterModel model : paramModels) {
            model.setDomainKey(CustomParameterDomainKey.EXECUTION.getValue()); // Target is always an execution
            model.setDomainValue(targetExecutionId);
        }
        insertParameters(paramModels);
    }

    public void insertParameters(List<CustomParameterModel> models) {
        final int INSERT_PARAMETER_TABLE_PARAM_SIZE = 5;
        final String insertParameterSql = String.format(
                "INSERT INTO `%s` (`%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?,?,?,?,?);",
                TABLE,
                Fields.KEY,
                Fields.VALUE,
                Fields.DOMAIN_KEY,
                Fields.DOMAIN_VALUE,
                Fields.TYPE);
        final Object[][] insertParametersSqlParams = new Object[models.size()][INSERT_PARAMETER_TABLE_PARAM_SIZE];

        for (int i = 0; i < models.size(); i++) {
            insertParametersSqlParams[i][0] = models.get(i).getKey();
            insertParametersSqlParams[i][1] = models.get(i).getValue();
            insertParametersSqlParams[i][2] = models.get(i).getDomainKey();
            insertParametersSqlParams[i][3] = models.get(i).getDomainValue();
            insertParametersSqlParams[i][4] = models.get(i).getType();
        }

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Db.batch(insertParameterSql, insertParametersSqlParams, 500);
                return true;
            }
        });
    }

    public void insertParameters(String[] keys, String[] values, CustomParameterDomainKey domainKey, long domainValue, String[] types) {
        insertParameters(initModels(keys, values, domainKey, domainValue, types));
    }

    private List<CustomParameterModel> initModels(String[] keys, String[] values, CustomParameterDomainKey domainKey, long domainValue, String[] types) {
        List<CustomParameterModel> customParameterModels = new ArrayList<CustomParameterModel>();
        CustomParameterType[] type = convertType2Enum(types);

        for (int i = 0; i < keys.length; i++) {
            CustomParameterModel model = new CustomParameterModel();
            model.setDomainKey(domainKey.getValue());
            model.setDomainValue(domainValue);
            // model.setType(type[i].getValue());
            model.setType(CustomParameterType.JVM.getValue()); // TODO default to JVM
            model.setKey(keys[i]);
            model.setValue(values[i]);
            customParameterModels.add(model);
        }
        return customParameterModels;
    }

    public void overwriteTestSuiteParameters(String[] keys, String[] values, final long testsuiteId, String[] types) {
        final String deleteSql = String.format(
                "DELETE FROM `%s` WHERE `%s`=%s AND `%s`=?",
                TABLE,
                Fields.DOMAIN_KEY,
                CustomParameterDomainKey.TEST_SUITE.getValue(),
                Fields.DOMAIN_VALUE);
        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Db.update(deleteSql, testsuiteId);
                return true;
            }
        });

        insertParameters(keys, values, CustomParameterDomainKey.TEST_SUITE, testsuiteId, types);
    }

    private CustomParameterType[] convertType2Enum(String[] type) {
        if (type == null) {
            return null;
        }
        CustomParameterType[] types = new CustomParameterType[type.length];
        for (int i = 0; i < type.length; i++) {
            String typeValue = type[i].toLowerCase();
            switch (typeValue) {
            case "jvm":
                types[i] = CustomParameterType.JVM;
                break;
            case "testng":
                types[i] = CustomParameterType.TESTNG;
                break;
            default:
                types[i] = CustomParameterType.JVM;
            }

        }
        return types;
    }

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

    public int getDomainKey() {
        return this.getInt(Fields.DOMAIN_KEY);
    }

    public void setDomainKey(int domainKey) {
        this.set(Fields.DOMAIN_KEY, domainKey);
    }

    public Long getDomainValue() {
        return this.getLong(Fields.DOMAIN_VALUE);
    }

    public void setDomainValue(long domainValue) {
        this.set(Fields.DOMAIN_VALUE, domainValue);
    }

    public int getType() {
        return this.getInt(Fields.TYPE);
    }

    public void setType(int type) {
        this.set(Fields.TYPE, type);
    }
}
