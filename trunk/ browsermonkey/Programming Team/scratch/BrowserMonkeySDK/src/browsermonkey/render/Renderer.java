package browsermonkey.render;

import java.util.*;
import browsermonkey.document.*;
import browsermonkey.utility.BrowserMonkeyLogger;
import java.text.AttributedCharacterIterator.Attribute;
import java.awt.font.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;

/**
 *
 * @author Paul Calcraft
 */
public class Renderer {
    private URL documentContext;
    private Map<String, TagRenderer> rendererMap;
    private Linkable linker;
    private String title = null;
    private ArrayList<Integer> headingNumbering;
    public static final Map<Attribute,Object> DEFAULT_FORMATTING;

    public String getHeadingString(int headingLevel) {
        if (headingLevel >= headingNumbering.size()) {
            for (int i = headingNumbering.size(); i <= headingLevel; i++)
                headingNumbering.add(1);
        }
        else {
            headingNumbering.set(headingLevel, headingNumbering.get(headingLevel)+1);
            for (int i = headingNumbering.size()-1; i > headingLevel; i--)
                headingNumbering.remove(i);
        }

        StringBuilder headingString = new StringBuilder();

        for (Integer i : headingNumbering) {
            headingString.append(i);
            headingString.append('.');
        }

        return headingString.toString();
    }

    public TextRenderNode constructIndentTextNode(Map<Attribute,Object> formatting) {
        TextRenderNode result = new TextRenderNode(linker);
        result.addText("&nbsp;&nbsp;&nbsp;&nbsp;", formatting);
        return result;
    }

    static {
        DEFAULT_FORMATTING = new HashMap<Attribute,Object>();
        DEFAULT_FORMATTING.put(TextAttribute.SIZE, 12f);
        DEFAULT_FORMATTING.put(TextAttribute.FAMILY, "Times New Roman");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] loadResource(String path) {
        return FileLoader.readFile(FileLoader.getURL(path, documentContext));
    }

    private void loadRenderers() {
        rendererMap = new HashMap<String, TagRenderer>();

        Properties rendererMapProperties = new Properties();
        try {
            FileInputStream in = new FileInputStream("tagRenderers.properties");
            rendererMapProperties.load(in);

            File pluginsDirectory = new File("plugins/");
       
            String[] pluginJARs = pluginsDirectory.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jar"));
                }
            });

            String jarFiles = "";
            URL[] urls = new URL[pluginJARs.length];
            for (int i = 0; i < pluginJARs.length; i++) {
                urls[i] = new File("plugins/"+pluginJARs[i]).toURI().toURL();
                jarFiles += "\""+urls[i]+"\"";
                if (i != pluginJARs.length)
                    jarFiles += ", ";
            }

            BrowserMonkeyLogger.info("Loading tag plugins from: "+jarFiles);
            ClassLoader pluginClassLoader = new URLClassLoader(urls);

            Set<Map.Entry<Object, Object>> rendererMappings = rendererMapProperties.entrySet();

            int rendererCount = 0;
            int loadedCount = 0;

            for (Map.Entry<Object, Object> entry : rendererMappings) {
                rendererCount++;
                try {
                    Class rendererClass = pluginClassLoader.loadClass(entry.getValue().toString());
                    Class parameterTypes[] = new Class[] { Linkable.class };
                    Constructor ctor = rendererClass.getConstructor(parameterTypes);
                    Object arguments[] = new Object[] { this.linker };
                    Object newInstance = ctor.newInstance(arguments);
                    TagRenderer tagRenderer = TagRenderer.class.cast(newInstance);

                    if (tagRenderer != null) {
                        rendererMap.put(entry.getKey().toString(), tagRenderer);
                        loadedCount++;
                    }
                    
                } catch (ClassNotFoundException ex) {
                    BrowserMonkeyLogger.warning("TagRenderer class could not be found in the plugins folder.");
                } catch (Exception ex) {
                    BrowserMonkeyLogger.warning("TagRenderer class could not be instantiated: "+ex);
                }
            }

            BrowserMonkeyLogger.info(loadedCount+"/"+rendererCount+" TagRenderers loaded successfully.");
        } catch (IOException ex) {
            BrowserMonkeyLogger.notice("Could not read tagRenderers.properties: "+ex);
        }
    }

    public Renderer(Linkable linker) {
        this.linker = linker;
        headingNumbering = new ArrayList<Integer>();
        loadRenderers();
    }

    public LayoutRenderNode renderRoot(DocumentNode root, float zoom, URL documentContext) {
        this.documentContext = documentContext;
        headingNumbering.clear();
        title = null;

        LayoutRenderNode renderRoot = new LayoutRenderNode(linker);
        render(root, renderRoot, DEFAULT_FORMATTING);
        renderRoot.setZoomLevel(zoom);
        return renderRoot;
    }

    public void render(DocumentNode node, LayoutRenderNode parent, Map<Attribute,Object> formatting) {
        if (node instanceof TextDocumentNode) {
            TextDocumentNode textNode = (TextDocumentNode)node;
            parent.getTextNode().addText(textNode.getText(), formatting);
        }
        else {
            TagDocumentNode tagNode = (TagDocumentNode)node;
            getTagRenderer(tagNode).render(this, tagNode, parent, formatting);
        }
    }

    private TagRenderer getTagRenderer(TagDocumentNode tagNode) {
        TagRenderer renderer = rendererMap.get(tagNode.getType());
        if (renderer == null)
            renderer = new TransparentTagRenderer(linker);
        return renderer;
    }
}