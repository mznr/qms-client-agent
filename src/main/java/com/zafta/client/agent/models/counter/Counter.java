package com.zafta.client.agent.models.counter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Counter implements Serializable {


    private int id;
    private int displayId;
    private int userId;
    private String username;
    private boolean state;
    private String ip;
//    private List<QueueEntityWrapper> queues = new ArrayList<>();
//    private List<OnlineQueueEntity> onlineQueues = new ArrayList<>();
    private DisplayType displayType;
    private CounterType counterType;
    private int lastInvokedNumber = -1;
    private int lastInvokedQueueId = -1;
    private String lastInvokedTurnId;
    private boolean waitingForNumber;
    private CounterConnectionState connectionState;

    /*private int id;
    private int queueId = -1;
    private int displayId;
    private int userId;
    private String username;
    private boolean state;

    private DisplayType displayType;

    private CounterType counterType;

    private int lastInvokedNumber = -1;
    private boolean waitingForNumber;
    private CounterConnectionState connectionState;*/

}
