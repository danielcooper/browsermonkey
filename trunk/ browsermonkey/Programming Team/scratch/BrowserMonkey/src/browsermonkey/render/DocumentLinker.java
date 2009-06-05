package browsermonkey.render;

/**
 *
 * @author prtc20
 */
public class DocumentLinker implements Linkable {
    private DocumentPanel documentPanel;

    public DocumentLinker(DocumentPanel documentPanel) {
        this.documentPanel = documentPanel;
    }

    public void followLink(String path) {
        documentPanel.load(path);
    }
}