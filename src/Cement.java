
public class Cement extends Square {
	
	private String imgName;
	private boolean hidden;
	
	Cement(Location loc, Blocks g, char ch) {
		
		super(loc, g, ch);
		imgName = "Box";
		hidden = true;
	}
	
	public String getImageName() {
		return imgName;
	}
	
	public void setImageName(String name){
		imgName = name;
	}
	
	public boolean canEnter() {
		return canPush(0);
	}
	
	public boolean canPush(int dir) {
		if(hidden) {
			setImageName("Cement");
			hidden = false;
			drawSelf();
		}
		return false;
	}
	
}