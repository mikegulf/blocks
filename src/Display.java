

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
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;


/**
 * Display is the class responsible for drawing images, and
 * monitoring user input. All input is parsed and stored in a 
 * private vector. There are methods for obtaining such input.
 * This class, and its nested classes do not need to be modified.
 */


public class Display extends JApplet
{
	public Display() {
	}
	Blocks game;
	File directory;
	
	@Override
	public void init() {
		
		NamedImage.preloadImages(this);
		game = new Blocks(this);
	}
	Thread gt = null;
	
	@Override
	public void start() {
		if(gt == null) {
			gt = new Thread() {
				@Override
				public void run() {
					gamePlay();
				}
			};
			gt.start();
		}
	}
	
	@Override
	public void destroy() {

		game.quit();
		
		if(Files.exists(directory.toPath(), LinkOption.NOFOLLOW_LINKS)) {
			try {
				Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
					
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				        Files.delete(file);
				        return FileVisitResult.CONTINUE;
				    }
				});
				
				Files.delete(directory.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void gamePlay() {
		long startTime = System.currentTimeMillis();
		directory = new File(Long.toString(startTime));
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println(directory + " directory was created.");
            } else {
                System.out.println(directory + " directory already exists.");
            }
        }
        
		game.mapTest(directory.getPath());
		game.play(9, directory.getPath());
		game.play(9, directory.getPath(), Blocks.GameType.UNLABELED_GAME);
		game.play(9, directory.getPath(), Blocks.GameType.IRRELEVANT_GAME);
		game.play(9, directory.getPath(), Blocks.GameType.IMMOVABLE_GAME);
		finishLine();
		game.uploadAndDelete(directory.toPath());

	}
	
	public void finishLine() {
		getContentPane().removeAll();
		
		JTextPane textarea = new JTextPane();
		textarea.setEditable(false);
		textarea.setContentType("text/html");
		textarea.setFont(new Font(FontName, Font.PLAIN, FontSize * 10));
		textarea.setText("<center><h1>Thanks for playing!</h1></center>");
		
		getContentPane().add(textarea, BorderLayout.CENTER);
		
		revalidate();
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

			public Image img;
			public char ch;
			public Rectangle r;
			
			BlockImage(Rectangle rect, Image i, char c) {
				r = rect;
				img = i;
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
		    setBackground(Color.white);
		    setFont(new Font("SansSerif", Font.PLAIN, 12));
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
		    g.setColor(Color.black);
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
		    
		    // Draw image at location
		    drawLocation(loc, NamedImage.findImageNamed(imageFileName), ch);	
		}
		
		private void drawLocation(Location loc, NamedImage ni, char letter)
		{
		    Rectangle r = rectForLocation(loc.getRow(), loc.getCol());
		    
		    if (letter > 0)
		    {
		    	drawCenteredString(getGraphics(), letter + "", r);
		    }
		    
		    images.add(new BlockImage(r, ni.image, letter));
		    
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
		    	g.drawImage(bi.img, bi.r.x, bi.r.y, bi.r.width, bi.r.height, this);
		    	if(bi.hasChar())
		    		drawCenteredString(g, bi.ch + "", bi.r);
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
    
    
    static final long serialVersionUID = 1;
    private static final int Margin = 10;
    private static final int FontSize = 10; 
    private static final String FontName = "Helvetica";
    private static final int BlockSize = 40;
    private GridCanvas gridCanvas;

    private Label msgField;

    private Vector<Command> cmds = new Vector<Command>();
    
    public synchronized void addCommand(KeyEvent ke)
    {
		Command cmd = new Command(ke.getKeyCode());
		cmds.addElement(cmd);
		
		// Rendezvous with anyone waiting
		notify();		
    }

    public synchronized void addCommand(MouseEvent me)
    {
		int row = gridCanvas.numRows - 1 - (me.getPoint().y) / BlockSize;
		int col = (me.getPoint().x) / BlockSize;
		Location loc = new Location(row, col);
		Command cmd = new Command(loc);
		cmds.addElement(cmd);
		
		// Rendezvous with anyone waiting
		notify();		
    }
    

    public void configureForSize(int numRows, int numCols)
    {
		gridCanvas.configureForSize(numRows, numCols);
		revalidate();
    }

    private void configureBlocks(int numRows, int numCols)
    {
    	getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout(Margin, Margin));
		setBackground(Color.lightGray);
				
		JTextPane textarea = new JTextPane();
		textarea.setEditable(false);
		textarea.setContentType("text/html");
		textarea.setFont(new Font(FontName, Font.PLAIN, FontSize));
		textarea.setText("<center>Move with the <b>arrow keys</b>, and <b>U</b> for undo.<br />"
				+ "To move to a square, where there is a clear path, just click the mouse.<br />"
				+ "Press <b>N</b> to skip this level, <b>Q</b> to quit, and <b>R</b> to restart this level.</center>");
		
		Panel bp = new Panel();
		bp.setFont(new Font(FontName, Font.PLAIN, FontSize));
		// numRows, numCols, hGap, vGap
		bp.setLayout(new GridLayout(2, 1, 10, 0)); 
		//bp.add(new Label("Move with the <b>arrow keys</b>, and <b>U</b> for undo.", Label.CENTER));
		bp.add(textarea);
		//bp.add(new Label("To move to a square, where there is a clear path, just click the mouse.", Label.CENTER));
		//bp.add(new Label("Press <b>N</b> to skip this level, <b>Q</b> to quit, and <b>R</b> to restart this level.", Label.CENTER));
		bp.add(msgField = new Label("New game", Label.CENTER));
		msgField.setFont(new Font (FontName, Font.BOLD, FontSize + 2));
		
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
		
		gridCanvas.addMouseListener
		(
			new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent me)
				{
					Display.this.addCommand(me);
				}
		    }
		);
    }
       
    private int mapLevel;
    public int[][] mapPrompt(String[][] prompts) {
    	
    	int[][] results = new int[prompts.length][];
    	mapLevel = 0;
    	final Display app = this;
    	
    	getContentPane().removeAll();
    	getContentPane().setLayout(new BorderLayout(Margin, Margin));
    	
    	JLabel imgLabel = new JLabel();
    	imgLabel.setIcon(
    			new ImageIcon(this.getImage(this.getClass().getClassLoader().getResource("Images/Map" + mapLevel + ".png"))));
    	
    	getContentPane().add(imgLabel, BorderLayout.CENTER);
    	
    	JPanel panel = new JPanel();
    	
    	panel.setLayout(new GridLayout(prompts[mapLevel].length + 1,2));
    	
    	JLabel timeLabel = new JLabel();
    	
    	timeLabel.setFont(new Font(FontName, Font.PLAIN, 54));
    	
    	getContentPane().add(timeLabel, BorderLayout.NORTH);
    	
    	for(String prompt : prompts[mapLevel]) {
    		panel.add(new JLabel(prompt));
    		panel.add(new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)));
    	}
    	
    	JButton button = new JButton("Enter");
    	button.addActionListener(new AbstractAction() {
    		private static final long serialVersionUID = 1L;

			@Override
    		public void actionPerformed(ActionEvent e) {
				if(mapLevel < prompts.length) {

					results[mapLevel] = new int[prompts[mapLevel].length];
					for(int i = 0; i < prompts[mapLevel].length; ++i) {
						results[mapLevel][i] = (int) ((JSpinner)panel.getComponent(2*i + 1)).getValue();
					}
					
					mapLevel++;
					if(mapLevel == prompts.length) return;
					
			    	imgLabel.setIcon(
			    			new ImageIcon(app.getImage(app.getClass().getClassLoader().getResource("Images/Map" + mapLevel + ".png"))));
					
					panel.removeAll();
			    	panel.setLayout(new GridLayout(prompts[mapLevel].length+1,2));
					for(String prompt : prompts[mapLevel]) {
			    		panel.add(new JLabel(prompt));
			    		panel.add(new JSpinner(new SpinnerNumberModel(1, 1, prompts.length, 1)));
			    	}
					panel.add(button);
					revalidate();
				}
    		}
    	});
    	
    	panel.add(button);
    	getContentPane().add(panel, BorderLayout.EAST);
    	revalidate();
    	
    	int timeLeft = 1;
    	int lastLevel = mapLevel;
    	
    	while(mapLevel < prompts.length) {
    		
    		if(mapLevel > 0)
    			timeLabel.setText(String.format("%d:%02d",  timeLeft / 60, timeLeft % 60));
    		
    		if(lastLevel != mapLevel) {
    			timeLeft = 60 * 3;
    			lastLevel = mapLevel;
    			timeLabel.setText(String.format("%d:%02d",  timeLeft / 60, timeLeft % 60));
    		}
    		
    		if(timeLeft < 1 && mapLevel > 0) {
    			button.getActionListeners()[0].actionPerformed(null);
    		}
    		
    		timeLeft--;
    		
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    	
		configureBlocks(0,0);
	    	
    	return results;
    }

    public void doDrawStatusMessage(String msg)
    {
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
}





