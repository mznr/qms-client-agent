package com.zafta.client.agent.models;

public record InvocationRequest(
    int counterAddress,
    int invokedNumber,
    int remainingNumbersInQueue
){

}
