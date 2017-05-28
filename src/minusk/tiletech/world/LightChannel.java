package minusk.tiletech.world;

/**
 * @author MinusKelvin
 */
public enum LightChannel {
	RED,
	GREEN,
	BLUE,
	SUN;
	
	public int getShiftAmmount() {
		return ordinal() * 4;
	}
}
