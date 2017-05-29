package minusk.tiletech.utils;

import minusk.tiletech.world.LightChannel;

import static minusk.tiletech.world.LightChannel.*;

/**
 * Created by MinusKelvin on 2/14/16.
 */
public class Util {
	public static int cnkIdx(int xyz) {
		return xyz < 0 ? (xyz+1) % 32 + 31 : xyz % 32;
	}
	
	public static int getCnk(int xyz) {
		return xyz < 0 ? (xyz-31) / 32 : xyz / 32;
	}
	
	public static boolean contains(short v, short[] a) {
		for (int c : a)
			if (v == c)
				return true;
		return false;
	}
	
	public static int toLightRenderInt(int raw) {
		return lightValues[getRawLightComponent(raw, SUN)] << 24 |
				lightValues[getRawLightComponent(raw, BLUE)] << 16 |
				lightValues[getRawLightComponent(raw, GREEN)] << 8 |
				lightValues[getRawLightComponent(raw, RED)];
	}
	
	private static int getRawLightComponent(int raw, LightChannel channel) {
		return (raw >> channel.getShiftAmmount()) & 0xF;
	}
	
	private static final int[] lightValues = {
			  5,  11,  19,  28,  39,  52,  66,  81,
			 98, 116, 136, 157, 180, 204, 228, 255,
	};
}
