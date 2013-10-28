package edu.cmu.lti.f13.hw4.hw4_yuerany.casconsumers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import edu.cmu.lti.f13.hw4.hw4_yuerany.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_yuerany.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_yuerany.utils.Utils;

@SuppressWarnings("serial")
public class FeatureVector extends HashMap<String, Integer> {
  public boolean isCorrect = false;
  public boolean isQuery = false;
  public String text;
  
  public FeatureVector(Document doc) {
    this.text = doc.getText();
    switch (doc.getRelevanceValue()) {
      case 99:
        isQuery = true;
        isCorrect = false;
        break;
      case 1:
        isCorrect = true;
        isQuery = false;
        break;
      case 0:
        isCorrect = false;
        isQuery = false;
        break;
      default:
        System.out.println("Error: could not parse document relevance value " + doc.getRelevanceValue());
    }
    
    generateFeatures(doc);
  }
  
  private void generateFeatures(Document doc) {
    Collection<Token> tokens = Utils.fromFSListToCollection(doc.getTokenList(), Token.class);
    Iterator<Token> iterTok = tokens.iterator();
    while (iterTok.hasNext()) {
      Token tok = iterTok.next();
      String word = tok.getText();
      int freq = tok.getFrequency();
      if (containsKey(tok.getText())) {
        put(word, get(word) + freq);
      } else {
        put(word, freq);
      }
    }
  }
}
