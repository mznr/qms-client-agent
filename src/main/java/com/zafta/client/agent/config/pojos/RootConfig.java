package com.zafta.client.agent.config.pojos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RootConfig {
    public ServerConfig server;
    public ZaftaConfig zafta;
    public QmsConfig qms;
    public ProjectInfo info;
}
