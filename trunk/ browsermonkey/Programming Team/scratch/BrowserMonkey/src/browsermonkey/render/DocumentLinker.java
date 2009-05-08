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
        try {
        documentPanel.load(path);
        } catch (Exception e) {
            // TODO: Work out how to handle 404/etc. from links.
        }
    }
}