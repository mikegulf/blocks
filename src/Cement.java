
public class Cement extends Square {
	
	
	public Cement(Location loc, Blocks g, char ch) {
		super(loc, g, ch);
		isHidden = true;
		imgName = "Box";
	}
	
	public String getImageName() {
		return imgName;
	}
	
	public void setImageName(String name){
		imgName = name;
	}
	
	
	public boolean canEnter() {
		return false;
	}
	
	
}