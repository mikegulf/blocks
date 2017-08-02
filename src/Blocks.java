

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

public class Blocks{
	public static final boolean VERBOSE = false;
	private final static String alphabet="abcdefghijklmnopqrstuvwxyz";
	
	/**
	 * Maximum allowed number of undo moves. Set to -1 for unlimited.
	 */
	public static final int maximumAllowedUndos = 100;


	private Search search;
	private Vector<Move> undoMoveHistory = new Vector<Move>();

	private int totalMoves = 0;
	private Display display;
	private Man man;
	private int vacantSlots = 0;
	
	//list of moves read in from file
	private List<String> moveList;

	//Whether the level is over.
	private boolean levelOver;
	private int level = 0;
	private int replayNum = 0;
	private boolean replayLevel = false;
	
	private boolean gameQuit = false;
	
	private GameType gameType;
	
	private Grid squares;
	public Tracking track;
	
	public enum GameType {
		REGULAR_GAME,
		UNLABELED_GAME,
		IRRELEVANT_GAME,
		IMMOVABLE_GAME
	};

	public Blocks(Display display)
	{
		this.display = display;
		man = null;
		search = new Search(this);
	}
	
	/**
	 * Add move to list of all moves from the level
	 * @param move
	 */
	public void addMove(Move move)
	{
		undoMoveHistory.add(move);
		if (undoMoveHistory.size() > maximumAllowedUndos || maximumAllowedUndos == -1)
		{
			undoMoveHistory.remove(0);
		}
		totalMoves++;
		
		if (Blocks.VERBOSE)
		{
			System.out.println("Total moves: " + totalMoves);
		}
	}
	
	/**
	 * Undo move
	 */
	public void undoLastMove()
	{
		if (undoMoveHistory.size() > 0)
		{
			man.doMove(undoMoveHistory.remove(undoMoveHistory.size() - 1));
		}
		
		totalMoves++;
	}
	
	/**
	 * 
	 * @return number of open goals in the level
	 */
	public int vacantSlots()
	{
		return this.vacantSlots;
	}

