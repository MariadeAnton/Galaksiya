package com.galaksiya.newsObserver.master;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.galaksiya.newsObserver.parser.FeedMessage;
import com.galaksiya.newsObserver.parser.RssReader;

public class NewsChecker {

	private final static Logger LOG = Logger.getLogger(NewsChecker.class);

	private static  Hashtable<String, String> lastNews = new Hashtable<String, String>();//bütün classlarda ortak olmasını istediğimiz bir hashtable
	
	/**
	 * It takes rss links and give one by one to travelInNews.
	 * @param RssLinksAL This arraylist is rss links list.
	 * @return true :success false :fail
	 */
	public boolean updateActualNews(ArrayList<URL> RssLinksAL) {
		if (RssLinksAL == null || RssLinksAL.isEmpty() )
			return false;

		for (URL rssURLs : RssLinksAL) { // it read all rss urls
			lastNews.put(rssURLs.toString(), "");
			travelInNews(lastNews, rssURLs);
			LOG.debug(rssURLs + " checked.");
		}
		return true;
	}
	/**
	 * It travel news in url which is given with param.
	 * It gives news one by one to handleMessage.
	 * Also this function save the last news.
	 * @param lastNews  It is hashtable which occurs rssLink-lastNew for this link.
	 * @param rssURLs This is the url which will be read.
	 */
	public void travelInNews(Hashtable<String, String> lastNews, URL rssURLs) {
		String[] lastNewsArray = new String[2];
		boolean updateNew = true, updated = false;
		RssReader parserOfRss = new RssReader();
		for (FeedMessage message : parserOfRss.parseFeed(rssURLs)) { // item to
																		// item
																		// reading
			boolean isThereAnyNewNews = !message.getTitle().equals(lastNews.get(rssURLs.toString()));
			if (isThereAnyNewNews) { // if there is a new news we should insert
										// or increment it
				if (updateNew) {
					lastNewsArray[0] = rssURLs.toString();
					lastNewsArray[1] = message.getTitle();
					updateNew = false;
					updated = true;
				}
				handleMessage(message);
			} else {// if we come the lately new we can break
				if (updated) {
					lastNews.put(lastNewsArray[0], lastNewsArray[1]);
				}
				break;
			}
		}
	}
	/**
	 * It takes message and handle it to date-word-frequency.Then,It increment(update) or insert it.
	 * @param message It is only one new with title-description-pubdate.
	 * @return wordFrequencyPerNew It is a hash table which occurs word-frequency
	 */
	public Hashtable<String, Integer> handleMessage(FeedMessage message) {
		WordProcessor processOfWords = new WordProcessor();
		Hashtable<String, Integer> wordFrequencyPerNew = new Hashtable<String, Integer>();
		String datePerNew = dateCustomize(message.getpubDate());
		wordFrequencyPerNew = processOfWords.splitAndHashing(message.getTitle() + " " + message.getDescription());
		// wordFrequency test edecez
		if (!(travelWordByWord(datePerNew, wordFrequencyPerNew)))
			return null;
		return wordFrequencyPerNew;
	}
	/**
	 * It controls 'Is this new in rss link?'
	 * @param title A new of title
	 * @param rssURLs A URL to read.
	 * @return true :Success false :fail
	 */
	public boolean containNewsTitle(String title, URL rssURLs) {
		RssReader parserOfRss = new RssReader();
		for (FeedMessage message : parserOfRss.parseFeed(rssURLs)) { 
			if (message.getTitle().equals(title)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * It convert a String which occurs date like 'Fri May 13 10:24:56 EEST 2016' to 13 May 2016.
	 * @param pubDate A date string like 'Fri May 13 10:24:56 EEST 2016'
	 * @return ıt returns a String like '13 May 2016'.(date-month-year)
	 */
	public String dateCustomize(String pubDate) {
		String datePerNew;
		if (pubDate.length() == 29)
			datePerNew = pubDate.substring(8, 10) + " " + pubDate.toString().substring(4, 7) + " "
					+ pubDate.toString().substring(25, 29);
		else
			datePerNew = pubDate.substring(8, 10) + " " + pubDate.toString().substring(4, 7) + " "
					+ pubDate.toString().substring(24, 28);
		return datePerNew;
	}
	/**
	 * It controls is given String can convertable to date.
	 * @param datePerNew String occurs date
	 * @return true :Success false :fail
	 */
	public boolean canConvert(String datePerNew) {
		SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yy");
		if (datePerNew.length() != 11)
			return false;
		try {
			format1.parse(datePerNew.replaceAll("\\s+", "-"));
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	/**
	 * It travel word by word and control the database has it already inserted.
	 * If yes,then increment it to database.
	 * If not,the insert it.
	 * @param datePerNew A hash table occurs message's pubdate.
	 * @param wordFrequencyPerNew It occurs from word-frequency.
	 * @return true :Success false :fail
	 */
	private boolean travelWordByWord(String datePerNew, Hashtable<String, Integer> wordFrequencyPerNew) {
		DbHelper dbHelper = DbHelper.getInstance();
		boolean proccessSuccessful = false;
		Enumeration<String> e = wordFrequencyPerNew.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();// key:key
													// wordFrequencyPerNew.get(key):value
			proccessSuccessful = dbHelper.addDatabase(datePerNew, key, wordFrequencyPerNew.get(key));
		}
		return proccessSuccessful;
	}
}
