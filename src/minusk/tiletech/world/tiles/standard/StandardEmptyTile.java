package minusk.tiletech.world.tiles.standard;

import minusk.tiletech.world.entities.Entity;
import org.joml.RayAabIntersection;

/**
 * Created by MinusKelvin on 2/5/16.
 */
public class StandardEmptyTile extends StandardTransparentTile {
	public StandardEmptyTile(short id, int top, int bottom, int east, int west, int north, int south) {
		super(id, top, bottom, east, west, north, south);
	}
	
	@Override
	public boolean collide(int x, int y, int z, int dim, Entity entity) {
		return false;
	}
	
	@Override
	public boolean raytrace(int x, int y, int z, int dim, RayAabIntersection ray) {
		return false;
	}
}
