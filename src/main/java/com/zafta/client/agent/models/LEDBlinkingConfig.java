package com.zafta.client.agent.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LEDBlinkingConfig {
    private int numberOfBlinks;
    private int blinkingInterval;
    private int stayOnDuration;
}
