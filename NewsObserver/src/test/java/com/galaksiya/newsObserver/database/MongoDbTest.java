package com.galaksiya.newsObserver.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;

public class MongoDbTest {
	private MongoDb mongoDb ;
	private String date;
	private String word;
	@Before
	public void before(){
		mongoDb = new MongoDb("test");
		word ="test";
		date="17-May-2016";
	}
	@After
	public void After(){ 
		mongoDb.delete();
	}
	@Test
	public void containNullInput(){
		assertEquals(-1, mongoDb.contain(date, null));		
	}
	@Test
	public void containInvalidInput(){
		assertEquals(-1, mongoDb.contain(date, ""));		
	}
	
	@Test
	public void saveNullInput(){
		assertFalse(mongoDb.save(date, null, 2));
	}
	@Test
	public void saveInvalidInput(){
		assertFalse(mongoDb.save(date, "", 2));
	}
	@Test
	public void updateNullInput(){
		assertFalse(mongoDb.update(date, null,2));
	}
	@Test
	public void updateInvalidInput(){
		assertFalse(mongoDb.update(date, "",2));
	}
	@Test
	public void getMongoClient(){
		assertTrue(mongoDb.getMongoClient() instanceof MongoClient);
	}
	
	@Test
	public void save(){
		assertEquals(0, mongoDb.contain(date, word));
		assertTrue(mongoDb.save(date,word, 2));
		assertEquals(1, mongoDb.contain(date, word));
	}
	@Test
	public void delete(){
		mongoDb.save(date,word, 3 );
		mongoDb.save("17 Mar 2015", "test2", 4);
		assertEquals(2, mongoDb.totalCount());
		assertTrue(mongoDb.delete());
		assertEquals(0, mongoDb.totalCount());
	}
	@Test
	public void update(){
		mongoDb.save(date, word, 2);
		assertTrue(mongoDb.update(date, word, 2));
	}
	@Test
	public void updateCanIncrement(){
		mongoDb.save(date, word, 2);
		int frequencyLocal = Integer.parseInt(mongoDb.fetchFirstWDocument().get(2));
		mongoDb.update(date, word, 2);
		assertEquals(Integer.parseInt(mongoDb.fetchFirstWDocument().get(2)) , frequencyLocal + 2 );
	}
	public int iterarorSize(FindIterable<Document> iterator){
		int size=0;
		for(Document current : iterator){
			size++;
		}
		return size;
	}
	@Test
	public void overrideFetch(){
		mongoDb.save(date, word, 2);
		FindIterable<Document> iterable=mongoDb.fetch();
		assertEquals( mongoDb.totalCount(),iterarorSize(iterable));
	}
	@Test 
	public void overrideFetchDate(){
		mongoDb.save(date, word, 2);
		mongoDb.save(date, word, 2);
		mongoDb.save(date, word, 2);
		mongoDb.save(date, word, 2);
		FindIterable<Document> iterable=mongoDb.fetch(date);
		assertEquals(mongoDb.contain(date, word),iterarorSize(iterable));
	}
	@Test
	public void overrideFetchDateLimit(){
		mongoDb.save(date, word, 2);
		mongoDb.save(date, word, 2);
		mongoDb.save(date, word, 2);
		mongoDb.save(date, word, 2);

		FindIterable<Document> iterable=mongoDb.fetch(date,2);
		assertEquals(2,iterarorSize(iterable));
	}
	@Test
	public void fetchFirstWDOcument(){
		ArrayList<String> firstDoc=mongoDb.fetchFirstWDocument();
		if(firstDoc!=null)
		  fail("fetchFirstDocument test has broken because of :" 
				  			 +firstDoc.get(0)
				  		+"\t"+firstDoc.get(1)
				  		+"\t"+firstDoc.get(2));
		mongoDb.save(date, word, 2);
		firstDoc=mongoDb.fetchFirstWDocument(); 
		assertFalse(firstDoc.isEmpty());
	}
	@Test
	public void contain() {
		assertEquals(0,mongoDb.contain(date, word));
		mongoDb.save(date, word, 2);
		assertEquals(1,mongoDb.contain(date, word));
	}
	@Test
	public void totalCount() {
		assertEquals(0,mongoDb.totalCount());
		for (int i = 0; i < 6; i++) {
			mongoDb.save(date, word, 2);
		}
		assertEquals(6,mongoDb.totalCount());
	}
}