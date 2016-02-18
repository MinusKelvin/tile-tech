package minusk.tiletech.world.structures;

import minusk.tiletech.world.Tile;

/**
 * Created by MinusKelvin on 2/14/16.
 */
public interface PlacementFunction {
	boolean limitedReplace(int x, int y, int z, int dim, int[] canReplace, int replaceWith);
	Tile getTile(int x, int y, int z, int dim);
	
	int[] airMask = new int[] {Tile.Air.id};
}
