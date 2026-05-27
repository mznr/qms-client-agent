package com.zafta.client.agent.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage {
    private boolean succeed;
    private int statusCode;
    private String content;
    private Object payload;
}
