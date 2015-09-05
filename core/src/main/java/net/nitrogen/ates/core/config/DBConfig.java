package net.nitrogen.ates.core.config;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;

import net.nitrogen.ates.core.model.custom_parameter.CustomParameterModel;
import net.nitrogen.ates.core.model.email.EmailModel;
import net.nitrogen.ates.core.model.execution.ExecutionModel;
import net.nitrogen.ates.core.model.feedback.FeedbackModel;
import net.nitrogen.ates.core.model.project.ProjectModel;
import net.nitrogen.ates.core.model.queue_entry.QueueEntryModel;
import net.nitrogen.ates.core.model.slave.SlaveModel;
import net.nitrogen.ates.core.model.test_case.TestCaseModel;
import net.nitrogen.ates.core.model.test_group.TestGroupModel;
import net.nitrogen.ates.core.model.test_group.TestGroupTestCaseModel;
import net.nitrogen.ates.core.model.test_result.TestResultModel;
import net.nitrogen.ates.core.model.test_suite.TestSuiteModel;
import net.nitrogen.ates.core.model.test_suite.TestSuiteTestCaseModel;

public class DBConfig {
    public static DruidPlugin createDruidPlugin(String jdbcUrl, String user, String password, int initialSize, int minIdle, int maxActive) {
        DruidPlugin druidPlugin = new DruidPlugin(jdbcUrl, user, password);
        druidPlugin.setInitialSize(initialSize);
        druidPlugin.setMinIdle(minIdle);
        druidPlugin.setMaxActive(maxActive);
        return druidPlugin;
    }

    public static ActiveRecordPlugin createActiveRecordPlugin(DruidPlugin druidPlugin) {
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        addActiveRecordPluginMapping(arp);
        return arp;
    }

    public static ActiveRecordPlugin createActiveRecordPlugin(DruidPlugin druidPlugin, String configName) {
        ActiveRecordPlugin arp = new ActiveRecordPlugin(configName, druidPlugin);
        addActiveRecordPluginMapping(arp);
        return arp;
    }

    private static void addActiveRecordPluginMapping(ActiveRecordPlugin arp) {
        arp.addMapping(QueueEntryModel.TABLE, QueueEntryModel.class);
        arp.addMapping(ExecutionModel.TABLE, ExecutionModel.class);
        arp.addMapping(TestCaseModel.TABLE, TestCaseModel.class);
        arp.addMapping(TestGroupModel.TABLE, TestGroupModel.class);
        arp.addMapping(TestGroupTestCaseModel.TABLE, TestGroupTestCaseModel.class);
        arp.addMapping(TestSuiteModel.TABLE, TestSuiteModel.class);
        arp.addMapping(TestSuiteTestCaseModel.TABLE, TestSuiteTestCaseModel.class);
        arp.addMapping(TestResultModel.TABLE, TestResultModel.class);
        arp.addMapping(SlaveModel.TABLE, SlaveModel.class);
        arp.addMapping(ProjectModel.TABLE, ProjectModel.class);
        arp.addMapping(CustomParameterModel.TABLE, CustomParameterModel.class);
        arp.addMapping(FeedbackModel.TABLE, FeedbackModel.class);
        arp.addMapping(EmailModel.TABLE, EmailModel.class);
    }
}
