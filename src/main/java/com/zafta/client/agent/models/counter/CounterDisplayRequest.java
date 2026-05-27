package com.zafta.client.agent.models.counter;


public record CounterDisplayRequest(
        int counterAddress,
        int invokedNumber
) {
}
