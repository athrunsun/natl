package net.nitrogen.ates.daemon.email;

import org.joda.time.DateTime;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;

import net.nitrogen.ates.core.config.DBConfig;
import net.nitrogen.ates.util.PropertiesUtil;

import java.util.Properties;

public class EmailEngine {
    public static void main(String[] args) throws Exception {
        Logger log = LoggerFactory.getLogger(EmailEngine.class);
        Properties props = PropertiesUtil.load("config.txt");
        DruidPlugin druidPlugin = DBConfig.createDruidPlugin(
                props.getProperty("jdbcUrl"),
                props.getProperty("dbuser"),
                props.getProperty("dbpassword"),
                0,
                0,
                5);
        druidPlugin.start();
        String configName = String.format("ates_email_monitor_config_%d", DateTime.now().getMillis());
        log.info("ConfigName:" + configName);
        ActiveRecordPlugin arp = DBConfig.createActiveRecordPlugin(druidPlugin, configName);
        arp.start();

        try {
            SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            sched.start();

            JobDetail job = JobBuilder.newJob(EmailMonitorJob.class).withIdentity("job1", "group1").build();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever()).build();

            sched.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            System.out.println(e);
        }

    }

}
