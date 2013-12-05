package puz;

import java.awt.GridLayout;

import javax.swing.JFrame;

class SlidePuzzle extends JFrame{
	public GlassPane glass;
	
	public SlidePuzzle() {
	    super("Slide Puzzle");
	    SlidePuzzleGUI _guiPls = new SlidePuzzleGUI(this);
	    setContentPane(_guiPls);
	    
	    glass = new GlassPane(getJMenuBar(), getContentPane());
	    glass.setLayout(new GridLayout(0, 1));
	    glass.setOpaque(false);
	    setGlassPane(glass);
	   
	    setLocationRelativeTo(null);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    pack();  
	    setResizable(false);
	}
	public void setGlassVisible() {
	     glass.setNeedToRedispatch(false);
	     glass.setVisible(true);
	}
	public void setGlassInvisible(){
		glass.setVisible(false);
		glass.setNeedToRedispatch(true);
	}
	 
 public static void main(String[] args) {
	 
	 SlidePuzzle ge = new SlidePuzzle();
	 ge.setVisible(true); }
}