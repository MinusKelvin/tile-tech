package minusk.tiletech.gui.menus;

import minusk.tiletech.gui.Gui;
import minusk.tiletech.gui.nodes.GuiNode;
import minusk.tiletech.inventory.Slot;

/**
 * @author MinusKelvin
 */
public class PauseMenu extends GuiNode {
	private PauseMenu() {
		super(0,0);
	}
	
	@Override
	public void onClose(Slot[] slots, GuiNode opening) {
		
	}
	
	@Override
	public void render() {
		Gui.drawNinepatch(1, -100, -100, 200, 32, -1);
		String str = "Horrible Hundred";
		Gui.drawText(str, 1-Gui.getTextWidth(str)/2, -100, -1);
	}
	
	@Override
	public void layout() {
		
	}
	
	@Override
	public void onOpen(Slot[] slots, GuiNode closing) {
		
	}
	
	private static GuiNode instance = new PauseMenu();
	public static GuiNode get() {
		return instance;
	}
}
