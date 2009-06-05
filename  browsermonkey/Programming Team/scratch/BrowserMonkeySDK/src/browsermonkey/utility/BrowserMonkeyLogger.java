package browsermonkey.utility;

import java.util.logging.*;

public class BrowserMonkeyLogger {
    private static Logger logger = Logger.getLogger("uk.ac.sussex.browsermonkey");
    private static Logger alertLogger = Logger.getLogger("uk.ac.sussex.browsermonkey.alert");
    private static boolean logOpened = false;

    private static FileHandler getFile() {
        try {
            FileHandler fh = new FileHandler("BrowserMonkey.log", true);
            fh.setFormatter(new SimpleFormatter());
            return fh;
        } catch (Exception x) {
            return null;
        }
    }

    public static void addAlertHandler(Handler handler) {
        if (handler == null)
            return;

        try {
            alertLogger.addHandler(handler);
        } catch (SecurityException ex) {
            warning("New alert handler of type "+handler.getClass()+" could not be added due to a Security Exception: "+ex);
        }
    }

    private static boolean ensureOpen() {
        if (logOpened)
            return true;
        
        FileHandler file = getFile();

        if (file != null) {
            logOpened = true;
            logger.addHandler(file);
            logger.setLevel(Level.ALL);
            
            return true;
        }

        alertLogger.warning("Couldn't open log file.");
        return false;
    }

    public static void status(String status) {
        alertLogger.info(status);
        if (ensureOpen())
            logger.info(status);
    }

    public static void notice(String notice) {
        alertLogger.warning(notice);
        if (ensureOpen())
            logger.warning(notice);
    }
    
    public static void conformance(String string) {
        if (ensureOpen())
            logger.warning(string);
    }
    
    public static void warning(String warning) {
        if (ensureOpen())
            logger.warning(warning);
    }

    public static void info(String info) {
        if (ensureOpen())
            logger.info(info);
    }
}