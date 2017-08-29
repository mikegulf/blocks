

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;


/**
 * Display is the class responsible for drawing images, and
 * monitoring user input. All input is parsed and stored in a 
 * private vector. There are methods for obtaining such input.
 * This class, and its nested classes do not need to be modified.
 */


public class Display extends JApplet
{
	
    static final long serialVersionUID = 1;
    
    private static final int Margin = 0;
    private static final String FontName = "Helvetica";
    

	final Font INSTRUCTION_FONT = new Font(FontName, Font.PLAIN, 15);
	final Font PROMPT_FONT = new Font(FontName, Font.PLAIN, 18);
	final Font TIME_FONT = new Font(FontName, Font.BOLD, 54);
	final Font MSG_FONT = new Font(FontName, Font.PLAIN, 12);
	final Font BOX_FONT = new Font("SansSerif", Font.BOLD, 16);
	
    private static final int BlockSize = 40;
	
	public Display() {
	}
	
	Blocks game;
	File directory;
	
	@Override
	public void init() {
		
		NamedImage.preloadImages(this);
		game = new Blocks(this);
		
	}
	
	@Override
	public void start() {
		
		directory = new File(Long.toString(System.currentTimeMillis()));
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println(directory + " directory was created.");
            } else {
                System.out.println(directory + " directory already exists.");
            }
        }
        (new Thread() {
        	public void run() {
        		gamePlay();
        	}
        }).start();
	}
	
	@Override
	public void destroy() {

		game.quit();
		
		game.uploadAndDelete(directory.toPath());
		
	}
	
	private void gamePlay() {
		
		instructionPrompt("You are being asked to join a research study looking to understand how people make spatial " + 
				"decisions. Your decision to be in this research is voluntary. You can stop at any time. You do not " + 
				"have to answer any questions you do not want to answer. " + 
				"You will first complete the Map Planning Task to assess your spatial ability. It contains a " + 
				"practice map, followed by 4 additional maps. Then you will get directions to complete the game. " + 
				"In the game you will use the avatar to push boxes to target locations. You will complete 4 " + 
				"different sets of tasks, each having 9 levels of increased difficulty. You will be notified at the " + 
				"completion of the study. " + 
				"The entire study should take up to 1 hour to complete. " +
				"Please contact Dr. Kristin " + 
				"Schaefer-Lay, Kristin.e.schaefer-lay.civ@mail.mil, if you have any questions or require more information " +
				"about this study. " + 
				"You may also contact the Human Protection Administrator (HPA) of the Army Research Laboratory " + 
				"at (410) 278-5928 with questions, complaints, or concerns about this research, or if you feel this " + 
				"study has harmed you. The HPA can also answer questions about your rights as a research " + 
				"subject. You may also call the HPA if you cannot reach the research team or wish to talk to " + 
				"someone who is not a member of the research team.", 
				
				"Click here to begin the Map Planning Task Practice Map");

		mapInsructions();
		game.mapTest(directory.getPath());
		
		game.practiceRound();
		
		instructionPrompt("In this part of the game, you will be asked to complete 9 levels of increasing difficulty. You will " + 
				"control an avatar through a room by using the arrow keys on your keyboard. The goal is to move " + 
				"the correctly labeled blue box to the correctly labeled white target location (i.e., box 1 should be pushed to " + 
				"target location 1). <b>Please note that the numbers are only to link the box with a target location and " + 
				"are not a suggested order for moving the boxes.</b>", 
				
				"Start Set 1");
		game.play(9, directory.getPath());
		
		instructionPrompt("In this part of the game, you will be asked to complete 9 levels of increasing difficulty. You " + 
				"will control an avatar through a room by using the arrow keys on your keyboard. The goal is to " + 
				"move the correctly labeled blue box to the correctly labeled white target location (i.e., box 1 should be " + 
				"pushed to target location 1). <b>In this set of levels, you will not know the box label until you push " + 
				"the box with your avatar. Please note that the numbers are only to link the box with a target " + 
				"location and are not a suggested order for moving the boxes.</b>",
				
				"Start Set 2");
		game.play(9, directory.getPath(), Blocks.GameType.UNLABELED_GAME);
		
		instructionPrompt("In this part of the game, you will be asked to complete 9 levels of increasing difficulty. You will " + 
				"control an avatar through a room by using the arrow keys on your keyboard. The goal is to move " + 
				"the correctly labeled blue box to the correctly labeled white target location (i.e., box 1 should be pushed to " + 
				"target location 1). In this set of levels, you will not know the box label until you push the box " + 
				"with your avatar. Please note that the numbers are only to link the box with a target location and " + 
				"are not a suggested order for moving the boxes. <b>In addition, there is an extra box that does not " + 
				"have a label. This box is movable but will not have a target location.</b>", 
				
				"Start Set 3");
		game.play(9, directory.getPath(), Blocks.GameType.IRRELEVANT_GAME);
		
		instructionPrompt("In this part of the game, you will be asked to complete 9 levels of increasing difficulty. You will " + 
				"control an avatar through a room by using the arrow keys on your keyboard. The goal is to move " + 
				"the correctly labeled blue box to the correctly labeled white target location (i.e., box 1 should be pushed to " + 
				"target location 1). In this set of levels, you will not know the box label until you push the box " + 
				"with your avatar. Please note that the numbers are only to link the box with a target location and " + 
				"are not a suggested order for moving the boxes. <b>In addition, there is an extra box that does not " + 
				"have a label. This box is immovable. When you push this box with your avatar, it will change " + 
				"color to let you know that it is an immovable box.</b>", 
				
				"Start Set 4");
		game.play(9, directory.getPath(), Blocks.GameType.IMMOVABLE_GAME);
		
		
		finishGame();
//		game.uploadAndDelete(directory.toPath());
//		
//		
//		instructionPrompt("Thank you for participating in our research study. Please go back to the Amazon Mechanical Turk site and enter this number: " + directory.toString() + ".", null);
	}
	
	public void finishGame(){
		game.uploadAndDelete(directory.toPath());

		instructionPrompt("Thank you for participating in our research study. Please go back to the Amazon Mechanical Turk site and enter this number: " + directory.toString() + ".", null);
	}
	
	
	//inner class GridCanvas
    public class GridCanvas extends JPanel
    {
    	static final long serialVersionUID = 1;
    	
		private int numRows, numCols, blockSize;
		private List<BlockImage> images = new CopyOnWriteArrayList<>();
		private List<Point> trail;
		
		private class BlockImage {
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + getOuterType().hashCode();
				result = prime * result + ((r == null) ? 0 : r.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				BlockImage other = (BlockImage) obj;
				if (!getOuterType().equals(other.getOuterType()))
					return false;
				if (r == null) {
					if (other.r != null)
						return false;
				} else if (!r.equals(other.r))
					return false;
				return true;
			}

			public String name;
			public char ch;
			public Rectangle r;
			
			BlockImage(Rectangle rect, String n, char c) {
				r = rect;
				name = n;
				ch = c;
			}
			
			public boolean hasChar() {
				return ch > 0;
			}

			private GridCanvas getOuterType() {
				return GridCanvas.this;
			}
		}
		
		public GridCanvas(int size)
		{	
		    setFont(BOX_FONT);
		    blockSize = size;
		}
		
		/**
		 * Helper to ensure a particular location is in bounds for this Canvas, throws exception if not
		 */
		private boolean badLocation(Location loc)
		{
		    return (loc.getRow() < 0 || loc.getRow() >= numRows || loc.getCol() < 0 || loc.getCol() >= numCols);
		}
		
		private void checkLocation(Location loc)
		{
		    if (badLocation(loc))
		    {
				throw new IndexOutOfBoundsException("Grid Canvas asked to draw at location " + loc + " which is outside grid boundaries.");
		    }
		}
	
		public void configureForSize(int nRows, int nCols)
		{
		    numRows = nRows;
		    numCols = nCols;
		    setSize(blockSize*numCols, blockSize*numRows);
		    images.clear();
		    repaint();
		}
		
		private void drawCenteredString(Graphics g, String s, Rectangle r)
		{
		    FontMetrics fm = g.getFontMetrics();
		    g.drawString(s, r.x + (r.width - fm.stringWidth(s)) / 2, r.y + (r.height + fm.getHeight()) / 2);
		}
		
		public void addToTrail(Location loc) {
			if(trail == null) 
				trail = new CopyOnWriteArrayList<>();
			
			Rectangle r = rectForLocation(loc.getRow(), loc.getCol());
			
			trail.add(new Point(r.x + (r.width / 2), r.y + (r.height / 2)));
			repaint();
		}
		
		public void drawImageAndLetterAtLocation(String imageFileName, char ch, Location loc)
		{
			// Make sure location is valid
		    checkLocation(loc);
		    
		    Rectangle r = rectForLocation(loc.getRow(), loc.getCol());
		    
		    images.add(new BlockImage(r, imageFileName, ch));
		    
		    repaint(r);	
		}
		
		@Override
		public Dimension getPreferredSize()
		{
		    return new Dimension(blockSize * numCols, blockSize * numRows);
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
		    for(BlockImage bi : images) {
		    	g.drawImage(NamedImage.findImageNamed(bi.name).image, bi.r.x, bi.r.y, bi.r.width, bi.r.height, this);
		    	if(bi.hasChar()) {
		    		g.setColor(bi.name.equals("Box") ? Color.WHITE : Color.BLACK);
		    		drawCenteredString(g, bi.ch + "", bi.r);
		    	}
		    }
		    if(trail != null && trail.size() > 1) {
		    	Point p1 = trail.get(0);
		    	g.setColor(Color.GREEN);
		    	for(Point p2: trail) {
		    		g.drawLine(p1.x, p1.y, p2.x, p2.y);
		    		p1 = p2;
		    	}
		    }
		}
		
		private Rectangle rectForLocation(int row, int col)
		{
		    return new Rectangle(col * blockSize, (numRows - row - 1) * blockSize, blockSize, blockSize);
		}
	
		@Override
		public void update(Graphics g)
		{
		    paint(g);
		}
    }
	
    // static nested class NamedImage
    static class NamedImage
    {
		private static Vector<NamedImage> allImages = new Vector<NamedImage>();
		private static MediaTracker mt;
		private static String things[] = { "Man", "Box" };
		private static String squares[] = { "Empty", "Wall", "Goal" };
		private static JApplet app;
		
		static public NamedImage findImageNamed(String name)
		{
		    return findImageNamed(name, false);
		}
		static public NamedImage findImageNamed(String name, boolean isBackgroundImage)
		{
		    NamedImage key = new NamedImage(name);
		    int foundIndex = allImages.indexOf(key);
		    
		    // Search cache for this name
		    if (foundIndex != -1)
		    {
		    	return allImages.elementAt(foundIndex);
		    }
		    // Return shared version
		    else
		    {
				key.image = app.getImage(app.getClass().getClassLoader().getResource("Images/" + name + ".gif"));
				
				// Create image from file
				mt.addImage(key.image, 0);
				
				// Add to Media Tracker
				try
				{
					mt.waitForID(0);
				}
				catch (InterruptedException ie)
				{
				}
				
				allImages.addElement(key);	
				
				// Add to list of all images
				key.isBackgroundImage = isBackgroundImage;
				return key;		
		    }
		}
		static public void preloadImages(JApplet target)
		{
			app = target;
		    mt = new MediaTracker(target);
		    
		    for (int i = 0; i < things.length; i++)
		    {
		    	findImageNamed(things[i]);
		    }
		    
		    for (int i = 0; i < squares.length; i++)
		    {
		    	findImageNamed(squares[i], true);
		    }
		}
		
		public String name;
		
		public Image image;
		
		public boolean isBackgroundImage;
		
		private NamedImage(String n)
		{
		    name = n;
		}
		
		@Override
		public boolean equals(Object o)
		{
		    return ((o instanceof NamedImage) && name.equals(((NamedImage)o).name));
		}
    }
	
	private Label msgField;
	
    private GridCanvas gridCanvas;

    private Vector<Command> cmds = new Vector<Command>();
    
    public synchronized void addCommand(KeyEvent ke)
    {
		Command cmd = new Command(ke.getKeyCode());
		cmds.addElement(cmd);
		
		// Rendezvous with anyone waiting
		notify();		
    }
    

    public void configureForSize(int numRows, int numCols)
    {
    	try {
			SwingUtilities.invokeAndWait(() -> {
				configureBlocks(numRows, numCols);
		    	gridCanvas.configureForSize(numRows, numCols);
		    	repaint();
				revalidate();
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
    }

    private void configureBlocks(int numRows, int numCols)
    {
    	getContentPane().removeAll();
    	
		getContentPane().setLayout(new BorderLayout(Margin, Margin));
				
		JTextPane textarea = new JTextPane();
		textarea.setEditable(false);
		textarea.setContentType("text/html");
		textarea.setFont(INSTRUCTION_FONT);
		textarea.setText("<center>Move with the <b>arrow keys</b>,"
				+ "Press <b>N</b> to skip this level, <b>Q</b> to quit, and <b>R</b> to restart this level.</center>");
		
		JPanel bp = new JPanel();
		// numRows, numCols, hGap, vGap
		bp.setLayout(new GridLayout(2, 1, 10, 0)); 
		//bp.add(new Label("Move with the <b>arrow keys</b>, and <b>U</b> for undo.", Label.CENTER));
		bp.add(textarea);
		//bp.add(new Label("To move to a square, where there is a clear path, just click the mouse.", Label.CENTER));
		//bp.add(new Label("Press <b>N</b> to skip this level, <b>Q</b> to quit, and <b>R</b> to restart this level.", Label.CENTER));
		bp.add(msgField = new Label("New game", Label.CENTER));
		msgField.setFont(MSG_FONT);
		
		JPanel panel = new JPanel();
		gridCanvas = new GridCanvas(BlockSize);
		panel.add(gridCanvas);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(bp, BorderLayout.SOUTH);
		
		gridCanvas.addKeyListener
		(
			new KeyAdapter()
			{
				@Override
				public void keyPressed(KeyEvent ke)
				{
				    Display.this.addCommand(ke);
				}
			}
		);
    }
    

	public void instructionPrompt(String instructions, String label) {
		
		final Semaphore clicked = new Semaphore(0);
		
    	try {
			
			SwingUtilities.invokeAndWait(() -> {
				getContentPane().removeAll();
				
				getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
				
				JTextPane textarea = new JTextPane();
				textarea.setEditable(false);
				textarea.setContentType("text/html");
				textarea.setText("<html><style> p { font: " + PROMPT_FONT.getSize() + " " + PROMPT_FONT.getFamily() + "; text-align: center}</style><p>"
						+ instructions + "</p></html>");
				
				getContentPane().add(textarea);
				
				if(label != null) {
					JButton button = new JButton(label);
			    	button.addActionListener(new AbstractAction() {
			    		private static final long serialVersionUID = 1L;
	
						@Override
			    		public void actionPerformed(ActionEvent e) {
							clicked.release();
			    		}
			    	});
			        	
			    	getContentPane().add(button);
			    	
			    	SwingUtilities.getRootPane(this).setDefaultButton(button);
			    	button.requestFocus();

				}
		    	
				repaint();
				revalidate();
			});
		}catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
    	
    	try {
			clicked.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
   	}
	
	private void mapInsructions() {
		final Semaphore clicked = new Semaphore(0);
		
    	try {
			
			SwingUtilities.invokeAndWait(() -> {
				getContentPane().removeAll();
				
				getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
				
				JTextPane instructionPane = new JTextPane();
				instructionPane.setEditable(false);
				instructionPane.setFont(INSTRUCTION_FONT);
				
				final Document doc = instructionPane.getDocument();
				
				final Style style = new StyleContext().addStyle("myStyle", null);
				try {
					doc.insertString(0, "This is a test of your ability to find the shortest route between two places as quickly as possible.\r\n" + 
							"The drawing below is a map of a city. The dark lines are streets. The circles are road-blocks, and\r\n" + 
							"you cannot pass at the places where there are circles. The numbered squares are buildings. You\r\n" + 
							"are to find the shortest route between two lettered points. ", style);
					StyleConstants.setBold(style, true);
					doc.insertString(doc.getLength(), "The number on the building passed is your answer.\r\n", style);
					
					StyleConstants.setBold(style, false);
					doc.insertString(doc.getLength(), "\r\nRules:\r\n" + 
			    			"1. The shortest route will always pass along the side of one and only one building.\r\n" + 
			    			"2. A building is not considered as having been passed if a route passes only a corner.\r\n" + 
			    			"3. The same numbered building may be used on more than one route.\r\n" +
			    			"\r\nAn example of the shortest route from A to Z is provided below. The answer is Building 1.", style);
				} catch (BadLocationException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				    	
		    	getContentPane().add(instructionPane);
		    	
		    	JLabel imgLabel = new JLabel();
		    	
		    	imgLabel.setIcon(
		    			new ImageIcon(this.getImage(this.getClass().getClassLoader().getResource("Images/PracticeMap_Example.png"))));
		    	
		    	getContentPane().add(imgLabel);
				
				JButton button = new JButton("Begin Map Planning Task");
		    	button.addActionListener(new AbstractAction() {
		    		private static final long serialVersionUID = 1L;

					@Override
		    		public void actionPerformed(ActionEvent e) {
						clicked.release();
		    		}
		    	});
		        	
		    	getContentPane().add(button);
		    	
		    	SwingUtilities.getRootPane(this).setDefaultButton(button);
		    	button.requestFocus();

		    	
				repaint();
				revalidate();
			});
		}catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
    	
    	try {
			clicked.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
       
    int mapLevel;
	int timeLeft;
    public int[][] mapPrompt(String[][] prompts, String[] answers) {
    	    	
    	int[][] results = new int[prompts.length][];
    	mapLevel = 0;
    	final Display app = this;
    	
    	getContentPane().removeAll();
    	getContentPane().setLayout(new BorderLayout(Margin, Margin));
    	    	
    	JLabel imgLabel = new JLabel();
    	imgLabel.setIcon(
    			new ImageIcon(this.getImage(this.getClass().getClassLoader().getResource("Images/Map" + mapLevel + ".png"))));
    	
    	getContentPane().add(imgLabel, BorderLayout.CENTER);
    	
    	JPanel labelPanel = new JPanel();
    
		JTextPane instructionPane = new JTextPane();
		instructionPane.setEditable(false);
		instructionPane.setFont(INSTRUCTION_FONT);
		
		final String practiceText = "\r\nPractice by finding the shortest route between the various points listed at the right of the map";
		final Document doc = instructionPane.getDocument();
		
		final Style style = new StyleContext().addStyle("myStyle", null);
		try {
			doc.insertString(0, "This is a test of your ability to find the shortest route between two places as quickly as possible.\r\n" + 
					"The drawing below is a map of a city. The dark lines are streets. The circles are road-blocks, and\r\n" + 
					"you cannot pass at the places where there are circles. The numbered squares are buildings. You\r\n" + 
					"are to find the shortest route between two lettered points. ", style);
			StyleConstants.setBold(style, true);
			doc.insertString(doc.getLength(), "The number on the building passed is your answer.\r\n", style);
			
			StyleConstants.setBold(style, false);
			doc.insertString(doc.getLength(), "\r\nRules:\r\n" + 
	    			"1. The shortest route will always pass along the side of one and only one building.\r\n" + 
	    			"2. A building is not considered as having been passed if a route passes only a corner.\r\n" + 
	    			"3. The same numbered building may be used on more than one route.\r\n" + practiceText, style);
		} catch (BadLocationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		    	
    	labelPanel.add(instructionPane);
    	
    	JLabel timeLabel = new JLabel();
    	
    	timeLabel.setFont(TIME_FONT);
    	
    	labelPanel.add(timeLabel);
    	
    	getContentPane().add(labelPanel, BorderLayout.NORTH);
    	
    	JPanel panel = new JPanel();
    	
    	panel.setLayout(new GridLayout(prompts[mapLevel].length +1, 1));
    	
    	for(String prompt : prompts[mapLevel]) {
    		JLabel lbl = new JLabel(prompt);
    		panel.add(lbl);
    		
    		JSpinner spn = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
    		lbl.setLabelFor(spn);
    		panel.add(spn);
    	}
    	
    	JButton button = new JButton("Next");
    	
    	button.addActionListener(new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		boolean reviewed = false;

			@Override
    		public void actionPerformed(ActionEvent e) {
				
				timeLeft = -1;
				
				//Completed practice level
				if(!reviewed && mapLevel == 0) {
					reviewed = true;
					for(int i = 0; i < answers.length; ++i) {
						JLabel l = (JLabel) panel.getComponent(2*i);
						l.setText(String.format("<html>%s <font color='red'>(%s)</font></html>", 
						        l.getText(), answers[i]));
					}
					JOptionPane.showMessageDialog(app, "The correct answers are shown in red\r\nPlease review them and select Next again when done", "Review", JOptionPane.INFORMATION_MESSAGE);
					
					try {
						doc.remove(doc.getLength() - practiceText.length(), practiceText.length());
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return;
				}
				
				if(mapLevel < prompts.length) {
					
					results[mapLevel] = new int[prompts[mapLevel].length];
					for(int i = 0; i < prompts[mapLevel].length; ++i) {
						results[mapLevel][i] = (int) ((JSpinner)panel.getComponent(2*i + 1)).getValue();
					}

					if(++mapLevel == prompts.length) return;
					
					JOptionPane.showMessageDialog(app, "Begin Map " + (mapLevel) + "\r\n"
	    					+ "You will have 3 minutes to complete 10 routes", "Next Map", JOptionPane.INFORMATION_MESSAGE);
										
			    	imgLabel.setIcon(
			    			new ImageIcon(app.getImage(app.getClass().getClassLoader().getResource("Images/Map" + mapLevel + ".png"))));
					
					panel.removeAll();
			    	panel.setLayout(new GridLayout(prompts[mapLevel].length+1,2));
					for(String prompt : prompts[mapLevel]) {
			    		panel.add(new JLabel(prompt));
			    		panel.add(new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)));
			    	}
					panel.add(button);
					SwingUtilities.getRootPane(app).setDefaultButton(button);
			    	button.requestFocus();
					
	    			timeLeft = 60 * 3;

				}
    		}
    	});
    	
    	panel.add(button);
    	getContentPane().add(panel, BorderLayout.EAST);
    	
    	SwingUtilities.getRootPane(this).setDefaultButton(button);
    	button.requestFocus();
    	
    	revalidate();
    	repaint();
    	    	
    	timeLeft = -1;
    	
    	while(mapLevel < prompts.length) {
    		
    		if(timeLeft > 0) {
    			timeLabel.setText(String.format("%d:%02d",  timeLeft / 60, timeLeft % 60));
        		timeLeft--;
    		}
    		else if(timeLeft == 0) {
    			button.getActionListeners()[0].actionPerformed(null);
    		}
    		  		    		
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
    	}
    		    	
    	return results;
    }

    public void doDrawStatusMessage(String msg)
    {
    	if(msgField != null)
    		msgField.setText(msg);
    }
    
    public void drawAtLocation(String name, char ch, Location loc)
    {
    	gridCanvas.drawImageAndLetterAtLocation(name, ch, loc);
    }
    
//    public void drawAtLocation(String name, Location loc)
//    {
//    	drawAtLocation(name, '1', loc);
//    }
     
    public void drawStatusMessage(String msg)
    {
    	doDrawStatusMessage(msg);
    }
    
    public synchronized Command getCommandFromUser()
    {
		while (cmds.size() == 0)
		{	
		    // while vector of commands is empty
		    try
		    {
		    	wait();
		    } 
		    catch (InterruptedException e)
		    {
		    }
		    // wait for notify
		}
		
		Command cmd = cmds.elementAt(0);
		
		// Pull first command out of queue
		cmds.removeElementAt(0);
		
		return cmd;
    }
    
    public boolean grabFocus()
    {
    	gridCanvas.requestFocus();
    	return gridCanvas.hasFocus();
    }

	public void addToTrail(Location location) {
		gridCanvas.addToTrail(location);
	}

	public void configurePractice(int numRows, int numCols) {
		try {
			SwingUtilities.invokeAndWait(() -> {
				
				getContentPane().removeAll();
		    	
				getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
						
				JTextPane textarea = new JTextPane();
				textarea.setEditable(false);
				textarea.setContentType("text/html");
				textarea.setText("<html><style> center { font: " + INSTRUCTION_FONT.getSize() + " " + INSTRUCTION_FONT.getFamily() + "}</style><center>" +
						"Thank you for completing the Map Planning Task. You will now be asked to complete the Box\r\n" + 
						"Moving Game. You will move an avatar around an open room in order to push boxes to target\r\n" + 
						"locations. You will use the arrow keys to move the avatar. A level is complete once all boxes\r\n" + 
						"have reached their target locations. The R key\r\n" + 
						"will allow you to restart a level. You will start by completing a practice round moving 1 box to 1 target location.\r\n" + 
						"</center></html>");
				
				getContentPane().add(textarea);
				
				JPanel panel = new JPanel();
				gridCanvas = new GridCanvas(BlockSize);
				gridCanvas.configureForSize(numRows, numCols);
							
				gridCanvas.addKeyListener
				(
					new KeyAdapter()
					{
						@Override
						public void keyPressed(KeyEvent ke)
						{
						    Display.this.addCommand(ke);
						}
					}
				);
				
				panel.add(gridCanvas);
				
				getContentPane().add(panel, BorderLayout.CENTER);
				
				JButton button = new JButton("Continue to Set 1 Instructions");
				
				button.addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						game.quit();
						addCommand(new KeyEvent(gridCanvas, 0, 0, 0, 0));
					}
					
				});
				
				getContentPane().add(button);
				
				repaint();
				revalidate();
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}





