package master;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import database.MongoDb;
import rssparser.FeedMessage;
import rssparser.RssReader;

public class IntervalFetcher {
	private MongoDb mongoDbHelper;
	private static final Logger LOG = Logger.getLogger(IntervalFetcher.class);

	private static Hashtable<String,String> lastNewsStatic = new Hashtable<String, String>();
	ScheduledExecutorService executor;
	public IntervalFetcher (){
		mongoDbHelper = new MongoDb();
		
	}
	public void intervaller(ArrayList<URL> RssLinksAL,Hashtable<String, String> lastNews) {
		
		lastNewsStatic = lastNews;
		executor = Executors.newSingleThreadScheduledExecutor();

			Runnable periodicTask = new Runnable() {
			    public void run() {
			        // Every 5 min this which controlling a 
			    	intervalChecker(RssLinksAL,lastNewsStatic);
			    	LOG.info("Checked all the rss links.");
			    }
			};
			executor.scheduleAtFixedRate(periodicTask, 0, 5, TimeUnit.MINUTES);
			
	}
	public void intervalCloser(){
	      executor.shutdownNow();	
	      LOG.debug("Intervaller closed.");
	}
	private void intervalChecker(ArrayList<URL> RssLinksAL, Hashtable<String, String> lastNews) {
		
			int flagLastNew=0;
			String[] lastNewsArray =  new String[2];
			 Hashtable<String, Integer> wordFrequencyPerNew = new Hashtable<String,Integer>();//it contain word and freq per item
				for(URL rssURLs : RssLinksAL) {  //it read all rss codes
					
					flagLastNew=0;
					RssReader parserOfRss = new RssReader();//--new
					
				    WordProcessor processOfWords = new WordProcessor();
				    
					    for (FeedMessage message : parserOfRss.feedParser(rssURLs)) {  //item to item reading
					     
					      if(!message.getTitle().equals(lastNews.get(rssURLs.toString()))) { //if there is a new news we should insert or increment it
					    	  flagLastNew++;//it provides us to take new
						      if(flagLastNew==1){
						    	  lastNewsArray[0]=rssURLs.toString(); //link
						    	  lastNewsArray[1]=message.getTitle();//başlık
						    	  }
						      String datePerNew=null;
						      if(message.getpubDate().length()==29)
				        		  datePerNew=message.getpubDate().substring(8, 10)+" "+message.getpubDate().toString().substring(4, 7)+" "+message.getpubDate().toString().substring(25, 29);//date of new
				        	  else
				        		  datePerNew=message.getpubDate().substring(8, 10)+" "+message.getpubDate().toString().substring(4, 7)+" "+message.getpubDate().toString().substring(24, 28);//date of new
							      //Mon May 02 20:03:40 EEST 2016       -456-Month  //-89-day //25-8 year  2016-01-21
					        	  //Tue Mar 22 14:15:00 EET 2016   EET,EEST,       2016-01-21
					        	  //Tue May 03 21:58:31 EEST 2016
					        	  //Tue May 03 23:25:52 EEST 2016
						      
						      wordFrequencyPerNew=processOfWords.splitAndHashing(message.getTitle()+ " " +  message.getDescription());//It Brıng us hashtable which contains word and freq hashtable per new
					          
					          Enumeration<String> e = wordFrequencyPerNew.keys(); //iterator of hashlist to read word:freq to save to the database
						          while (e.hasMoreElements()) { 
						          String key = (String) e.nextElement();//key:key   wordFrequencyPerNew.get(key):value
						          if(mongoDbHelper.fetchCount(datePerNew,key)==1){  //check key value per new to increment or add  
						        	  mongoDbHelper.update(datePerNew, key, wordFrequencyPerNew.get(key));//if it has alreadt inserted just increment  
						          }
						          else {
						        	  mongoDbHelper.save(datePerNew, key, wordFrequencyPerNew.get(key));//if not insert it
						          }
						       }
					          
						    }else {//if we come the lately new we can break
						    	if (flagLastNew==1)lastNews.put(lastNewsArray[0], lastNewsArray[1]);
						    	break;
						    }
					    wordFrequencyPerNew.clear();//clear the hashlist for each new
					    
					}
					LOG.debug(rssURLs+" checked.");
				}}
		
	
	
}