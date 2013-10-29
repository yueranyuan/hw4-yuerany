package edu.cmu.lti.f13.hw4.hw4_yuerany.casconsumers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import edu.cmu.lti.f13.hw4.hw4_yuerany.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_yuerany.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_yuerany.utils.Utils;

/**
 * Largely serves as another way to store the information from the Document annotation.
 * Conveniently, we make it extend a hashmap since the structure is predominately a set of features
 * (with annotations about the original document it was derived from)
 * 
 * @author yueran
 *
 */
@SuppressWarnings("serial")
public class FeatureVector extends HashMap<String, Integer> {
  public boolean isCorrect = false;
  public boolean isQuery = false;
  public String text;
  
  /**
   * Largely serves as another way to store the information from the Document annotation.
   * Conveniently, we make it extend a hashmap since the structure is predominately a set of features
   * (with annotations about the original document it was derived from)
   * @param doc the document to get make a feature vector of
   */
  public FeatureVector(Document doc) {
    this.text = doc.getText();
    // figure out which 'type' of document this is
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
    
    // generate the features from tokenlist
    generateFeatures(doc);
  }
  
  /**
   * interfaces with the UIMA Annotation to extract the tokenList info
   * it is converted to a map so that other functions and objects could interface
   * with this data without worry about going through the UIMA boilerplate
   * @param doc  the document to get features from
   */
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
