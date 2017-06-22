import java.util.*;

public class Search
{
	private Blocks game;
	
	public Search(Blocks game)
	{
		this.game = game;
	}
	
	public Vector<Move> getMovesForLocation(Location start, Location end)
	{
		Vector<Move> moves = new Vector<Move>();
		
		int startX = start.getCol();
		int startY = start.getRow();
		
		int endX = end.getCol();
		int endY = end.getRow();
		
		int directionX = startY > endY ? Location.North : Location.South;
		int directionY = startX > endX ? Location.West : Location.East;
		
		if (!(startX == startY && endX == endY))
		{
			if ((game.squareAt(end).canEnter() || game.squareAt(end).canPush(directionX)) && startX == endX)
			{
				int difference = startY > endY ? startY - endY : endY - startY;
				
				while (difference-- > 0)
				{
					moves.add(new Move(directionX, false));
				}
			}
			if (game.squareAt(end).canEnter() || game.squareAt(end).canPush(directionY) && startY == endY)
			{
				int difference = startX > endX ? startX - endX : endX - startX;
				
				while (difference-- > 0)
				{
					moves.add(new Move(directionY, false));
				}
			}
		}
		
		return moves;
	}
}
