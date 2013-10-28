package edu.cmu.lti.f13.hw4.hw4_yuerany.annotators;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.Morphology;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f13.hw4.hw4_yuerany.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_yuerany.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_yuerany.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {
  
  private StopWordsChecker stopWordsChecker;
  private boolean runStopwords = false;
  private boolean runStemmer = false;
  private boolean runLowercase = false;
  @Override
  public void initialize(UimaContext aContext) throws IllegalArgumentException {
    String stopwordsFile = (String) aContext.getConfigParameterValue("stopwordsFile");
    if (stopwordsFile != null) {
      runStopwords = true;
      try {
        stopWordsChecker = new StopWordsChecker(stopwordsFile);
      } catch (Exception e) {
        throw new IllegalArgumentException("stop words could not be read");
      }
    }
    runStemmer = (Boolean) aContext.getConfigParameterValue("stem");
    runLowercase = (Boolean) aContext.getConfigParameterValue("lowercase");
  }

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
    
    ArrayList<Token> tokens = tokenize(jcas, doc);
    
    // token-wise processing
    Iterator<Token> tokIter = tokens.iterator();
    while (tokIter.hasNext()) {
      Token tok = tokIter.next();
      if (runLowercase) {
        lowerCase(tok);
      }
      if (runStopwords) {
        removeStopWords(tok);
      }
      if (runStemmer) {
        stem(tok);
      }
    }
    
    doc.setTokenList(Utils.fromCollectionToFSList(jcas, tokens));
  }
	
	public ArrayList<Token> tokenize(JCas jcas, Document doc) {
  	String docText = doc.getText();
    
    TokenizerFactory<Word> factory = PTBTokenizerFactory.newTokenizerFactory();
    Tokenizer<Word> tokenizer = factory.getTokenizer(new StringReader(docText));
    List<Word> tokens = tokenizer.tokenize();
    Iterator<Word> tokIter = tokens.iterator();
    
    HashMap<String, Integer> m = new HashMap<String, Integer>();
    
    while (tokIter.hasNext()) {
      Word word = tokIter.next();
      String wordValue = word.value(); // store lower case words
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
    return tokenList;
  }
	
	private void lowerCase(Token tok) {
    String word = tok.getText();
    tok.setText(word.toLowerCase());
	}
	
	private void removeStopWords(Token tok) {
    String word = tok.getText();
    // if the token is a stop word, empty the feature
    if (stopWordsChecker.Check(word)) {
      tok.setFrequency(0);
    }
	}
	
	//http://code.google.com/p/dkpro-core-gpl/source/browse/de.tudarmstadt.ukp.dkpro.core-gpl/trunk/de.tudarmstadt.ukp.dkpro.core.stanfordnlp-gpl/src/main/java/de/tudarmstadt/ukp/dkpro/core/stanfordnlp/StanfordLemmatizer.java?r=153
  private void stem(Token tok) {
    String word = tok.getText();
    tok.setText(Morphology.stemStatic(new WordTag(word)).word());
  }
}
