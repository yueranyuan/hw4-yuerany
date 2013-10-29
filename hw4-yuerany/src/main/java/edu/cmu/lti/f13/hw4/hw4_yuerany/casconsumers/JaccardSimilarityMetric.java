package edu.cmu.lti.f13.hw4.hw4_yuerany.casconsumers;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * a computer for jaccard similarity
 * jaccard similarity is defined by:
 * 2p / (2p + q + d)
 * where p is intersect of the positive features from the query and the document
 * q is the # of positive features from the query
 * d is the # of positive features from the document
 * importantly, this disregards feature counts, and just uses whether the features are present
 */ 
public class JaccardSimilarityMetric implements SimilarityMetric {
  
  /**
   * compute p
   * where p is intersect of the positive features from the query and the document
   * @param queryVector the query
   * @param docVector   the potential answer
   * @return p
   */
  protected double computeIntersect(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double p = 0;
    Iterator<Entry<String, Integer>> qIter = queryVector.entrySet().iterator();
    while (qIter.hasNext()) {
      Entry<String, Integer> qFeat = qIter.next();
      Integer qVal = qFeat.getValue();
      if (docVector.containsKey(qFeat.getKey())) {
        Integer dVal = docVector.get(qFeat.getKey());
        if (dVal > 0 && qVal > 0) {
          p += 1;
        }
      }
    }
    return p;
  }
  
  /**
   * compute d union q
   * q is the positive features from the query
   * d is the positive features from the document
   * @warning this double counts the values that are in both d and q
   * @param queryVector the query
   * @param docVector   the potential answer
   * @return d union q
   */
  protected double computeUnion(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double d = 0;
    double q = 0;
    
    Iterator<Entry<String, Integer>> dIter = docVector.entrySet().iterator();
    while (dIter.hasNext()) {
      Entry<String, Integer> dFeat = dIter.next();
      Integer dVal = dFeat.getValue();
      if (dVal > 0) {
        d += 1;
      }
    }
    
    Iterator<Entry<String, Integer>> qIter = queryVector.entrySet().iterator();
    while (qIter.hasNext()) {
      Entry<String, Integer> qFeat = qIter.next();
      Integer qVal = qFeat.getValue();
      if (qVal > 0) {
        q += 1;
      }
    }
    
    return d + q;
  }
  
  /**
   * jaccard similarity is defined by:
   * p / (p + q + d)
   * where p is intersect of the positive features from the query and the document
   * q is the # of positive features from the query
   * d is the # of positive features from the document
   * importantly, this disregards feature counts, and just uses whether the features are present
   * @return jaccard_similiarity
   */ 
  public double Compare(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double similarity=0.0;

    double intersect = computeIntersect(queryVector, docVector);
    double union = computeUnion(queryVector, docVector);
    
    similarity = intersect / (union - intersect);
    return similarity;
  }

}
