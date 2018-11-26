import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

//adds typology of sponsors into bill data
public class BillTypologyAnalyzer extends Analyzer {
	
	public String[][] tempData;
	
	public BillTypologyAnalyzer() {
		tempData = new String[547][4];
	}
	
	public void go(int start, int end) throws BiffException, IOException, WriteException {
		//initialize necessary variables
		WritableWorkbook out; //excel file output
		WritableSheet sheet; //excel sheet output
		Label label; //excel cell output
		Document d; //current page
		Document dd; //roll call vote page
		String curPage = ""; //current page
		
		int j; //keeps track of which variable it's on (2nd index)
		int memberIndex; //used to search for correct member
		
		String temp;
		String temp2;
		String tempName;
		String tempVote;
		String[] tempArr;
		String[] tempArr2;
		
		String lastName;
		String firstName;
		String party;
		String state;
		
		int tempScoreValue;
		
		//get member data to find typology
		Workbook memberData = Workbook.getWorkbook(new File("memberData.xls"));
		Sheet dataSheet = memberData.getSheet(0);
		
		//get and set writable bill data
		Workbook workbook = Workbook.getWorkbook(new File("mainData.xls"));
		Sheet billSheet = workbook.getSheet(0);
		out = Workbook.createWorkbook(new File("mainData.xls"), workbook);
		sheet = out.getSheet(0);
		
		//first put all the data from the dataSheet into tempData for ease
		//just need name, state, party, typology
		for (int i = 0; i < 547; i++) {
			tempData[i][0] = dataSheet.getCell(0,i).getContents(); //name
			tempData[i][1] = dataSheet.getCell(1,i).getContents(); //party (Democrat, Republican, Independent)
			tempData[i][2] = dataSheet.getCell(2,i).getContents(); //state (non-abbreviated)
			tempData[i][3] = dataSheet.getCell(31,i).getContents(); //typology
			
			//for (int k = 0; k < 4; k++) {
				//System.out.println(i+" "+k+" "+tempData[i][k]);
			//}
		}
		
		//iterate through all the bills, get the member, identify the member's and his/her typology, write typology
		for (int i = start-1; i < end; i++) {
			printProgress(start, end, i);
			
			//check for special situations
			if (billSheet.getCell(3,i).getContents().indexOf("Young, Todd")>=0||billSheet.getCell(3,i).getContents().indexOf("Jolly, David")>=0||billSheet.getCell(3,i).getContents().indexOf("McMorris Rodgers")>=0) {
				label = new Label(18,i,"Country First Conservative");
				sheet.addCell(label);
				continue;
			}
			else if (billSheet.getCell(3,i).getContents().indexOf("Jackson Lee")>=0||billSheet.getCell(3,i).getContents().indexOf("Watson Coleman")>=0||billSheet.getCell(3,i).getContents().indexOf("Lujan Grisham")>=0||billSheet.getCell(3,i).getContents().indexOf("Van Hollen")>=0||billSheet.getCell(3,i).getContents().indexOf("Wasserman Schultz")>=0) {
				label = new Label(18,i,"Solid Liberal");
				sheet.addCell(label);
				continue;
			}
			else if (billSheet.getCell(3,i).getContents().indexOf("Herrera Beutler")>=0) {
				label = new Label(18,i,"Market Skeptic Republican");
				sheet.addCell(label);
				continue;
			}
			else if (billSheet.getCell(3,i).getContents().indexOf("Murphy, Christopher")>=0) {
				label = new Label(18,i,"Disaffected Democrat");
				sheet.addCell(label);
				continue;
			}
			
			tempArr = billSheet.getCell(3,i).getContents().split(","); //get array of last name, first name
			if (tempArr.length!=2) {
				System.out.println("Name not correct format: "+billSheet.getCell(3,i).getContents());
				continue;
			}
			lastName = tempArr[0].trim();
			firstName = tempArr[1].trim();
			party = billSheet.getCell(1,i).getContents(); //D or R
			state = billSheet.getCell(2,i).getContents(); //abbreviated state
			//now search for this member in the member data
			for (int m = 0; m < 547; m++) {
				if (tempData[m][0].indexOf(lastName)>=0&&tempData[m][0].indexOf(firstName)>=0) {
					if ((party.trim().indexOf("D")>=0&&tempData[m][1].indexOf("Dem")>=0)||(party.trim().indexOf("R")>=0&&tempData[m][1].indexOf("Rep")>=0)||(party.trim().indexOf("I")>=0&&tempData[m][1].indexOf("Ind")>=0)) {
						if (state.indexOf(stateAbbreviation(tempData[m][2].trim()))>=0) {
							//found member
							label = new Label(18,i,tempData[m][3]);
							sheet.addCell(label);
							break;
						}
					}
				}
				//if last member and still haven't found, then something went wrong
				if (m==546) {
					System.out.println("Could not find member: "+lastName+" "+" "+firstName+" "+" "+party+" "+" "+state);
				}
			}
			
			
			
			
		}
		
		out.write();
		out.close();
		
	}
}
