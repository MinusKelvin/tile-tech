package minusk.tiletech.utils;

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
}
