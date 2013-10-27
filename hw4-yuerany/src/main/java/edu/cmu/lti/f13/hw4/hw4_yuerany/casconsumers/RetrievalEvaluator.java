package edu.cmu.lti.f13.hw4.hw4_yuerany.casconsumers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f13.hw4.hw4_yuerany.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_yuerany.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_yuerany.utils.Utils;


public class RetrievalEvaluator extends CasConsumer_ImplBase {

	protected class FeatureVector extends HashMap<String, Integer> {
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
	  }
	}
	
	protected class Query {
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
	
	protected Set<String> globalWordList;

	protected Map<Integer, Query> queries;
		
	public void initialize() throws ResourceInitializationException {
	  queries = new HashMap<Integer, Query> ();
	  globalWordList = new HashSet<String>();
	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
	
		if (it.hasNext()) {
			Document doc = (Document) it.next();
			
			int qid = doc.getQueryID();
			FeatureVector fv = generateFeatureVector(doc);
			Query q;
			if (queries.containsKey(qid)) {
			  q = queries.get(qid);
			} else {
			  q = new Query();
			  queries.put(qid, q);
			}
			q.AddFeatureVector(fv);
			
			//Make sure that your previous annotators have populated this in CAS
			FSList fsTokenList = doc.getTokenList();
			ArrayList<Token>tokenList=Utils.fromFSListToCollection(fsTokenList, Token.class);
			
			// population global dictionary
			Collection<Token> tokens = Utils.fromFSListToCollection(doc.getTokenList(), Token.class);
			Iterator<Token> iterTok = tokens.iterator();
			while (iterTok.hasNext()) {
			  Token tok = iterTok.next();
			  globalWordList.add(tok.getText());
			}

		}

	}

	/**
	 * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 */
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
        double correctScore = computeCosineSimilarity(qFv, q.correctAnswer);
        int rank = 1;
        
        // compute the score of the incorrect documents
        Iterator<FeatureVector> iterAns = q.answers.iterator();
        while(iterAns.hasNext()) {
          FeatureVector aFv = iterAns.next();
          if (aFv == q.correctAnswer) {
            continue;
          }
          double score = computeCosineSimilarity(qFv, aFv);
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
	
	private FeatureVector generateFeatureVector(Document doc) {
	  FeatureVector fv = new FeatureVector(doc);
	  Collection<Token> tokens = Utils.fromFSListToCollection(doc.getTokenList(), Token.class);
    Iterator<Token> iterTok = tokens.iterator();
    while (iterTok.hasNext()) {
      Token tok = iterTok.next();
      fv.put(tok.getText(), tok.getFrequency());
    }
    return fv;
	}

	/**
	 * 
	 * @return cosine_similarity
	 */
	private double computeCosineSimilarity(Map<String, Integer> queryVector,
			Map<String, Integer> docVector) {
		double cosine_similarity=0.0;

		// TODO :: compute cosine similarity between two sentences
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
		  divisor += dFeat.getValue();
		}
		
		cosine_similarity = dividend / divisor;

		return cosine_similarity;
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
