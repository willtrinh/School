package model;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

public class InvertedIndex  {
	
	private LinkedHashMap<String, PostingList> indexes;
	
	public InvertedIndex() {
		super();
		indexes = new LinkedHashMap<String, PostingList>();
	}
	
	public void feed(String docId) throws IOException
	{
		String[] terms = Occurrence.getTerms(docId);
		String[] stopwords = {"a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from", "further", "had", "has", "have", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "it", "it's", "its", "itself", "let's", "me", "more", "most", "my", "myself", "nor", "of", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "she", "she'd", "she'll", "she's", "should", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "we", "we'd", "we'll", "we're", "we've", "were", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "would", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"};
		for (String term : terms)
		{
			if (!Arrays.asList(stopwords).contains(term))
				put(term, docId);
		}
	}
	
	public void put(String term, String docID) {
		if (indexes.containsKey(term))
			indexes.get(term).addOrUpdateDoc(docID);
		else
			indexes.put(term, new PostingList(docID));
	}

	public Set<String> getAllTerms() {
		return indexes.keySet();
	}
	
	public Collection<Occurrence> getAllOccurrences(String term) {
		if (indexes.containsKey(term))
			return indexes.get(term).getDocs();
		return null;
	}
	
	public int getIndexCount() {
		return indexes.size();
	}
	
	public void printIndex() throws IOException {
		SearchEngine engine = new SearchEngine();
		double idfScore;
		double tfidf;
		double tfScore;
		for (String term : getAllTerms())
		{
			System.out.print("Term: " + term);
			int docCount = 0;
			int tf = 0;
			for (Occurrence page : getAllOccurrences(term))
			{
				
				System.out.print(" | docId: " + page.getId() + " Termfreq: "+ page.getTf() +" ");
				docCount++;
				tf += page.getTf();
			}
			System.out.println();
			System.out.println("Docfreq = " + docCount);
			System.out.println("Term frequency = " + tf);
			idfScore = Math.log10(engine.getNumberOfDocuments()/docCount);
			tfScore = 1.0 + Math.log10(tf);
			System.out.println("tfScore = " + tfScore);
			tfidf = tfScore * idfScore;
			System.out.println("idfScore = " + idfScore);
			System.out.println("td-idf = " + tfidf);
		}
	}
}
