//---------------------------------------------------------------80 columns---|

/* Square.java
 * --------------
 */

class Square
{
	protected Location location;
	protected Blocks game;
	protected Thing contents;
	protected String imgName;
	protected char pch;

	public Square(Location loc, Blocks g, char ch)
	{
		location = loc;
		game = g;
		imgName = "";
		pch = ch;
	}

	public boolean addContents(Thing c)
	{
		if (canEnter())
		{
			boolean wasGoal = (c.getSquare() instanceof Goal);
			
			char pastPch = c.getSquare().pch;

			c.getSquare().removeContents();

			// Add to this square
			contents = c;
				
			
			// Make sure thing knows where it is
			c.setSquare(this);


			// Draw the contents of the square
			drawSelf(); 

			if (this instanceof Goal && c instanceof Box && c.pch == this.pch)
			{
				System.out.println("They match");
				game.decrementSlots();
			}
			else if (!(this instanceof Goal) && c instanceof Box && wasGoal && c.pch == pastPch)
			{
				game.incrementSlots();
			}

			if (Blocks.VERBOSE)
			{
				System.out.println("Vacant slots: " + game.vacantSlots());
			}

			return true;
		}

		return false;
	}

	public boolean canEnter()
	{
		return (contents == null);
	}

	public boolean canPush(int direction)
	{
		Square neighbour = game.squareAt(location.adjacentLocation(direction));

		return (contents != null && neighbour != null && neighbour.canEnter());
	}

	public void drawContents() {
		if (contents != null) {
			contents.drawSelf(getLocation());
		}
	}

	public void drawImage() {
		game.drawAtLocation(getImageName(), pch, getLocation());
	}

	public void drawSelf() {
		drawImage();
		drawContents();
	}

	public Thing getContents() {
		return contents;
	}

	public String getImageName() {
		return "Empty";
	}

	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location loc) {
		location = loc;
	}

	public boolean pushContents(int direction) {
		
		if(contents != null)
			contents.unHide();
		
		if (!canPush(direction)) {
			return false;
		}

		Square neighbour = game.squareAt(location.adjacentLocation(direction));
		neighbour.addContents(contents);

		return true;
	}

	public void removeContents() {
		contents = null;
		drawSelf();
	}
	
	public void setImageName(String name){
		imgName = name;
	}
}
