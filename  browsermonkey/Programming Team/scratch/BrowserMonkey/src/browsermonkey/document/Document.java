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
    private URL url;
    private URL context;
    private DocumentNode nodeTree;
    private boolean isConformant;

    /**
     * Returns true if the document is conformant to the html standards.
     * @return True if conformant
     */
    public boolean isIsConformant() {
        return isConformant;
    }

    /**
     * Constructs a new <code>Document</code> with the specified path.
     * @param path the file path
     * @param context URL context for linking purposes
     */
    /*public Document(String path) {
        this(path, null);
    }*/

    public Document(String path, URL context) {
        this.path = path;
        this.context = context;
    }

    /**
     * Returns the <code>URL</code> of the current document
     * @return the <code>URL</code> of the current document
     */
    public URL getURL() {
        return url;
    }

    /**
     * Opens the file specified by this <code>Document</code>'s path and
     * parses it, generating the <code>DocumentNode</code> tree.
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void load() throws FileNotFoundException, IOException {
        url = FileLoader.getURL(path, context);
        byte[] data = FileLoader.readFile(url);

        String pageText;
        if (data == null) {
            // TODO: Error
            pageText = "<title>File Not Found</title>Error 404 - file not found.";
        }
        else
            pageText = new String(data);

        Parser parser = new Parser(pageText);
        parser.parse();
        isConformant = parser.isConformant();
        nodeTree = parser.getRootNode();
    }

    /**
     * Load testing method, use the tagText variable to select which load test to use.
     * @param tagText "table" for the table load test, "a" for the non-table version
     */
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

    /**
     * Returns the <code>DocumentNode</code> tree for this document node.
     * @return Node tree for this DocumentNode
     */
    public DocumentNode getNodeTree() {
        return nodeTree;
    }
}