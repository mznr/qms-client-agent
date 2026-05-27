package com.zafta.client.agent.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CounterService implements Serializable {

    private int id;
    private String title;
    private String startTime;
    private String endTime;
}