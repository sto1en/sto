package com.example.sto.model;

public class ServiceItem {
    private String name;
    private String price;
    private String duration;

    public ServiceItem() {}

    public ServiceItem(String name, String price, String duration) {
        this.name = name;
        this.price = price;
        this.duration = duration;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}