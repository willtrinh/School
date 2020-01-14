import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import model.SearchEngine;

public class Main {

	public static void main(String[] args) {
		try {
			SearchEngine engine = new SearchEngine();
//			engine.parseFromBookKeep();
//			engine.saveToFile("index.json");
//			engine.getIndex().printIndex();
//			engine.index = new InvertedIndex();
			engine.loadFromFile("index.json");
			
			System.out.println("Number of documents: " + engine.getNumberOfDocuments());
			System.out.println("Total size of the index file: " + new File("index.json").length() + " bytes");
			
			engine.prinSearch("Informatics");
			engine.prinSearch("Mondego");
			engine.prinSearch("Irvine");
			
//			engine.printIndex();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
