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
	public boolean up,down,north,south,east,west;
	
	public DirectionalBoolean(boolean init) {
		up = init;
		down = init;
		north = init;
		south = init;
		east = init;
		west = init;
	}
}
