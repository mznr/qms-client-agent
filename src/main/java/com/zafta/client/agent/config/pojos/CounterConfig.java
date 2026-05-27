package com.zafta.client.agent.config.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterConfig {
    public String id;
    public String displayId;
}
