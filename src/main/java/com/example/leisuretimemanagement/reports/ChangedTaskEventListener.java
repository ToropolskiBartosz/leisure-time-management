package com.example.leisuretimemanagement.reports;

import com.example.leisuretimemanagement.model.event.TaskDone;
import com.example.leisuretimemanagement.model.event.TaskEvent;
import com.example.leisuretimemanagement.model.event.TaskUndone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ChangedTaskEventListener {
    private final static Logger logger = LoggerFactory.getLogger(ChangedTaskEventListener.class);

    private final PersistedTaskEventRepository repository;

    public ChangedTaskEventListener(PersistedTaskEventRepository repository) {
        this.repository = repository;
    }

    @Async
    @EventListener
    public void on(TaskDone event){
        onChanged(event);
    }

    @Async
    @EventListener
    public void on(TaskUndone event){
        onChanged(event);
    }

    private void onChanged(final TaskEvent event) {
        logger.info("[Event] Got " + event);
        repository.save(new PersistedTaskEvent(event));
    }
}
