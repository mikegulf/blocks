//---------------------------------------------------------------80 columns---|

/* comp285 Sokoban class
 * ---------------
 * This is the top level game class.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

public class Blocks{
	private static int numLevels = 50;
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
	
	private Grid squares;
	public static Tracking track;

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
	 * @return List of moves from the file
	 */
	public List<String> openFileForRead(String file){
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	moveList= Arrays.asList(line.split(","));
		    	
		    	if (Blocks.VERBOSE)
		    	{
		    		//print out the moves from the file
		    		for(int i=0;i < moveList.size();i++){
		    			System.out.print(moveList.get(i) +",");
		    		}
		    		System.out.println();
		    	}
		    	return moveList;
		    }
		}catch (IOException ioe){
			errorMessage("Exception: " + ioe);
		}
		
		return null;
    }
	
	/**
	 * Write all the moves from the completed level
	 * @param filename - file to write to
	 */
	public void openFileForWrite(String filename){
		
		PrintWriter out = null;
        try {
            out = new PrintWriter(filename);
            
            Iterator<Move> it = undoMoveHistory.iterator();
			
            while(it.hasNext()) {
            	Move element = new Move(Location.reverseDirection(it.next().getDirection()), true);

            	//print the move to the screen
            	if (Blocks.VERBOSE)
            	{
            		System.out.print(element);
            	}
            	//print to file
            	out.write(element.toString());
            }
            
            //out.write(Arrays.deepToString(track.getGrid()));

            //openFileWriteBlocks("test");
            
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
	
	public void openFileWriteBlocks(String filename){
		
		Object[][] obj = track.getGrid();
		
		PrintWriter out = null;
        try {
            out = new PrintWriter(filename);
            
            for (int row = 0; row <= track.getCurrentMove(); row++)
			{
            	out.write("[");
				for (int col = 0; col < track.getNumBoxes(); col++)
				{
					out.write(obj[row][col].toString());
					if(col + 1 < track.getNumBoxes())
						out.write(",");
				}

				// Skip over newline at end of row
				out.write("]\n");
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
	 * Runs the application to completion. Starts at level defined in the first 
	 * command line argument, reads in the level file, activates the level, waits 
	 * for level to end, writes all the moves out to a file, and then stops the level. 
	 * It then advances to next level.
	 * 
	 * @param chosenLvel - level that is input from command line
	 */
	public void play(int chosenLevel, String dir) {
		level = chosenLevel;
		while (level < numLevels) {
			levelOver = false;
			vacantSlots = 0;
			readLevelFileForLevel(level);
			while (!levelOver && !gameQuit) {
				processSingleCommand(display.getCommandFromUser());
			}
			
			if(gameQuit)
				break;
			
			if(!replayLevel){
				int writelevel = level - 1;
				openFileForWrite(dir + "/" + writelevel + ".txt");
				openFileWriteBlocks(dir + "/" + writelevel + "blocks.txt");
				replayNum = 0;
			}else {
				int currentLevel = level;
				char replayChar = alphabet.charAt(replayNum - 1);
				openFileForWrite(dir + "/" + currentLevel + "" + replayChar + ".txt");
				openFileWriteBlocks(dir + "/" + currentLevel + "" + replayChar + "blocks.txt");
				replayLevel = false;
			}
		}
	}
	/**
	 * Reruns a level based on the moves from a file written from a user.
	 * @param level - starting level
	 * @param speed - speed between moves in milliseconds
	 */
	public void watchRun(String level, int speed) {

		levelOver = false;
		vacantSlots = 0;
		readLevelFileForLevel(Integer.valueOf(level));
		while (!levelOver) {
			for(int i = 0; i < moveList.size(); i++){
				//processSingleCommand(display.getCommandFromUser());
				man.move(new Move(Integer.valueOf(moveList.get(i)), false));
				try
				{
					Thread.sleep(speed);
				}
				catch (InterruptedException ie)
				{
					ie.printStackTrace();
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
						== JOptionPane.YES_OPTION)
					quit();
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
				Vector<Move> moves = search.getMovesForLocation(man.getLocation(), cmd.getGoal());
				while (moves.size() > 0)
				{
					man.doMove(moves.remove(0));
				}
				break;
			case Command.Directional:
				track.setNextMove(cmd.getMove());
				track.copyLocationsNextMove();
				man.move(cmd.getMove());
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

	public void quit()
	{
		gameQuit = true;
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
			//tracking of boxes for each move
			
			
			int numRows = Integer.valueOf(in.readLine().trim()).intValue();
			int numCols = Integer.valueOf(in.readLine().trim()).intValue();
			int numBoxes =  Integer.valueOf(in.readLine().trim()).intValue();
			
			track = new Tracking(numBoxes);
			
			squares = new Grid(numRows, numCols);
			display.configureForSize(numRows, numCols);
			display.drawStatusMessage("Loading level " + level + "...");
			for (int row = 0; row < numRows; row++)
			{
				for (int col = 0; col < numCols; col++)
				{
					readOneSquare(new Location(row, col), (char) in.read());
				}

				// Skip over newline at end of row
				in.readLine();
			}
			display.drawStatusMessage("Loaded level " + level + "...");
			display.setVisible(true);
			display.grabFocus();
			undoMoveHistory.clear();
			totalMoves = 0;
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
		case 'a':
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '0'));
			track.setElementAt(location, 0);
			break;
		case 'b':
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '1'));
			track.setElementAt(location, 1);
			break;
		case 'c':
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '2'));
			track.setElementAt(location, 2);
			break;
		case 'd':
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '3'));
			track.setElementAt(location, 3);
			break;
		case 'e':
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '4'));
			track.setElementAt(location, 4);
			break;
		case 'f':
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '5'));
			track.setElementAt(location, 5);
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
		case '0':
			square = new Goal(location, this, '0');
			incrementSlots();
			break;
		case '1':
			square = new Goal(location, this, '1');
			incrementSlots();
			break;
		case '2':
			square = new Goal(location, this, '2');
			incrementSlots();
			break;
		case '3':
			square = new Goal(location, this, '3');
			incrementSlots();
			break;
		case '4':
			square = new Goal(location, this, '4');
			incrementSlots();
			break;
		case '5':
			square = new Goal(location, this, '5');
			incrementSlots();
			break;
		case '6':
			square = new Goal(location, this, '6');
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
	
	private void errorMessage(String message) {
		JOptionPane.showMessageDialog(display, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
