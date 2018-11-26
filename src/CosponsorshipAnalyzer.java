import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

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

public class CosponsorshipAnalyzer extends Analyzer {
	
	public static final int CORECONSERVATIVE = 13;
	public static final int COUNTRYFIRSTCONSERVATIVE = 271;
	public static final int MARKETSKEPTICREPUBLICAN = 15;
	public static final int NEWERAENTERPRISER = 7;
	public static final int DEVOUTANDDIVERSE = 7;
	public static final int DISAFFECTEDDEMOCRAT = 45;
	public static final int OPPORTUNITYDEMOCRAT = 1; //actually it's 0
	public static final int SOLIDLIBERAL = 189;
	
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
	
	//tempData is data of % of cosponsored bills that are from each typology
	public Double[][] tempData;
	
	public CosponsorshipAnalyzer() {
		tempData = new Double[547][16];
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
		int lastPageNum;
		String temp;
		String temp2;
		String temp3;
		boolean temp7;
		Document dTemp;
		Element eTemp;
		Elements t;
		
		String lastName;
		String firstName;
		String state;
		String party;
		String lastName2;
		String firstName2;
		String state2;
		String party2;
		
		int memberIndex;
		
		int coreConservative = 0;
		int countryFirstConservative = 0;
		int marketSkepticRepublican = 0;
		int newEraEnterpriser = 0;
		int devoutAndDiverse = 0;
		int disaffectedDemocrat = 0;
		int opportunityDemocrat = 0;
		int solidLiberal = 0;
		int totalCosponsored = 0;
		
		Workbook data = Workbook.getWorkbook(new File("memberData.xls"));
		Sheet dataSheet = data.getSheet(0);
		
		Workbook workbook = Workbook.getWorkbook(new File("memberData.xls"));
		out = Workbook.createWorkbook(new File("memberData.xls"), workbook);
		sheet = out.getSheet(0);
		
		/*
		Workbook outsideData = Workbook.getWorkbook(new File("ideologyLeadershipData.xls"));
		Sheet dataSheet = outsideData.getSheet(0);
		
		Workbook outsideData2 = Workbook.getWorkbook(new File("bipartisanshipData.xls"));
		Sheet dataSheet2 = outsideData2.getSheet(0);
		*/
		
		
		curPage = getCurPage(start-1);
		d = Jsoup.connect(curPage).timeout(100000).get();
		
		//now iterate through to gather data on each member of Congress
		for (int i = start-1; i < end; i++){
			
			printProgress(start, end, i);
			
			coreConservative = 0;
			countryFirstConservative = 0;
			marketSkepticRepublican = 0;
			newEraEnterpriser = 0;
			devoutAndDiverse = 0;
			disaffectedDemocrat = 0;
			opportunityDemocrat = 0;
			solidLiberal = 0;
			totalCosponsored = 0;
			
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
			temp3 = temp2;
			
			//connect to member's page
			dd = Jsoup.connect(temp2+"?page=1").timeout(100000).get();
			
			//start at first variable (last name of legislator)
			j = 0;
			temp = dd.getElementsByTag("title").first().html();
			temp = temp.substring(0,temp.indexOf("|")).trim();
			if (temp.indexOf(", Jr")>=0) {
				temp = temp.substring(0,temp.indexOf(", Jr"));
			}
			firstName = temp.split(" ")[0].trim();
			lastName = temp.split(" ")[temp.split(" ").length-1].trim();
			
			/*
			//then find legislator's party
			temp = dd.html();
			if (temp.indexOf("Republican")>=0)
				party = "Rep";
			if (temp.indexOf("Democratic")>=0)
				party = "Dem";
			if (temp.indexOf("Republican")>=0&&temp.indexOf("Democratic")>=0)
				party = "Rep";
			if (temp.indexOf("Republican")<0&&temp.indexOf("Democratic")<0)
				party = "Independent";
			*/
			
			//then find state
			temp = dd.getElementsByTag("tbody").first().html();
			temp2 = temp.substring(temp.indexOf("<td>")+4,temp.indexOf("</td>")).trim();
			state = stateAbbreviation(temp2);
			
			//then find how many pages there are of bills
			lastPageNum = 1;
			temp = dd.html();
			if (temp.indexOf("a class=\"last\"")>=0) {
				temp = temp.substring(temp.indexOf("a class=\"last\""));
				temp = temp.substring(temp.indexOf("page=")+5);
				temp = temp.substring(0,temp.indexOf("\""));
				lastPageNum = Integer.parseInt(temp);
			}
			else {
				System.out.println("Could not find a with class=last for "+firstName+lastName+state);
			}
			
			for (int p = 1; p <= lastPageNum;p++) {
			
			dd = Jsoup.connect(temp3+"?page="+p).timeout(100000).get();
			
			//get all the <span class="result-item">s, and if there's a Rep. or Sen., check typology and add on score
			t = dd.getElementsByClass("result-item");
			
			for (Element result : t) {
				temp = result.html();
				//check if it was introduced in 2015/2016
				if (temp.indexOf("/2015")<0&&temp.indexOf("/2016")<0) {
					continue;
				}
				if (temp.indexOf("Rep.")>=0) {
					temp = temp.substring(temp.indexOf("Rep.")+5);
				}
				else if (temp.indexOf("Sen.")>=0) {
					temp = temp.substring(temp.indexOf("Sen.")+5);
				}
				else {
					continue;
				}
				temp = temp.substring(0,temp.indexOf("<"));
				//now you have something like Zinke, Ryan K. [R-MT-At Large]
				if (temp.indexOf("Johnson, Henry C")>=0) {
					temp = "Johnson, Henry C. \"Hank\" [D-GA-4]";
				}
				if (temp.indexOf(", Jr.")>=0) {
					temp = removeSegment(temp,", Jr.");
				}
				if (temp.indexOf(", Sr.")>=0) {
					temp = removeSegment(temp,", Sr.");
				}
				if (temp.indexOf(", III")>=0) {
					temp = removeSegment(temp,", III");
				}
				try {
					lastName2 = temp.substring(0,temp.indexOf(",")).trim();
					firstName2 = temp.substring(temp.indexOf(",")+2,temp.indexOf("[")).trim();
				}
				catch (StringIndexOutOfBoundsException se){
					continue;
				}
				state2 = temp.substring(temp.indexOf("["),temp.indexOf("]")).split("-")[1];
				if (lastName2.indexOf("Kirk")>=0&&firstName2.indexOf("Mark")>=0&&state2.indexOf("IL")>=0) {
					lastName2 = "Kirk";
					firstName2 = "Mark";
				}
				if (lastName2.indexOf("Hunter")>=0&&firstName2.indexOf("Duncan")>=0&&state2.indexOf("CA")>=0) {
					lastName2 = "Hunter";
					firstName2 = "Duncan";
				}
				if (lastName2.indexOf("Rogers")>=0&&firstName2.indexOf("Mike")>=0&&state2.indexOf("AL")>=0) {
					lastName2 = "Rogers";
					firstName2 = "Mike";
				}
				if (lastName2.indexOf("Sablan")>=0&&firstName2.indexOf("Gregorio")>=0&&state2.indexOf("MP")>=0) { //outlier
					continue;
				}
				if (lastName2.indexOf("Horsford")>=0||(lastName2.indexOf("Frank")>=0&&firstName2.indexOf("Barney")>=0)) { //no longer in congress
					continue;
				}
				//check if it's the same member's bill
				if (lastName.indexOf(lastName2)>=0&&firstName.indexOf(firstName2)>=0&&state.indexOf(state2)>=0) {
					continue;
				}
				memberIndex = -3;
				//now search for correct member
				for (int w = 0; w < 547; w++){
					if (dataSheet.getCell(0,w).getContents().indexOf(lastName2)>=0&&dataSheet.getCell(0,w).getContents().indexOf(firstName2)>=0&&stateAbbreviation(dataSheet.getCell(2,w).getContents().trim()).indexOf(state2)>=0) {
						memberIndex = w;
						break;
					}	
				}
				if (memberIndex!=-3) {
					//found member, now get their typology and change this member's typology cosponsorship score
					if (dataSheet.getCell(31,memberIndex).getContents().indexOf("Core")>=0) {
						coreConservative++;
					}
					else if (dataSheet.getCell(31,memberIndex).getContents().indexOf("Country")>=0) {
						countryFirstConservative++;
					}
					else if (dataSheet.getCell(31,memberIndex).getContents().indexOf("Market")>=0) {
						marketSkepticRepublican++;
					}
					else if (dataSheet.getCell(31,memberIndex).getContents().indexOf("New")>=0) {
						newEraEnterpriser++;
					}
					else if (dataSheet.getCell(31,memberIndex).getContents().indexOf("Devout")>=0) {
						devoutAndDiverse++;
					}
					else if (dataSheet.getCell(31,memberIndex).getContents().indexOf("Disaffected")>=0) {
						disaffectedDemocrat++;
					}
					else if (dataSheet.getCell(31,memberIndex).getContents().indexOf("Opp")>=0) {
						opportunityDemocrat++;
					}
					else if (dataSheet.getCell(31,memberIndex).getContents().indexOf("Solid")>=0) {
						solidLiberal++;
					}
					else {
						System.out.println("Could not find typology of member "+lastName2+firstName2+state+" for "+lastName+firstName+state);
					}
					totalCosponsored++;
					
				}
				else {
					//did not find member, deal with this
					System.out.println("Could not find member: "+lastName2+ " "+firstName2+" "+state2+" as typology for: "+lastName+firstName+state);
				}
			
			
				
				
			}}
			
			if (totalCosponsored==0) {
				totalCosponsored++;
			}
			
			//make cosponsorship percentages
			tempData[i][0]=((double)coreConservative)/totalCosponsored;
			tempData[i][1]=((double)countryFirstConservative)/totalCosponsored;
			tempData[i][2]=((double)marketSkepticRepublican)/totalCosponsored;
			tempData[i][3]=((double)newEraEnterpriser)/totalCosponsored;
			tempData[i][4]=((double)devoutAndDiverse)/totalCosponsored;
			tempData[i][5]=((double)disaffectedDemocrat)/totalCosponsored;
			tempData[i][6]=((double)opportunityDemocrat)/totalCosponsored;
			tempData[i][7]=((double)solidLiberal)/totalCosponsored;
			
			//make adjusted cosponsorship percentages too by dividing by total number of each typology
			tempData[i][8]=tempData[i][0]/CORECONSERVATIVE;
			tempData[i][9]=tempData[i][1]/COUNTRYFIRSTCONSERVATIVE;
			tempData[i][10]=tempData[i][2]/MARKETSKEPTICREPUBLICAN;
			tempData[i][11]=tempData[i][3]/NEWERAENTERPRISER;
			tempData[i][12]=tempData[i][4]/DEVOUTANDDIVERSE;
			tempData[i][13]=tempData[i][5]/DISAFFECTEDDEMOCRAT;
			tempData[i][14]=tempData[i][6]/OPPORTUNITYDEMOCRAT;
			tempData[i][15]=tempData[i][7]/SOLIDLIBERAL;
			
			//now write it into the spreadsheet
			for (int s = 0; s<16; s++) {
				try {
					label = new Label(44+s,i,new BigDecimal(tempData[i][s]).toPlainString());
				}
				catch (NumberFormatException n) {
					label = new Label(44+s,i,0+"");
				}
				sheet.addCell(label);
			}
		}
		
		
		
		out.write();
		out.close();
		
	}

}
