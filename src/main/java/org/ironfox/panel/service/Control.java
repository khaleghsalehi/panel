package org.ironfox.panel.service;

import com.google.gson.Gson;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.ironfox.panel.config.JavascriptRender;
import org.ironfox.panel.config.app;
import org.ironfox.panel.model.ServiceConfig;
import org.ironfox.panel.repo.ServiceConfigRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;

@RestController
@Service
public class Control {
    @Autowired
    ServiceConfigRepo serviceConfigRepo;

    @Autowired
    Environment environment;


    private static final Logger log = LoggerFactory.getLogger(Control.class);


    private final static String[] START_IRONFOX = {"sudo", "./nginx", "-c", "sbin/iron.conf"};
    private final static String[] RELOAD_IRONFOX = {"sudo", "./nginx", "-s", "reload", "-c", "sbin/iron.conf"};
    private final static String[] STOP_IRONFOX = {"sudo", "./nginx", "-s", "stop", "-c", "sbin/iron.conf"};
    public final static String[] IRONFOX_RUN_STATUS = {"pidof", "-s", "nginx"};

    public final static String IRONFOX_PATH_BIN = "ironfox.path.bin";
    public final static String IRONFOX_PATH = "ironfox.path";
    public final static String IRONFOX_RATE_HASHMAP_SIZE = "ironfox.rate.hashmap.size";


    private final static String ANOMALY_PROTECTION_STATUS = "anomaly_protection_status";
    private final static String PROTECTION_STATUS = "protection_status";
    private final static String SERVER_STATUS = "server_status";
    private final static String PROTECTION_LEVEL_TAG = "level";
    private final static String PORT = "port";
    private final static String RATE_LIMITER = "rate";
    private final static String VIRTUAL_HOST = "virtualHost";


    private final static Gson gson = new Gson();


