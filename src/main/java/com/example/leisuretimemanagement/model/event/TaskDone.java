package com.example.leisuretimemanagement.model.event;

import com.example.leisuretimemanagement.model.Task;

import java.time.Clock;

public class TaskDone extends TaskEvent {
    TaskDone(Task source) {
        super(source.getId(), Clock.systemDefaultZone());
    }
}
