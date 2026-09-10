package com.example.sto.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequest {
    private String name;
    private String phoneNumber;
    private String car;
    private String carService = "";
}