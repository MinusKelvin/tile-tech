package minusk.tiletech.gui.render;

import java.nio.ByteBuffer;

/**
 * @author MinusKelvin
 */
public class NinepatchData implements RenderData {
	private final int x, y, w, h, id, color;
	
	public NinepatchData(int _x, int _y, int _w, int _h, int _id, int _color) {
		x = _x;
		y = _y;
		w = _w;
		h = _h;
		id = _id;
		color = _color;
	}
	
	@Override
	public void render(ByteBuffer buffer) {
		renderPart(buffer, x,     y,     8,    8,    0,     0,     0.25f, 0.25f, id, color); // TOP LEFT
		renderPart(buffer, x+w-8, y,     8,    8,    0.75f, 0,     1,     0.25f, id, color); // TOP RIGHT
		renderPart(buffer, x,     y+h-8, 8,    8,    0,     0.75f, 0.25f, 1,     id, color); // BOTTOM LEFT
		renderPart(buffer, x+w-8, y+h-8, 8,    8,    0.75f, 0.75f, 1,     1,     id, color); // BOTTOM RIGHT
		renderPart(buffer, x+8,   y,     w-16, 8,    0.25f, 0,     0.75f, 0.25f, id, color); // TOP CENTER
		renderPart(buffer, x+8,   y+h-8, w-16, 8,    0.25f, 0.75f, 0.75f, 1,     id, color); // BOTTOM CENTER
		renderPart(buffer, x,     y+8,   8,    h-16, 0,     0.25f, 0.25f, 0.75f, id, color); // MIDDLE LEFT
		renderPart(buffer, x+w-8, y+8,   8,    h-16, 0.75f, 0.25f, 1,     0.75f, id, color); // MIDDLE RIGHT
		renderPart(buffer, x+8,   y+8,   w-16, h-16, 0.25f, 0.25f, 0.75f, 0.75f, id, color); // MIDDLE CENTER
	}
	
	private static void renderPart(ByteBuffer buffer, int x, int y, int w, int h, float tx1, float ty1, float tx2, float ty2, int id, int color) {
		// TOP LEFT
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putInt(color);
		buffer.putFloat(tx1);
		buffer.putFloat(ty1);
		buffer.putFloat(id);
		// TOP RIGHT
		buffer.putFloat(x+w);
		buffer.putFloat(y);
		buffer.putInt(color);
		buffer.putFloat(tx2);
		buffer.putFloat(ty1);
		buffer.putFloat(id);
		// BOTTOM RIGHT
		buffer.putFloat(x+w);
		buffer.putFloat(y+h);
		buffer.putInt(color);
		buffer.putFloat(tx2);
		buffer.putFloat(ty2);
		buffer.putFloat(id);
		// TOP LEFT
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putInt(color);
		buffer.putFloat(tx1);
		buffer.putFloat(ty1);
		buffer.putFloat(id);
		// BOTTOM RIGHT
		buffer.putFloat(x+w);
		buffer.putFloat(y+h);
		buffer.putInt(color);
		buffer.putFloat(tx2);
		buffer.putFloat(ty2);
		buffer.putFloat(id);
		// BOTTOM LEFT
		buffer.putFloat(x);
		buffer.putFloat(y+h);
		buffer.putInt(color);
		buffer.putFloat(tx1);
		buffer.putFloat(ty2);
		buffer.putFloat(id);
	}
	
	@Override
	public int vertexCount() {
		return 54;
	}
}
