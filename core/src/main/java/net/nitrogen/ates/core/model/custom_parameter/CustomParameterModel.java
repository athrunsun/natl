package net.nitrogen.ates.core.model.custom_parameter;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;

import net.nitrogen.ates.core.enumeration.CustomParameterDomainKey;
import net.nitrogen.ates.core.enumeration.CustomParameterType;
import net.nitrogen.ates.core.model.custom_parameter.ProjectEmailSetting.Keys;
import net.nitrogen.ates.core.model.execution.ExecutionModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public ProjectEmailSetting getProjectEmailSettings(long projectId) {
        return new ProjectEmailSetting(this.findParameters(CustomParameterDomainKey.PROJECT, projectId, CustomParameterType.EMAIL));
    }

    /**
     * Ideally, project.isEmailEnabled should have the highest priority.
     * 
     * If it's false, no email should be sent.
     * 
     * Else if true, then project.settings > execution.settings.
     * 
     * @param projectId
     * @param testsuiteId
     * @param executionId
     * @return
     */
    public ProjectEmailSetting getExecutionEmailSettings(long executionId) {
        ExecutionModel exeModel = ExecutionModel.me.findById(executionId);
        ProjectEmailSetting projectSettings = this.getProjectEmailSettings(exeModel.getProjectId());
        ProjectEmailSetting executionSettings = new ProjectEmailSetting(this.findParameters(
                CustomParameterDomainKey.EXECUTION,
                executionId,
                CustomParameterType.EMAIL));
        executionSettings.setEmailEnabled(projectSettings.isEmailEnabled());
        executionSettings.setSendWhenExecutionStarted(projectSettings.isSendWhenExecutionStarted());
        executionSettings.setSendWhenExecutionFinished(projectSettings.isSendWhenExecutionFinished());
        // There is one exception for defaultRecipients, which is execution > project
        if (executionSettings.getDefaultRecipients().isEmpty()) {
            executionSettings.setDefaultRecipients(projectSettings.getDefaultRecipients());
        }
        return executionSettings;
    }

    public void updateProjectEmailSettings(long projectId, ProjectEmailSetting settings) {
        // if everything is empty from client, we only need to update isEmailEnabled, and keep other field values.
        if (!settings.isEmailEnabled() && !settings.isSendWhenExecutionStarted() && !settings.isSendWhenExecutionFinished()
                && settings.getDefaultRecipients().isEmpty()) {
            ProjectEmailSetting originalSettings = this.getProjectEmailSettings(projectId);
            settings.setSendWhenExecutionStarted(originalSettings.isSendWhenExecutionStarted());
            settings.setSendWhenExecutionFinished(originalSettings.isSendWhenExecutionFinished());
            settings.setDefaultRecipients(originalSettings.getDefaultRecipients());
        }
        this.deleteParameters(CustomParameterDomainKey.PROJECT, projectId, CustomParameterType.EMAIL);

        List<CustomParameterModel> models = new ArrayList<CustomParameterModel>();
        models.add(new CustomParameterModel().set(Fields.DOMAIN_KEY, CustomParameterDomainKey.PROJECT.getValue()).set(Fields.DOMAIN_VALUE, projectId)
                .set(Fields.TYPE, CustomParameterType.EMAIL.getValue()).set(Fields.KEY, Keys.EMAIL_ENABLED).set(Fields.VALUE, settings.isEmailEnabled() + ""));
        models.add(new CustomParameterModel().set(Fields.DOMAIN_KEY, CustomParameterDomainKey.PROJECT.getValue()).set(Fields.DOMAIN_VALUE, projectId)
                .set(Fields.TYPE, CustomParameterType.EMAIL.getValue()).set(Fields.KEY, Keys.SEND_WHEN_EXECUTION_STARTED)
                .set(Fields.VALUE, settings.isSendWhenExecutionStarted() + ""));
        models.add(new CustomParameterModel().set(Fields.DOMAIN_KEY, CustomParameterDomainKey.PROJECT.getValue()).set(Fields.DOMAIN_VALUE, projectId)
                .set(Fields.TYPE, CustomParameterType.EMAIL.getValue()).set(Fields.KEY, Keys.SEND_WHEN_EXECUTION_FINISHED)
                .set(Fields.VALUE, settings.isSendWhenExecutionFinished() + ""));
        models.add(new CustomParameterModel().set(Fields.DOMAIN_KEY, CustomParameterDomainKey.PROJECT.getValue()).set(Fields.DOMAIN_VALUE, projectId)
                .set(Fields.TYPE, CustomParameterType.EMAIL.getValue()).set(Fields.KEY, Keys.DEFAULT_RECIPIENTS)
                .set(Fields.VALUE, settings.getDefaultRecipients()));

        insertParameters(models);
    }

    public void deleteParameters(final CustomParameterDomainKey key, final long domainValue, final CustomParameterType type) {
        final String deleteSql = String.format(
                "DELETE FROM `%s` WHERE `%s`=? AND `%s`=? AND `%s`=?",
                TABLE,
                Fields.DOMAIN_KEY,
                Fields.DOMAIN_VALUE,
                Fields.TYPE);

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Db.update(deleteSql, key.getValue(), domainValue, type.getValue());
                return true;
            }
        });
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

    private void insertParameters(List<CustomParameterModel> models) {
        if (models == null || models.size() == 0) {
            return;
        }
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

    public void insertParameters(Map<String, CustomParameterModel> rawCustomParameterMap, CustomParameterDomainKey domainKey, long domainValue) {
        insertParameters(initModels(rawCustomParameterMap, domainKey, domainValue));
    }

    private List<CustomParameterModel> initModels(Map<String, CustomParameterModel> rawCustomParameterMap, CustomParameterDomainKey domainKey, long domainValue) {
        for (Map.Entry<String, CustomParameterModel> keyValuePair : rawCustomParameterMap.entrySet()) {
            CustomParameterModel model = keyValuePair.getValue();
            model.setDomainKey(domainKey.getValue());
            model.setDomainValue(domainValue);
            model.setType(CustomParameterType.JVM.getValue()); // TODO default to JVM by now
        }

        return new ArrayList<CustomParameterModel>(rawCustomParameterMap.values());
    }

    public void overwriteTestSuiteParameters(Map<String, CustomParameterModel> rawCustomParameterMap, final long testsuiteId) {
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

        insertParameters(rawCustomParameterMap, CustomParameterDomainKey.TEST_SUITE, testsuiteId);
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
