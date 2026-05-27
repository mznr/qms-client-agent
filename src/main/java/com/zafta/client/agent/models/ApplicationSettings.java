package com.zafta.client.agent.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationSettings {
    private String port;
    private String counterId;
    private String serverUrl;
}
