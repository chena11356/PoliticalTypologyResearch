import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class URLGatherer {
	
	//removes duplicates from an ArrayList
	public void removeDuplicates(ArrayList<String> input){
		Set<String> hs = new HashSet<>();
		hs.addAll(input);
		input.clear();
		input.addAll(hs);
	}
	
	//checks if given range overlaps with another given range
	public boolean rangeIncludesNumBetween(int start, int end, int b1, int b2) {
		if ((start<=b1&&end>=b1)||(start>=b1&&end<=b2)||(start>=b1&&start<=b2)||(end>=b1&&end<=b2))
			return true;
		else
			return false;
	}
	
	public ArrayList<String> gatherB(int start, int end) throws IOException {
		ArrayList<String> urls = new ArrayList<String>();
		
		//first connect to websites
		//house bills
		if (start<=6462){
			Document hr=Jsoup.connect("https://www.congress.gov/bill/115th-congress/house-bill/"+start
					).timeout(100000).get(); 
			}
		//senate bills
		if (rangeIncludesNumBetween(start,end,6463,9716)){
			if (start>6463) {
				Document s=Jsoup.connect("https://www.congress.gov/bill/115th-congress/senate-bill/"+(start-6462)
						).timeout(100000).get();
			}
			else {
				Document s=Jsoup.connect("https://www.congress.gov/bill/115th-congress/senate-bill/1"
						).timeout(100000).get(); 
			}
		}
		//house resolutions
		if (rangeIncludesNumBetween(start,end,9717,10722)){
			if (start>9717) {
				Document hres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/house-resolution/"+(start-9716)
						).timeout(100000).get();
			}
			else {
				Document hres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/house-resolution/1"
						).timeout(100000).get(); 
			}
		}
		//senate resolutions
		if (rangeIncludesNumBetween(start,end,10723,11308)){
			if (start>10723) {
				Document sres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/senate-resolution/"+(start-10722)
						).timeout(100000).get();
			}
			else {
				Document sres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/senate-resolution/1"
						).timeout(100000).get(); 
			}
		}
		//house joint resolutions
		if (rangeIncludesNumBetween(start,end,11309,11444)){
			if (start>11309) {
				Document hjres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/house-joint-resolution/"+(start-11308)
						).timeout(100000).get();
			}
			else {
				Document hjres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/house-joint-resolution/1"
						).timeout(100000).get(); 
			}
		}
		//senate joint resolutions
		if (rangeIncludesNumBetween(start,end,11445,11505)){
			if (start>11445) {
				Document sjres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/senate-joint-resolution/"+(start-11444)
						).timeout(100000).get();
			}
			else {
				Document sjres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/senate-joint-resolution/1"
						).timeout(100000).get(); 
			}
		}
		//house concurrent resolutions
		if (rangeIncludesNumBetween(start,end,11506,11634)){
			if (start>11506) {
				Document hconres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/house-concurrent-resolution/"+(start-11505)
						).timeout(100000).get();
			}
			else {
				Document sconres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/house-concurrent-resolution/1"
						).timeout(100000).get(); 
			}
		}
		//senate concurrent resolutions
		if (rangeIncludesNumBetween(start,end,11635,11675)){
			if (start>11635) {
				Document sconres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/senate-concurrent-resolution/"+(start-11634)
						).timeout(100000).get();
			}
			else {
				Document sconres=Jsoup.connect("https://www.congress.gov/bill/115th-congress/senate-concurrent-resolution/1"
						).timeout(100000).get(); 
			}
		}
		
		
		
		
		

		
		//then remove duplicates just in case
		removeDuplicates(urls);
		
		
		
		//then return urls
		return urls;
	}
    
	public ArrayList<String> gatherM(int start, int end) throws IOException {
ArrayList<String> urls = new ArrayList<String>();
		
		//first connect to website
		Document d=Jsoup.connect("https://www.congress.gov/resources/display/content/Most-Viewed+Bills"
				).timeout(100000).get(); //connect to website
		
		//then parse and gather urls
		Element content = d.getElementById("content");
		Elements links = content.getElementsByClass("external-link");
		for (Element link : links) {
			if (limit>0&&link.attr("href").indexOf("bill")>=0) {
				//if above the limit and is a bill site
			  urls.add(link.attr("href"));
			}
			limit--;
		}

		
		//then remove duplicates
		removeDuplicates(urls);
		
		
		
		//then return urls
		return urls;
	}
	
	

}
