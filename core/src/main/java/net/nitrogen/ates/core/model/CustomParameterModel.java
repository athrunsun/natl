package net.nitrogen.ates.core.model;

import java.sql.SQLException;

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

    /**
     * There are 5 ways to insert parameters:
     * 
     * 1. create execution from test cases
     * 
     * 2. create execution from test groups
     * 
     * 3. modify test suite parameters
     * 
     * 4. create execution from test suite
     * 
     * 5. create execution from execution
     * 
     */
    public static final CustomParameterModel me = new CustomParameterModel();

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
            insertParametersSqlParams[i][2] = ExecutionModel.TABLE;
            insertParametersSqlParams[i][3] = executionId;
            insertParametersSqlParams[i][4] = intType[i];
        }

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Db.batch(insertParameterSql, insertParametersSqlParams, 500);
                return true;
            }
        });
    }

    private int[] convertType2Int(String[] type) {
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