	/**
	 * Decrease number of open goals to know when to end the level.
	 */
	public void decrementSlots() {
		vacantSlots--;
		if (vacantSlots == 0)
		{
			levelOver = true;
			
			display.drawStatusMessage("You completed level " + level++ );
			try
			{
				Thread.sleep(2000);
			}
			catch (InterruptedException ie)
			{
			}
		}
	}
	/**
	 * Open file and parse each move.
	 * @param file - file to read from
	 */
	public void openFileForRead(String file){
				
		gameType = GameType.REGULAR_GAME;
		
		moveList = new ArrayList<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    
		    //Skip the header "X,Y,dir,timestamp,level,condition"
		    br.readLine();
		    
		    String[] line1 = br.readLine().split(",");
		    
		    moveList.add(line1[2]);
		    level = Integer.parseInt(line1[4]);
		    gameType = GameType.valueOf(line1[5]);
		    
		    while ((line = br.readLine()) != null) {
		    	if(line.split(",").length > 2)
		    		moveList.add(line.split(",")[2]);		    	
		    }
		    
		}catch (IOException ioe){
			errorMessage("Exception: " + ioe);
		}
    }
	
	/**
	 * Write all the moves from the completed level
	 * @param dir - directory in which to save file
	 * @param level - level number to save
	 */
	public void openFileForWrite(String dir, int level){
		
		PrintWriter out = null;
		String filename;
		
		filename = dir + "\\" + level + (replayLevel ? alphabet.charAt(replayNum - 1) : "") + gameType.toString() + ".txt";
		
        try {
            out = new PrintWriter(filename);
            			
            out.write("X,Y,direction,timestamp,level,condition" + System.lineSeparator());
            
            Location loc = track.getStartingLocation();
            
            Object[] moves = track.getMoves();
            
            for(int i = 0; i < track.getCurrentMove(); ++i) {
            	out.write(loc + "," + ((Move)moves[i]).getDirection() + "," + track.getTimeStamp(i) + ",");
            	out.write(level + "," + gameType.toString() + System.lineSeparator());
            	loc = loc.adjacentLocation(((Move)moves[i]).getDirection());
        	}
            out.write(loc + System.lineSeparator());
            
        } catch (IOException ioe) {
        	errorMessage( "Exception: " + ioe); 
        } finally {
            if(out != null){
            	out.close();
            }
            System.out.println("\n--- File Closed ---");
            //System.out.println(Arrays.deepToString(track.getGrid()));
        }
    }
	
	public void openFileWriteBlocks(String dir, int level){
		
		String filename;
		
		filename = dir + "\\blocks" + level + (replayLevel ? alphabet.charAt(replayNum - 1) : "") + gameType.toString() + ".txt";
		
		Location[][] obj = track.getGrid();
		
		PrintWriter out = null;
        try {
            out = new PrintWriter(filename);
            
            for(int i = 0; i < track.getNumBoxes(); ++i) {
             	if(gameType == GameType.IRRELEVANT_GAME && i == track.getNumBoxes() - 1)
            		out.write("irrelevantBoxX, irrelevantBoxY, irrelevantBoxMoved,");
             	else
             		out.write("box" + i + "x,box" + i + "y,box" + i + "moved,");
            }
            out.write("timestamp" + System.lineSeparator());
            
            Location[] lastMove = obj[0];
            
            for (int move = 0; move <= track.getCurrentMove(); move++)
			{
            	for (int box = 0; box < track.getNumBoxes(); box++)
            	{
					out.write(obj[move][box].toString() + ",");
					out.write((obj[move][box] == lastMove[box]) ? "0," : "1,");
				}
            	if(move == track.getCurrentMove())
            		out.write(System.lineSeparator());
            	else
            		out.write(track.getTimeStamp(move) + System.lineSeparator());
				lastMove = obj[move];
			}

            //System.out.println(Arrays.deepToString(track.getGrid()));
			

        } catch (IOException ioe) {
        	errorMessage( "Exception: " + ioe); 
        } finally {
            if(out != null){
            	out.close();
            }
            System.out.println("\n--- Blocks File Closed ---");
        }
    }

	public void drawAtLocation(String name,char ch, Location loc) {
		display.drawAtLocation(name, ch, loc);
	}

	public boolean inBounds(Location location) {
		return squares.inBounds(location);
	}

	public void incrementSlots() {
		vacantSlots++;
	}

	/**
	 * Runs one "Round" of the game starting at chosenLevel
	 * @param numLevels - number of levels to play
	 * @param dir - pathname of directory to save game data in
	 * @param gameType - determines which "round" to play, defaults to regular
	 */
	public void play(int numLevels, String dir, GameType gt) {
		level = 0;
		
		gameType = gt;
		
		while (level < numLevels && !gameQuit) {
			
			levelOver = false;
			vacantSlots = 0;
			readLevelFileForLevel(level);
			while (!levelOver && !gameQuit) {
				processSingleCommand(display.getCommandFromUser());
			}
			
			if(!replayLevel){
				int writelevel = level - 1;
				openFileForWrite(dir, writelevel);
				openFileWriteBlocks(dir, writelevel);
				replayNum = 0;
			}else {
				int currentLevel = level;
				openFileForWrite(dir, currentLevel);
				openFileWriteBlocks(dir, currentLevel);
				replayLevel = false;
			}
		}
	}
	
	public void play(int chosenLevel, String dir) {
		play(chosenLevel, dir, GameType.REGULAR_GAME);
	}
	/**
	 * Reruns a level based on the moves from a file written from a user.
	 * @param levelFile - filename of the level to view
	 */
	public void watchRun(String levelFile) {

		levelOver = false;
		vacantSlots = 0;
		
		openFileForRead(levelFile);
		readLevelFileForLevel(level);
		
		while (!levelOver) {
			
			display.addToTrail(man.getSquare().getLocation());
						
			for(int i = 0; i < moveList.size(); i++){
				//processSingleCommand(display.getCommandFromUser());
				
				man.move(new Move(Integer.valueOf(moveList.get(i)), false));
				
				display.addToTrail(man.getSquare().getLocation());

				try
				{
					Thread.sleep(250);
				}
				catch (InterruptedException ie)
				{
					errorMessage("Exception: " + ie);
				}
			}
			levelOver = true;
		}
	}

	/**
	 * Process each command
	 * @param cmd
	 */
	private void processSingleCommand(Command cmd)
	{
		switch (cmd.getType())
		{
			case Command.Quit:
				if(JOptionPane.showConfirmDialog(display, "Are you sure you want to quit?", "Confirm Quit", JOptionPane.YES_NO_OPTION)
						== JOptionPane.YES_OPTION) {
					quit();
					display.stop();
				}
				return;
			case Command.Next:
				levelOver = true;
				level++;
				break;
			case Command.Replay:
				levelOver = true;
				replayNum++;
				replayLevel = true;
				break;
			case Command.Jump:
//				Vector<Move> moves = search.getMovesForLocation(man.getLocation(), cmd.getGoal());
//				while (moves.size() > 0)
//				{
//					man.doMove(moves.remove(0));
//				}
				System.out.println(cmd.getGoal());
				break;
			case Command.Directional:
				
				track.setNextMove(cmd.getMove());
				man.move(cmd.getMove());
				track.copyLocationsNextMove();
				break;
			case Command.Undo:
				undoLastMove();
				break;
			case Command.Error:
				break;
			default:
				break;
		}
	}
	
	public void quit() {
		gameQuit = true;
		display.finishLine();
	}
	
	/**
	 * Reads the configuration file. The files are assumed to be stored in a
	 * subdirectory "Levels" of the current directory. The level filenames should
	 * be "Level0.data", "Level1.data" and so on. The first two lines of the
	 * file identify how many rows and columns are in the particular level. The
	 * grid and display are reconfigured to match that.
	 */
	private void readLevelFileForLevel(int level)
	{
		BufferedReader in;
		try
		{
			InputStream is = new URL(display.getCodeBase() + "Levels/level" + level + ".data").openStream();
			in = new BufferedReader(new InputStreamReader(is));
		}
		catch (Exception e)
		{
			errorMessage("Cannot open file \"Levels/Level" + level + ".data\".");
			return;
		}

		try
		{			
			
			int numRows = Integer.valueOf(in.readLine().trim()).intValue();
			int numCols = Integer.valueOf(in.readLine().trim()).intValue();
			int numBoxes =  Integer.valueOf(in.readLine().trim()).intValue();
			
			track = new Tracking(numBoxes + (gameType == GameType.IRRELEVANT_GAME ? 1 : 0));
			
			squares = new Grid(numRows, numCols);
			display.configureForSize(numRows, numCols);
			display.drawStatusMessage("Loading level " + level + "...");
			for (int row = numRows - 1; row >= 0; row--)
			{
				for (int col = 0; col < numCols; col++)
				{
					readOneSquare(new Location(row, col), (char) in.read());
				}

				// Skip over newline at end of row
				in.readLine();
			}
			
			for (int row = 0; row < numRows; row++) {
				for ( int col = 0; col < numCols; col++) {
					
					Square sq = squareAt(new Location(row, col));
					
					switch (gameType) {
					case UNLABELED_GAME:
						
						if(col >= numCols / 2) {
							swapSquares(sq, squareAt(new Location(row, numCols - col - 1)));
						}
						

						if(sq.getContents() instanceof Box) {
							sq.getContents().setHidden(true);
							track.setElementAt(sq.getLocation(), sq.pch);
						}
					case REGULAR_GAME:
						if(sq instanceof Cement) {
							sq = new Square(sq.getLocation(), this, '\0');
							squares.setElementAt(sq.getLocation(), sq);
						}
						break;
						

					case IRRELEVANT_GAME:
						
						if(row >= numRows / 2) {
							swapSquares(sq, squareAt(new Location(numRows - row - 1, col)));
						}
						
						if(sq.getContents() instanceof Box) {
							sq.getContents().setHidden(true);
							track.setElementAt(sq.getLocation(), sq.pch);
						}
						else if(sq instanceof Cement) {
							sq = new Square(sq.getLocation(), this, '\0');
							sq.addContents(new Box(sq, this, '\0'));
							squares.setElementAt(sq.getLocation(), sq);
							
							//Use numBoxes as an unused valid value for the irrelevant box
							track.setElementAt(sq.getLocation(), numBoxes);
						}
						break;
						
					case IMMOVABLE_GAME:

						if(sq.getContents() instanceof Box) {
							sq.getContents().setHidden(true);
							track.setElementAt(sq.getLocation(), sq.pch);
						}
						
						if(row > numRows - col - 1 || (row == numRows - col - 1 && row >= numRows / 2)) {
							
							swapSquares(sq, squareAt(new Location(numRows - row - 1, numCols - col - 1)));
							
							sq = squareAt(new Location(row, col));
							
							if(sq.getContents() instanceof Box) {
								sq.getContents().setHidden(true);
								track.setElementAt(sq.getLocation(), sq.pch);
							}
							
						}
						
					default:
						break;
					}
					sq.drawSelf();
				}
			}
			display.drawStatusMessage("Loaded level " + level + "...");
			display.setVisible(true);
			display.grabFocus();
			undoMoveHistory.clear();
			totalMoves = 0;
			track.setMan(man);
		}
		catch (IOException e)
		{
			System.out.println("File improperly formatted, quitting");
			return;
		}
		
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Given the character just read from the Level file and that location 
	 * that it represents, this method is supposed to configure that square 
	 * in the grid to have the proper contents.
	 */
	private void readOneSquare(Location location, char ch) {
		Square square = null;

		switch (ch) {
		case '#':
			square = new Wall(location, this, '\0');
			break;
		case ' ':
			square = new Square(location, this, '\0');
			break;
		// Boxes
		case '$': //Box without number
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '\0'));
			break;
		// Goals
		case '*': //goal with box already on it
			square = new Goal(location, this, '\0');
			square.addContents(new Box(square, this, '\0'));
			incrementSlots();
			break;
		case '.': // default goal without number
			square = new Goal(location, this, '\0');
			incrementSlots();
			break;
		case '^':
			square = new Cement(location, this, '\0');
			break;
		case '@': 
			square = new Square(location, this, '\0');
			man = new Man(square, this, '\0');
			square.addContents(man);
			break;
		
		case '!':
			square = new Space(location, this, '\0');
			break;
		}
		
		if('0' <= ch && ch <= '9') {
			square = new Goal(location, this, ch);
			incrementSlots();
		}
		else if('a' <= ch && ch <= 'j') {
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, (char)(ch - 'a' + '0')));
			track.setElementAt(location, ch - 'a');
		}

		if (square == null) {
			errorMessage("problem interpreting character " + ch);
			return;
		}

		squares.setElementAt(location, square);
		square.drawSelf();
	}

	/**
	 * Get the element at a specific location
	 * @param location
	 * @return Square for that location
	 */
	public Square squareAt(Location location) {
		return (squares.inBounds(location) ? ((Square) squares
				.elementAt(location)) : null);
	}
	
	private void swapSquares(Square sq1, Square sq2) {
		Location swapLoc = sq1.getLocation();
		
		squares.setElementAt(sq1.getLocation(), sq2);
		squares.setElementAt(sq2.getLocation(), sq1);
		
		sq1.setLocation(sq2.getLocation());
		sq2.setLocation(swapLoc);
		
		sq1.drawSelf();
		sq2.drawSelf();
	}
	
	private void errorMessage(String message) {
		JOptionPane.showMessageDialog(display, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
