package minusk.tiletech.world.structures;

import minusk.tiletech.world.Tile;
import minusk.tiletech.world.World;

/**
 * Created by MinusKelvin on 2/14/16.
 */
public class Trees {
	private static boolean createMaple(PlacementFunction place, int x, int y, int z, int dim) {
		if (!place.limitedReplace(x,y,z,dim,new short[] {Tile.Grass.id, Tile.Dirt.id}, Tile.Dirt.id))
			return false;
		
		int height = World.getWorld().random(5,7);
		for (int i = 0; i < height; i++)
			place.limitedReplace(x,y+i+1,z,dim, PlacementFunction.airMask, Tile.Maple_Log.id);
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				place.limitedReplace(x+i,y+height-2,z+j,dim,PlacementFunction.airMask,Tile.Maple_Leaves.id);
				place.limitedReplace(x+i,y+height-1,z+j,dim,PlacementFunction.airMask,Tile.Maple_Leaves.id);
				if (Math.abs(i)+Math.abs(j) < 4)
					place.limitedReplace(x+i,y+height,z+j,dim,PlacementFunction.airMask,Tile.Maple_Leaves.id);
				if (Math.abs(i)+Math.abs(j) < 2)
					place.limitedReplace(x+i,y+height+1,z+j,dim,PlacementFunction.airMask,Tile.Maple_Leaves.id);
			}
		}
		return true;
	}
	
	public static boolean placeMapleTree(int x, int y, int z, int dim) {
		//return createMaple(World.getWorld()::limitedReplace,  x, y, z, dim); not impl'd
		return false;
	}
	
	public static boolean genMapleTree(int x, int y, int z, int dim) {
		return createMaple(new PlacementFunction() {
			@Override public boolean limitedReplace(int x, int y, int z, int dim, short[] canReplace, short replaceWith) {
				return World.getWorld().genLimitedReplace(x,y,z,dim,canReplace,replaceWith);
			}
			@Override public Tile getTile(int x, int y, int z, int dim) {
				return World.getWorld().genGetTile(x,y,z,dim);
			}
		}, x, y, z, dim);
	}
}
