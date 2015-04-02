package net.nitrogen.ates.testresultreporter;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import net.nitrogen.ates.core.config.DBConfig;
import net.nitrogen.ates.core.enumeration.ExecResult;
import net.nitrogen.ates.core.env.EnvParameter;
import net.nitrogen.ates.core.model.TestResultModel;
import net.nitrogen.ates.util.PropertiesUtil;
import net.nitrogen.ates.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Properties;

public class TestResultReporter {
    public static final String DRIVER_ATTR = "driver";
    private static final String TESTCLASS_TESTMETHOD_DELIMITER = ".";
    private static final String NGINX_PORT = "8000";
    private static final String NGINX_PATH = "ates_test_result_screenshots";
    private static final String NGINX_REAL_PATH = "C:/ates/test_output_screenshots";

    public void report(ITestResult result, ExecResult status) throws ClassNotFoundException, SQLException, UnknownHostException {
        Properties props = PropertiesUtil.load("config.txt");
        DruidPlugin druidPlugin = DBConfig.createDruidPlugin(props.getProperty("jdbcUrl"), props.getProperty("dbuser"), props.getProperty("dbpassword"), 0, 0, 1);
        druidPlugin.start();

        String configName = String.format("ates_testresultreporter_arp_config_%d", DateTime.now().getMillis());
        System.out.println("ConfigName:" + configName);
        ActiveRecordPlugin arp = DBConfig.createActiveRecordPlugin(druidPlugin, configName);
        arp.start();
        TestResultModel.me.insertTestResult(this.prepareTestResult(result, status));
        arp.stop();
        druidPlugin.stop();
    }

    private TestResultModel prepareTestResult(ITestResult result, ExecResult status) throws UnknownHostException {
        TestResultModel testResult = new TestResultModel();
        testResult.setEntryId(EnvParameter.entryId());
        testResult.setTestName(String.format("%s%s%s", result.getTestClass().getName(), TESTCLASS_TESTMETHOD_DELIMITER, result.getMethod().getMethodName()));
        testResult.setSlaveName(EnvParameter.machineName());
        testResult.setStartTime(new DateTime(result.getStartMillis()));
        testResult.setEndTime(new DateTime(result.getEndMillis()));
        testResult.setMessage("");
        testResult.setStackTrace("");
        testResult.setScreenshotUrl("");
        testResult.setExecutionId(EnvParameter.executionId());
        testResult.setProjectId(EnvParameter.projectId());
        testResult.setEnv(StringUtil.isNullOrWhiteSpace(EnvParameter.env()) ? "" : EnvParameter.env());
        StringBuilder message = new StringBuilder();

        switch (status) {
            case FAILED:
                testResult.setExecResult(ExecResult.FAILED.getValue());

                if (result.getThrowable() != null) {
                    Throwable t = result.getThrowable();
                    StringWriter errors = new StringWriter();
                    t.printStackTrace(new PrintWriter(errors));
                    testResult.setStackTrace(errors.toString());
                    message.append(t.getMessage());
                }

                this.takeScreenshot(result, message, testResult);
                break;
            case PASSED:
                testResult.setExecResult(ExecResult.PASSED.getValue());
                break;
            case SKIPPED:
                testResult.setExecResult(ExecResult.SKIPPED.getValue());
                break;
            default:
                testResult.setExecResult(ExecResult.UNKNOWN.getValue());
                break;
        }

        testResult.setMessage(message.toString());
        return testResult;
    }

    private void takeScreenshot(ITestResult result, StringBuilder message, TestResultModel testResult) throws UnknownHostException {
        String fileName = String.format("%s.png", result.getName() + "_" + System.currentTimeMillis());
        String imageFolderPath = String.format("%s/%d", NGINX_REAL_PATH, testResult.getProjectId());
        String imagePath = String.format("%s/%d/%s", NGINX_REAL_PATH, testResult.getProjectId(), fileName);
        File folder = new File(imageFolderPath);
        folder.mkdirs();
        //String imagePath = String.format("%s%s", file.getAbsolutePath(), imageRelativePath);
        String imageUrl = String.format("http://%s:%s/%s/%d/%s", Inet4Address.getLocalHost().getHostAddress(), NGINX_PORT, NGINX_PATH, testResult.getProjectId(), fileName);
        ITestContext context = result.getTestContext();
        WebDriver driver = (WebDriver) context.getAttribute(DRIVER_ATTR + Thread.currentThread().getId());

        if (driver != null && !(driver.toString().contains("(null)"))) {
            message.append(String.format(" ; Current url:%s", driver.getCurrentUrl()));

            try {
                File f = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File saved = new File(imagePath);
                FileUtils.copyFile(f, saved);
                testResult.setScreenshotUrl(imageUrl);
            } catch (Exception e) {
                System.out.println("Error generating screenshot: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
