package com.zafta.client.agent.models.counter;

public enum CounterConnectionState {
    DISCONNECTED(0),
    CONNECTED(1);


    private final int value;

    CounterConnectionState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CounterConnectionState fromValue(int value) {
        for (CounterConnectionState connectionState : CounterConnectionState.values()) {
            if (connectionState.value == value) {
                return connectionState;
            }
        }
        throw new IllegalArgumentException("Invalid value for TurnStatus: " + value);
    }
}
