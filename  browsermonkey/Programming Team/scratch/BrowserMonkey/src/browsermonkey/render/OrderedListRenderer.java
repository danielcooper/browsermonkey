package browsermonkey.render;

/**
 *
 * @author Paul Calcraft
 */
public class OrderedListRenderer extends ListTagRenderer{
    public OrderedListRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    protected String getListElementText(int index) {
        return " "+(index+1)+". ";
    }
}