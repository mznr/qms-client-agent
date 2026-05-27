package com.zafta.client.agent.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Queue implements Serializable {
    private int id;
    //private String type;
    private CounterService service;
    private int startFrom;
    private int max;
    private boolean state;
    private String latinName;
    private String disablingTime;
    //    private String startTime;
//    private String endTime;
    private boolean passiveTicket;

    private int currentNumberInQueue = startFrom;
    private int lastInvokedNumber = startFrom;

    private int waitingInQueue;
    /*public int getWaitingInQueue(){
        return currentNumberInQueue - lastInvokedNumber;
    }*/
}