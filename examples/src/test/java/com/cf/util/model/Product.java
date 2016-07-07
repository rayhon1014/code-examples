package com.cf.util.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ray on 6/13/16.
 */
public class Product{
    @JsonProperty
    private Float price;
    @JsonProperty
    private Integer sharedCount;
    @JsonProperty
    private Boolean isPrime;
    @JsonProperty
    private String title;

    public Product(){

    }
    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getSharedCount() {
        return sharedCount;
    }

    public void setSharedCount(Integer sharedCount) {
        this.sharedCount = sharedCount;
    }

    public Boolean getPrime() {
        return isPrime;
    }

    public void setPrime(Boolean prime) {
        isPrime = prime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
