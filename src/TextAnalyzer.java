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

public class TextAnalyzer extends Analyzer {
	
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
	
	public void go(int start,int end) throws IOException, RowsExceededException, WriteException, BiffException {
		//initialize necessary variables
		WritableWorkbook out; //excel file output
		WritableSheet sheet; //excel sheet output
		Label label; //excel cell output
		Document d; //current page
		String curPage = ""; //current page
		int j; //keeps track of which variable it's on (2nd index)
		String temp;
		String temp2;
		Element eTemp;
		
		Workbook workbook = Workbook.getWorkbook(new File("mainData.xls"));
		out = Workbook.createWorkbook(new File("mainData.xls"), workbook);
		sheet = out.getSheet(0);
		
		//now iterate through to gather bill title and summary for each bils
		for (int i = start-1; i < end; i++) {
			printProgress(start, end, i);
			
			temp = "";
			temp2 = "";
			
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
				curPage = "https://www.congress.gov/bill/114th-congress/senate-joint-resolution/"+(i+1-11866);
			else if (findBillType(i).equals("hconres"))
				curPage = "https://www.congress.gov/bill/114th-congress/house-concurrent-resolution/"+(i+1-11924);
			else if (findBillType(i).equals("sconres"))
				curPage = "https://www.congress.gov/bill/114th-congress/senate-concurrent-resolution/"+(i+1-12032);
			else {
				System.out.println("Invalid URL while trying to connect to URL with index "+i);
				System.exit(1);
			}
			d = Jsoup.connect(curPage).timeout(100000).get();
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
					curPage = "https://www.congress.gov/bill/114th-congress/senate-joint-resolution/"+(i+1-11866);
				else if (findBillType(i).equals("hconres"))
					curPage = "https://www.congress.gov/bill/114th-congress/house-concurrent-resolution/"+(i+1-11924);
				else if (findBillType(i).equals("sconres"))
					curPage = "https://www.congress.gov/bill/114th-congress/senate-concurrent-resolution/"+(i+1-12032);
				else {
					System.out.println("Invalid URL while trying to connect to URL with index "+i);
					System.exit(1);
				}
				d = Jsoup.connect(curPage).timeout(100000).get();
				temp = d.getElementsByTag("title").first().text(); //title of bill
			}
			
			
			j = 17;
			
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
			
		}
		
		out.write();
		out.close();
		
	}
}
