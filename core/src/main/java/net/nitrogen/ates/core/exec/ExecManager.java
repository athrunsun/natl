package net.nitrogen.ates.core.exec;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.nitrogen.ates.core.enumeration.CustomParameterDomainKey;
import net.nitrogen.ates.core.enumeration.CustomParameterType;
import net.nitrogen.ates.core.env.EnvParameter;
import net.nitrogen.ates.core.model.custom_parameter.CustomParameterModel;
import net.nitrogen.ates.core.model.project.ProjectModel;
import net.nitrogen.ates.core.model.queue_entry.QueueEntryModel;
import net.nitrogen.ates.core.model.slave.SlaveModel;
import net.nitrogen.ates.core.model.test_case.TestCaseModel;
import net.nitrogen.ates.util.StringUtil;

import java.io.IOException;
import java.util.List;

public class ExecManager {
    public static final String EXEC_PARAM_KEY_DEBUG = "nitrogen_atl_debug";
    public static final String EXEC_PARAM_KEY_ENTRY_ID = "nitrogen_atl_entryid";
    public static final String EXEC_PARAM_KEY_ENTRY_INDEX = "nitrogen_atl_entryindex";
    public static final String EXEC_PARAM_KEY_EXECUTION_ID = "nitrogen_atl_executionid";
    public static final String EXEC_PARAM_KEY_PROJECT_ID = "nitrogen_atl_projectid";

    public static final String TEST_RESULT_REPORTER_JAR_PATH = "C:\\ates\\lib\\ates-testresultreporter-jar-with-dependencies.jar";
    public static final String TEST_RESULT_REPORTER_CLASS_NAME = "net.nitrogen.ates.testresultreporter.TestResultListener";

    private Logger log = LoggerFactory.getLogger(ExecManager.class);

    public void fetchAndExecQueueEntry() {
        if (!SlaveModel.me.slaveExists(EnvParameter.machineName())) {
            SlaveModel.me.insertSlave(EnvParameter.machineName());
        }

        QueueEntryModel entry = QueueEntryModel.me.fetchEntry(EnvParameter.machineName());

        if (entry != null) {
            TestCaseModel testCase = TestCaseModel.me.findById(entry.getTestCaseId());

            List<CustomParameterModel> parameterModels = CustomParameterModel.me.findParameters(
                    CustomParameterDomainKey.EXECUTION,
                    entry.getExecutionId(),
                    CustomParameterType.JVM);

            CommandLine cmdLine = new CommandLine("java.exe");
            cmdLine.addArgument(String.format("-D%s=%s", EXEC_PARAM_KEY_DEBUG, "0"), false);
            cmdLine.addArgument(String.format("-D%s=%s", EXEC_PARAM_KEY_ENTRY_ID, String.valueOf(entry.getId())), false);
            cmdLine.addArgument(String.format("-D%s=%s", EXEC_PARAM_KEY_ENTRY_INDEX, String.valueOf(entry.getIndex())), false);
            cmdLine.addArgument(String.format("-D%s=%s", EXEC_PARAM_KEY_EXECUTION_ID, String.valueOf(entry.getExecutionId())), false);
            cmdLine.addArgument(String.format("-D%s=%s", EXEC_PARAM_KEY_PROJECT_ID, String.valueOf(entry.getProjectId())), false);

            for (CustomParameterModel parameterModel : parameterModels) {
                cmdLine.addArgument(String.format("-D%s=%s", parameterModel.getKey(), parameterModel.getValue()), false);
            }

            cmdLine.addArgument("-cp", false);

            cmdLine.addArgument(
                    String.format("\"%s;%s\"", ProjectModel.me.findJarWithDependencyName(entry.getProjectId()), TEST_RESULT_REPORTER_JAR_PATH),
                    false);

            cmdLine.addArgument("org.testng.TestNG", false);
            // cmdLine.addArgument("-testclass", false);
            // cmdLine.addArgument(testCase.getName().substring(0, testCase.getName().lastIndexOf(".")), false);
            cmdLine.addArgument("-methods", false);
            cmdLine.addArgument(testCase.getName(), false);
            cmdLine.addArgument("-listener", false);
            cmdLine.addArgument(TEST_RESULT_REPORTER_CLASS_NAME, false);
            // cmdLine.addArgument(entry.getParams(), false); // Function to be Added
            System.out.println("cmdLine.toString:" + cmdLine.toString());

            QueueEntryExecResultHandler resultHandler = new QueueEntryExecResultHandler(entry.getId());

            // ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
            Executor executor = new DefaultExecutor();
            executor.setWorkingDirectory(StringUtil.combinePath("C:/ates/project", String.valueOf(entry.getProjectId())));
            // executor.setExitValue(1);
            // executor.setWatchdog(watchdog);

            try {
                executor.execute(cmdLine, resultHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                resultHandler.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // For debug purpose
            // try {
            // Thread.sleep(10000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
        }
    }
}
