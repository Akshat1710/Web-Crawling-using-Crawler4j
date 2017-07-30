package com.usc.webcrawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler {
	private static FileWriter fetchFile;
	private static FileWriter visitFile;
	private static FileWriter urlFile;
	private static FileWriter crawlFile;
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}
	
	public Crawler() throws IOException{
		 try {
			fetchFile = new FileWriter("fetch_NYTimes.csv");
			visitFile = new FileWriter("visit_NYTimes.csv");
			urlFile = new FileWriter("url_NYTimes.csv");
			
			fetchFile.append("URL"+", "+"Status Code"+"\n");
			visitFile.append("URL"+", "+"Size"+", "+"Number of Outlinks"+", "+"Conytent-Type"+"\n");
			urlFile.append("URL"+", "+"Indicator"+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	 private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v" + "|rm|smil|wmv|swf|wma|zip|json|xml|rss+xml|rss|rar|gz))$");
	 private static final Pattern IMGPATTERNS = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?))$");
	 
	 @Override
	 public boolean shouldVisit(Page referringPage, WebURL url) {
		 String href = url.getURL().toLowerCase();
		 try {
			urlFile.write(url.getURL().replace(',', '-'));
			urlFile.write(",");
			if(href.startsWith("http://www.nytimes.com/") || href.startsWith("https://www.nytimes.com/")) {
				urlFile.write("OK");
			}else{
				urlFile.write("N_OK");
			}
			urlFile.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return (!FILTERS.matcher(href).matches() && href.startsWith("https://www.nytimes.com/")) || (IMGPATTERNS.matcher(href).matches() && href.startsWith("https://www.nytimes.com/"));
		 
	 }
	 
	 @Override
	 public void visit(Page page) {
		 String contentTypeForCurrentPage = page.getContentType().split(";")[0];
		 int size = page.getContentData().length;
		 String url = page.getWebURL().getURL().replaceAll(",", "-");		 
		 if (page.getParseData() instanceof HtmlParseData) {
			 HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			 String text = htmlParseData.getText();
			 String html = htmlParseData.getHtml();
			 Set<WebURL> links = htmlParseData.getOutgoingUrls();
			 /*System.out.println("Text length: " + text.length());
			 System.out.println("Html length: " + html.length());
			 System.out.println("Number of outgoing links: " + links.size());
			 */
			 
			 try {
				visitFile.write(url+", "+size+", "+links.size()+", "+contentTypeForCurrentPage+"\n");
			 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
		 }else{
			 try {
					visitFile.write(url+", "+size+", "+"0"+", "+contentTypeForCurrentPage+"\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
	 }
	@Override
	public void onBeforeExit() {
		// TODO Auto-generated method stub
		super.onBeforeExit();
		try {
			fetchFile.close();
			visitFile.close();
			urlFile.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		// TODO Auto-generated method stub
		super.handlePageStatusCode(webUrl, statusCode, statusDescription);
		try {
			fetchFile.write(webUrl.getURL().replaceAll(",", "-")+", "+String.valueOf(statusCode)+"\n");			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
