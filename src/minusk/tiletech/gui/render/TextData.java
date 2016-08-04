package minusk.tiletech.gui.render;

import minusk.tiletech.gui.Gui;

import java.nio.ByteBuffer;

/**
 * @author MinusKelvin
 */
public class TextData implements RenderData {
	private final String text;
	private final int x, y, color;
	
	public TextData(String _text, int _x, int _y, int _color) {
		text = _text;
		x = _x;
		y = _y;
		color = _color;
	}
	
	@Override
	public void render(ByteBuffer buffer) {
		int offset = 0;
		for (int i = 0; i < text.length(); i++) {
			renderChar(buffer, x + offset, y, color, text.charAt(i));
			offset += 2 + Gui.getCharWidth(text.charAt(i));
		}
	}
	
	private static void renderChar(ByteBuffer buffer,  int x, int y, int color, int ch) {
		// TOP LEFT
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putInt(color);
		buffer.putFloat(0);
		buffer.putFloat(0);
		buffer.putFloat(ch+16);
		// TOP RIGHT
		buffer.putFloat(x+32);
		buffer.putFloat(y);
		buffer.putInt(color);
		buffer.putFloat(1);
		buffer.putFloat(0);
		buffer.putFloat(ch+16);
		// BOTTOM RIGHT
		buffer.putFloat(x+32);
		buffer.putFloat(y+32);
		buffer.putInt(color);
		buffer.putFloat(1);
		buffer.putFloat(1);
		buffer.putFloat(ch+16);
		// TOP LEFT
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putInt(color);
		buffer.putFloat(0);
		buffer.putFloat(0);
		buffer.putFloat(ch+16);
		// BOTTOM RIGHT
		buffer.putFloat(x+32);
		buffer.putFloat(y+32);
		buffer.putInt(color);
		buffer.putFloat(1);
		buffer.putFloat(1);
		buffer.putFloat(ch+16);
		// BOTTOM LEFT
		buffer.putFloat(x);
		buffer.putFloat(y+32);
		buffer.putInt(color);
		buffer.putFloat(0);
		buffer.putFloat(1);
		buffer.putFloat(ch+16);
	}
	
	@Override
	public int vertexCount() {
		return 6*text.length();
	}
}
