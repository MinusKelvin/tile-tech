package minusk.tiletech.gui.menus;

import minusk.tiletech.gui.GuiStructure;
import minusk.tiletech.inventory.Slot;

/**
 * @author MinusKelvin
 */
public class PauseMenu extends GuiStructure {
	@Override
	public void onClose(Slot[] slots, GuiStructure opening) {
		
	}
	
	@Override
	public void onOpen(Slot[] slots, GuiStructure closing) {
		
	}
	
	private static GuiStructure instance = new PauseMenu();
	public static GuiStructure get() {
		return instance;
	}
}
