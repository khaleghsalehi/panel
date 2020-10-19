package utils;

import org.ironfox.panel.config.app;
import org.ironfox.panel.web.Dashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogTailed implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Dashboard.class);
    private static final String botRegex = "bot_protection req count failed:(.*?), client:";
    private static final String successRegex = "bot_protection req count success:(.*?), client:";

    private boolean debug = false;
    private static Pattern pattern;

    private int runEveryNSeconds = 1000;
    private long lastKnownPosition = 0;
    private boolean shouldIRun = true;
    private File file = null;
    private static int counter = 0;

    public LogTailed(String myFile, int myInterval) {
        file = new File(myFile);
        this.runEveryNSeconds = myInterval;
    }

    private void printLine(String message) {
        if (message.contains("bot_protection req count success")) {
            log.info("read passed req signals: " + message);
            pattern = Pattern.compile(successRegex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                //dived 2, cause of duplication in nginx request processing chain
                app.config.put("userPassed", matcher.group(1));
            }
        } else if (message.contains("bot_protection req count failed")) {
            log.info("read attacks signals: " + message);

            pattern = Pattern.compile(botRegex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                app.config.put("botBlocked", matcher.group(1));
            }
        }
    }

    public void stopRunning() {
        shouldIRun = false;
    }

    public void run() {
        try {
            while (shouldIRun) {
                Thread.sleep(runEveryNSeconds);
                long fileLength = file.length();
                if (fileLength > lastKnownPosition) {

                    // Reading and writing file
                    RandomAccessFile readWriteFileAccess = new RandomAccessFile(file, "rw");
                    readWriteFileAccess.seek(lastKnownPosition);
                    String readLine = null;
                    while ((readLine = readWriteFileAccess.readLine()) != null) {
                        this.printLine(readLine);
                        counter++;
                    }
                    lastKnownPosition = readWriteFileAccess.getFilePointer();
                    readWriteFileAccess.close();
                } else {
                    if (debug)
                        this.printLine("Hmm.. Couldn't found new line after line # " + counter);
                }
            }
        } catch (Exception e) {
            stopRunning();
        }
        if (debug)
            this.printLine("Exit the program...");
    }
}

