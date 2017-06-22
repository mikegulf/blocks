//---------------------------------------------------------------80 columns---|

/* comp285 Sokoban class
 * ---------------
 * This is the top level game class.
 */

import java.io.*;
import java.util.*;

public class Blocks{
	private static int numLevels = 50;
	public static final boolean VERBOSE = false;
	private final static String alphabet="abcdefghijklmnopqrstuvwxyz";
	
	/**
	 * Maximum allowed number of undo moves. Set to -1 for unlimited.
	 */
	public static final int maximumAllowedUndos = 100;

	public static void main(String args[])
	{
		Blocks game = new Blocks(new Display("Block Test RCTA"));
		
		
		String thisLevel = args[0];
		String startLevel = "";
		
		for ( int i = 0; i < thisLevel.length(); i++ ) {
			
			if(Character.isDigit(thisLevel.charAt(i)))
				startLevel += String.valueOf(thisLevel.charAt(i));
		}
		
		long startTime = System.currentTimeMillis();

		File directory = new File(Long.toString(startTime));
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println(directory + " directory was created.");
            } else {
                System.out.println(directory + " directory already exists.");
            }
        }

		if(args[1].equals("true")){
			int thisSpeed = Integer.valueOf(args[2]);
			String dir = args[3];
			game.openFileForRead(dir + "/" + args[0] + ".txt");	
			game.watchRun(startLevel, thisSpeed);
		}else{
			game.play(Integer.valueOf(startLevel), Long.toString(startTime));
		}
		
	}

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
	
	private Grid squares;

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
			System.out.print(ioe);
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

        } catch (IOException ioe) {
        	System.out.println( "Exception: " + ioe); 
        } finally {
            if(out != null){
            	out.close();
            }
            System.out.println("\n--- File Closed ---");
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
			while (!levelOver) {
				processSingleCommand(display.getCommandFromUser());
			}
			if(!replayLevel){
				int writelevel = level - 1;
				openFileForWrite(dir + "/" + writelevel + ".txt");
				replayNum = 0;
			}else {
				int currentLevel = level;
				char replayChar = alphabet.charAt(replayNum - 1);
				openFileForWrite(dir + "/" + currentLevel + "" + replayChar + ".txt");
				replayLevel = false;
			}
		}

		quit();
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

		quit();
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
				//need to output any moves so for when quitting
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
		System.exit(1);
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
		String levelDirectory = System.getProperty("user.dir") + java.io.File.separator + "Levels" + java.io.File.separator;
		String filename = levelDirectory + "Level" + level + ".data";
		
		BufferedReader in;
		try
		{
			in = new BufferedReader(new FileReader(filename));
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Cannot find file \"" + filename + "\".");
			quit();
			return;
		}

		try
		{
			int numRows = Integer.valueOf(in.readLine().trim()).intValue();
			int numCols = Integer.valueOf(in.readLine().trim()).intValue();
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
			square.addContents(new Box(square, this, '1'));
			break;
		case 'b':
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '2'));
			break;
		case 'c':
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '3'));
			break;
		case 'd':
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '4'));
			break;
		case 'e':
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '5'));
			break;
		case 'f':
			square = new Square(location, this, '\0');
			square.addContents(new Box(square, this, '6'));
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
			System.out.println("problem interpreting character " + ch);
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
}
