package edu.cmu.lti.f13.hw4.hw4_yuerany.casconsumers;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CosineSimilarityMetric implements SimilarityMetric {
  
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
