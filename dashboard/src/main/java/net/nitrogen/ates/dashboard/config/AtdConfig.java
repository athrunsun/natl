package net.nitrogen.ates.dashboard.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.*;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import net.nitrogen.ates.core.config.DBConfig;
import net.nitrogen.ates.dashboard.controller.*;
import net.nitrogen.ates.dashboard.controller.admin.AdminIndexController;
import net.nitrogen.ates.dashboard.controller.admin.AdminProjectController;
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
        me.add("/round", RoundController.class, "/tpl/round");
        me.add("/queue", QueueController.class, "/tpl/queue");
        me.add("/testcase", TestCaseController.class, "/tpl/testcase");
        me.add("/testgroup", TestGroupController.class, "/tpl/testgroup");
        me.add("/project", ProjectController.class, "/tpl/project");
        me.add("/env", EnvController.class, "/tpl/env");
        me.add("/testresult", TestResultController.class, "/tpl/testresult");
        me.add("/admin", AdminIndexController.class);
        me.add("/admin/project", AdminProjectController.class, "/tpl/admin/project");
        me.add("/admin/slave", AdminSlaveController.class, "/tpl/admin/slave");
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
