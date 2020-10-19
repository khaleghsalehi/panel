package org.ironfox.panel.web;


import org.ironfox.panel.config.app;
import org.ironfox.panel.service.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.ExecutionException;


@Controller
public class Dashboard {
    private static final Logger log = LoggerFactory.getLogger(Dashboard.class);

    @Autowired
    Environment environment;

    @RequestMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.getName().equals(environment.getProperty("ironfox.username")))
            return "login";
        File f = new File(environment.getProperty("ironfox.path.bin") + "iron.conf");
        if (!f.exists() && !f.isDirectory()) {
            model.addAttribute("config", "no");
            log.info("config file not found or service not properly configured");
            model.addAttribute("secret", Control.getRandomString(64));
            model.addAttribute("key", Control.getRandomString(32));
            model.addAttribute("iv", Control.getRandomString(32));
            return "config";
        }
        return "index";
    }

    @RequestMapping("/config")
    public String config(Model model) {
        model.addAttribute("config", "yes");
        model.addAttribute("secret", Control.getRandomString(64));
        model.addAttribute("key", Control.getRandomString(32));
        model.addAttribute("iv", Control.getRandomString(32));
        return "config";
    }

    @RequestMapping("/login")
    public String login(Model model) throws ExecutionException {
        return "login";
    }

    @RequestMapping("/logout")
    public String logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }


    @RequestMapping("/document")
    public String document() {
        return "document";
    }

    @RequestMapping("/status")
    public String status(Model model) throws IOException, InterruptedException, ExecutionException, ParseException {
        int status = Control.execute(Control.IRONFOX_RUN_STATUS);
        if (status == 0) {
            //log.info("get status ironfox is running");
            model.addAttribute("server_status", "ON");
        } else {
            model.addAttribute("server_status", "OFF");
            //log.info("ironfox is off");
        }

        model.addAttribute("port", app.config.get("port"));
        model.addAttribute("rate", app.config.get("rate") + "/rps");
        model.addAttribute("protection_mode", app.config.get("protection_mode"));
        model.addAttribute("virtualHost", app.config.get("virtualHost"));
        model.addAttribute("userPassed", app.config.get("userPassed"));
        model.addAttribute("botBlocked", app.config.get("botBlocked"));
        model.addAttribute("version", app.config.get("version"));
        model.addAttribute("protection_status", app.config.get("protection_status"));
        model.addAttribute("anomaly_protection_status", app.config.get("anomaly_protection_status"));


        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date start = format.parse(app.config.get("startTime"));
        Date date2 = format.parse(String.valueOf(LocalTime.now()));
        long difference = (date2.getTime() - start.getTime());
        int days = (int) ((difference / 1000) / 3600) / 24;
        int hours = (int) (difference / 1000) / 3600;
        int remainder = (int) (difference / 1000) - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
        model.addAttribute("upTime", days + " Day " + hours + " Hour " + mins + " Minute " + secs + " Second");
        return "status";
    }
}
