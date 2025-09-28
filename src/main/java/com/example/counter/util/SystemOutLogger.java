package com.example.counter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

@Component
public class SystemOutLogger {
    
    private static final Logger logger = LoggerFactory.getLogger("SYSTEM_OUT");
    private static final PrintStream originalOut = System.out;
    
    @PostConstruct
    public void redirectSystemOut() {
        System.setOut(new LoggingPrintStream(logger));
    }
    
    /**
     * Custom PrintStream that logs to SLF4J and also writes to original System.out
     */
    private static class LoggingPrintStream extends PrintStream {
        private final Logger logger;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        
        public LoggingPrintStream(Logger logger) {
            super(new ByteArrayOutputStream());
            this.logger = logger;
        }
        
        @Override
        public void write(int b) {
            buffer.write(b);
            if (b == '\n') {
                flush();
            }
        }
        
        @Override
        public void write(byte[] buf, int off, int len) {
            buffer.write(buf, off, len);
            if (len > 0 && buf[off + len - 1] == '\n') {
                flush();
            }
        }
        
        @Override
        public void flush() {
            try {
                String message = buffer.toString().trim();
                if (!message.isEmpty()) {
                    logger.info(message);
                    // Also write to original System.out for console display
                    originalOut.println(message);
                }
                buffer.reset();
            } catch (Exception e) {
                // Handle silently
            }
        }
        
        @Override
        public void println(String x) {
            if (x != null) {
                // Only log messages that look like application logs, not third-party library logs
                if (shouldLogMessage(x)) {
                    logger.info(x);
                }
                originalOut.println(x);
            } else {
                if (shouldLogMessage("null")) {
                    logger.info("null");
                }
                originalOut.println("null");
            }
        }
        
        @Override
        public void print(String s) {
            if (s != null) {
                if (shouldLogMessage(s)) {
                    logger.info(s);
                }
                originalOut.print(s);
            } else {
                if (shouldLogMessage("null")) {
                    logger.info("null");
                }
                originalOut.print("null");
            }
        }
        
        /**
         * Determines if a message should be logged to the file.
         * Filters out third-party library logs and only captures application logs.
         */
        private boolean shouldLogMessage(String message) {
            if (message == null) return false;
            
            // Skip third-party library logs
            if (message.contains("HikariPool") || 
                message.contains("hikari") ||
                message.contains("DEBUG") ||
                message.contains("TRACE") ||
                message.contains("keepalive") ||
                message.contains("connection") ||
                message.contains("org.postgresql") ||
                message.contains("com.zaxxer")) {
                return false;
            }
            
            // Only log messages that look like application logs
            // You can customize this logic based on your application's logging patterns
            return true;
        }
    }
}
