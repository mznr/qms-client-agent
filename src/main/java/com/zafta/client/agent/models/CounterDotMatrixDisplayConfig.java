package com.zafta.client.agent.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CounterDotMatrixDisplayConfig {
    private String defaultText;
    private int defaultOnDurationSeconds;
}
