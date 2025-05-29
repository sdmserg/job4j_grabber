package ru.job4j.quartz;

import java.io.InputStream;
import java.util.Properties;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {
    public static void main(String[] args) {
        Properties properties = loadProperties();
        int interval;
        try {
            interval = Integer.parseInt(properties.getProperty("rabbit.interval"));
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Property 'rabbit.interval' must be integer", ex);
        }
        try {
            Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
            sched.start();
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData("param1", "Hello, Rabbit!")
                    .usingJobData("param2", 42)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            sched.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            if (input == null) {
                throw new IllegalStateException("Property file 'rabbit.properties' not found");
            }
            properties.load(input);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load properties", ex);
        }
        return properties;
    }

    public static class Rabbit implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            String param1 = context.getJobDetail().getJobDataMap().getString("param1");
            int param2 = context.getJobDetail().getJobDataMap().getInt("param2");
            System.out.println("Rabbit runs here with param1: " + param1 + " and param2: " + param2);
        }
    }
}
