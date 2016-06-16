package minusk.tiletech.utils;

/**
 * Created by MinusKelvin on 2/3/16.
 * 
 * +X = East
 * -X = West
 * +Y = Up
 * -Y = Down
 * +Z = South
 * -Z = North
 */
public class DirectionalBoolean {
	/** +Y */
	public boolean up;
	/** -Y */
	public boolean down;
	/** -Z */
	public boolean north;
	/** +Z */
	public boolean south;
	/** +X */
	public boolean east;
	/** -X */
	public boolean west;
	
	public DirectionalBoolean(boolean init) {
		up = init;
		down = init;
		north = init;
		south = init;
		east = init;
		west = init;
	}
}
