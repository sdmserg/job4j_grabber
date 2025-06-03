package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import ru.job4j.grabber.stores.Store;

public class HabrCareerGrab implements Job {
    private static final Logger LOG = Logger.getLogger(HabrCareerParse.class);

    @Override
    public void execute(JobExecutionContext context) {
        var store = (Store) context.getJobDetail().getJobDataMap().get("store");
        var parse = (Parse) context.getJobDetail().getJobDataMap().get("parse");
        try {
            parse.fetch().forEach(store::save);
        } catch (Exception ex) {
            LOG.error("Unexpected error occurred during job execution", ex);
        }
    }
}
