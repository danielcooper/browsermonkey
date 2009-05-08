package browsermonkey.document;

import java.util.Map;

/**
 * Represents a tag.
 * @author Paul Calcraft
 */
public class TagDocumentNode extends DocumentNode {
    private String type;
    private Map<String, String> attributes;

    public String getType() {
        return type;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public TagDocumentNode(String type, Map<String, String> attributes, DocumentNode... children) {
        super(children);
        this.type = type;
        this.attributes = attributes;
    }

}