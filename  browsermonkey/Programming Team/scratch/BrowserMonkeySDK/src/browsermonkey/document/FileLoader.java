package browsermonkey.document;

import browsermonkey.utility.BrowserMonkeyLogger;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class FileLoader {
    private static File getIndexFile(File directory) {
        String[] indexFiles = directory.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equalsIgnoreCase("index.html") || name.equalsIgnoreCase("index.htm");
            }
        });
        if (indexFiles.length > 0)
            return new File(indexFiles[0]);
        return null;
    }

    private static URL resolveFile(File file) {
        try {
            if (file.isDirectory())
                file = getIndexFile(file);
            else if (!file.exists())
                file = null;

            if (file != null) {
                return file.toURI().toURL();
            }
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static URL getURL(String path, URL context) {
        URL result = resolveFile(new File(path));
        if (result != null)
            return result;

        try {
            result = new URL(context, path);
        } catch (MalformedURLException ex) {
            return null;
        }

        if (result.getProtocol().equals("file")) {
            try {
                result = resolveFile(new File(result.toURI().getPath()));
            } catch (Exception e) {
                result =  null;
            }
        }

        return result;
    }

    public static byte[] readFile(URL url) {
        if (url == null)
            return null;
        
        InputStream urlStream;
        ArrayList<Byte> data = new ArrayList<Byte>();
        try {
            urlStream = url.openStream();
            int b;
            BrowserMonkeyLogger.status("Loading "+url.toString());
            Thread.yield();

            while ((b = urlStream.read()) != -1)
                data.add((byte)b);
        } catch (IOException ex) {
            return null;
        }
        try {
            urlStream.close();
        } catch (IOException ex) {
            // Can't close, needn't be reported.
        }

        byte[] byteData = new byte[data.size()];
        for (int i = 0; i < byteData.length; i++)
            byteData[i] = data.get(i);

        return byteData;
    }
}