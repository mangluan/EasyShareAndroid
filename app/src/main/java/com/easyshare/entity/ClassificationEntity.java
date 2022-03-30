package com.easyshare.entity;

import java.io.Serializable;

public class ClassificationEntity implements Serializable {

    private int classificationId;
    private String name;
    private int priority;


    public int getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(int classificationId) {
        this.classificationId = classificationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
