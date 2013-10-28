package edu.cmu.lti.f13.hw4.hw4_yuerany.casconsumers;

import java.util.Map;

/**
 * An interface for similarity metrics
 * there is a compare method which takes feature vectors and computes their similarity
 * @author yueran
 *
 */
public interface SimilarityMetric {
  
  /**
   * computes the similarity of a function
   * @param queryVector  the query
   * @param docVector    the document
   * @return
   */
  public double Compare(Map<String, Integer> queryVector,
          Map<String, Integer> docVector);
}
