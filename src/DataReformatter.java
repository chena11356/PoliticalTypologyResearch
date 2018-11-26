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

public class DataReformatter extends Analyzer {
public void go(int start, int end) throws IOException, RowsExceededException, WriteException, BiffException {
		
		//initialize necessary variables
		WritableWorkbook out; //excel file output
		WritableSheet sheet; //excel sheet output
		Label label; //excel cell output
		
		int j; //keeps track of which variable it's on (2nd index)
		int k; //keeps track of which member on the page
		int x = 999; //for convenience
		int y = -999; //for convenience
		int lastPageNum;
		String temp;
		String temp2;
		String temp3;
		boolean temp7;
		
		String lastName;
		String firstName;
		String state;
		String party;
		String lastName2;
		String firstName2;
		String state2;
		String party2;
		
		Workbook data = Workbook.getWorkbook(new File("memberData.xls"));
		Sheet dataSheet = data.getSheet(0);
		
		Workbook workbook = Workbook.getWorkbook(new File("memberData.xls"));
		out = Workbook.createWorkbook(new File("memberData.xls"), workbook);
		sheet = out.getSheet(0);
		
		
		
		//now iterate through to gather data on each member of Congress
		for (int i = start-1; i < end; i++){
			
			printProgress(start, end, i);
			label = new Label(63, i, stateAbbreviation(dataSheet.getCell(2,i).getContents().trim())); //make a state abbreviation variable
			sheet.addCell(label);
			label = new Label(64, i, partyAbbreviation(dataSheet.getCell(1,i).getContents().trim())); //make a party abbreviation variable
			sheet.addCell(label);
			
		}
		
		
		
		out.write();
		out.close();
		
	}
}
