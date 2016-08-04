package minusk.tiletech.gui.nodes;

import minusk.tiletech.inventory.Slot;

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
	
	public abstract void render();
	/** Positions the children of this element. Does not modify this element. */
	public abstract void layout();
	public abstract void onOpen(Slot[] slots, GuiNode closing);
	public abstract void onClose(Slot[] slots, GuiNode opening);
}
