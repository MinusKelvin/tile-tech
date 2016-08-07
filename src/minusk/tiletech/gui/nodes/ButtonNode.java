package minusk.tiletech.gui.nodes;

/**
 * @author MinusKelvin
 */
public abstract class ButtonNode extends GuiNode {
	private final boolean round;
	
	public ButtonNode(boolean round, int w, int h) {
		super(w, h);
		this.round = round;
	}
	
	@Override
	public void draw() {
		
	}
	
	@Override
	public void layout() {
		
	}
	
	@Override
	public void tick() {
		
	}
}
