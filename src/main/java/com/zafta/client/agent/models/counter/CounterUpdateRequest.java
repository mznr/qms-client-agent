package com.zafta.client.agent.models.counter;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CounterUpdateRequest {
    private Integer id;
    private Integer displayId;
    private Integer userId;
    private String ip;
    private DisplayType displayType;
    private CounterType counterType;
}
