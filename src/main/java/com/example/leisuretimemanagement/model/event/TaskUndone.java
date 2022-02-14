package com.example.leisuretimemanagement.model.event;

import com.example.leisuretimemanagement.model.Task;

import java.time.Clock;

public class TaskUndone extends TaskEvent {
    TaskUndone(Task source) {
        super(source.getId(), Clock.systemDefaultZone());
    }
}
