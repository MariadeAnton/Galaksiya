package com.galaksiya.newsObserver.parser;

public class FeedMessage {

  private String title;
  private String description;
  private String pubDate;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public String getpubDate() {
    return pubDate;
  }

  public void setPubDate(String pubDate) {
    this.pubDate = pubDate;
  }


  @Override
  public String toString() {
	  return "Feed [\n title = " + title 
		    	+ "\n description = " + description
		        + " pubDate = " + pubDate
		        + "\n]\n_______________________________________________\n";
  }

} 