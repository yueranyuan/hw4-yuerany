package edu.cmu.lti.f13.hw4.hw4_yuerany.annotators;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f13.hw4.hw4_yuerany.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_yuerany.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_yuerany.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
		}

	}
	/**
	 * 
	 * @param jcas
	 * @param doc
	 */

	private void createTermFreqVector(JCas jcas, Document doc) {

		String docText = doc.getText();
		
		TokenizerFactory<Word> factory = PTBTokenizerFactory.newTokenizerFactory();
    Tokenizer<Word> tokenizer = factory.getTokenizer(new StringReader(docText));
    List<Word> tokens = tokenizer.tokenize();
    Iterator<Word> tokIter = tokens.iterator();
    
    HashMap<String, Integer> m = new HashMap<String, Integer>();
    
    while (tokIter.hasNext()) {
      Word word = tokIter.next();
      String wordValue = word.value();
      if (m.containsKey(wordValue)) {
        m.put(wordValue, m.get(wordValue));
      } else {
        m.put(wordValue, 1);
      }
    }
    
    Iterator<Entry<String, Integer>> iter = m.entrySet().iterator();
    ArrayList<Token> tokenList = new ArrayList<Token>();
    while (iter.hasNext()) {
        Entry<String, Integer> entry = iter.next();
        
        Token token = new Token(jcas);
        token.setText(entry.getKey());
        token.setFrequency(entry.getValue());
        tokenList.add(token);
    }
    doc.setTokenList(Utils.fromCollectionToFSList(jcas, tokenList));
		//TO DO: construct a vector of tokens and update the tokenList in CAS

	}

}
