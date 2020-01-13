package com.coupne.bosniaguide;

import java.io.Serializable;
import java.util.HashMap;

public class Commit implements Serializable {
    public String name;
    public String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commit(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Commit() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}