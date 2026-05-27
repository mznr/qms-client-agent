package com.zafta.client.agent.services;

import com.zafta.client.agent.ClientAgentApplication;

public class ClientServiceRunner {

    private Thread thread;

    public void start() {
        thread = new Thread(() -> {
            ClientAgentApplication.main(new String[]{});
        }, "client-service-thread");

        thread.setDaemon(true);
        thread.start();
    }
}
