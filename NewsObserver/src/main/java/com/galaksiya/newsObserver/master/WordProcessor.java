package com.galaksiya.newsObserver.master;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class WordProcessor {

	/**
	 * It takes concat of title and description and delete the tokens inside this,make lower case,split it according to " "
	 * @param titleDescription It is concat of title and description.
	 * @return It returns hash table which occurs from word and frequency.
	 */
	public Hashtable<String, Integer> splitAndHashing(String titleDescription) {

		List<String> processedStr = Arrays.asList(
				titleDescription.replaceAll("\\<[^>]*>", "").replaceAll("\\p{P}", "").toLowerCase().split("\\s+"));
		// splitting from " " and delete punctions or others without alphabet
		// and lower case
		Hashtable<String, Integer> wordFrequency = new Hashtable<String, Integer>();
		Set<String> uniqueWords = new HashSet<String>(processedStr);
		for (String word : uniqueWords) {
			int frequency = Collections.frequency(processedStr, word);
			wordFrequency.put(word, frequency);
		}
		return wordFrequency;
	}
}