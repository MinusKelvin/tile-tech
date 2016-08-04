package minusk.tiletech.gui.nodes;

import minusk.tiletech.gui.Gui;
import minusk.tiletech.inventory.Slot;

/**
 * @author MinusKelvin
 */
public class SlotNode extends GuiNode {
	private final int slotID;
	private final boolean big;
	
	public SlotNode(int slotID, boolean big) {
		super(big ? 64 : 48, big ? 64 : 48);
		this.slotID = slotID;
		this.big = big;
	}
	
	@Override
	public void render() {
		Gui.drawNinepatch(0,x,y,48,48,-1);
		Slot slot = Gui.getSlot(slotID);
		Gui.drawItem(slot.getItem() == null ? slot.getGhost() : slot.getItem(), x+8, y+8);
	}
	
	@Override
	public void layout() {}
	
	@Override
	public void onOpen(Slot[] slots, GuiNode closing) {
		
	}
	
	@Override
	public void onClose(Slot[] slots, GuiNode opening) {
		
	}
}
