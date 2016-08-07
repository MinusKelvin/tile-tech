package minusk.tiletech.gui.nodes;

/**
 * @author MinusKelvin
 */
public abstract class GuiNode {
	protected int x,y;
	public final int width, height;
	
	public GuiNode(int w, int h) {
		width = w;
		height = h;
	}
	
	public abstract void draw();
	/** Positions the children of this element. Does not modify this element. */
	public abstract void layout();
	public abstract void tick();
}
