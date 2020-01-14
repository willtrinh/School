package model;

import java.io.IOException;
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
		for (String term : terms)
		{
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
	
	public void printIndex() {
		for (String term : getAllTerms())
		{
			System.out.print("Term " + term);
			for (Occurrence page : getAllOccurrences(term))
			{
				System.out.print(" -- " + page.getId() + " **"+ page.getTf() +"**");
			}
			System.out.println();
		}
	}
		
}
