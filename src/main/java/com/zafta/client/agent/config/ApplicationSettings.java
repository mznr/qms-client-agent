package com.zafta.client.agent.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApplicationSettings {
    private String port;
    private String counterId;
    private String serverUrl;
}
