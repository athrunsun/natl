package net.nitrogen.ates.testimporter;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import net.nitrogen.ates.core.config.DBConfig;
import net.nitrogen.ates.core.entity.TestCase;
import net.nitrogen.ates.core.entity.TestGroup;
import net.nitrogen.ates.core.entity.TestGroupTestCase;
import net.nitrogen.ates.core.model.TestCaseModel;
import net.nitrogen.ates.core.model.TestGroupModel;
import net.nitrogen.ates.core.model.TestGroupTestCaseModel;
import net.nitrogen.ates.core.model.TestSuiteTestCaseModel;
import net.nitrogen.ates.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Importer {
	private long projectId;

	public Importer(long projId) {
		this.projectId = projId;
	}

	public void doImport(List<TestCase> testCasesToReload, List<TestGroup> testGroupsToReload, Map<String, List<String>> rawTestGroupTestCasesToReload) {
		Properties props = PropertiesUtil.load("config.txt");
		DruidPlugin druidPlugin = DBConfig.createDruidPlugin(props.getProperty("jdbcUrl"), props.getProperty("dbuser"), props.getProperty("dbpassword"), 0, 0, 10);
		druidPlugin.start();
		ActiveRecordPlugin arp = DBConfig.createActiveRecordPlugin(druidPlugin);
		arp.start();

		TestCaseModel.me.reloadTestCases(this.projectId, testCasesToReload);
		TestGroupModel.me.deleteTestGroupsAndRespectiveTestGroupTestCases(this.projectId);
		TestGroupModel.me.insertTestGroups(testGroupsToReload);
		List<TestGroup> testGroups = TestGroupModel.me.findTestGroups(this.projectId);
		List<TestGroupTestCase> testGroupTestCases = new ArrayList<TestGroupTestCase>();

		for (Map.Entry<String, List<String>> rawTestGroupTestCase : rawTestGroupTestCasesToReload.entrySet()) {
			TestGroup targetTestGroup = this.searchTestGroup(testGroups, rawTestGroupTestCase.getKey());

			for (String testName : rawTestGroupTestCase.getValue()) {
				TestGroupTestCase tg_tc = new TestGroupTestCase();
				tg_tc.setTestGroupId(targetTestGroup.getId());
				tg_tc.setTestName(testName);
				testGroupTestCases.add(tg_tc);
			}
		}

		TestGroupTestCaseModel.me.insertTestGroupTestCases(testGroupTestCases);
		TestSuiteTestCaseModel.me.deleteNonexistent(this.projectId);

		arp.stop();
		druidPlugin.stop();
	}

	private TestGroup searchTestGroup(List<TestGroup> testGroups, String name) {
		for (TestGroup testGroup : testGroups) {
			if (testGroup.getName().equals(name)) {
				return testGroup;
			}
		}

		throw new RuntimeException(String.format("Test group not found by name:'%s'", name));
	}
}
