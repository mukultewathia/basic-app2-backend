# System.out Logging Configuration

This project is configured to capture all `System.out.println()` and `System.out.print()` statements and write them to log files.

## Log Files

- **`logs/application.log`** - General application logs
- **`logs/system-out.log`** - All System.out messages
- **`logs/system-out.YYYY-MM-DD.log`** - Daily rotated System.out logs
- **`logs/counter-app.log`** - JWT Filter authentication logs
- **`logs/counter-app.YYYY-MM-DD.log`** - Daily rotated JWT Filter logs

## How It Works

1. **SystemOutLogger** component automatically redirects `System.out` to a custom logger
2. **Logback configuration** writes these logs to separate files
3. **Console output** is preserved - you still see System.out in the console
4. **File logging** captures everything for persistence and analysis
5. **JWT Filter logging** captures authentication events in dedicated log file

## Usage

Simply use `System.out.println()` as normal:

```java
System.out.println("Creating challenge for user: " + username);
System.out.println("Challenge created successfully");
```

These messages will appear in:
- Console (as usual)
- `logs/system-out.log` file
- Daily rotated files

## Log Rotation

- Logs are rotated daily
- 30 days of history are kept
- Old logs are automatically deleted

## Configuration

The logging configuration is in `src/main/resources/logback-spring.xml`:

- **SYSTEM_OUT logger** - Captures System.out messages
- **FILE appender** - Writes to `logs/system-out.log`
- **Rolling policy** - Daily rotation with 30-day retention

## Testing

Use the `LoggingTest` component to test the logging:

```java
@Autowired
private LoggingTest loggingTest;

// This will log to both console and file
loggingTest.testSystemOutLogging();
```
