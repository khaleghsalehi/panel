package org.ironfox.panel;

import org.ironfox.panel.config.app;
import org.ironfox.panel.model.ServiceConfig;
import org.ironfox.panel.repo.ServiceConfigRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import utils.LogTailed;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class PanelApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(PanelApplication.class);
    private static final String VERSION = "The Desert Fox (0.0.4)";


    public static void main(String[] args) {
        SpringApplication.run(PanelApplication.class, args);
    }

    @Autowired
    ServiceConfigRepo serviceConfigRepo;

    @Autowired
    Environment environment;
    private static final String nginxErrorLogPath = "logs/error.log";


    @Override
    public void run(String... args) throws Exception {

        String rawConfigFile = "# version 0.0.5\n" +
                "#$ironfox_datetime\n" +
                "worker_processes  auto;\n" +
                "error_log   logs/error.log  debug;\n" +
                "\n" +
                "load_module  $ironfox_path_bot_module;\n" +
                "load_module  $ironfox_path_javascript_module;\n" +
                "\n" +
                "\n" +
                "events {\n" +
                "                    worker_connections  1024;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "http {\n" +
                "    default_type     application/octet-stream;\n" +
                "    limit_req_zone   $cookie_kooki  zone=gateway:$ironfox_rate_limiter_buffer_size rate=$ironfox_rps;\n" +
                "    server_tokens off;\n" +
                "    sendfile        on;\n" +
                "    keepalive_timeout  65;\n" +
                "\n" +
                "    server {\n" +
                "                    listen 80;\n" +
                "                    server_name _;\n" +
                "                    return 301 https://$host$request_uri;\n" +
                "    }\n" +
                "\n" +
                "    server {\n" +
                "                    server_name _;\n" +
                "                    listen $ironfox_port ssl;\n" +
                "\n" +
                "                    ssl_certificate     /home/ironfox/iron/cert/nginx-selfsigned.crt;\n" +
                "                    ssl_certificate_key /home/ironfox/iron/cert/nginx-selfsigned.key;\n" +
                "                    ssl_protocols       TLSv1 TLSv1.1 TLSv1.2;\n" +
                "                    ssl_ciphers         HIGH:!aNULL:!MD5;\n" +
                "\n" +
                "                    set $rnd0 $ironfox_randomValue_0;\n" +
                "                    set $rnd1 $ironfox_randomValue_1;\n" +
                "\n" +
                "                    bot_protection_cookie_name kooki;\n" +
                "                    bot_protection_secret $ironfox_secret;\n" +
                "                    bot_protection_session $remote_addr;\n" +
                "                    bot_protection_arg idcount;\n" +
                "                    bot_protection_max_attempts 3;\n" +
                "                    bot_protection_fallback /captcha.html?backurl=http://$host$request_uri;\n" +
                "                    bot_protection_get_only on;\n" +
                "                    bot_protection_redirect_via_refresh on;\n" +
                "                    bot_protection_refresh_encrypt_cookie on;\n" +
                "                    bot_protection_refresh_encrypt_cookie_key $ironfox_key;\n" +
                "                    bot_protection_refresh_encrypt_cookie_iv  $ironfox_iv;\n" +
                "                    bot_protection_deny_keepalive on;\n" +
                "                    bot_protection_https_location on;\n" +
                "                    bot_protection_refresh_template $ironfox_mode;\n" +
                "\n" +
                "        location / {\n" +
                "                    #gzip on;\n" +
                "                    proxy_set_header Accept-Encoding \"\";\n" +
                "                    limit_req zone=gateway burst=$ironfox_rps;\n" +
                "                    limit_req_status 433;\n" +
                "                    bot_protection_manager $ironfox_status;\n" +
                "                    bot_protection_anomaly_detection $ironfox_anomaly_detection;\n" +
                "\n" +
                "                    proxy_set_header X-Real-IP $remote_addr;\n" +
                "                    proxy_set_header Host $host;\n" +
                "                    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;\n" +
                "                    proxy_pass $ironfox_virtual_host;\n" +
                "                    replace_filter_max_buffered_size $ironfox_render_buffer_size;\n" +
                "                    replace_filter \"</head>\" \"$ironfox_header_value\";\n" +
                "                    replace_filter \"</body>\" \"$ironfox_body_value\";\n" +
                "\n" +
                "        }\n" +
                "        location = /hourglass.gif {\n" +
                "                    gzip on;\n" +
                "                    gzip_min_length 1000;\n" +
                "                    gzip_types text/plain;\n" +
                "                    root /home/ironfox/iron/html;\n" +
                "            }\n" +
                "\n" +
                "        location = /iron.js {\n" +
                "                    gzip on;\n" +
                "                    gzip_min_length 1000;\n" +
                "                    gzip_types text/plain;\n" +
                "                    root /home/ironfox/iron/html;\n" +
                "            }\n" +
                "\n" +
                "        location = /sweet-alert.css {\n" +
                "                    gzip on;\n" +
                "                    gzip_min_length 1000;\n" +
                "                    gzip_types text/plain;\n" +
                "                    root /home/ironfox/iron/html;\n" +
                "            }\n" +
                "\n" +
                "        location = /sweet-alert.min.js {\n" +
                "                    gzip on;\n" +
                "                    gzip_min_length 1000;\n" +
                "                    gzip_types text/plain;\n" +
                "                    root /home/ironfox/iron/html;\n" +
                "            }\n" +
                "\n" +
                "        location = /ironfox.html {\n" +
                "                    gzip on;\n" +
                "                    gzip_min_length 1000;\n" +
                "                    gzip_types text/plain;\n" +
                "                    root /home/ironfox/iron/html;\n" +
                "            }\n" +
                "        error_page   500 502 503 504  /50x.html;\n" +
                "        error_page   404   /40x.html;\n" +
                "        error_page   433   /bot.html;\n" +
                "        location = /50x.html {\n" +
                "                    root  /home/ironfox/iron/html;\n" +
                "        }\n" +
                "        location = /40x.html {\n" +
                "                    root  /home/ironfox/iron/html;\n" +
                "                }\n" +
                "        location = /bot.html {\n" +
                "                    root  /home/ironfox/iron/html;\n" +
                "         }\n" +
                "        location = /fingerprint.js {\n" +
                "                    gzip on;\n" +
                "                    gzip_min_length 1000;\n" +
                "                    gzip_types text/plain;\n" +
                "                    root /home/ironfox/iron/html;\n" +
                "             }\n" +
                "        location = /cryptojs.js {\n" +
                "                 gzip on;\n" +
                "                 gzip_min_length 1000;\n" +
                "                 gzip_types text/plain;\n" +
                "                 root /home/ironfox/iron/html;\n" +
                "          }\n" +
                "\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "\n";
        try {
            FileWriter myWriter = new FileWriter("ironfox_render_template.vm");
            myWriter.write(rawConfigFile);
            myWriter.close();
            log.info("successfully wrote raw config file.");
        } catch (IOException e) {
            log.info("error writing raw config file.");
            e.printStackTrace();
        }
        /* init cache */

        Optional<List<ServiceConfig>> serviceConfigList;
        serviceConfigList = Optional.ofNullable(serviceConfigRepo.getLastConfig());
        ServiceConfig lastConfig = new ServiceConfig();

        if (serviceConfigList.isPresent()) {
            if (!serviceConfigList.get().isEmpty()) {
                lastConfig = serviceConfigList.get().iterator().next();
                app.config.put("port", String.valueOf(lastConfig.getPort()));
                app.config.put("server_status", "UNKNOWN");
                app.config.put("protection_status", "UNKNOWN");
                app.config.put("anomaly_protection_status", "UNKNOWN");
                app.config.put("rate", String.valueOf(lastConfig.getRPS()));
                app.config.put("protection_mode", lastConfig.getProtectionMode());
                app.config.put("virtualHost", lastConfig.getVirtualServer());
                app.config.put("userPassed", "0");
                app.config.put("botBlocked", "0");
                app.config.put("startTime", String.valueOf(LocalTime.now()));
                app.config.put("version", VERSION);

            } else {
                log.warn("ironfox not configured yet");
                app.config.put("server_status", "UNKNOWN");
                app.config.put("protection_status", "UNKNOWN");
                app.config.put("anomaly_protection_status", "UNKNOWN");
                app.config.put("port", "0");
                app.config.put("status", "0");
                app.config.put("rate", "0");
                app.config.put("protection_mode", "0");
                app.config.put("virtualHost", "0");
                app.config.put("userPassed", "0");
                app.config.put("botBlocked", "0");
                app.config.put("startTime", String.valueOf(LocalTime.now()));
                app.config.put("version", VERSION);

            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        String logPath = environment.getProperty("ironfox.path") + nginxErrorLogPath;
        //todo read statistical information  from redis?
        LogTailed logTailed = new LogTailed(logPath, 1000);
        executorService.execute(logTailed);


    }
}
