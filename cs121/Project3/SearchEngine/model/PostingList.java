/**
 * 
 */
package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author An Le
 *
 */
public class PostingList {
	
	// <docID, occurrence
	// Occurrence will store various attribute
	LinkedHashMap<String, Occurrence> o;
	
	public PostingList() {
		super();
		o = new LinkedHashMap<String, Occurrence>();
	}
	
	public Occurrence getDoc(String docId) {
		return o.get(docId);
	}
	
	public PostingList(String docId) {
		super();
		o = new LinkedHashMap<String, Occurrence>();
		addOrUpdateDoc(docId);
	}

	public void addOrUpdateDoc(String docId) {
		if (o.containsKey(docId))
		{
			o.get(docId).tf++;
		}
		else
		{
			o.put(docId, new Occurrence(docId));
		}
		
	}
	
	public Collection<Occurrence> getDocs() {
		return o.values();
	}
}
