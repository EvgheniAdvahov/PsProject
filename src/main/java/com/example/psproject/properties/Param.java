package com.example.psproject.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "param")
@Data
public class Param {

    private String PING;
}
