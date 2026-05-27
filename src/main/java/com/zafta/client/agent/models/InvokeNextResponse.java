package com.zafta.client.agent.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvokeNextResponse {
    private int queueId;
    private int invokedNumber;
    private int waitingInQueue;
}
