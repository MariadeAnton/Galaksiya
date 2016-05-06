package master;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import database.MongoDb;

public class Main {
		private static final Logger LOG = Logger.getLogger(Main.class);

		public static void main(String[] args) throws IllegalArgumentException, IOException {
			LOG.error("qwerqwerqwerqwerqwr");
			final String FILE_PATH="/home/francium/new.txt";//txt file path
			MainProcess mainprocessor = new MainProcess();
			FileParser fileParser = new FileParser(FILE_PATH); //process tx file and return ArrayList which contains URLs
			Hashtable<String, String> lastNews = mainprocessor.getResult(fileParser.getRssLinksAL()); //it returns with hashtable rssLinks-Their last news
			mainprocessor.postToDatabase();//insert for all the news in all rss
			IntervalFetcher intervalFetcher = new IntervalFetcher();
			intervalFetcher.intervaller(fileParser.getRssLinksAL(),lastNews);//check the rss in every 5 min
			menu();
	    }
		private static void menu(){
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));//kapat bunu
			String dateChoosed = null;
			int flagMenu=0;
			MongoDb mongoHelper = new MongoDb();
			try {
				Runtime.getRuntime().exec("clear");
			do{
				
				System.out.println("Insert a number for; [0 : Exit]");
				System.out.println("(Press 1)-  10 most used word from a date which will be chosen from you");
				System.out.println("(Press 2)-  Sort by frequency from a date which will be chosen from you (All Worlds)");
				System.out.println("(Press 3)-  Sort all date sorted(freq)");

				System.out.println();
	
		        try {
		            flagMenu=input.read()-48;//when read it's always 48 more and then...(WILL SEARCH)
		            dateChoosed=input.readLine();//eating the line for reading error
		            
					
				
					switch (flagMenu) {
					case 0:
						mongoHelper.fetch("03 May 2016");
						System.out.println(" --Wait for Exit--");
						break;
					case 1:
						System.out.println("Insert a date of day like :  17 Mar 2016\n");
			            dateChoosed=input.readLine();
						System.out.println("10 most used word from " + dateChoosed);
						mongoHelper.fetch(dateChoosed);//from a day coming limited our is top 10 and sorted(frequency)
						break;
					case 2:
						System.out.println("Insert a date of day like :  17 Mar 2016\n");
			            dateChoosed=input.readLine();
						System.out.println("Sorted By Frequency from " + dateChoosed);
						mongoHelper.fetch(dateChoosed,10);//from a date,printing all,our document is top 10 and sorted(frequency)
						break;
					case 3:
						System.out.println("Wait For Print... ");
						mongoHelper.fetch();//from a date,printing all,our document is top 10 and sorted(frequency)
						break;
					default:
						System.out.println("Nice try but you need to insert that has already had functionality");
						break;
					}}
		        catch (IOException e) {
		        	LOG.error("Menu String input problem",e);
		        }
		}while(flagMenu!=0);
			} catch (IOException e1) {
				LOG.error("Problem to clear console",e1);
			}finally {
				mongoHelper.getMongoClient().close();
			}
		}
	
		}