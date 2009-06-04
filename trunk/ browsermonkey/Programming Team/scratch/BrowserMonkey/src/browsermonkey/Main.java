package browsermonkey;

/**
 *
 * @author Paul Calcraft
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GUI browser = new GUI();
        String initialFile = "index.html";
        if (args.length > 0) {
            initialFile = args[0];
        }
        browser.loadFile(initialFile);
        browser.setVisible(true);
    }
}