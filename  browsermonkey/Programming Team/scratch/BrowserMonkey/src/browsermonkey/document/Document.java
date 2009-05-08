package browsermonkey.document;

import java.io.*;

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
    
    /**
     * Opens the file specified by this <code>Document</code>'s path and
     * parses it, generating the <code>DocumentNode</code> tree.
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void load() throws FileNotFoundException, IOException {
        FileReader reader = new FileReader(path);
        BufferedReader br = new BufferedReader(reader);
        String line;
        StringBuilder result = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException ex) {
            reader.close();
            throw ex;
        }
        nodeTree = new TagDocumentNode("html", null,
                new TextDocumentNode(result.toString()),
                new TagDocumentNode("b", null,
                    new TextDocumentNode(" Bold text"),
                    new TextDocumentNode(" over multiple text nodes.")
                ),
                new TextDocumentNode(" No longer bold.")
            );
    }

    public DocumentNode getNodeTree() {
        return nodeTree;
    }
}