package edu.cmu.lti.f13.hw4.hw4_yuerany.casconsumers;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * a comptuer for cosine similarity
 * cosine similarity is defined by:
 * p / (||q||*||d||)
 * where p is dot product of the feature vectors from the query and the document
 * q is the magnitude of feature vector of the query
 * d is the magnitude of feature vector of the document
 * 
 * in our case, we just do p / ||d|| because q is the same for all queries we will be comparing
 */     
public class CosineSimilarityMetric implements SimilarityMetric {
  
  /**
   * cosine similarity is defined by:
   * p / (||q||*||d||)
   * where p is dot product of the feature vectors from the query and the document
   * q is the magnitude of feature vector of the query
   * d is the magnitude of feature vector of the document
   * 
   * in our case, we just do p / ||d|| because q is the same for all queries we will be comparing
   * @returns cosine_similarity
   */
  public double Compare(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
        double similarity=0.0;

        // compute dividend
        float dividend = 0;
        Iterator<Entry<String, Integer>> qIter = queryVector.entrySet().iterator();
        while (qIter.hasNext()) {
          Entry<String, Integer> qFeat = qIter.next();
          if (!docVector.containsKey(qFeat.getKey())) {
            continue;
          }
          
          Integer dVal = docVector.get(qFeat.getKey());
          dividend += dVal * qFeat.getValue();
        }
        
        /* Note: I am not dividing by the length of the query vector because
         * all that value is constant across all answers to the same query
         * therefore, it gets factored out in the comparison
         */
        
        float divisor = 0;
        Iterator<Entry<String, Integer>> dIter = docVector.entrySet().iterator();
        while (dIter.hasNext()) {
          Entry<String, Integer> dFeat = dIter.next();
          divisor += Math.pow(dFeat.getValue(), 2);
        }
        
        similarity = dividend / Math.sqrt(divisor);

        return similarity;
      }
}
