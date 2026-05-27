package com.zafta.client.agent.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationInfo implements Serializable {
    private int id;
    private String name;
    private String version;
    private String description;
    //private String branch;
}
