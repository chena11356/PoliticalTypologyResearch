import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import java.util.ArrayList;

public class BillAnalyzer extends Analyzer {
	
	
	
	public boolean containsFromArray (String[] arr, String text) {
		boolean res = false;
		for (String s : arr) {
			if (text.toLowerCase().contains(s.toLowerCase()))
				res = true;
		}
		return res;
	}
	

	//finds bill type based on current index
	public String findBillType(int index) {
		if (index<6536)
			return "hr";
		else if (index<10084)
			return "s";
		else if (index<11041)
			return "hres";
		else if (index<11683)
			return "sres";
		else if (index<11791)
			return "hjres";
		else if (index<11832)
			return "sjres";
		else if (index<12015)
			return "hconres";
		else if (index<12073)
			return "sconres";
		else
			return "invalid input";
	}
	
	public void go(int start, int end) throws IOException, RowsExceededException, WriteException, BiffException {
		
		//initialize necessary variables
		WritableWorkbook out; //excel file output
		WritableSheet sheet; //excel sheet output
		Label label; //excel cell output
		Document d; //current page
		String curPage = ""; //current page
		int j; //keeps track of which variable it's on (2nd index)
		String temp;
		String temp2;
		Document dTemp;
		Element eTemp;
		Elements t;
		
		Workbook workbook = Workbook.getWorkbook(new File("mainData.xls"));
		out = Workbook.createWorkbook(new File("mainData.xls"), workbook);
		sheet = out.getSheet(0);
		
		//now iterate through to gather data for each and every bill
		for (int i = start-1; i < end; i++){
			
			printProgress(start, end, i);
			
			
			//first connect to correct website
			if (findBillType(i).equals("hr"))
				curPage = "https://www.congress.gov/bill/114th-congress/house-bill/"+(i+1);
			else if (findBillType(i).equals("s"))
				curPage = "https://www.congress.gov/bill/114th-congress/senate-bill/"+(i+1-6536);
			else if (findBillType(i).equals("hres"))
				curPage = "https://www.congress.gov/bill/114th-congress/house-resolution/"+(i+1-10084);
			else if (findBillType(i).equals("sres"))
				curPage = "https://www.congress.gov/bill/114th-congress/senate-resolution/"+(i+1-11041);
			else if (findBillType(i).equals("hjres"))
				curPage = "https://www.congress.gov/bill/114th-congress/house-joint-resolution/"+(i+1-11683);
			else if (findBillType(i).equals("sjres"))
				curPage = "https://www.congress.gov/bill/114th-congress/senate-joint-resolution/"+(i+1-11791);
			else if (findBillType(i).equals("hconres"))
				curPage = "https://www.congress.gov/bill/114th-congress/house-concurrent-resolution/"+(i+1-11832);
			else if (findBillType(i).equals("sconres"))
				curPage = "https://www.congress.gov/bill/114th-congress/senate-concurrent-resolution/"+(i+1-12015);
			else {
				System.out.println("Invalid URL while trying to connect to URL with index "+i);
				System.exit(1);
			}
			d = Jsoup.connect(curPage+"/cosponsors").timeout(100000).get();
			
			//make sure bill is not reserved
			temp = d.getElementsByTag("title").first().text(); //title of bill
			while (temp.indexOf("Reserved")!=-1) {
				i++;
				if (findBillType(i).equals("hr"))
					curPage = "https://www.congress.gov/bill/114th-congress/house-bill/"+(i+1);
				else if (findBillType(i).equals("s"))
					curPage = "https://www.congress.gov/bill/114th-congress/senate-bill/"+(i+1-6536);
				else if (findBillType(i).equals("hres"))
					curPage = "https://www.congress.gov/bill/114th-congress/house-resolution/"+(i+1-10084);
				else if (findBillType(i).equals("sres"))
					curPage = "https://www.congress.gov/bill/114th-congress/senate-resolution/"+(i+1-11041);
				else if (findBillType(i).equals("hjres"))
					curPage = "https://www.congress.gov/bill/114th-congress/house-joint-resolution/"+(i+1-11683);
				else if (findBillType(i).equals("sjres"))
					curPage = "https://www.congress.gov/bill/114th-congress/senate-joint-resolution/"+(i+1-11791);
				else if (findBillType(i).equals("hconres"))
					curPage = "https://www.congress.gov/bill/114th-congress/house-concurrent-resolution/"+(i+1-11832);
				else if (findBillType(i).equals("sconres"))
					curPage = "https://www.congress.gov/bill/114th-congress/senate-concurrent-resolution/"+(i+1-12015);
				else {
					System.out.println("Invalid URL while trying to connect to URL with index "+i);
					System.exit(1);
				}
				d = Jsoup.connect(curPage+"/cosponsors").timeout(100000).get();
				temp = d.getElementsByTag("title").first().text(); //title of bill
			}
			
			//start at first variable (type of bill)
			j = 0;
			label = new Label(j, i, findBillType(i));
			sheet.addCell(label);
			j++;
			
			//retrieve sponsParty
			t = d.getElementsByClass("overview_wrapper bill");
			eTemp = t.first();
			temp = eTemp.html(); //gets the whole div
			dTemp = Jsoup.parseBodyFragment(temp); //makes the div part a temp document
			t = dTemp.getElementsByTag("td"); //gives the td elements of the overview
			temp = t.html();
			temp2 = temp.substring(temp.indexOf("[")+1, temp.indexOf("[")+2); //gives the party
			label = new Label(j,i, temp2);
			sheet.addCell(label);
			j++;
			
			//retrieve sponsState
			temp = temp.substring(temp.indexOf("["));
			temp2 = temp.substring(temp.indexOf("-")+1,temp.indexOf("-")+3); //gives the state abbr
			label = new Label(j,i,temp2);
			sheet.addCell(label);
			j++;
			
			//retrieve sponsor
			t = d.getElementsByAttributeValue("name", "dc.creator");
			temp = t.first().attr("content");
			label = new Label(j,i,temp);
			sheet.addCell(label);
			j++;
			
			//retrieve committees, which may be separated by | or ;
			t = d.getElementsByClass("overview_wrapper bill");
			temp = t.first().html(); //gets the whole div
			dTemp = Jsoup.parseBodyFragment(temp); //makes the div part a temp document
			t = dTemp.getElementsByTag("td"); //gives the td elements of the overview
			eTemp = t.get(1);
			temp2 = eTemp.text();
			if (temp2.split("|").length==2 || temp2.split(";").length==2) {
				if (temp2.split("|").length==2) {
					label = new Label(j,i,temp2.split("|")[0].trim());
					sheet.addCell(label);
					j++;
					label = new Label(j,i,temp2.split("|")[1].trim());
					sheet.addCell(label);
					j++;
				}
				else {
					label = new Label(j,i,temp2.split(";")[0].trim());
					sheet.addCell(label);
					j++;
					label = new Label(j,i,temp2.split(";")[1].trim());
					sheet.addCell(label);
					j++;
				}
			}
			else {
				label = new Label(j,i,temp2.trim());
				sheet.addCell(label);
				j++;
				label = new Label(j,i,"-");
				sheet.addCell(label);
				j++;
			}
			
			//retrieve bill status
			t = d.getElementsByClass("hide_fromsighted");
			temp = t.eq(t.size()-2).last().text();
			temp = temp.substring(temp.indexOf("status")+7);
			label = new Label(j,i, temp);
			sheet.addCell(label);
			j++;
			
			//retrieve subject-policy area
			t = d.getElementsByAttributeValue("name", "dc.subject");
			temp = t.get(1).attr("content");
			label = new Label(j,i,temp);
			sheet.addCell(label);
			j++;
			
			//retrieve # of roll call votes
			temp = d.html();
			if (temp.indexOf("There has been")==-1&&temp.indexOf("There have been")==-1) {
				label = new Label(j,i,"0");
				sheet.addCell(label);
				j++;
			}
			else if (temp.indexOf("1 roll call vote")!=-1) {
				label = new Label(j,i,"1");
				sheet.addCell(label);
				j++;
			}
			else {
				temp2 = removeNonNums(temp.substring(temp.indexOf("roll call votes")-5,temp.indexOf("roll call votes")));
				label = new Label(j,i,temp2);
				sheet.addCell(label);
				j++;
			}
			
			//now on to cosponsor statistics
			int numCo, coDem, coRep, coInd;
			
			//retrieve # of cosponsors
			t = d.getElementsByClass("selected");
			temp = t.last().html(); //gets the whole selected li
			if (temp.indexOf("span class=")==-1)
				temp = t.eq(t.size()-2).last().html();
			temp = temp.substring(temp.indexOf("span class="),temp.indexOf("</span"));
			temp = removeNonNums(temp);
			numCo = Integer.parseInt(temp);
			label = new Label(j,i, temp);
			sheet.addCell(label);
			j++;
			
			//retrieve # of democrat cosponsors
			eTemp = d.getElementById("facetItempartyDemocraticcount");
			if (eTemp!=null) {
				temp = eTemp.text();
				temp = removeNonNums(temp);
			}
			else {
				temp = "0";
			}
			coDem = Integer.parseInt(temp);
			label = new Label(j,i, temp);
			sheet.addCell(label);
			j++;
			
			//retrieve # of republican cosponsors
			eTemp = d.getElementById("facetItempartyRepublicancount");
			if (eTemp!=null) {
				temp = eTemp.text();
				temp = removeNonNums(temp);
			}
			else {
				temp = "0";
			}
			coRep = Integer.parseInt(temp);
			label = new Label(j,i, temp);
			sheet.addCell(label);
			j++;
			
			//retrieve # of independent cosponsors
			eTemp = d.getElementById("facetItempartyIndependentcount");
			if (eTemp!=null) {
				temp = eTemp.text();
				temp = removeNonNums(temp);
			}
			else {
				temp = "0";
			}
			coInd = Integer.parseInt(temp);
			label = new Label(j,i, temp);
			sheet.addCell(label);
			j++;
			
			//retrieve % of democrat cosponsors
			if (numCo!=0) {
				temp = "" + (double)(coDem)/(coDem+coRep+coInd);
			} else {
				temp = "0";
			}
			label = new Label(j,i, temp);
			sheet.addCell(label);
			j++;
			
			//retrieve % of republican cosponsors
			if (numCo!=0) {
				temp = "" + (double)(coRep)/(coDem+coRep+coInd);
			} else {
				temp = "0";
			}
			label = new Label(j,i, temp);
			sheet.addCell(label);
			j++;

			//now get bill's title and summary
			j = 17;
			temp2 = "";
			d = Jsoup.connect(curPage).timeout(100000).get();
			temp = d.getElementsByTag("title").first().text(); //title of bill
			//see if there is a bill summary
			if (d.html().indexOf("A summary is in progress")<0) {
				eTemp = d.getElementById("bill-summary");
				try {
					temp2 = eTemp.html();
					if (temp2.length()>0) {
						if (temp2.indexOf("This bill")>=0&&temp2.lastIndexOf("</p>")>=0&&temp2.indexOf("This bill")<temp2.lastIndexOf("</p>"))
							temp2 = temp2.substring(temp2.indexOf("This bill"), temp2.lastIndexOf("</p>")); //summary of bill
						else
							temp2 = "";
					}
					else
						temp2 = "";
				}
				catch (RuntimeException e) {
					temp2 = "";
				}
			}
			else {
				temp2 = "";
			}
		
		
		
		
			temp = temp.trim() + " " + temp2.trim(); //combine title and summary
			if (temp.length()>32767)
				temp = temp.substring(0,32767);
			label = new Label(j,i,temp);
			sheet.addCell(label);
			
			
			
			
			//also get bill urgency and scope?
			
			
				
		}
		
		out.write();
		out.close();
		
	}

}
