package minusk.tiletech.inventory;

/**
 * @author MinusKelvin
 */
public class Slot {
	private ItemStack item, ghost;
	private int[] whitelist;
	private boolean disabled;
	
	public Slot(boolean disabled) {
		this.disabled = disabled;
	}
	
	public ItemStack getGhost() {
		return ghost;
	}
	
	public void setGhost(ItemStack ghost) {
		this.ghost = ghost;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
}
