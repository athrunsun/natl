package net.nitrogen.ates.dashboard.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;

import net.nitrogen.ates.core.config.DBConfig;
import net.nitrogen.ates.dashboard.controller.AtesHelpController;
import net.nitrogen.ates.dashboard.controller.ExecutionController;
import net.nitrogen.ates.dashboard.controller.IndexController;
import net.nitrogen.ates.dashboard.controller.ProjectController;
import net.nitrogen.ates.dashboard.controller.QueueController;
import net.nitrogen.ates.dashboard.controller.SummaryController;
import net.nitrogen.ates.dashboard.controller.TestCaseController;
import net.nitrogen.ates.dashboard.controller.TestGroupController;
import net.nitrogen.ates.dashboard.controller.TestResultController;
import net.nitrogen.ates.dashboard.controller.TestSuiteController;
import net.nitrogen.ates.dashboard.controller.admin.AdminEmailController;
import net.nitrogen.ates.dashboard.controller.admin.AdminIndexController;
import net.nitrogen.ates.dashboard.controller.admin.AdminProjectController;
import net.nitrogen.ates.dashboard.controller.admin.AdminQueueController;
import net.nitrogen.ates.dashboard.controller.admin.AdminSlaveController;
import net.nitrogen.ates.dashboard.interceptor.MasterTplProjectListInterceptor;
import net.nitrogen.ates.dashboard.interceptor.ProjectPrefInterceptor;

public class AtdConfig extends JFinalConfig {
    @Override
    public void configConstant(Constants me) {
        loadPropertyFile("config.txt");
        me.setDevMode(getPropertyToBoolean("devMode", false));
    }

    @Override
    public void configRoute(Routes me) {
        me.add("/", IndexController.class);
        me.add("/summary", SummaryController.class, "/tpl/summary");
        me.add("/queue", QueueController.class, "/tpl/queue");
        me.add("/execution", ExecutionController.class, "/tpl/execution");
        me.add("/testcase", TestCaseController.class, "/tpl/testcase");
        me.add("/testsuite", TestSuiteController.class, "/tpl/testsuite");
        me.add("/testgroup", TestGroupController.class, "/tpl/testgroup");
        me.add("/project", ProjectController.class, "/tpl/project");
        me.add("/testresult", TestResultController.class, "/tpl/testresult");
        me.add("/admin", AdminIndexController.class);
        me.add("/admin/queue", AdminQueueController.class, "/tpl/admin/queue");
        me.add("/admin/project", AdminProjectController.class, "/tpl/admin/project");
        me.add("/admin/slave", AdminSlaveController.class, "/tpl/admin/slave");
        me.add("/admin/email", AdminEmailController.class, "/tpl/admin/email");
        me.add("/help", AtesHelpController.class, "/tpl/help");
    }

    @Override
    public void configPlugin(Plugins me) {
        DruidPlugin druidPlugin = DBConfig.createDruidPlugin(getProperty("jdbcUrl"), getProperty("dbuser"), getProperty("dbpassword"), 10, 10, 50);
        druidPlugin.addFilter(new StatFilter());
        WallFilter wall = new WallFilter();
        wall.setDbType("mysql");
        druidPlugin.addFilter(wall);
        me.add(druidPlugin);
        me.add(DBConfig.createActiveRecordPlugin(druidPlugin));
    }

    @Override
    public void configInterceptor(Interceptors me) {
        me.add(new ProjectPrefInterceptor());
        me.add(new MasterTplProjectListInterceptor());
    }

    @Override
    public void configHandler(Handlers me) {
        me.add(new ContextPathHandler("CPATH"));
        DruidStatViewHandler dvh = new DruidStatViewHandler("/druid");
        me.add(dvh);
    }
}
