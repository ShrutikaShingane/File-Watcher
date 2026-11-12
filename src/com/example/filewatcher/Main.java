
package com.example.filewatcher;

import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class Main {

    public static void main(String[] args) {

        Path directory;

        if (args.length > 0) {
            directory = Paths.get(args[0]);
        } else {
            directory = Paths.get(
            		"C:\\Users\\vedan\\OneDrive\\Documents\\FileWatcherTest");
        }

        if (!Files.isDirectory(directory)) {
            System.out.println("Invalid directory: " + directory);
            return;
        }

        try (WatchService watchService =
                     FileSystems.getDefault().newWatchService()) {

            directory.register(
                    watchService,
                    ENTRY_CREATE,
                    ENTRY_MODIFY,
                    ENTRY_DELETE
            );

            System.out.println("File Watcher Started!");
            System.out.println("Monitoring: " + directory);
            System.out.println("Create, modify, or delete a file to test.");
            System.out.println("Press Ctrl+C in the Console to stop.");

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {

                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == OVERFLOW) {
                        System.out.println(
                                "Warning: Some file events may have been lost.");
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathEvent =
                            (WatchEvent<Path>) event;

                    Path fileName = pathEvent.context();

                    System.out.println(
                            "Event: " + kind.name()
                            + " | File: " + fileName);
                }

                boolean valid = key.reset();

                if (!valid) {
                    System.out.println(
                            "Directory watch is no longer valid.");
                    break;
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("File watcher interrupted. Shutting down.");

        } catch (IOException e) {
            System.out.println(
                    "Unable to watch directory: " + e.getMessage());
        }
    }
}
