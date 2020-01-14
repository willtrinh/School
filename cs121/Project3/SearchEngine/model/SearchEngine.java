package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import com.google.gson.Gson;

public class SearchEngine {
	
	public InvertedIndex index;
	private LinkedHashMap<String, String> bookkeep;
	
	final private String bookkeepPath = "WEBPAGES_RAW_TEST/bookkeeping.json";
	
	public SearchEngine() throws IOException {
		super();
		index = new InvertedIndex();
		loadBookKeep();
	}
	
	public int getNumberOfDocuments()
	{
		return bookkeep.size();
	}
	
	public InvertedIndex getIndex() {
		return index;
	}

	public ArrayList<String> search(String key) {
		Collection<Occurrence> list = index.getAllOccurrences(key.toLowerCase());
		ArrayList<String> urls = new ArrayList<String>();
		for (Occurrence o : list )
		{
			urls.add(bookkeep.get(o.getId()));
		}
		return urls;
	}
	
	public ArrayList<String> search(String key, int topTF) {
		List<Occurrence> list = new ArrayList<Occurrence>(index.getAllOccurrences(key.toLowerCase()));
		Collections.sort(list);
		ArrayList<String> urls = new ArrayList<String>();
		int count = 0;
		for (Occurrence o : list )
		{
			count++;
			urls.add("ID: " + o.getId() + " - TF: " + o.getTf() + " | " + bookkeep.get(o.getId()));
			if (count >= topTF) break;
		}
		return urls;
		
//		TreeMap<Integer, String> tree  = new TreeMap<Integer, String>();
//		for (Occurrence o : list)
//		{
//			tree.put(o.getTf(), o.getId());
//		}
//		ArrayList<String> urls = new ArrayList<String>();
//		int count = 0;
//		for (Integer tf : tree.keySet())
//		{
//			count++;
//			urls.add(tree.get(tf) + " occurs " + tf + ": " + bookkeep.get(tree.get(tf)));
//			if (count >= topTF) break;
//		}
//		return urls;
	}
	
	public void prinSearch(String keyword) {
		System.out.println("Query Results for: " + keyword);
		ArrayList<String> result = search(keyword, 10);
		for (String url : result)
		{
			System.out.println(url);
		}
	}

	public void saveToFile(String filepath) throws IOException {
		try (Writer writer = new FileWriter(filepath)) {
		    Gson gson = new Gson();
		    gson.toJson(index, writer);
		}
		System.out.println("Index is saved to " + filepath);
	}
	
	public void loadFromFile(String filepath) throws IOException {
		System.out.println("Loading indexes from file:" + filepath);
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		Gson gson = new Gson();
		index = gson.fromJson(br, index.getClass());
		System.out.println("Number of unique tokens present in the index: " + index.getIndexCount());
	}
	
	@SuppressWarnings("Gson checked")
	public void loadBookKeep() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(bookkeepPath));
		Gson gson = new Gson();
		bookkeep = gson.fromJson(br, LinkedHashMap.class);
	}	
	
	public void parseFromBookKeep() throws IOException {
		if (bookkeep == null)
			loadBookKeep();

		System.out.println("Parsing indexes from bookkeep:" + bookkeepPath);
		int i = 0;
		for (String id : bookkeep.keySet())
		{
			i++;
			index.feed(id);
//			if (i==20)
//				break;
		}		
		System.out.println("Finished feeding " + i + " pages.");
		System.out.println("Number of unique tokens present in the index: " + index.getIndexCount());
	}	
	
}
