package puz;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;

import puz.Highscore.Score;

class SlidePuzzleGUI extends JPanel implements Serializable {

 // Default rows and columns.
 private static int ROWS = 5;
 private static int COLS = 5;
 
 // Static access for setting and getting current number of 
 // rows and columns.
 public static int getRows(){return ROWS;}
 public static int getCols(){return COLS;}
 
 public static void setRows(int a){ ROWS = a;}
 public static void setCols(int a){ COLS = a;}
 
 // Default cell size.
 private static int CELL_SIZE_X = 80;
 private static int CELL_SIZE_Y = 80;
 
 // Static access for setting and getting current width 
 // and height of cell.
 public static int getCCX(){return CELL_SIZE_X;}
 public static void setCCX(int a){ CELL_SIZE_X = a;}
 
 public static int getCCY(){return CELL_SIZE_Y;}
 public static void setCCY(int a){ CELL_SIZE_Y = a;}
 
 private JTextField textField, textField2;
 private JButton button;
 private JLabel label;
 private JLabel lab;
 private Image source;
 private Image image;
 int width, height;	
 int[][] pos;
 
 // Used to check if game has ended so that user cannot
 // cheat the game by starting with done puzzle, it gets
 // set to false once the game starts with the "New Game".
 boolean ended = true;
 
 private SlidePuzzle parent;
 private ImageIcon sid;

 // Used for timing the player and counting the moves 
 // player made so that it can be displayed.
 private GameTimer timex;
 private JLabel timeLabel;
 
 private JLabel moveLabel;
 private int counter;

 // Used for managing score of the player once he finishes
 // the game.
 private Highscore highscore;
 
 // Contains _topPane and _puzzleGrapphics
 private JLayeredPane _layeredPane;
 // Uses <null> for layout and is used for transition.
 private JPanel _topPane;
 // Uses <GridLayout> and stores the cells of the puzzle.
 private GraphicsPanel    _puzzleGraphics;

 // File chooser for the image.
 private JFileChooser fc;
 
 // Selected file name for the file chooser, set & get
 private static String selectedFile = "";
 public void setSelectedFile(String s) { selectedFile = s;}
 public String getSelectedFile() { return selectedFile;}
 
 public SlidePuzzleGUI(SlidePuzzle p) {
	 
	 parent = p;
	 
     //Create a file chooser
     fc = new JFileChooser();
     
     highscore = new Highscore();
     
     //Create buttons and add listeners where needed.
     JButton newGameButton = new JButton("New Game");
     newGameButton.addActionListener(new NewGameAction());

     JButton openButton = new JButton("Open Image");
     openButton.addActionListener(new OpenImageAction());  
     
     JButton pauseButton = new JButton("Pause");
     pauseButton.addActionListener(new PauseAction());
     
     JButton resizeButton = new JButton("Resize");
     resizeButton.addActionListener(new SizeChangeAction());
     
     JButton highscoreButton = new JButton("Highscores");
     highscoreButton.addActionListener(new HighscoreAction());
     
     timeLabel = new JLabel("Time:");
     
     JLabel l = new JLabel("Rows&Columns:");   
     textField = new JTextField(); 
     JLabel l2 = new JLabel("");   
     textField.setText("4");
     
     JLabel lss = new JLabel("");   
     JLabel lss2 = new JLabel("");   
     //Create control panel
     JPanel controlPanel = new JPanel();
     controlPanel.setLayout(new GridLayout(0,4,5,5));
     
     controlPanel.add(newGameButton);
     controlPanel.add(pauseButton);
     controlPanel.add(openButton);
     controlPanel.add(highscoreButton);
    
     controlPanel.add(l);
     controlPanel.add(textField);
     controlPanel.add(resizeButton);
     controlPanel.add(lss);

     controlPanel.add(timeLabel);
   
     
     //Create graphics panel
     sid = new ImageIcon(SlidePuzzleGUI.class.getResource("default.jpg"));
     _puzzleGraphics = new GraphicsPanel();
     _puzzleGraphics.setBackground(Color.gray);
     Dimension boardSize = new Dimension(_puzzleGraphics.getWidth(),_puzzleGraphics.getHeight());
     
     _topPane = new JPanel();
     _topPane.setLayout(null);
     _topPane.setOpaque(false);
     _topPane.setVisible(true);
     _topPane.setBounds(0, 0, _puzzleGraphics.getWidth(),_puzzleGraphics.getHeight());
     _topPane.setPreferredSize(boardSize);
     
     _layeredPane = new JLayeredPane();
   	 _layeredPane.add(_puzzleGraphics, JLayeredPane.DEFAULT_LAYER);
   	 _layeredPane.add(_topPane, JLayeredPane.MODAL_LAYER);
   	 
     _layeredPane.setBounds(0, 0, _puzzleGraphics.getWidth(),_puzzleGraphics.getHeight());
     _layeredPane.setPreferredSize(boardSize);
     
     //Set the layout and add the components
     this.setLayout(new BorderLayout());
     this.add(controlPanel, BorderLayout.NORTH);
     //_glassPane.add(_layeredPane);
     this.add(_layeredPane, BorderLayout.CENTER);
     //*/
     int ll = 0;
     pos = new int[COLS][ROWS];
     for (int i = 0; i < getCols(); i++) 
         for (int j = 0; j < getRows(); j++) {
        	 pos[i][j] = ll++;
         }
     timex = new GameTimer(this);
 }
 
