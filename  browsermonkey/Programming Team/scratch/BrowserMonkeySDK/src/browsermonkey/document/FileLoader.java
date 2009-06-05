package browsermonkey.document;

import browsermonkey.utility.BrowserMonkeyLogger;
import java.io.*;
import java.net.MalformedURLException;
import java.net.*;
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
        URL result;
        if (context == null) {
            result = resolveFile(new File(path));
            if (result != null)
                return result;
        }

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

    public static byte[] readFile(URL url, int[] outErrorCode) {
        if (url == null)
            return null;
        
        InputStream urlStream;
        URLConnection connection;
        ArrayList<Byte> data = new ArrayList<Byte>();
        try {
            connection = url.openConnection();
            connection.setRequestProperty("User-agent", "Mozilla/5.0");
            urlStream = connection.getInputStream();
            int b;
            BrowserMonkeyLogger.status("Loading "+url.toString());
            Thread.yield();

            while ((b = urlStream.read()) != -1)
                data.add((byte)b);
        } catch (SocketTimeoutException ex) {
            outErrorCode[0] = 408;
            return null;
        }
        catch (IOException ex) {
            outErrorCode[0] = 404;
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