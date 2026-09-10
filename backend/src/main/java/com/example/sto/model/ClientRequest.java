package com.example.sto.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClientRequest {
    private String name;
    private String number;
    private String car;
    private String carService = "";
}