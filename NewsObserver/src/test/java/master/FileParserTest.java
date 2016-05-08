package master;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FileParserTest {
	/*
	*emptyPathWay:checks behaviour when inputs are null or empty string
	*wrongPathWay: checks pathway is true but is there an any txt file which we need
	*/
	private String file="fileParserTest.txt";
	private FileParser testFileParser = new FileParser(file);
	private ArrayList<String> rssLinksAL = new ArrayList<String>();
	
	@Before
	public void setup() throws IOException
	{
		try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(file))){
			rssLinksAL.add("http://rss.cnn.com/rss/edition.rss");
			for (String rssLink : rssLinksAL) {
				writer.write(rssLink);//We wrote a txt file 
			}
		}
	}
	@Test
	public void emptyPathWay(){
		assertFalse( testFileParser.readerOfFile("") );
		assertFalse( testFileParser.readerOfFile(null) );
	}
	@Test
	public void wrongPathWay(){
		
		assertFalse( testFileParser.readerOfFile(System.getProperty("user.dir")));//what will function do in worng path?
		assertFalse( !testFileParser.readerOfFile(file) ); //givin truepath and response should be true
		/*    examples
		 *    path("c:/test");      //returns true
         *    path("c:/te:t");      //returns false
         *    path("c:/te?t");      //returns false
         *    path("c/te*t");       //returns false
         *    path("good.txt");     //returns true
         *    path("not|good.txt"); //returns false
         *    path("not:good.txt"); //returns false
		 */
	}
	
	@Test
	public void canReadTxt(){  //check reading line size with arraylist size
		testFileParser.readerOfFile(file);
		assertEquals(rssLinksAL.size(), testFileParser.getRssLinksAL().size());
	}
	@Test
	public void givenArrayListContainsURL(){//can it translate all the lines to url
		testFileParser.readerOfFile(file);
		try{
			for (URL URLs : testFileParser.getRssLinksAL()) //This is just iterate on rssLinksAL and check are they all URL?
			{
				URL checkerURL = new URL(URLs.toString());
			}
		}catch(MalformedURLException exception){
			Assert.fail("FileParrser.readerOFFile can't convert string to URL");
		}
	}
	/*
	 @Test  //should we change the methods process to test 
	public void emptyLine(){  //it should be last test because we changed the file in this test
		try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(file))){
			writer.write("");
			writer.write("http://www.radikal.com.tr/d/rss/Rss_85.xml");
			assertFalse( testFileParser.readerOfFile(file) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	@After
	public void deleteSetup(){
		try{
			Files.deleteIfExists(Paths.get(file));
		}catch (IOException x) {
		    // File permission problems are caught here.
		    System.err.println(x);
		}
	}

}