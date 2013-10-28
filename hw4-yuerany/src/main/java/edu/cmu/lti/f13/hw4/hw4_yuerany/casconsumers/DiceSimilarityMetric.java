package edu.cmu.lti.f13.hw4.hw4_yuerany.casconsumers;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DiceSimilarityMetric implements SimilarityMetric {
  
  /**
   * importantly, this disregards feature counts, just numbers
   * @return dice_similiarity
   */     
  public double Compare(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
      double similarity=0.0;

      // compute p and q
      float p = 0;
      float q = 0;
      float d = 0;
      Iterator<Entry<String, Integer>> qIter = queryVector.entrySet().iterator();
      while (qIter.hasNext()) {
        Entry<String, Integer> qFeat = qIter.next();
        Integer qVal = qFeat.getValue();
        if (docVector.containsKey(qFeat.getKey())) {
          Integer dVal = docVector.get(qFeat.getKey());
          if (dVal > 0 && qVal > 0) {
            p += 1;
          } else if (qVal > 0) {
            q += 1;
          } else if (dVal > 0) {
            d += 1;
          }
        } else if (qVal > 0) {
          q += 1;
        }
      }
      
      // sum all the features of d that weren't in q
      Iterator<Entry<String, Integer>> dIter = docVector.entrySet().iterator();
      while (dIter.hasNext()) {
        Entry<String, Integer> dFeat = dIter.next();
        Integer dVal = dFeat.getValue();
        if (!queryVector.containsKey(dFeat.getKey()) && dVal > 0) {
          d += 1;
        }
      }
      
      similarity = 2 * p / (2 * p + q + d);
      return similarity;
    }
}
