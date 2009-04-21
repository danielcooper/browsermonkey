/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package browsermonkey.utility;

import java.util.logging.*;

public class BrowserMonkeyLogger {

    private static Logger logger = Logger.getLogger("uk.ac.sussex.browsermonkey");

    public BrowserMonkeyLogger() {
    }

    private static FileHandler getFile() {
        try {
            FileHandler fh = new FileHandler("BrowserMonkey.log", true);
            fh.setFormatter(new SimpleFormatter());
            return fh;
        } catch (Exception x) {
            System.out.println("can't log to log file");
        }
        return null;
    }

    public static void warning(String error) {
        logger.addHandler(getFile());
        logger.setLevel(Level.ALL);
        logger.warning(error);
    }

    public static void info(String error) {
        logger.addHandler(getFile());
        logger.setLevel(Level.ALL);
        logger.info(error);
    }
}