package minusk.tiletech.utils;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * @author MinusKelvin
 */
public enum Direction {
	/** +Y */
	UP(0,1,0),
	/** -Y */
	DOWN(0,-1,0),
	/** -Z */
	NORTH(0,0,-1),
	/** +Z */
	SOUTH(0,0,1),
	/** +X */
	EAST(1,0,0),
	/** -X */
	WEST(-1,0,0);
	
	public final int xOffset, yOffset, zOffset;
	public final Vector3fc direction;
	
	Direction(int x, int y, int z) {
		xOffset = x;
		yOffset = y;
		zOffset = z;
		direction = new Vector3f(x,y,z);
	}
}
