//---------------------------------------------------------------80 columns---|

/* Man.java
 * --------------
 */
class Man extends Thing {
	public Man(Square sq, Blocks g, char ch) {
		super(sq, g, ch);
	}

	public boolean doMove(Move m) {
		int direction = m.getDirection();
		
		Move undo = null;
		if (!m.getIsUndo())
		{
			undo = new Move(Location.reverseDirection(m.getDirection()), true);
		}

		Location newLoc = square.getLocation().adjacentLocation(direction);
		
		if (game.inBounds(newLoc))
		{
			Square newSquare = game.squareAt(newLoc);
			
			if (!m.getIsUndo()) 
				undo.setPushed(newSquare.getContents());
			
			if (newSquare.pushContents(direction))
			{
				if (!m.getIsUndo()) {
					game.addMove(undo);
					
				}
				return newSquare.addContents(this);
			}else if(newSquare.checkHidden()){
				System.out.println("Hidden");
			}
			
			
			else if (newSquare.canEnter())
			{
				if (!m.getIsUndo()) 
					undo.setPushed(null);
				
				boolean returnValue = newSquare.addContents(this);
				
				if (m.getIsUndo() && m.getPushed() != null)
				{
					game.squareAt(m.getPushed().getLocation().adjacentLocation(direction)).addContents(m.getPushed());
				}
				
				if (!m.getIsUndo()) {
					game.addMove(undo);
					//Blocks.track.setNextMove();
				}
				return returnValue;
			}
		}

		return false;
	}

	public String getImageName() {
		return "Man";
	}

	public boolean move(Move m) {
		return doMove(m);
	}
}
