package com.zafta.client.agent.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvokeNextRequest {
    int queueId;
    int counterId;
    boolean next;
}