 /**
  * Randomly shuffles the cells in _puzzleGraphics for the New Game to start.
  */
 public void reset(){
	 for (int c=0; c<COLS; c++) {
		 for (int r=0; r<ROWS; r++) {
         		int temp1 = (int)(Math.random()*(ROWS-1));
         		int temp2 = (int)(Math.random()*(COLS-1));
         		while( temp1 == r && temp2 == c)
         			temp2 = (int)(Math.random()*COLS);
         		Component but1 = _puzzleGraphics.findComponentAt(c*COLS, ROWS*r);
         		Component but2 = _puzzleGraphics.findComponentAt(temp2*COLS, ROWS*temp1);
         		but1 = _puzzleGraphics.getComponent(pos[c][r]);
         		but2 = _puzzleGraphics.getComponent(pos[temp2][temp1]);
         		_puzzleGraphics.add(but1, pos[temp2][temp1]);
         		_puzzleGraphics.add(but2, pos[c][r]);
			    _puzzleGraphics.validate();
         }    
     }//*/
	 ended = false;
 }
 
 /**
  * Checks if all cells are in their final position in order
  * to see if the player has finished the game.
  * @return True if all cells are in their final position.
  */
 public boolean isGameOver() {
	 int count = 0;
	 int ll = 0;
     for (int r=0; r<ROWS; r++) {
         for (int c=0; c<COLS; c++) {
        	 String cc = _puzzleGraphics.getComponent(pos[r][c]).getName();  //.getComponentAt(c, r).getName();
        	 if(cc.equals(""+ll++))
        		 count++;
        	 else
        		 if(c == COLS - 1 && r == ROWS - 1 && cc.equals("l"))
        			 count++;
         }
         if(count == ROWS * COLS) {
        	 timex.doStop();
        	 ended = true;
        	 return true;
         }
     }
     return false;
 }
 
 /**
  * Updates the move label every time a player makes a move
  * from the moment that game starts.
  * @param counter - number of moves
  */
 public void UpdateMoveLabel(int counter) {
     moveLabel.setText("Moves Done " + ++counter);
 }
 
 /**
  * Updates the time label every second from the moment the
  * game starts.
  * @param counterz - number of seconds that passed
  */
 public void UpdateTimerLabel(int counterz) {
     timeLabel.setText("Time: " + counterz);
 }
 
