package net.nitrogen.ates.core.exec;

import net.nitrogen.ates.core.env.EnvParameter;
import net.nitrogen.ates.core.model.ProjectModel;
import net.nitrogen.ates.core.model.QueueEntryModel;
import net.nitrogen.ates.core.model.SlaveModel;
import net.nitrogen.ates.util.StringUtil;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExecManager {
    public static final String EXEC_PARAM_KEY_DEBUG = "nitrogen_ates_debug";
    public static final String EXEC_PARAM_KEY_ENTRY_ID = "nitrogen_ates_entryid";
    public static final String EXEC_PARAM_KEY_ENTRY_INDEX = "nitrogen_ates_entryindex";
    public static final String EXEC_PARAM_KEY_EXECUTION_ID = "nitrogen_ates_executionid";
    public static final String EXEC_PARAM_KEY_PROJECT_ID = "nitrogen_ates_projectid";
    public static final String EXEC_PARAM_KEY_ENV = "nitrogen_ates_env";

    public static final String TEST_RESULT_REPORTER_JAR_PATH = "C:\\ates\\lib\\ates-testresultreporter-jar-with-dependencies.jar";
    public static final String TEST_RESULT_REPORTER_CLASS_NAME = "net.nitrogen.ates.testresultreporter.TestResultListener";

    public void fetchAndExecQueueEntry() {
        if (!SlaveModel.me.slaveExists(EnvParameter.machineName())) {
            SlaveModel.me.insertSlave(EnvParameter.machineName());
        }

        QueueEntryModel entry = QueueEntryModel.me.fetchEntry(EnvParameter.machineName());

        if (entry != null) {
            //Map<String, String> envVars = new HashMap<>();
            String[] rawJvmOptions = entry.getJvmOptions().split(" -D");
            List<String> jvmOptions = new ArrayList<>();

            for(String rawJvmOption : rawJvmOptions) {
                rawJvmOption = rawJvmOption.trim();
                if(!StringUtil.isNullOrWhiteSpace(rawJvmOption)) {
                    if(rawJvmOption.startsWith("-D")) {
                        jvmOptions.add(rawJvmOption);
                    }else {
                        jvmOptions.add(String.format("-D%s", rawJvmOption));
                    }
                }
            }

            CommandLine cmdLine = new CommandLine("java.exe");
            cmdLine.addArgument(String.format("-D%s=%s", EXEC_PARAM_KEY_DEBUG, "0"), false);
            cmdLine.addArgument(String.format("-D%s=%s", EXEC_PARAM_KEY_ENTRY_ID, String.valueOf(entry.getId())), false);
            cmdLine.addArgument(String.format("-D%s=%s", EXEC_PARAM_KEY_ENTRY_INDEX, String.valueOf(entry.getIndex())), false);
            cmdLine.addArgument(String.format("-D%s=%s", EXEC_PARAM_KEY_EXECUTION_ID, String.valueOf(entry.getExecutionId())), false);
            cmdLine.addArgument(String.format("-D%s=%s", EXEC_PARAM_KEY_PROJECT_ID, String.valueOf(entry.getProjectId())), false);
            cmdLine.addArgument(String.format("-D%s=%s", EXEC_PARAM_KEY_ENV, entry.getEnv()), false);
            //cmdLine.addArgument(entry.getJvmOptions(), false);
            for(String jvmOption : jvmOptions) {
                cmdLine.addArgument(jvmOption, false);
            }

            cmdLine.addArgument("-cp", false);
            cmdLine.addArgument(String.format("\"%s;%s\"", ProjectModel.me.findJarWithDependencyName(entry.getProjectId()), TEST_RESULT_REPORTER_JAR_PATH), false);
            cmdLine.addArgument("org.testng.TestNG", false);
            cmdLine.addArgument("-testclass", false);
            cmdLine.addArgument(entry.getName().substring(0, entry.getName().lastIndexOf(".")), false);
            cmdLine.addArgument("-methods", false);
            cmdLine.addArgument(entry.getName(), false);
            cmdLine.addArgument("-listener", false);
            cmdLine.addArgument(TEST_RESULT_REPORTER_CLASS_NAME, false);
            cmdLine.addArgument("-excludegroups", false);
            cmdLine.addArgument("atesonly", false);
            cmdLine.addArgument(entry.getParams(), false);
            System.out.println("cmdLine.toString:" + cmdLine.toString());

            QueueEntryExecResultHandler resultHandler = new QueueEntryExecResultHandler(entry.getId());

            //ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
            Executor executor = new DefaultExecutor();
            executor.setWorkingDirectory(StringUtil.combinePath("C:/ates/project", String.valueOf(entry.getProjectId())));
            //executor.setExitValue(1);
            //executor.setWatchdog(watchdog);

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
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
