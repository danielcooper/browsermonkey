package browsermonkey.document;

import java.util.Map;

/**
 * Represents a tag.
 * @author Paul Calcraft
 */
public class TagDocumentNode extends DocumentNode {
    private String type;  //Tag
    private Map<String, String> attributes;

    public String getType() {
        return type;
    }

    public String getAttribute(String attribute) {
        if (attributes == null)
            return null;
        return attributes.get(attribute);
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public TagDocumentNode(String type, Map<String, String> attributes){
        super();
        this.type = type;
        this.attributes = attributes;
    }

    public TagDocumentNode(String type, Map<String, String> attributes, DocumentNode... children) {
        super(children);
        this.type = type;
        this.attributes = attributes;
    }

    @Override
    public String toDebugString() {
        StringBuilder builder = new StringBuilder();
        builder.append('<');
        builder.append(type);
        if (attributes != null)
            for (Map.Entry<String, String> attribute : attributes.entrySet())
               builder.append(" "+attribute.getKey()+"=\""+attribute.getValue()+"\"");
        builder.append('>');
        builder.append("\r\n");
        for (DocumentNode child : children)
            builder.append(child.toDebugString()+"\r\n");
        builder.append("</");
        builder.append(type);
        builder.append('>');
        return builder.toString();
    }
}