 /**
  * Subclass for the container for puzzle cells, only 
  * has a constructor.
  * @author Damien
  *
  */
 class GraphicsPanel extends JPanel {
     /**
      * Constructor for GraphicsPanel that fills up 
      * the panel with cell images for default image.    
      */
     public GraphicsPanel() {
    	 setLayout(new GridLayout(ROWS, COLS, 0, 0));
    	 
    	 lab = new JLabel("");
    	 lab.setName("l");
         this.setBackground(Color.black);

         source = sid.getImage();
         
         width = sid.getIconWidth();
         height = sid.getIconHeight();
         
         Dimension boardSize = new Dimension(width, height);
         this.setBounds(0, 0, width, height);
         this.setPreferredSize(boardSize);
         
         setCCX(width/COLS);
         setCCY(height/ROWS);
         CELL_SIZE_X = getCCX();
         CELL_SIZE_Y = getCCY();
         
         this.setPreferredSize(
                 new Dimension(CELL_SIZE_X * COLS, CELL_SIZE_Y*ROWS));
         int ll = 0;
         for (int i = 0; i < getRows(); i++) {
             for (int j = 0; j < getCols(); j++) {
                 if (j == getCols()-1 && i == getRows()-1) {
                     label = new JLabel("");
                     label.setName("l");
                     this.add(label);
                 } else {
                     button = new JButton();
                     button.addActionListener(new ImageClickedAction());
                     button.addMouseListener(new BorderAction());
                     this.add(button);
                     image = createImage(new FilteredImageSource(source.getSource(),
                             new CropImageFilter(j * width / COLS, i * height / ROWS,
                             (width / COLS) + 1, height / ROWS)));
                     button.setName(""+(ll++));
                     button.setIcon(new ImageIcon(image));
                 }
             }
         }
     }
 }
 
 // Defines the lenght of the translation effect for cells.
 private static final int translationLength = 10;
 
 /**
  * Defines translation in each direction that runs on new
  * Thread which activates the glass pane and makes player 
  * unable to produce any action during the translation.
  * Required to run on separate Thread or else effects 
  * will not be seen.
  * @author Damien
  *
  */
 class ButtonTranslationDown extends Thread
 {
	 JButton but;
	 SlidePuzzle ps;
	 int x, y;
	 ButtonTranslationDown(JButton b, SlidePuzzle p, int xx, int yy){
		 but = b;
		 ps = p;
		 x = xx;
		 y = yy;
	 }
	 
     @Override
     synchronized public void run()
     {
    	 ps.setGlassVisible();
    	 Dimension size = but.getSize();
    	 for(int i = 0; i <= size.height; i++){
    		 but.setLocation(size.width *  x , (size.height * y) + i );
	    	 but.repaint();
	    	 but.validate();
	    	 _topPane.repaint();
	    	 _topPane.validate();
	    	 try {
				Thread.sleep(translationLength);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	     }
    	 _puzzleGraphics.remove(buttonIndex);
    	 _puzzleGraphics.add(label, buttonIndex);
         _puzzleGraphics.add(but, labelIndex);
         _puzzleGraphics.validate();
    	 ps.setGlassInvisible();
    	 CheckGame();
     }
 }
 class ButtonTranslationUp extends Thread
 {
	 JButton but;
	 SlidePuzzle ps;
	 int x, y;
	 
	 ButtonTranslationUp(JButton b, SlidePuzzle p, int xx, int yy){
		 but = b;
		 ps = p;
		 x = xx;
		 y = yy;
	 }
	 
     @Override
     synchronized public void run()
     {
    	 ps.setGlassVisible();
    	 Dimension size = but.getSize();
    	 for(int i = 0; i <= size.height; i++){
    		 but.setLocation(size.width *  x , (size.height * y) - i);
    		 but.repaint();
	    	 but.validate();
	    	 _topPane.repaint();
	    	 _topPane.validate();
	    	 try {
	    		 Thread.sleep(translationLength);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	     }
    	 _topPane.remove(but);
    	 _puzzleGraphics.remove(buttonIndex);
    	 _puzzleGraphics.add(label, buttonIndex - 1);
         _puzzleGraphics.add(but, labelIndex);
    	 
    	 //_puzzleGraphics.add(button, labelIndex);
		 
		 _puzzleGraphics.validate();
    	 ps.setGlassInvisible();
    	 CheckGame();
     }
 }
 class ButtonTranslationLeft extends Thread
 {
	 JButton but;
	 SlidePuzzle ps;
	 int x, y;
	 
	 ButtonTranslationLeft(JButton b, SlidePuzzle p, int xx, int yy){
		 but = b;
		 ps = p;
		 x = xx;
		 y = yy;
	 }
	 
