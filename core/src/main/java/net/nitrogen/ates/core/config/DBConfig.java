package net.nitrogen.ates.core.config;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import net.nitrogen.ates.core.model.*;

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
        arp.addMapping("queue_entry", QueueEntryModel.class);
        arp.addMapping("round", RoundModel.class);
        arp.addMapping("test_case", TestCaseModel.class);
        arp.addMapping("test_group", TestGroupModel.class);
        arp.addMapping("test_group-test_case", TestGroupTestCaseModel.class);
        arp.addMapping("test_suite", TestSuiteModel.class);
        arp.addMapping("test_suite-test_case", TestSuiteTestCaseModel.class);
        arp.addMapping("test_result", TestResultModel.class);
        arp.addMapping("slave", SlaveModel.class);
        arp.addMapping("project", ProjectModel.class);
        arp.addMapping("custom_env", CustomEnvModel.class);
    }
}
