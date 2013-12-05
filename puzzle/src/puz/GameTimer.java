package puz;

import puz.SlidePuzzleGUI;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer that counts the seconds that passed
 * since the timer was started.
 *
 */
public class GameTimer {
   
    private SlidePuzzleGUI UI;
    Timer timer;
    private boolean active = false;
    private int cntr = 0;
    /**
     * Attaches the timer to the SlidePuzzleGui class
     * and updates it accordingly if it is running.
     * @param GUI
     */
    public GameTimer(SlidePuzzleGUI GUI) {
        UI = GUI;

        timer = new Timer();
        timer.schedule(new TimerTask() {
        	
            @Override
            public void run() {
                if (active == true) {
                    ++cntr;
                }
                UI.UpdateTimerLabel(cntr);
            }
        }, 0, 1000);
    }
    
    public int getTime() { return cntr; }
    public void doStart() { active = true; }
    public void doStop() { active = false; }
    public void doReset() { cntr = 0; active = false; }
}