     @Override
     synchronized public void run()
     {
    	 ps.setGlassVisible();
    	 Dimension size = but.getSize();
    	 for(int i = 0; i <= size.width; i++){
    		 but.setLocation(size.width *  x - i, size.height * y + 1);
    		 but.repaint();
	    	 but.validate();
	    	 _topPane.repaint();
	    	 _topPane.validate();
	    	 try {
				Thread.sleep(translationLength);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	     }
    	 _puzzleGraphics.remove(buttonIndex);
    	 _puzzleGraphics.add(but, labelIndex);
    	 _puzzleGraphics.add(label, buttonIndex);
	     _puzzleGraphics.validate();
    	 ps.setGlassInvisible();
    	 CheckGame();
     }
 }
 class ButtonTranslationRight extends Thread
 {
	 JButton but;
	 SlidePuzzle ps;
	 int x, y;
	 
	 ButtonTranslationRight(JButton b, SlidePuzzle p, int xx, int yy){
		 but = b;
		 ps = p;
		 x = xx;
		 y = yy;
	 }
	 
     @Override
     synchronized public void run()
     {
    	 ps.setGlassVisible();
    	 Dimension size = but.getSize();
    	 for(int i = 0; i <= size.width; i++){
    		 but.setLocation(size.width *  x + i, size.height * y + 1);
    		 but.repaint();
	    	 but.validate();
	    	 _topPane.repaint();
    		 _topPane.validate();
	    	 try {
				Thread.sleep(translationLength);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	     }
    	 _puzzleGraphics.remove(buttonIndex);
	     _puzzleGraphics.add(label, buttonIndex);
	     _puzzleGraphics.add(but, labelIndex);
	     _puzzleGraphics.validate();
    	 ps.setGlassInvisible();
    	 CheckGame();
     }
 }
 
 /**
  * Defines calls to set GlassPane visible and to 
  * set it invisible on new Thread. Required to run
  * on separate Thread in order for the GlassPane 
  * to take an effect or else it would remain unchanged. 
  * @author Damien
  *
  */
 class SetGlass extends Thread
 {
	 SlidePuzzle but;
	 SetGlass(SlidePuzzle b){
		 but = b;
	 }
	 
     @Override
     public void run()
     {
    	 but.setGlassVisible();
     }
 }
 class UnsetGlass extends Thread
 {
	 SlidePuzzle but;
	 UnsetGlass(SlidePuzzle b){
		 but = b;
	 }
	 
     @Override
     public void run()
     {
    	 but.setGlassInvisible();
     }
 }
 

 /**
  * Set new selected image as a background and 
  * displays it right away in its starting position.
  */
 public void setImageBackground(){
	 
     _layeredPane.setEnabled(false);
	 _layeredPane.setVisible(false);
	 SlidePuzzleGUI cx = (SlidePuzzleGUI) _layeredPane.getParent();
	 cx.remove(_layeredPane);
	 
	 _puzzleGraphics = new GraphicsPanel();
     _puzzleGraphics.setBackground(Color.gray);
     Dimension boardSize = new Dimension(_puzzleGraphics.getWidth(),_puzzleGraphics.getHeight());
     
     _topPane = new JPanel();
     _topPane.setLayout(null);
     _topPane.setOpaque(false);
     _topPane.setBounds(0, 0, _puzzleGraphics.getWidth(),_puzzleGraphics.getHeight());
     _topPane.setPreferredSize(boardSize);
     
     _layeredPane = new JLayeredPane();
   	 _layeredPane.add(_puzzleGraphics, JLayeredPane.DEFAULT_LAYER);
   	 _layeredPane.add(_topPane, JLayeredPane.DRAG_LAYER);
   	 
     _layeredPane.setBounds(0, 0, _puzzleGraphics.getWidth(),_puzzleGraphics.getHeight());
     _layeredPane.setPreferredSize(boardSize);
     cx.add(_layeredPane, BorderLayout.CENTER);
     
     int ll = 0;
     pos = new int[COLS][ROWS];
     for (int i = 0; i < getCols(); i++) 
         for (int j = 0; j < getRows(); j++) 
        	 pos[i][j] = ll++;
     
     _layeredPane.setEnabled(true);
     _layeredPane.setVisible(true);
     _layeredPane.validate();

     parent.pack();
 }
 
 /**
  * Action listener for <New Game> button. Initiates
  * new game by shuffling cells and starting the timer.
  * @author Damien
  *
  */
 public class NewGameAction implements ActionListener {
     public void actionPerformed(ActionEvent e) {
         reset();
         timex.doReset();
         timex.doStart();
     }
 }
 
