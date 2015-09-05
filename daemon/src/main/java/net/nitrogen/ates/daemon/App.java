package net.nitrogen.ates.daemon;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import net.nitrogen.ates.core.config.DBConfig;
import net.nitrogen.ates.core.exec.ExecManager;
import net.nitrogen.ates.util.PropertiesUtil;
import org.joda.time.DateTime;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class App {
    public static void main(String[] args) {
        Properties props = PropertiesUtil.load("config.txt");
        DruidPlugin druidPlugin = DBConfig.createDruidPlugin(props.getProperty("jdbcUrl"), props.getProperty("dbuser"), props.getProperty("dbpassword"), 0, 0, 5);
        druidPlugin.start();
        String configName = String.format("ates_daemon_arp_config_%d", DateTime.now().getMillis());
        System.out.println("ConfigName:" + configName);
        ActiveRecordPlugin arp = DBConfig.createActiveRecordPlugin(druidPlugin, configName);
        arp.start();

        try {
            Logger log = LoggerFactory.getLogger(App.class);

            SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            sched.start();

            JobDetail job = JobBuilder.newJob(QueueEntryExecJob.class)
                    .withIdentity("job1", "group1")
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(10)
                            .repeatForever())
                    .build();

            sched.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            System.out.println(e);
        }

        // For debugging purpose
        //new ExecManager().fetchAndExecQueueEntry();
    }
}
