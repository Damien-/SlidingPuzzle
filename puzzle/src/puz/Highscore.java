package puz;
package puz;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Highscore implements Serializable{
 
 // An arraylist of the type "score" we will use to work with the scores inside the class
 private ArrayList<Score> scores;

 // The name of the file where the highscores will be saved
 private static String HIGHSCORE_FILE = "scores"+SlidePuzzleGUI.getRows()+SlidePuzzleGUI.getCols()+".dat";
 public static void updateHSF(){HIGHSCORE_FILE = "scores"+SlidePuzzleGUI.getRows()+SlidePuzzleGUI.getCols()+".dat";}
 
 //Initialising an in and outputStream for working with the file
 transient ObjectOutputStream outputStream = null;
 transient ObjectInputStream inputStream = null;

 /**
  * Constructor, initializes the scores-array list.
  */
 public Highscore() {
     scores = new ArrayList<Score>();
 }

 public ArrayList<Score> getScores() {
     loadScoreFile();
     sort();
     return scores;
 }
 
 /**
  * Sorts the scores inside the collection.
  */
 private void sort() {
     ScoreComparator comparator = new ScoreComparator();
     Collections.sort(scores, comparator);
 } 
 
 /**
  * Adds new score to the collection.
  * @param name - player's name
  * @param score - player's time
  */
 public void addScore(String name, int score) {
     loadScoreFile();
     scores.add(new Score(name, score));
     updateScoreFile();
 } 
 
 /**
  * Loads the score file if it exists.
  */
 public void loadScoreFile() {
     try {
         inputStream = new ObjectInputStream(new FileInputStream(HIGHSCORE_FILE));
         scores = (ArrayList<Score>) inputStream.readObject();
     } catch (FileNotFoundException e) {
     } catch (IOException e) {
     } catch (ClassNotFoundException e) {
     } finally {
         try {
             if (outputStream != null) {
                 outputStream.flush();
                 outputStream.close();
             }
         } catch (IOException e) {
         }
     }
 }
 
 /**
  * Writes to the score file when new score is 
  * added, creates file if it does not exist.
  */
 public void updateScoreFile() {
     try {
         outputStream = new ObjectOutputStream(new FileOutputStream(HIGHSCORE_FILE));
         outputStream.writeObject(scores);
     } catch (FileNotFoundException e) {
     } catch (IOException e) {
     } finally {
         try {
             if (outputStream != null) {
                 outputStream.flush();
                 outputStream.close();
             }
         } catch (IOException e) {
         }
     }
 }
 
 /**
  * Computes the scores from the array list that 
  * is keeping the scores and converts it to string.
  * @return String with the ordered high scores.
  */
 public String getHighscore() {
     String str = "";
	 int max = 10;
 
     ArrayList<Score> scores;
     scores = getScores();
 
     int i = 0;
     int x = scores.size();
     if (x > max) {
         x = max;
     } 
     while (i < x) {
    	 str += (i + 1) + ".\t " + scores.get(i).getNaam() + "\t\t " + scores.get(i).getScore() + "\n";
         i++;
     }
     if(x == 0) str = "No scores for "+SlidePuzzleGUI.getRows()+"x"+SlidePuzzleGUI.getCols()+ " puzzle!";
     return str;
 }

 public class Score  implements Serializable {
	 private int score;
	 private String naam;

	 public int getScore() {
		 return score;
	 }

	 public String getNaam() {
	     return naam;
	 }

	 public Score(String naam, int score) {
	     this.score = score;
	     this.naam = naam;
	 }
}
 public class ScoreComparator implements Comparator<Score> {
	 public int compare(Score score1, Score score2) {

		 int sc1 = score1.getScore();
         int sc2 = score2.getScore();

         if (sc1 > sc2){
             return +1;
         }else if (sc1 < sc2){
             return -1;
         }else{
             return 0;
         }
     }
 }
}
