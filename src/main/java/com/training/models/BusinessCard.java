package com.training.models;

/**
 * Created by Sandeep.K on 7/8/2016.
 */
public class BusinessCard {
    private String desgination;
    private String name;
    private String companyName;

    public BusinessCard(String name, String desgination, String companyName){
        this.name = name;
        this.desgination = desgination;
        this.companyName = companyName;
    }

    public String getDesgination() {
        return desgination;
    }

    public void setDesgination(String desgination) {
        this.desgination = desgination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "BusinessCard{" +
                "desgination='" + desgination + '\'' +
                ", name='" + name + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
