package minusk.tiletech.gui.menus;

import minusk.tiletech.TileTech;
import minusk.tiletech.gui.Gui;
import minusk.tiletech.inventory.Slot;

/**
 * @author MinusKelvin
 */
public final class PauseMenu extends Gui {
	private PauseMenu() {}
	
	@Override
	public void onClose(Slot[] slots, Gui opening) {
		TileTech.game.unpause();
	}
	
	@Override
	public void draw() {
		Gui.drawNinepatch(1, -100, -100, 200, 32, -1);
		String str = "Horrible Hundred";
		Gui.drawText(str, 1-Gui.getTextWidth(str)/2, -100, -1);
	}
	
	@Override
	public void layout() {
		
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public void onOpen(Slot[] slots, Gui closing) {
		TileTech.game.pause();
	}
	
	private static Gui instance = new PauseMenu();
	public static Gui get() {
		return instance;
	}
}
