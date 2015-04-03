package net.nitrogen.ates.core.model;

import java.sql.SQLException;
import java.util.List;

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

    public String getJvmParametersForExecution(long executionId) {
        StringBuffer params = new StringBuffer();
        for (CustomParameterModel model : findExecutionParameters(executionId, 0)) {
            params.append("-D");
            params.append(model.getKey());
            params.append("=");
            params.append(model.getValue());
            params.append(" ");
        }
        return params.toString().trim();
    }

    public String getJvmParametersForTestSuite(long testsuiteId) {
        StringBuffer params = new StringBuffer();
        for (CustomParameterModel model : findTestSuiteParameters(testsuiteId, 0)) {
            params.append("-D");
            params.append(model.getKey());
            params.append("=");
            params.append(model.getValue());
            params.append(" ");
        }
        return params.toString().trim();
    }

    public List<CustomParameterModel> findExecutionParameters(long executionId, int type) {
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
                0,
                Fields.TYPE,
                type,
                Fields.DOMAIN_VALUE), executionId);
    }

    public List<CustomParameterModel> findTestSuiteParameters(long testsuiteId, int type) {
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
                1,
                Fields.TYPE,
                type,
                Fields.DOMAIN_VALUE), testsuiteId);
    }

    public List<CustomParameterModel> findExecutionParameters(long executionId) {
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
                0,
                Fields.DOMAIN_VALUE), executionId);
    }

    public List<CustomParameterModel> findTestSuiteParameters(long testSuiteId) {
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
                1,
                Fields.DOMAIN_VALUE), testSuiteId);
    }

    public void cloneExecutionParameters(long sourceExecutionId, long targetExecutionId) {
        List<CustomParameterModel> paramModels = findExecutionParameters(sourceExecutionId);
        for (CustomParameterModel model : paramModels) {
            model.setDomainValue(targetExecutionId);
        }
        insertParameters(paramModels);
    }

    public void cloneExecutionParametersFromTestSuite(long testSuiteId, long targetExecutionId) {
        List<CustomParameterModel> paramModels = findTestSuiteParameters(testSuiteId);
        for (CustomParameterModel model : paramModels) {
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
            insertParametersSqlParams[i][4] = 0; // TODO for now, default type value as 0=JVM
        }

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Db.batch(insertParameterSql, insertParametersSqlParams, 500);
                return true;
            }
        });
    }

    public void insertExecutionParameters(String[] keys, String[] values, long executionId, String[] types) {
        int[] intType = convertType2Int(types);
        final int INSERT_PARAMETER_TABLE_PARAM_SIZE = 5;
        final String insertParameterSql = String.format(
                "INSERT INTO `%s` (`%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?,?,?,?,?);",
                TABLE,
                Fields.KEY,
                Fields.VALUE,
                Fields.DOMAIN_KEY,
                Fields.DOMAIN_VALUE,
                Fields.TYPE);
        final Object[][] insertParametersSqlParams = new Object[keys.length][INSERT_PARAMETER_TABLE_PARAM_SIZE];

        for (int i = 0; i < keys.length; i++) {
            insertParametersSqlParams[i][0] = keys[i];
            insertParametersSqlParams[i][1] = values[i];
            insertParametersSqlParams[i][2] = 0; // 0 for execution
            insertParametersSqlParams[i][3] = executionId;
            insertParametersSqlParams[i][4] = 0; // TODO for now, default type value as 0=JVM
        }

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Db.batch(insertParameterSql, insertParametersSqlParams, 500);
                return true;
            }
        });
    }

    public void overwriteTestSuiteParameters(String[] keys, String[] values, final long testsuiteId, String[] types) {
        final String deleteSql = String.format("DELETE FROM `%s` WHERE `%s`=%s AND `%s`=?", TABLE, Fields.DOMAIN_KEY, 1, // 1 for test suite
                Fields.DOMAIN_VALUE);

        int[] intType = convertType2Int(types);
        final int INSERT_PARAMETER_TABLE_PARAM_SIZE = 5;
        final String insertParameterSql = String.format(
                "INSERT INTO `%s` (`%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?,?,?,?,?);",
                TABLE,
                Fields.KEY,
                Fields.VALUE,
                Fields.DOMAIN_KEY,
                Fields.DOMAIN_VALUE,
                Fields.TYPE);
        final Object[][] insertParametersSqlParams = new Object[keys.length][INSERT_PARAMETER_TABLE_PARAM_SIZE];

        for (int i = 0; i < keys.length; i++) {
            insertParametersSqlParams[i][0] = keys[i];
            insertParametersSqlParams[i][1] = values[i];
            insertParametersSqlParams[i][2] = 1; // 1 for 'test suite'
            insertParametersSqlParams[i][3] = testsuiteId;
            insertParametersSqlParams[i][4] = 0; // TODO for now, default type value as 0=JVM
        }

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Db.update(deleteSql, testsuiteId);
                Db.batch(insertParameterSql, insertParametersSqlParams, 500);
                return true;
            }
        });

    }

    private int[] convertType2Int(String[] type) {
        if (type == null) {
            return null;
        }
        int[] intType = new int[type.length];
        for (int i = 0; i < type.length; i++) {
            String typeValue = type[i].toLowerCase();
            switch (typeValue) {
            case "jvm":
                intType[i] = 0;
                break;
            case "testng":
                intType[i] = 1;
                break;
            default:
                intType[i] = 0;
            }

        }
        return intType;
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
