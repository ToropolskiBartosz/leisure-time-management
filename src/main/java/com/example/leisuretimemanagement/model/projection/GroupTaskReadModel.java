package com.example.leisuretimemanagement.model.projection;

import com.example.leisuretimemanagement.model.Task;

public class GroupTaskReadModel {
    private String description;
    private boolean done;

    GroupTaskReadModel(Task source){
        this.description = source.getDescription();
        this.done = source.isDone();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
