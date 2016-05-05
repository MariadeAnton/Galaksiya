package main;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


public class WordProcessor {
	

	public Hashtable<String, Integer> splitAndHashing(String titleDescription){

		List<String> processedStr=Arrays.asList(titleDescription.replaceAll("\\p{P}", "").toLowerCase().split("\\s+"));
		//splitting from " " and delete punctions or others without alphabet and lower case
		Hashtable<String, Integer> wordFrequency = new Hashtable<String,Integer>();
		Set<String> uniqueWords = new HashSet<String>(processedStr);//set provide we can save words uniqely
		for (String word : uniqueWords) {
            int frequency=Collections.frequency(processedStr, word);//gives frequency for each word
            wordFrequency.put(word, frequency);
        }
		return wordFrequency;
	}
}