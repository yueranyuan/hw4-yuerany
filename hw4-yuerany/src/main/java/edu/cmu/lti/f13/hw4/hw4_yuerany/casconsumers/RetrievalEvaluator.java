package edu.cmu.lti.f13.hw4.hw4_yuerany.casconsumers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f13.hw4.hw4_yuerany.typesystems.Document;


public class RetrievalEvaluator extends CasConsumer_ImplBase {
	
	protected Map<Integer, Query> queries;
	protected SimilarityMetric similarityMetric;
	
	public void initialize() throws ResourceInitializationException {
	  queries = new HashMap<Integer, Query> ();
	  
	  String similarityParam = 
	          (String) getMetaData().getConfigurationParameterSettings().getParameterValue("SimilarityMetric");
	  if (similarityParam.equals("cosine")) {
	      similarityMetric = new CosineSimilarityMetric();
	  } else if (similarityParam.equals("dice")) {
	      similarityMetric = new DiceSimilarityMetric();
	  } else if (similarityParam.equals("jaccard")) {
      similarityMetric = new JaccardSimilarityMetric();
	  } else {
	    System.out.println("invalid similarity metric specified: defaulting to cosine");
      similarityMetric = new CosineSimilarityMetric();
    }
	}

	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator<Annotation> it = jcas.getAnnotationIndex(Document.type).iterator();
	
		if (it.hasNext()) {
			Document doc = (Document) it.next();
			
			int qid = doc.getQueryID();
			FeatureVector fv = new FeatureVector(doc);
			Query q;
			if (queries.containsKey(qid)) {
			  q = queries.get(qid);
			} else {
			  q = new Query();
			  queries.put(qid, q);
			}
			q.AddFeatureVector(fv);
		}

	}

	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);
		
		
		int[] ranks = new int[queries.size()];
    // compute the cosine similarity and rank of the documents  
		Iterator<Entry<Integer, Query>> iterQueries = queries.entrySet().iterator();
		int idx = 0; // keep track of the query index
    while (iterQueries.hasNext()) {
        Entry<Integer, Query> entry = iterQueries.next();
        Query q = entry.getValue();
        int qid = entry.getKey();
        FeatureVector qFv = q.query;
        
        // compute the score of the correct document
        double correctScore = computeSimilarity(qFv, q.correctAnswer);
        int rank = 1;
        
        // compute the score of the incorrect documents
        Iterator<FeatureVector> iterAns = q.answers.iterator();
        while(iterAns.hasNext()) {
          FeatureVector aFv = iterAns.next();
          if (aFv == q.correctAnswer) {
            continue;
          }
          double score = computeSimilarity(qFv, aFv);
          // bump down the rank of the correct answer because this distracter scores higher
          if (score > correctScore) {
            rank++;
          }
        }
        System.out.format("Score: %f\trank=%d\trel=1 qid=%d %s\n", 
                correctScore, rank, qid, q.correctAnswer.text);
        ranks[idx++] = rank;
    }
		
		double metric_mrr = compute_mrr(ranks);
		System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
	}
	
	private double computeSimilarity(Map<String, Integer> queryVector,
      Map<String, Integer> docVector) {
	  return similarityMetric.Compare(queryVector, docVector);
	}
    
	/**
	 * @param ranks: a rank array with strictly positive rank values
	 * @return mrr
	 */
	private double compute_mrr(int[] ranks) {
		double metric_mrr=0.0;
		
		for (int i = 0; i < ranks.length; i++) {
		  metric_mrr += 1.0 / ranks[i];
		}
		metric_mrr /= ranks.length;
		
		return metric_mrr;
	}

}
