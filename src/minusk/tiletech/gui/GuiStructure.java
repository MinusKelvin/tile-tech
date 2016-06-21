package minusk.tiletech.gui;

import minusk.tiletech.inventory.Slot;

/**
 * @author MinusKelvin
 */
public abstract class GuiStructure {
	public void render() {
		
	}
	
	public abstract void onOpen(Slot[] slots, GuiStructure closing);
	public abstract void onClose(Slot[] slots, GuiStructure opening);
}