 /**
  * Action listener for <Open Image> button. Opens the 
  * selected image and calls setImageBackground() to set
  * it to its starting position. 
  */
 public class OpenImageAction implements ActionListener {
     public void actionPerformed(ActionEvent e) {
    	 //Set up the file chooser.
         if (fc == null) 
             fc = new JFileChooser();
         
         //Show it.
         int returnVal = fc.showOpenDialog(SlidePuzzleGUI.this);
         
         //Process the results.
         if (returnVal == JFileChooser.APPROVE_OPTION) {
             File file = fc.getSelectedFile();
             setSelectedFile(file.getAbsolutePath());
             sid = new ImageIcon(getSelectedFile());
             setImageBackground();
         } 
         //Reset the file chooser for the next time it's shown.
         fc.setSelectedFile(null);
     }
 }
 
 int buttonIndex = 0;
 int labelIndex  = 0;
 /**
  * Takes appropriate action once one of the puzzle cells
  * is clicked on. Starts the timer if it has been paused.
  * @author Damien
  *
  */
 public class ImageClickedAction implements ActionListener {
	 
	 synchronized public void actionPerformed(ActionEvent e) {
    	 
    	 if (ended) return;
    	 
    	 timex.doStart();
    	 
		 JButton button = (JButton) e.getSource();
		 Dimension size = button.getSize();
		
		 
		 int labelX = label.getX();
		 int labelY = label.getY();
		 int buttonX = button.getX();
		 int buttonY = button.getY();
		 int buttonPosX = buttonX / size.width;
		 int buttonPosY = buttonY / size.height;
		 buttonIndex = pos[buttonPosY][buttonPosX];
		 
		 if (labelX == buttonX && (labelY - buttonY) == size.height) {
		
		     labelIndex = buttonIndex + COLS;
		     _puzzleGraphics.remove(buttonIndex);
		     _puzzleGraphics.add(lab, buttonIndex);
		     //--- Start translation ---
		     _topPane.add(button);
		     button.setLocation(buttonY, buttonX);
		     _topPane.validate();
		     
		     ButtonTranslationDown bb = new ButtonTranslationDown(button, parent, buttonPosX, buttonPosY);
		     bb.start();
		     
		     //--- End translation ---
		     //*/
		 }
		 else
		 if (labelX == buttonX && (labelY - buttonY) == -size.height) {

		     labelIndex = buttonIndex - COLS;
		     _puzzleGraphics.remove(buttonIndex);
		     _puzzleGraphics.add(lab, buttonIndex);
		     //--- Start translation ---
		     _topPane.add(button);
		     button.setLocation(buttonY, buttonX);
		     _topPane.validate();
		     
		     ButtonTranslationUp bb = new ButtonTranslationUp(button, parent, buttonPosX, buttonPosY);
		     bb.start();
		     
		     //--- End translation ---
		 }
		 else
		 if (labelY == buttonY && (labelX - buttonX) == size.width) {
		
		      labelIndex = buttonIndex + 1;
		     _puzzleGraphics.remove(buttonIndex);
		     _puzzleGraphics.add(lab, buttonIndex);
		     //--- Start translation ---
		     _topPane.add(button);
		     button.setLocation(buttonY, buttonX);
		     _topPane.validate();
		     
		     ButtonTranslationRight bb = new ButtonTranslationRight(button, parent, buttonPosX, buttonPosY);
		     bb.start();
		     
	         //--- End translation ---
		 }
		 else
		 if (labelY == buttonY && (labelX - buttonX) == -size.width) {
		
		     labelIndex = buttonIndex - 1;
		     _puzzleGraphics.remove(buttonIndex);
		     _puzzleGraphics.add(lab, buttonIndex);
		     //--- Start translation ---
		     _topPane.add(button);
		     button.setLocation(buttonY, buttonX);
		     _topPane.validate();
		     
		     ButtonTranslationLeft bb = new ButtonTranslationLeft(button, parent, buttonPosX, buttonPosY);
		     bb.start();
		     
		     //--- End translation ---
		 }
     }
 }
 public void CheckGame(){
	 // Check if game is over and take appropriate action.
	 if(isGameOver()){

		 ArrayList<Score> scores;
	     scores = highscore.getScores();
	     
	     boolean popped = false;
	     
	     int i = 0;
	     int x = scores.size();
	     if (x > 10) { x = 10; } 
	     if (x == 0) {
        	 String name =  JOptionPane.showInputDialog(this 
        		       ,"You've had the 1. best time! Please enter your name:");
        	 highscore.addScore(name, timex.getTime());
        	 JOptionPane.showMessageDialog(parent, "Your time for "+ROWS+"x"+COLS+" puzzle is: " + timex.getTime() + "seconds!");
        	 popped = true;
	     }
	     else
	     while (i < x) {
	         if(scores.get(i).getScore() >= timex.getTime()){
	        	 String name =  JOptionPane.showInputDialog(this 
	        		       ,"You've had the "+ ++i + ". best time! Please enter your name:");
	        	 highscore.addScore(name, timex.getTime());
	        	 JOptionPane.showMessageDialog(parent, "Your time for "+ROWS+"x"+COLS+" puzzle is: " + timex.getTime() + "seconds!");
	        	 i = 10;
	        	 popped = true;
	         }
	         i++;
	     }
	     if(!popped)
	     if(i < 10){
	    	 String name =  JOptionPane.showInputDialog(this 
        		       ,"You've had the "+ ++i + ". best time! Please enter your name!");
        	 highscore.addScore(name, timex.getTime());
        	 JOptionPane.showMessageDialog(parent, "Your time for "+ROWS+"x"+COLS+" puzzle is: " + timex.getTime() + "seconds!");
        	 popped = true;
	     }
	     if(!popped)
	    	 JOptionPane.showMessageDialog(parent, "Your time for "+ROWS+"x"+COLS+" puzzle is: " + timex.getTime() + "seconds");
		 System.out.println(highscore.getHighscore());
		 popped = false;
	 }
 }
 
