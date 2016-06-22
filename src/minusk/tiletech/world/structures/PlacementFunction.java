package minusk.tiletech.world.structures;

import minusk.tiletech.world.Tile;

/**
 * Created by MinusKelvin on 2/14/16.
 */
public interface PlacementFunction {
	boolean limitedReplace(int x, int y, int z, int dim, short[] canReplace, short replaceWith);
	Tile getTile(int x, int y, int z, int dim);
	
	short[] airMask = new short[] {Tile.Air.id};
}
