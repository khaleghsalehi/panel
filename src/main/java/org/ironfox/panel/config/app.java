package org.ironfox.panel.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;

public class app {

    public final static String BOT_MODULE="modules/ngx_http_bot_protection_module.so";
    public final static String JAVA_SCRIPT_RENDER_MODULE="modules/ngx_http_replace_filter_module.so";

    public static LoadingCache<String, String> config = CacheBuilder.newBuilder()
           .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    return null;
                }
            });
    public static String getHash(String value) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(value.getBytes("utf8"));
        return String.format("%040x", new BigInteger(1, digest.digest()));

    }
}

