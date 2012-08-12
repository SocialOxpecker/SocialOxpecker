package com.sociallangoliers.web;

import com.sociallangoliers.services.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class SchedulerResource {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private final Scheduler scheduler;

    @Autowired
    public SchedulerResource(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format(template, name);
    }

    @RequestMapping(value = "/runnow", method = RequestMethod.GET)
    public String runNow() {
        scheduler.runnow();
        return "woot";
    }
}
