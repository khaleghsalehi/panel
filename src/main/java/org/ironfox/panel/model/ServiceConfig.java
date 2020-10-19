package org.ironfox.panel.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ServiceConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private int port;

    @Getter
    @Setter
    private String aesSecret;

    @Getter
    @Setter
    private String aesKey;

    @Getter
    @Setter
    private String aesIv;

    @Getter
    @Setter
    private String virtualServer;

    @Getter
    @Setter
    private String protectionMode;

    @Getter
    @Setter
    private int rPS;

}
