public class ItemLock extends Item implements Visible{

	public ItemLock(String s, String sd, String[] a) {
		super(s, sd, a);
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean b) {
		this.visible = b;
	}
	protected boolean visible = true;
}
