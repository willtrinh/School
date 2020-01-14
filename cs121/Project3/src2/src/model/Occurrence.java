package model;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Occurrence implements Comparable<Occurrence> {
	private String docId;
	protected int tf;
	final private static String rootPath = "WEBPAGES_RAW";
	
	public Occurrence(String id) {
		super();
		this.docId = id;
		this.tf = 1;
	}
	
	public int getTf() {
		return tf;
	}

	public void setTf(int tf) {
		this.tf = tf;
	}

	public String getId() {
		return docId;
	}
	public void setId(String id) {
		this.docId = id;
	}
	
	public static String[] getTerms(String docId) throws IOException {
		return getText(docId).split("(\\W+|_+)");
	}
	
	
	public static Document loadFile(String id) throws IOException {
		String filepath = rootPath + "/" + id;
		File input = new File(filepath);
		Document doc = Jsoup.parse(input, "UTF-8");
		return doc;
	}
	
	public static String getText(String id) throws IOException {
		return loadFile(id).text();
	}

	@Override
	public int compareTo(Occurrence o) {
		return Integer.compare(o.tf, this.getTf());
	}
	
	
}
