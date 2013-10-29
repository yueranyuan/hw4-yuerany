package edu.cmu.lti.f13.hw4.hw4_yuerany.annotators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * uses a stopword list to check if a word is a stop word
 * @author yueran
 *
 */
public class StopWordsChecker {
  
  private ArrayList<String> stopwords;
  
  /**
   * uses a stopword list to check if a word is a stop word
   * expected file format:
   * [word]\n[word]\n[word]
   * @param fileLocation the location of the file storing the stop words
   * @throws Exception
   */
  public StopWordsChecker(String fileLocation) throws Exception {
    stopwords = new ArrayList<String>();
    String line;
    URL swUrl = StopWordsChecker.class.getResource(fileLocation);
    if (swUrl == null) {
       throw new IllegalArgumentException("Error opening stopwords.txt");
    }
    BufferedReader br = new BufferedReader(new InputStreamReader(swUrl.openStream()));
    int lineCnt = 0;
    while ((line = br.readLine()) != null)   {
      lineCnt ++;
      if (lineCnt < 3) {
        continue;
      }
      stopwords.add(line);
    }
    br.close();
    br=null;
  }
  
  /**
   * checks if the given word is in the stopword list
   * @param word  to check
   * @return  whether it's a stopword
   */
  public boolean Check(String word) {
    return stopwords.contains(word);
  }
}
