import java.io.File;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class TopicAnalyzer extends Analyzer {
	
	//finds index given bill type and bill number
		public int findBillIndex(String type, int num) {
			
			if (type.equalsIgnoreCase("HR"))
				return 0+num-1;
			else if (type.equalsIgnoreCase("S"))
				return 6536+num-1;
			else if (type.equalsIgnoreCase("HRES"))
				return 10084+num-1;
			else if (type.equalsIgnoreCase("SRES"))
				return 11041+num-1;
			else if (type.equalsIgnoreCase("HJRES"))
				return 11683+num-1;
			else if (type.equalsIgnoreCase("SJRES"))
				return 11791+num-1;
			else if (type.equalsIgnoreCase("HCONRES"))
				return 11832+num-1;
			else if (type.equalsIgnoreCase("SCONRES"))
				return 12015+num-1;
			else
				return -1;
			
		}
	
	public void go (int start, int end) throws IOException, RowsExceededException, WriteException, BiffException {
		
		
		//initialize necessary variables
		WritableWorkbook out; //excel file output
		WritableSheet sheet; //excel sheet output
		Cell[] cells;
		Label label; //excel cell output
		int j; //keeps track of which variable it's on (2nd index)
		String temp = ""; //keeps track of current outsideData string
		String temp2 = ""; //for convenience and major topic
		String temp3 = ""; //for convenience and minor topic
		String temp4 = ""; //for convenience and finding right bill index
		String[] tempArr = new String[2];
		int x; //for convenience
		int y; //for convenience
		boolean fail = false;
		
		
				
		Workbook workbook = Workbook.getWorkbook(new File("mainData.xls"));
		out = Workbook.createWorkbook(new File("mainData.xls"), workbook);
		sheet = out.getSheet(0);
		
		Workbook outsideData = Workbook.getWorkbook(new File("data114.xls"));
		Sheet dataSheet = outsideData.getSheet(0);
		
		//now iterate through the document to get what we want
		for (int i = start-1; i < end; i++){
			
			printProgress(start, end, i);
			
			fail = false;
			temp = ""; //keeps track of current outsideData string
			temp2 = ""; //for convenience
			temp3 = "";
			temp4 = "";
			tempArr = new String[2];
			
			//get wilkerson data for this bill as a single string
			cells = dataSheet.getRow(i);
			for (Cell cell : cells) {
				temp += cell.getContents();
			}
			
			
			//get bill topic section by finding last index of ; and cutting it off and finding last index again
			x = temp.lastIndexOf(';');
			if (x>=0) {
				temp2 = temp.substring(0, x);
				y = temp2.lastIndexOf(';');
				if (y>=0)
					temp2 = temp.substring(y+1); //get the whole bill topic section (basically up until the end)
				else
					fail = true;
			}
			else {
				fail = true;
			}
			
			
			
			//make an array with 2 strings holding the major and minor or something
			tempArr = temp2.split(";");
			if (tempArr.length!=2)
				fail = true;
			
			
			
			temp2 = tempArr[0];
			temp3 = removeQuotes(tempArr[1]);
			
			//now find the appropriate bill index
			x = temp.indexOf(';');

			
			
			
			tempArr = temp.substring(0,x).split("-");
			
			
			
			if (tempArr.length!=3)
				fail = true;
			
			
			//add a while loop here somewhere using fail
			while (fail) {
				
			i++;
			fail = false;
			temp = ""; //keeps track of current outsideData string
			temp2 = ""; //for convenience
			temp3 = "";
			temp4 = "";
			tempArr = new String[2];
			
			//get wilkerson data for this bill as a single string
			cells = dataSheet.getRow(i);
			for (Cell cell : cells) {
				temp += cell.getContents();
			}
			
			
			//get bill topic section by finding last index of ; and cutting it off and finding last index again
			x = temp.lastIndexOf(';');
			if (x>=0) {
				temp2 = temp.substring(0, x);
				y = temp2.lastIndexOf(';');
				if (y>=0)
					temp2 = temp.substring(y+1); //get the whole bill topic section (basically up until the end)
				else
					fail = true;
			}
			else {
				fail = true;
			}
			
			
			
			//make an array with 2 strings holding the major and minor or something
			tempArr = temp2.split(";");
			if (tempArr.length!=2)
				fail = true;
			
			
			
			temp2 = tempArr[0];
			temp3 = removeQuotes(tempArr[1]);
			
			//now find the appropriate bill index
			tempArr = temp.substring(0,x).split("-");
			

			if (tempArr.length!=3)
				fail = true;
				
				
			}
			
			
			//SO NOW, temp2 is major topic, temp3 is minor topic, tempArr[0] is 114,
			//tempArr[1] is HCONRES/HJRES/HR/HRES/S/SCONRES/SJRES/SRES, tempArr[2] is bill number
			//Note that HJRES starts at row 184, HR starts at row 292, HRES starts at 6800, S starts at 7756, 
			//SCONRES starts at 11303, SJRES starts at 11361, SRES starts at 11402
			
			j = findBillIndex(tempArr[1], Integer.parseInt(tempArr[2]));
			
			label = new Label(15,j, temp2);
			sheet.addCell(label);
			
			label = new Label(16,j, temp3);
			sheet.addCell(label);
			
			
			
			
			
			
		}
		out.write();
		out.close();
	}
}
