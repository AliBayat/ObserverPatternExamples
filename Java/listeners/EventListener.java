package design_patterns.observer.example.listeners;

import java.io.File;

public interface EventListener {
    void update(String eventType, File file);
}