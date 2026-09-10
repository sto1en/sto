package com.example.sto.model;

import java.util.List;

public class ServiceCategory {
    private String key;
    private String title;
    private List<ServiceItem> items;

    public ServiceCategory() {}

    public ServiceCategory(String key, String title, List<ServiceItem> items) {
        this.key = key;
        this.title = title;
        this.items = items;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<ServiceItem> getItems() { return items; }
    public void setItems(List<ServiceItem> items) { this.items = items; }
}