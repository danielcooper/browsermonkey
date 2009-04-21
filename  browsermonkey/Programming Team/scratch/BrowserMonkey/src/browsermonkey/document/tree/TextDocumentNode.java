package browsermonkey.document.tree;

/**
 *
 * @author Paul Calcraft
 */
public class TextDocumentNode extends DocumentNodeBase {
    private String text;

    public String getText() {
        return text;
    }

    public TextDocumentNode(String text) {
        this.text = text;
    }
}