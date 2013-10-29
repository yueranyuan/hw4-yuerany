package edu.cmu.lti.f13.hw4.hw4_yuerany.casconsumers;

import java.util.Map;

/**
 * a computer for dice similarity.
 * dice similarity is defined by:
 * 2p / (2p + q + d)
 * where p is intersect of the positive features from the query and the document
 * q is the # of positive features from the query
 * d is the # of positive features from the document
 * importantly, this disregards feature counts, and just uses whether the features are present
 * 
 * this is similar to jaccard metric and extends the methods from jaccard
 * @author yueran
 *
 */
public class DiceSimilarityMetric extends JaccardSimilarityMetric {
  
  /**
   * dice similarity is defined by:
   * 2p / (2p + q + d)
   * where p is intersect of the positive features from the query and the document
   * q is the # of positive features from the query
   * d is the # of positive features from the document
   * importantly, this disregards feature counts, and just uses whether the features are present
   * @return dice_similiarity
   */     
  public double Compare(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double similarity=0.0;

    double intersect = computeIntersect(queryVector, docVector);
    double union = computeUnion(queryVector, docVector);
    
    similarity = 2 * intersect / (union);
    return similarity;
  }
}
