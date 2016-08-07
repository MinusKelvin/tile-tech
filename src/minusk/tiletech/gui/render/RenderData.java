package minusk.tiletech.gui.render;

import java.nio.ByteBuffer;

/**
 * @author MinusKelvin
 */
public interface RenderData {
	void render(ByteBuffer buffer);
	int vertexCount();
}