 /**
  * Action listener for <Highscore> button. Displays high scores
  * for the current row/col combination.
  * @author Damien
  *
  */
 public class HighscoreAction implements ActionListener {
	 public void actionPerformed(ActionEvent e) { 
		 JOptionPane.showMessageDialog(parent, highscore.getHighscore());
     }
 }

/**
 * Action listener for <Pause> button. Pauses the 
 * game by stopping the timer.
 * @author Damien
 *
 */
 public class PauseAction implements ActionListener {
     public void actionPerformed(ActionEvent e) { 
    	 timex.doStop();
     }
 }
 
 /**
  * Action listener for <Resize> button. Resizes the
  * puzzle by changing the number of rows/cols and 
  * cell size.
  * @author Damien
  *
  */
 public class SizeChangeAction implements ActionListener {
     public void actionPerformed(ActionEvent e) { 
    	 String w = textField.getText();
    	 String h = w;
    	 
    	 timex.doReset();
    	 ended = true;
    	 if(!w.equals(""+getCols()) || !h.equals(""+getRows())){
    		 setCols(Integer.parseInt(w));
    		 setRows(Integer.parseInt(h));
    		 Highscore.updateHSF();
    		 highscore = new Highscore();
    		 setImageBackground();
    	 }
     } 
 }
 
 /**
  * Displays the border on hovering over the cells.
  * Green if the cell can be moved, red if it can't.
  * @author Damien
  * 
  */
 public class BorderAction implements MouseListener {
     
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		 JButton button = (JButton) e.getSource();
		 Dimension size = button.getSize();
		 Border red, green;
		 red = BorderFactory.createLineBorder(Color.red);
		 green = BorderFactory.createLineBorder(Color.green);
		 
		 int labelX = label.getX();
		 int labelY = label.getY();
		 int buttonX = button.getX();
		 int buttonY = button.getY();

		 if (labelX == buttonX && (labelY - buttonY) == size.height) {
			 button.setBorder(green);
		 }else
		 if (labelX == buttonX && (labelY - buttonY) == -size.height) {
			 button.setBorder(green);
		 }else
		 if (labelY == buttonY && (labelX - buttonX) == size.width) {
			 button.setBorder(green);
		 }else		
		 if (labelY == buttonY && (labelX - buttonX) == -size.width) {
			 button.setBorder(green);
		 }else{
			 button.setBorder(red);
		 }    
	}
	@Override
	public void mouseExited(MouseEvent e) {
		JButton button = (JButton) e.getSource();
		Border empty = BorderFactory.createLineBorder(Color.gray);//BorderFactory.createEmptyBorder();
		button.setBorder(empty);
	}
 }
}