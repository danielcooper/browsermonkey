package browsermonkey.render;

/**
 *
 * @author Paul Calcraft
 */
public class BulletListRenderer extends ListTagRenderer{
    public BulletListRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    protected String getListElementText(int index) {
        return "&nbsp;&nbsp;&nbsp;&nbsp;•&nbsp;";
    }
}