    public static String getRandomString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    public static String getHexString(int n) {
        String AlphaNumericString = "abcdef0123456789";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    public static int execute(String[] command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();
        return process.exitValue();
    }

    @GetMapping("/v1/start")
    public void start(HttpServletResponse response) throws IOException, InterruptedException {
        log.info("starting result {} ", execute(START_IRONFOX));
        app.config.put(SERVER_STATUS, "RUNNING");
        response.sendRedirect("/");

    }

    @GetMapping("/v1/reload")
    public void reload(HttpServletResponse response) throws IOException, InterruptedException {
        log.info("reload result {} ", execute(RELOAD_IRONFOX));
        app.config.put(SERVER_STATUS, "RELOADED/RUNNING");
        response.sendRedirect("/");
    }

    @GetMapping("/v1/stop")
    public void stop(HttpServletResponse response) throws IOException, InterruptedException {
        log.info("stopping result {} ", execute(STOP_IRONFOX));
        app.config.put(SERVER_STATUS, "STOP");

        response.sendRedirect("/");
    }

    @GetMapping("/v1/protectionOn")
    public void protectionOn(HttpServletResponse response) throws IOException, InterruptedException {


        FileUtils.modifyFile(environment.getProperty(IRONFOX_PATH_BIN) + "iron.conf",
                "bot_protection_manager off;", "bot_protection_manager on;");

        app.config.put(PROTECTION_STATUS, "ON");
        log.info("protection ON,,reload result {} ", execute(RELOAD_IRONFOX));
        response.sendRedirect("/");
    }

    @GetMapping("/v1/protectionOff")
    public void protectionOff(HttpServletResponse response) throws IOException, InterruptedException {
        app.config.put(PROTECTION_STATUS, "OFF");
        app.config.put(ANOMALY_PROTECTION_STATUS, "OFF");

        // if protection is off, the all of the switch down... WTF :/
        FileUtils.modifyFile(environment.getProperty(IRONFOX_PATH_BIN) + "iron.conf",
                "bot_protection_anomaly_detection on;",
                "bot_protection_anomaly_detection off;");
        FileUtils.modifyFile(environment.getProperty(IRONFOX_PATH_BIN) + "iron.conf",
                "bot_protection_manager on;",
                "bot_protection_manager off;");
        log.info("protection OFF, reload result {} ", execute(RELOAD_IRONFOX));
        response.sendRedirect("/");
    }

    @GetMapping("/v1/profilerOn")
    public void profilerOn(HttpServletResponse response) throws IOException, InterruptedException {
        // in this part, we MUST enable all of protection...
        app.config.put(PROTECTION_STATUS, "ON");
        app.config.put(ANOMALY_PROTECTION_STATUS, "ON");
        FileUtils.modifyFile(environment.getProperty(IRONFOX_PATH_BIN) + "iron.conf",
                "bot_protection_manager off;",
                "bot_protection_manager on;");
        FileUtils.modifyFile(environment.getProperty(IRONFOX_PATH_BIN) + "iron.conf",
                "bot_protection_anomaly_detection off;",
                "bot_protection_anomaly_detection on;");
        log.info("protection ON, reload result {} ", execute(RELOAD_IRONFOX));
        response.sendRedirect("/");
    }

    @GetMapping("/v1/profilerOff")
    public void profilerOff(HttpServletResponse response) throws IOException, InterruptedException {
        app.config.put(ANOMALY_PROTECTION_STATUS, "OFF");
        FileUtils.modifyFile(environment.getProperty(IRONFOX_PATH_BIN) + "iron.conf",
                "bot_protection_anomaly_detection on;",
                "bot_protection_anomaly_detection off;");
        log.info("protection OFF, reload result {} ", execute(RELOAD_IRONFOX));
        response.sendRedirect("/");
    }

    @PostMapping("/v1/config")
    public void config(@RequestParam(required = true, defaultValue = "443") int port,
                       @RequestParam(required = false, defaultValue = "hard") String mode,
                       @RequestParam(required = false, defaultValue = "5") String rps,
                       @RequestParam(required = true) String secret,
                       @RequestParam(required = true) String key,
                       @RequestParam(required = true) String iv,
                       @RequestParam(required = true) String virtualHost,
                       HttpServletResponse response) throws IOException, InterruptedException {

        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setPort(port);
        serviceConfig.setProtectionMode(mode);
        serviceConfig.setRPS(Integer.parseInt(rps));
        serviceConfig.setAesSecret(secret);
        serviceConfig.setAesKey(key);
        serviceConfig.setAesIv(iv);
        serviceConfig.setVirtualServer(virtualHost);
        serviceConfigRepo.save(serviceConfig);

        app.config.put(PROTECTION_LEVEL_TAG, mode.toUpperCase());
        app.config.put(RATE_LIMITER, rps.toUpperCase());
        app.config.put(PORT, String.valueOf(port));
        app.config.put(VIRTUAL_HOST, virtualHost);

        //todo .so path are hardcoded, fix it to dynamic paths
        Velocity.init();
        VelocityContext context = new VelocityContext();

        context.put("ironfox_path_bot_module", environment.getProperty(IRONFOX_PATH) + app.BOT_MODULE);
        context.put("ironfox_path_javascript_module", environment.getProperty(IRONFOX_PATH) + app.JAVA_SCRIPT_RENDER_MODULE);
        context.put("ironfox_datetime", Calendar.getInstance().getTime());
        context.put("ironfox_status", "on");
        context.put("ironfox_anomaly_detection", "on");
        context.put("ironfox_port", port);
        context.put("ironfox_mode", mode);
        context.put("ironfox_rps", rps);
        context.put("ironfox_secret", secret);
        context.put("ironfox_key", key);
        context.put("ironfox_iv", iv);
        context.put("ironfox_virtual_host", virtualHost);
        context.put("ironfox_randomValue_0", Control.getHexString(100));
        context.put("ironfox_randomValue_1", Control.getHexString(8192));
        context.put("ironfox_rate_limiter_buffer_size", environment.getProperty(IRONFOX_RATE_HASHMAP_SIZE));
        context.put("ironfox_header_value", JavascriptRender.HEADER_RENDER);
        context.put("ironfox_body_value", JavascriptRender.BODY_RENDER);
        context.put("ironfox_render_buffer_size", JavascriptRender.RENDER_BUFFER_SIZE);


        Template t = Velocity.getTemplate("ironfox_render_template.vm");
        StringWriter sw = new StringWriter();

        t.merge(context, sw);

        log.debug(sw.toString());
        try {
            FileWriter myWriter = new FileWriter(environment.getProperty(IRONFOX_PATH_BIN) + "iron.conf");
            myWriter.write(sw.toString());
            myWriter.close();
            log.info("successfully update config file.");
        } catch (IOException e) {
            log.info("error while updating config file.");
            e.printStackTrace();
        }
        response.sendRedirect("/");
    }


}
