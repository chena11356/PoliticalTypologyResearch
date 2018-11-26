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

public class TypologyAnalyzer extends Analyzer {
	
	public void go(int start, int end) throws BiffException, IOException, WriteException {
		//initialize necessary variables
		WritableWorkbook out; //excel file output
		WritableSheet sheet; //excel sheet output
		Label label; //excel cell output
		Label label2;
		Label label3;
		Document d; //current page
		Document dd; //roll call vote page
		String curPage = ""; //current page
		
		int j; //keeps track of which variable it's on (2nd index)
		int memberIndex; //used to search for correct member
		
		String temp;
		String temp2;
		String tempName;
		String tempVote;
		String[] curBillList;
		String[] tempArr;
		String[] tempArr2;
		Document dTemp;
		Element eTemp;
		Elements t;
		
		int tempScore;
		int[] tempScores;
		double typologyStrength; //strength of typology measured in average distance away from zero
		double typologyStrength2; //strength of typology measured in % of categories filled
		
		Workbook data = Workbook.getWorkbook(new File("memberData.xls"));
		Sheet dataSheet = data.getSheet(0);
		
		Workbook workbook = Workbook.getWorkbook(new File("memberData.xls"));
		out = Workbook.createWorkbook(new File("memberData.xls"), workbook);
		sheet = out.getSheet(0);
		
		for (int i = start-1; i < end; i++) {
			printProgress(start, end, i);
			tempScores = new int[16]; //16, not counting the gun score
			typologyStrength = 0;
			typologyStrength2 = 0;
			for (int k = 0; k < 16; k++) {
				tempScore = Integer.parseInt(dataSheet.getCell(13+k,i).getContents());
				typologyStrength += Math.abs(tempScore);
				if (tempScore!=0)
					typologyStrength2 += 1;
				tempScores[k] = tempScore;
			}
			temp = dataSheet.getCell(1,i).getContents(); //party
			temp2 = findTypology(
					tempScores[0],
					tempScores[1],
					tempScores[2],
					tempScores[3],
					tempScores[4],
					tempScores[5],
					tempScores[6],
					tempScores[7],
					tempScores[8],
					tempScores[9],
					tempScores[10],
					tempScores[11],
					tempScores[12],
					tempScores[13],
					tempScores[14],
					tempScores[15],
					temp
					);
			label = new Label(31,i,temp2);
			sheet.addCell(label);
			label = new Label(32,i,(typologyStrength/16)+"");
			sheet.addCell(label);
			label = new Label(33,i,(typologyStrength2/16)+"");
			sheet.addCell(label);
			
			//also determine whether legislator is bipartisan
			if (dataSheet.getCell(12,i).getContents().length()>0) {
				if (Double.parseDouble(dataSheet.getCell(12,i).getContents())>0) {
					label = new Label(34,i,1+"");
				}
				else {
					label = new Label(34,i,0+"");
				}
				sheet.addCell(label);
			}
			
			label2 = new Label(43,i,99+"");
			//also make dummy variables for each typology: core, country, market, new, devout, dis, opp, solid
			//label2 is typology as number
			if (temp2.indexOf("Core")>=0) {
				label = new Label(35,i,1+"");
				label2 = new Label(43,i,1+"");
			}
			else {
				label = new Label(35,i,0+"");
			}
			sheet.addCell(label);
			if (temp2.indexOf("Country")>=0) {
				label = new Label(36,i,1+"");
				label2 = new Label(43,i,2+"");
			}
			else {
				label = new Label(36,i,0+"");
			}
			sheet.addCell(label);
			if (temp2.indexOf("Market")>=0) {
				label = new Label(37,i,1+"");
				label2 = new Label(43,i,3+"");
			}
			else {
				label = new Label(37,i,0+"");
			}
			sheet.addCell(label);
			if (temp2.indexOf("New")>=0) {
				label = new Label(38,i,1+"");
				label2 = new Label(43,i,4+"");
			}
			else {
				label = new Label(38,i,0+"");
			}
			sheet.addCell(label);
			if (temp2.indexOf("Devout")>=0) {
				label = new Label(39,i,1+"");
				label2 = new Label(43,i,5+"");
			}
			else {
				label = new Label(39,i,0+"");
			}
			sheet.addCell(label);
			if (temp2.indexOf("Dis")>=0) {
				label = new Label(40,i,1+"");
				label2 = new Label(43,i,6+"");
			}
			else {
				label = new Label(40,i,0+"");
			}
			sheet.addCell(label);
			if (temp2.indexOf("Opp")>=0) {
				label = new Label(41,i,1+"");
				label2 = new Label(43,i,7+"");
			}
			else {
				label = new Label(41,i,0+"");
			}
			sheet.addCell(label);
			if (temp2.indexOf("Solid")>=0) {
				label = new Label(42,i,1+"");
				label2 = new Label(43,i,8+"");
			}
			else {
				label = new Label(42,i,0+"");
			}
			sheet.addCell(label);
			//extreme
			if(temp2.indexOf("Solid")>=0||temp2.indexOf("Core")>=0) {
				label3 = new Label(44,i,1+"");
			}
			else {
				label3 = new Label(44,i,0+"");
			} 
			sheet.addCell(label2);
			sheet.addCell(label3);
		}
		out.write();
		out.close();
	}	
}
