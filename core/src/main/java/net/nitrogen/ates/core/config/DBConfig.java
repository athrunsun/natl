package net.nitrogen.ates.core.config;

import net.nitrogen.ates.core.model.CustomParameterModel;
import net.nitrogen.ates.core.model.ExecutionModel;
import net.nitrogen.ates.core.model.FeedbackModel;
import net.nitrogen.ates.core.model.ProjectModel;
import net.nitrogen.ates.core.model.QueueEntryModel;
import net.nitrogen.ates.core.model.SlaveModel;
import net.nitrogen.ates.core.model.TestCaseModel;
import net.nitrogen.ates.core.model.TestGroupModel;
import net.nitrogen.ates.core.model.TestGroupTestCaseModel;
import net.nitrogen.ates.core.model.TestResultModel;
import net.nitrogen.ates.core.model.TestSuiteModel;
import net.nitrogen.ates.core.model.TestSuiteTestCaseModel;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;

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
    }
}
