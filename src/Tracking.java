import java.util.ArrayList;

//Quick fix to allow compilation

public class Tracking {

	private int nBoxes, currentMove;
	private ArrayList<Location[]> mGrid;
	
	public Tracking(int boxes) {
		nBoxes = boxes;
		currentMove = 0;
		mGrid = new ArrayList<Location[]>();
		mGrid.add(new Location[nBoxes]);
	}
	
	public void setElementAt(Location loc, int num) {
		mGrid.get(currentMove)[num] = loc;
	}
	
	public void copyLocationsNextMove() {
		for(int i = 0; i < nBoxes; ++i) {
			if(mGrid.get(currentMove)[i] == null) {
				mGrid.get(currentMove)[i] = mGrid.get(currentMove - 1)[i];
			}
		}
	}
	
	public void setNextMove(Move m) {
		mGrid.add(++currentMove, new Location[nBoxes]);
		
		if(m.getPushed() != null && m.getPushed().getImageName().equals("Box")
				&& m.getPushed().getSquare().canPush(m.getDirection())) {
			mGrid.get(currentMove)[m.getPushed().pch] = m.getPushed().getLocation().adjacentLocation(m.getDirection());
		}
	}
	
	public int getNumBoxes() {
		return nBoxes;
	}
	
	public Location[][] getGrid() {
		Location [][] grid = new Location[currentMove+1][nBoxes];
		
		for(int i = 0; i <= currentMove; ++i) {
			grid[i] = mGrid.get(i);
		}
		return grid;
	} 
	
	public int getCurrentMove() {
		return currentMove;
	}
}
