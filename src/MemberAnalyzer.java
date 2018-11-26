import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class MemberAnalyzer extends Analyzer {
	
	
	//gets current page given index
	public String getCurPage(int i) {
		if (i<250)
			return "https://www.congress.gov/members?q={%22congress%22:%22114%22}&pageSize=250";
		else if (i<500)
			return "https://www.congress.gov/members?q=%7B%22congress%22%3A%22114%22%7D&pageSize=250&page=2";
		else if (i<547)
			return "https://www.congress.gov/members?q=%7B%22congress%22%3A%22114%22%7D&pageSize=250&page=3";
		else
			return "Invalid index, could not get page";
	}
	
	//gets current member's index on page given index
	public int getMemberIndex(int i) {
		if (i<250)
			return i;
		else if (i<500)
			return i-250;
		else if (i<547)
			return i-500;
		else
			return -3;
	}
	

	public void go(int start, int end) throws IOException, RowsExceededException, WriteException, BiffException {
		
		//initialize necessary variables
		WritableWorkbook out; //excel file output
		WritableSheet sheet; //excel sheet output
		Label label; //excel cell output
		Document d; //current page
		String curPage = ""; //current page
		String prevPage = ""; //prev page
		
		Document dd; //current member page
		
		int j; //keeps track of which variable it's on (2nd index)
		int k; //keeps track of which member on the page
		int x = 999; //for convenience
		int y = -999; //for convenience
		String temp;
		String temp2;
		boolean temp7;
		Document dTemp;
		Element eTemp;
		Elements t;
		
		String lastName;
		String firstName;
		String state;
		
		int needyScore=0;
		int efficiencyScore=0;
		int peaceScore=0;
		int racialScore=0;
		int lgbtScore=0;
		int corporateScore=0;
		int environmentScore=0;
		int immigrationScore=0;
		int poorScore=0;
		int economicScore=0;
		int racialEqScore=0;
		int worldScore=0;
		int workScore=0;
		int genderScore=0;
		int compromiseScore=0;
		int gunScore=0;
		
		Workbook workbook = Workbook.getWorkbook(new File("memberData.xls"));
		out = Workbook.createWorkbook(new File("memberData.xls"), workbook);
		sheet = out.getSheet(0);
		
		Workbook outsideData = Workbook.getWorkbook(new File("ideologyLeadershipData.xls"));
		Sheet dataSheet = outsideData.getSheet(0);
		
		Workbook outsideData2 = Workbook.getWorkbook(new File("bipartisanshipData.xls"));
		Sheet dataSheet2 = outsideData2.getSheet(0);
		
		String[][] ideologyLeadership = new String[548][10];
		String[] tempArr;
		for (int p = 0; p < 548; p++) {
			tempArr = dataSheet.getCell(0,p).getContents().split(",");
			for (int q = 0; q < 10; q++) {
				ideologyLeadership[p][q] = tempArr[q];
			}
		}
		
		String[][] bipartisanship = new String[1050][6];
		for (int p = 0; p < 1050; p++) {
			for (int q = 0; q < 6; q++) {
				bipartisanship[p][q] = dataSheet2.getCell(q,p).getContents();
			}
		}
		
		curPage = getCurPage(start-1);
		d = Jsoup.connect(curPage).timeout(100000).get();
		
		//now iterate through to gather data on each member of Congress
		for (int i = start-1; i < end; i++){
			
			printProgress(start, end, i);
			
			curPage = getCurPage(i);
			
			//connect to new page if curPage is not the same as prevPage
			if (!(curPage.equals(prevPage))) {
				d = Jsoup.connect(curPage).timeout(100000).get();
			}
			//get all the members on the page into an array of li elements
			t = d.getElementsByClass("compact");
			//get index of member we're looking for in that array
			eTemp = t.get(getMemberIndex(i));
			//get everything inside the li as text
			temp = eTemp.html();
			dTemp = Jsoup.parseBodyFragment(temp); //makes the li part a temp document
			t = dTemp.getElementsByTag("a");
			eTemp = t.first();
			temp2 = eTemp.attr("href");
			
			//connect to member's page
			dd = Jsoup.connect(temp2).timeout(100000).get();
			
			//start at first variable (last name of legislator)
			j = 0;
			temp = dd.getElementsByTag("title").first().html();
			temp = temp.substring(0,temp.indexOf("|")).trim();
			if (temp.indexOf(", Jr")>=0) {
				temp = temp.substring(0,temp.indexOf(", Jr"));
			}
			firstName = temp.split(" ")[0].trim();
			lastName = temp.split(" ")[temp.split(" ").length-1].trim();
			label = new Label(j, i, temp);
			sheet.addCell(label);
			j++;
			
			
			//then find legislator's party
			temp = dd.html();
			if (temp.indexOf("Republican")>=0)
				temp2 = "Republican";
			if (temp.indexOf("Democratic")>=0)
				temp2 = "Democrat";
			if (temp.indexOf("Republican")>=0&&temp.indexOf("Democratic")>=0)
				temp2 = "Both";
			if (temp.indexOf("Republican")<0&&temp.indexOf("Democratic")<0)
				temp2 = "Independent";
			label = new Label(j, i, temp2);
			sheet.addCell(label);
			j++;
			
			//then find state
			temp = dd.getElementsByTag("tbody").first().html();
			temp2 = temp.substring(temp.indexOf("<td>")+4,temp.indexOf("</td>")).trim();
			state = stateAbbreviation(temp2);
			label = new Label(j, i, temp2);
			sheet.addCell(label);
			j++;
			
			//then find region
			temp = "Could not find region";
			if (temp2.equalsIgnoreCase("Connecticut")||temp2.equalsIgnoreCase("Maine")||temp2.equalsIgnoreCase("Massachusetts")||temp2.equalsIgnoreCase("New Hampshire")||temp2.equalsIgnoreCase("Rhode Island")||temp2.equalsIgnoreCase("Vermont")||temp2.equalsIgnoreCase("New Jersey")||temp2.equalsIgnoreCase("New York")||temp2.equalsIgnoreCase("Pennsylvania"))
				temp = "Northeast";
			if (temp2.equalsIgnoreCase("Illinois")||temp2.equalsIgnoreCase("Indiana")||temp2.equalsIgnoreCase("Michigan")||temp2.equalsIgnoreCase("Ohio")||temp2.equalsIgnoreCase("Wisconsin")||temp2.equalsIgnoreCase("Iowa")||temp2.equalsIgnoreCase("Kansas")||temp2.equalsIgnoreCase("Minnesota")||temp2.equalsIgnoreCase("Missouri")||temp2.equalsIgnoreCase("Nebraska")||temp2.equalsIgnoreCase("North Dakota")||temp2.equalsIgnoreCase("South Dakota"))
				temp = "Midwest";
			if (temp2.equalsIgnoreCase("Delaware")||temp2.equalsIgnoreCase("Florida")||temp2.equalsIgnoreCase("Georgia")||temp2.equalsIgnoreCase("Maryland")||temp2.equalsIgnoreCase("North Carolina")||temp2.equalsIgnoreCase("South Carolina")||temp2.equalsIgnoreCase("Virginia")||temp2.equalsIgnoreCase("District of Columbia")||temp2.equalsIgnoreCase("West Virginia")||temp2.equalsIgnoreCase("Alabama")||temp2.equalsIgnoreCase("Kentucky")||temp2.equalsIgnoreCase("Mississippi")||temp2.equalsIgnoreCase("Tennessee")||temp2.equalsIgnoreCase("Arkansas")||temp2.equalsIgnoreCase("Louisiana")||temp2.equalsIgnoreCase("Oklahoma")||temp2.equalsIgnoreCase("Texas"))
				temp = "South";
			if (temp2.equalsIgnoreCase("Arizona")||temp2.equalsIgnoreCase("Colorado")||temp2.equalsIgnoreCase("Idaho")||temp2.equalsIgnoreCase("Montana")||temp2.equalsIgnoreCase("Nevada")||temp2.equalsIgnoreCase("New Mexico")||temp2.equalsIgnoreCase("Utah")||temp2.equalsIgnoreCase("Wyoming")||temp2.equalsIgnoreCase("Alaska")||temp2.equalsIgnoreCase("California")||temp2.equalsIgnoreCase("Hawaii")||temp2.equalsIgnoreCase("Oregon")||temp2.equalsIgnoreCase("Washington")||temp2.equalsIgnoreCase("American Samoa"))
				temp = "West";
			label = new Label(j, i ,temp);
			sheet.addCell(label);
			j++;
			
			//then find number of years in Congress
			temp = dd.getElementsByClass("legDetail").first().html();
			
			if (temp.indexOf("In Congress")+29>=temp.length())
				continue;
			temp2 = removeNonNums(temp.substring(temp.indexOf("In Congress"), temp.indexOf("In Congress")+29));
			
			if (temp2.length()==8) {
				x = Integer.parseInt(temp2.substring(0, 4));
				y = Integer.parseInt(temp2.substring(4, 8));
			}
			else if (temp2.length()==4) {
				x = Integer.parseInt(temp2);
				y = 2018;
			}
			
			label = new Label(j, i, y-x+"");
			sheet.addCell(label);
			j++;
			
			//then find # of sponsored legislation
			temp = removeNonNums(dd.getElementById("facetItemsponsorshipSponsored_Legislationcount").text());
			label = new Label(j, i, temp);
			sheet.addCell(label);
			j++;
			
			//then find # of cosponsored legislation
			temp = removeNonNums(dd.getElementById("facetItemsponsorshipCosponsored_Legislationcount").text());
			String cosponsoredNum = temp;
			label = new Label(j, i, cosponsoredNum);
			sheet.addCell(label);
			j++;
			
			//then find bill success rate (NOTE: NOT SURE IF THIS IS RIGHT WAY)
			//first find number of bills that became law
			x = Integer.parseInt(removeNonNums(dd.getElementById("facetItembill-statusBecame_Lawcount").text()));
			//then find total number of bills
			y = Integer.parseInt(removeNonNums(dd.getElementById("facetItembill-statusIntroducedcount").text()));
			label = new Label(j, i, (double)x/y+"");
			sheet.addCell(label);
			j++;
			
			//then find ideology as typology
			//placeholder for now
			label = new Label(j,i,"typology");
			sheet.addCell(label);
			j++;
			
			//then find ideology as left-right
			//first find where member is in the ideologyLeadership data
			int memberIndex = -2;
			for (int w = 0; w < 548; w++) {
				if (ideologyLeadership[w][3].indexOf("[")>=0&&ideologyLeadership[w][3].indexOf(lastName)>=0) {
					//means duplicate last names, so check if same state
					if (ideologyLeadership[w][3].indexOf("["+state)>=0) {
						memberIndex = w;
						break;
					}
				}
				else if (ideologyLeadership[w][3].trim().equalsIgnoreCase(lastName)) {
					memberIndex = w;
					break;
				}
			}
			
			if (memberIndex!=-2) {
				temp = ideologyLeadership[memberIndex][1];
				label = new Label(j, i, temp);
				sheet.addCell(label);
				j++;
			
				//then find leadership
				temp = ideologyLeadership[memberIndex][2];
				label = new Label(j, i, temp);
				sheet.addCell(label);
				j++;
			}
			else {
				j+=2;
			}
			
			//then find chamber (House or Senate)
			memberIndex = -4;
			for (int w = 0; w < 1050; w++) {
				if (bipartisanship[w][0].trim().equalsIgnoreCase(firstName)&&bipartisanship[w][1].trim().equalsIgnoreCase(lastName)) {
					memberIndex = w;
					break;
				}
			}
			if (memberIndex!=-4) {
				label = new Label(j,i,bipartisanship[memberIndex][5]);
				sheet.addCell(label);
				j++;
			
				//then find bipartisanship
				label = new Label(j,i,bipartisanship[memberIndex][4]);
				sheet.addCell(label);
				j++;
					
					
				prevPage = curPage;
			}
			else {
				j+=2;
			}
			
			//then come stance scores, from indices 13 to 30, inclusive
			//this will be calculated by rollCallAnalyzer, along with the typology at index 8
			while (j<30) {
				label = new Label(j,i,"0");
				sheet.addCell(label);
				j++;
			}
			
		}
		
		out.write();
		out.close();
		
	}

}
