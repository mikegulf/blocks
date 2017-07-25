
import java.util.ArrayList;

//Quick fix to allow compilation

public class Tracking {

	private int nBoxes, currentMove;
	private ArrayList<Location[]> boxGrid;
	private ArrayList<Move> moves;
	private ArrayList<Long> times;
	private long startTime = 0;
	private Man man;
	private Location start;
	
	public Tracking(int boxes) {
		nBoxes = boxes;
		currentMove = 0;
		boxGrid = new ArrayList<>();
		boxGrid.add(new Location[nBoxes]);
		
		moves = new ArrayList<>();
		times = new ArrayList<>();
	}
	
	public void setElementAt(Location loc, int num) {
		boxGrid.get(currentMove)[num] = loc;
	}
	
	public void setMan(Man m) {
		man = m;
		start = man.getSquare().getLocation();
	}
	
	public void copyLocationsNextMove() {
		
		for(int i = 0; i < nBoxes; ++i) {
			if(boxGrid.get(currentMove - 1)[i].equals(man.getSquare().getLocation())) {
				boxGrid.get(currentMove)[i] 
						= boxGrid.get(currentMove - 1)[i].adjacentLocation(moves.get(currentMove-1).getDirection());
			} else {
				boxGrid.get(currentMove)[i] = boxGrid.get(currentMove - 1)[i];
			}
		}
	}
	
	public void setNextMove(Move m) {
		if(startTime == 0) {
			startTime = System.currentTimeMillis();
			times.add((long) 0);
		}
		else
			times.add(System.currentTimeMillis() - startTime);
		moves.add(m);
		currentMove++;
		boxGrid.add(currentMove, new Location[nBoxes]);
	}
	
	public int getNumBoxes() {
		return nBoxes;
	}
	
	public Location[][] getGrid() {
		Location [][] grid = new Location[currentMove+1][nBoxes];
		
		for(int i = 0; i <= currentMove; ++i) {
			grid[i] = boxGrid.get(i);
		}
		return grid;
	} 
	
	public long getTimeStamp(int move) {
		return times.get(move);
	}
	
	public Object[] getMoves() {
		return  moves.toArray();
	}
	
	public Location getStartingLocation() {
		return start;
	}
	
	public int getCurrentMove() {
		return currentMove;
	}
}