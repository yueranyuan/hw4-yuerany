package edu.cmu.lti.f13.hw4.hw4_yuerany.casconsumers;

import java.util.ArrayList;
import java.util.Collection;

public class Query {
  public FeatureVector correctAnswer;
  public FeatureVector query;
  public Collection<FeatureVector> answers;
  
  public Query() {
    answers = new ArrayList<FeatureVector>();
  }
  
  public void AddFeatureVector(FeatureVector fv) {
    if (fv.isQuery) {
      query = fv;
    } else {
      if (fv.isCorrect) {
        correctAnswer = fv;
      }
      answers.add(fv);
    }
  }
}