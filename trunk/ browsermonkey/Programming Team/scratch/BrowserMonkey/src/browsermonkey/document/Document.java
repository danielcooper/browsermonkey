package browsermonkey.document;

import java.io.*;
import java.net.*;

/**
 * Represents a document attached to a path, contains the parsed form of the
 * document as a <code>DocumentNode</code> tree.
 * @author Paul Calcraft
 */
public class Document {
    private String path;
    private DocumentNode nodeTree;

    /**
     * Constructs a new <code>Document</code> with the specified path.
     * @param path the file path
     */
    public Document(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
    
    /**
     * Opens the file specified by this <code>Document</code>'s path and
     * parses it, generating the <code>DocumentNode</code> tree.
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void load() throws FileNotFoundException, IOException {
        BufferedReader br;

        if (path.startsWith("http://")) {
            URL url = new URL(path);
            InputStream in = url.openStream();
            br = new BufferedReader(new InputStreamReader(in));
        }
        else {
            FileReader reader = new FileReader(path);
            br = new BufferedReader(reader);
        }
        
        char[] buffer = new char[256];
        int charactersRead;
        StringBuilder result = new StringBuilder();
        try {
            while ((charactersRead = br.read(buffer)) != -1) {
                result.append(buffer, 0, charactersRead);
            }
        } catch (IOException ex) {
            br.close();
            throw ex;
        }
        Parser parser = new Parser(result.toString());
        parser.parse();
        nodeTree = parser.getRootNode();
    }

    public void loadTest(String tagText) {
        if (tagText.equals("table")) {
            nodeTree = new TagDocumentNode("html", null,
                new TextDocumentNode("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."),
                new TagDocumentNode("table", null,
                    new TagDocumentNode("tr", null,
                        new TagDocumentNode("td", null,
                            new TextDocumentNode("Table Test, Cell 1")
                        ),
                        new TagDocumentNode("td", null,
                            new TextDocumentNode("Table Test, Cell 2")
                        ),
                        new TagDocumentNode("td", null,
                            new TextDocumentNode("Table Test, Cell 3"),
                            new TagDocumentNode("table", null,
                                new TagDocumentNode("tr", null,
                                    new TagDocumentNode("td", null,
                                        new TextDocumentNode("Table Test, Cell 1")
                                    ),
                                    new TagDocumentNode("td", null,
                                        new TextDocumentNode("Table Test, Cell 2")
                                    ),
                                    new TagDocumentNode("td", null,
                                        new TextDocumentNode("Table Test, Cell 3")
                                    )
                                ),
                                new TagDocumentNode("tr", null,
                                    new TagDocumentNode("td", null,
                                        new TextDocumentNode("Table Test, Second Row")
                                    ),
                                    new TagDocumentNode("td", null,
                                        new TextDocumentNode("Table Test, Second Row, Cell 2")
                                    ),
                                    new TagDocumentNode("td", null,
                                        new TextDocumentNode("Table Test, Second Row, Cell 3")
                                    )
                                )
                            )
                        )
                    ),
                    new TagDocumentNode("tr", null,
                        new TagDocumentNode("td", null,
                            new TextDocumentNode("Table Test, Second Row")
                        ),
                        new TagDocumentNode("td", null,
                            new TextDocumentNode("Table Test, Second Row, Cell 2")
                        ),
                        new TagDocumentNode("td", null,
                            new TextDocumentNode("Table Test, Second Row, Cell 3")
                        )
                    )
                ),
                new TextDocumentNode("Done.")
            );
            return;
        }
        else if (tagText.equals("a")) {
            java.util.HashMap<String, String> aAttributes = new java.util.HashMap<String, String>();
            aAttributes.put("href", "t table");
            nodeTree = new TagDocumentNode("html", null,
                new TextDocumentNode("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."),
                new TagDocumentNode("br", null),
                new TextDocumentNode("Check out the "),
                new TagDocumentNode("a", aAttributes,
                    new TextDocumentNode("table test")
                ),
                new TextDocumentNode(", it's really cool.")
            );
            return;
        }

        String[] parts = tagText.split(" ");
        String tag = parts[0];
        java.util.HashMap attributes = new java.util.HashMap<String, String>();
        for (int i = 1; i < parts.length; i++) {
            String[] keyValue = parts[i].split("=");
            attributes.put(keyValue[0].trim(), keyValue[1].trim().replace("\"", ""));
        }
        nodeTree = new TagDocumentNode("html", null,
                new TextDocumentNode("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. "),
                new TagDocumentNode(tag, attributes,
                    new TextDocumentNode("Test <"+tagText+">.")
                ),
                new TextDocumentNode(" Back to normal.")
            );
    }

    public DocumentNode getNodeTree() {
        return nodeTree;
    }
}