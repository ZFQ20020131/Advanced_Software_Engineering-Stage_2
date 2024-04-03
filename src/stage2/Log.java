package stage2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Implements a singleton pattern using an enum to handle logging operations.
 * Allows accumulation of log messages and writing them to a specified log file.
 */
public enum Log {

    INSTANCE; // Singleton instance

    private StringBuilder logContent;

    // Initializes the StringBuilder used to store log messages.
    private Log() {
        logContent = new StringBuilder();
    }

    /**
     * Writes the accumulated log messages to a file, then clears the buffer.
     */
    public void flushToDisk() {
        try (FileWriter writer = new FileWriter(new File("simulation_log.txt"), false)) {
            writer.write(logContent.toString());
            // Clear the StringBuilder after writing to disk
            logContent.setLength(0);
        } catch (IOException e) {
            // Handling IOException
            System.err.println("Failed to write log to file: " + e.getMessage());
        }
    }

    /**
     * Appends a log message to the log buffer, adding a newline character after each message.
     *
     * @param message The message to log.
     */
    public void addMessage(String message) {
        logContent.append(message).append(System.lineSeparator());
    }
}

