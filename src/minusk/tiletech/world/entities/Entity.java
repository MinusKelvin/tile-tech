package minusk.tiletech.world.entities;

import minusk.tiletech.utils.DirectionalBoolean;
import minusk.tiletech.world.World;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * Created by MinusKelvin on 1/26/16.
 */
public abstract class Entity {
	protected final float halfWidth, halfHeight;
	protected int dimension = 0;
	public final Vector3f center = new Vector3f(), velocity = new Vector3f(), look = new Vector3f(0,0,-1);
	
	public final Vector3f lastCenter = new Vector3f(), lastLook = new Vector3f();
	
	public Entity(float width, float height) {
		halfWidth = width/2;
		halfHeight = height/2;
	}
	
	public abstract void update();
	public abstract void render(boolean shadowpass);
	
	protected Vector3i[][][] collide(Vector3i[] ignore) {
		int minX = (int) Math.floor(center.x-halfWidth);
		int maxX = (int) Math.ceil(center.x+halfWidth);
		int minY = (int) Math.floor(center.y-halfHeight);
		int maxY = (int) Math.ceil(center.y+halfHeight);
		int minZ = (int) Math.floor(center.z-halfWidth);
		int maxZ = (int) Math.ceil(center.z+halfWidth);
		
		Vector3i[][][] results = new Vector3i[maxX-minX][maxZ-minZ][maxY-minY];
		
		for (int x = minX; x < maxX; x++) {
			for (int z = minZ; z < maxZ; z++) {
				lp:
				for (int y = minY; y < maxY; y++) {
					for (Vector3i v : ignore)
						if (v.x == x && v.z == z && v.y == y)
							continue lp;
					if (World.getWorld().getTile(x,y,z,dimension).collide(x,y,z, dimension, this))
						results[x-minX][z-minZ][y-minY] = new Vector3i(x,y,z);
				}
			}
		}
		return results;
	}
	
	protected DirectionalBoolean move() {
		DirectionalBoolean collides = new DirectionalBoolean(false);
		
		Vector3i[][][] v = collide(new Vector3i[0]);
		int c = 0;
		for (Vector3i[][] v1 : v)
			for (Vector3i[] v2 : v1)
				for (Vector3i v3 : v2)
					if (v3 != null)
						c++;
		
		Vector3i[] ignoreList = new Vector3i[c];
		c = 0;
		for (Vector3i[][] v1 : v)
			for (Vector3i[] v2 : v1)
				for (Vector3i v3 : v2)
					if (v3 != null)
						ignoreList[c++] = v3;
		
		int direction = (int) Math.signum(velocity.x);
		for (float i = 0; i < direction*velocity.x; i += halfWidth*2) {
			center.x += direction*Math.min(velocity.x*direction - i, halfWidth*2);
			v = collide(ignoreList);
			float bestX = Float.POSITIVE_INFINITY*direction;
			for (Vector3i[][] v1 : v) {
				for (Vector3i[] v2 : v1) {
					for (Vector3i v3 : v2) {
						if (v3 != null) {
							if (direction == 1)
								bestX = Math.min(bestX, World.getWorld().getTile(v3.x,v3.y,v3.z,dimension).lowX(v3.x,v3.y,v3.z,dimension,this));
							else
								bestX = Math.max(bestX, World.getWorld().getTile(v3.x,v3.y,v3.z,dimension).highX(v3.x,v3.y,v3.z,dimension,this));
						}
					}
				}
			}
			if (bestX*direction != Float.POSITIVE_INFINITY) {
				center.x = bestX - halfWidth*direction;
				velocity.x = 0;
				if (direction == 1)
					collides.west = true;
				else
					collides.east = true;
				break;
			}
		}
		
		direction = (int) Math.signum(velocity.z);
		for (float i = 0; i < direction*velocity.z; i += halfWidth*2) {
			center.z += direction*Math.min(velocity.z*direction - i, halfWidth*2);
			v = collide(ignoreList);
			float bestZ = Float.POSITIVE_INFINITY*direction;
			for (Vector3i[][] v1 : v) {
				for (Vector3i[] v2 : v1) {
					for (Vector3i v3 : v2) {
						if (v3 != null) {
							if (direction == 1)
								bestZ = Math.min(bestZ, World.getWorld().getTile(v3.x,v3.y,v3.z,dimension).lowZ(v3.x,v3.y,v3.z,dimension,this));
							else
								bestZ = Math.max(bestZ, World.getWorld().getTile(v3.x,v3.y,v3.z,dimension).highZ(v3.x,v3.y,v3.z,dimension,this));
						}
					}
				}
			}
			if (bestZ*direction != Float.POSITIVE_INFINITY) {
				center.z = bestZ - halfWidth*direction;
				velocity.z = 0;
				if (direction == 1)
					collides.north = true;
				else
					collides.south = true;
				break;
			}
		}
		
		direction = (int) Math.signum(velocity.y);
		for (float i = 0; i < direction*velocity.y; i += halfHeight*2) {
			center.y += direction*Math.min(velocity.y*direction - i, halfHeight*2);
			v = collide(ignoreList);
			float bestY = Float.POSITIVE_INFINITY*direction;
			for (Vector3i[][] v1 : v) {
				for (Vector3i[] v2 : v1) {
					for (Vector3i v3 : v2) {
						if (v3 != null) {
							if (direction == 1)
								bestY = Math.min(bestY, World.getWorld().getTile(v3.x,v3.y,v3.z,dimension).lowY(v3.x,v3.y,v3.z,dimension,this));
							else
								bestY = Math.max(bestY, World.getWorld().getTile(v3.x,v3.y,v3.z,dimension).highY(v3.x,v3.y,v3.z,dimension,this));
						}
					}
				}
			}
			if (bestY*direction != Float.POSITIVE_INFINITY) {
				center.y = bestY - halfHeight*direction;
				velocity.y = 0;
				if (direction == 1)
					collides.up = true;
				else
					collides.down = true;
				break;
			}
		}
		
		return collides;
	}
}
