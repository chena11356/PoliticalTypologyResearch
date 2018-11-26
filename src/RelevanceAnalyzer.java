import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

//traverses through mainData to create billsByTopic

public class RelevanceAnalyzer extends Analyzer {
	
	//so first, create arrayLists to hold 
	//bill indices of bills pertaining to a specific social issue 
	//AND the number of roll call votes of the bill
	//AND the number of cosponsors of the bill
	
	//then these will be added to the billsByTopic.xls spreadsheet, with each tab being an issue
	//and sorted from most roll call votes to least roll call votes
	
	//needy - 101, 103, 105, 110, 13, etc.
	public ArrayList<String> needyBills;
	//government efficiency - 20
	public ArrayList<String> efficiencyBills;
	//best way to ensure peace - 1600, 1602, 1604, 1605, 1929
	public ArrayList<String> peaceBills;
	//power of racial discrimination - 200, 201, keywords {race,ethnic,racial}
	public ArrayList<String> racialBills;
	//business regulation - 1500, 1501, 1502, 1504, 1505, 1507, 1520, 1521
	public ArrayList<String> businessBills;
	//lgbt rights - 202, keywords (or) {sexual orientation, lgbt, lesbian, gay, bisexual, transgender}
	public ArrayList<String> lgbtBills;
	//corporate profit - 1520
	public ArrayList<String> corporateBills;
	//environmental regulations - 7
	public ArrayList<String> environmentBills;
	//immigrants - 900
	public ArrayList<String> immigrationBills;
	//benefits to the poor - 1302, 101, 103, 1406
	public ArrayList<String> poorBills;
	//economic inequality - 107, 110, 5
	public ArrayList<String> economicBills;
	//changes for racial equality - 200, 201, keywords {race,ethnic,racial}
	public ArrayList<String> racialEqBills;
	//domestic or foreign focus - 1602, 1606, 18, 19
	public ArrayList<String> worldBills;
	//hard work - 1302, 101, 103, 1406, keywords {hard work, determination}
	public ArrayList<String> workBills;
	//gender discrimination - 202, keywords {gender, female, women}
	public ArrayList<String> genderBills;
	//compromise with foreign allies - 1602, 18, 19
	public ArrayList<String> compromiseBills;
	//guns - keywords {gun, firearm}
	public ArrayList<String> gunBills;
	
	//array of arrayList to easily iterate through these
	public ArrayList<ArrayList<String>> billList;
	
	public RelevanceAnalyzer() {
		needyBills = new ArrayList<String>();
		efficiencyBills = new ArrayList<String>();
		peaceBills = new ArrayList<String>();
		racialBills = new ArrayList<String>();
		businessBills = new ArrayList<String>();
		lgbtBills = new ArrayList<String>();
		corporateBills = new ArrayList<String>();
		environmentBills = new ArrayList<String>();
		immigrationBills = new ArrayList<String>();
		poorBills = new ArrayList<String>();
		economicBills = new ArrayList<String>();
		racialEqBills = new ArrayList<String>();
		worldBills = new ArrayList<String>();
		workBills = new ArrayList<String>();
		genderBills = new ArrayList<String>();
		compromiseBills = new ArrayList<String>();
		gunBills = new ArrayList<String>();
		billList = new ArrayList<ArrayList<String>>();
		billList.add(needyBills);
		billList.add(efficiencyBills);
		billList.add(peaceBills);
		billList.add(racialBills);
		billList.add(businessBills);
		billList.add(lgbtBills);
		billList.add(corporateBills);
		billList.add(environmentBills);
		billList.add(immigrationBills);
		billList.add(poorBills);
		billList.add(economicBills);
		billList.add(racialEqBills);
		billList.add(worldBills);
		billList.add(workBills);
		billList.add(genderBills);
		billList.add(compromiseBills);
		billList.add(gunBills);
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
		else if (index<11866)
			return "hconres";
		else if (index<11924)
			return "sconres";
		else if (index<12032)
			return "hjres";
		else if (index<12073)
			return "sjres";
		else
			return "invalid input";
	}
		
	public void go (int start, int end) throws IOException, RowsExceededException, WriteException, BiffException {
		WritableWorkbook out; //excel file output
		WritableSheet curSheet;
		Label label; //excel cell output
		String temp = ""; //for major topic
		String temp2 = ""; //for minor topic
		String temp3 = ""; //for storing bills as strings into arrayList
		int y;
		String[] tempArr;
		
		ArrayList<String> curBillList;
		
		
		Workbook workbook = Workbook.getWorkbook(new File("billsByTopic.xls"));
		out = Workbook.createWorkbook(new File("billsByTopic.xls"), workbook);
		WritableSheet needySheet = out.getSheet(0);
		WritableSheet efficiencySheet = out.getSheet(1);
		WritableSheet peaceSheet = out.getSheet(2);
		WritableSheet racialSheet = out.getSheet(3);
		WritableSheet businessSheet = out.getSheet(4);
		WritableSheet lgbtSheet = out.getSheet(5);
		WritableSheet corporateSheet = out.getSheet(6);
		WritableSheet environmentSheet = out.getSheet(7);
		WritableSheet immigrationSheet = out.getSheet(8);
		WritableSheet poorSheet = out.getSheet(9);
		WritableSheet economicSheet = out.getSheet(10);
		WritableSheet racialEqSheet = out.getSheet(11);
		WritableSheet worldSheet = out.getSheet(12);
		WritableSheet workSheet = out.getSheet(13);
		WritableSheet genderSheet = out.getSheet(14);
		WritableSheet compromiseSheet = out.getSheet(15);
		WritableSheet gunSheet = out.getSheet(16);
		
		Workbook outsideData = Workbook.getWorkbook(new File("mainData.xls"));
		Sheet dataSheet = outsideData.getSheet(0);
		
		WritableSheet[] sheetList = {needySheet,efficiencySheet,peaceSheet,racialSheet,businessSheet,lgbtSheet,corporateSheet,
				environmentSheet,immigrationSheet,poorSheet,economicSheet,racialEqSheet,worldSheet,workSheet,
				genderSheet,compromiseSheet,gunSheet};
		
		//build up the curBillList by iterating through the data
		for (int i = start-1; i<end; i++) {
			temp = dataSheet.getCell(15,i).getContents().trim(); //get the major bill topic
			temp2 = dataSheet.getCell(16,i).getContents().trim(); //get the minor bill topic
			temp3 = i+";"+dataSheet.getCell(8,i).getContents().trim()+";"+dataSheet.getCell(9,i).getContents().trim();
			
			if (temp.equals("13")||temp2.equals("101")||temp2.equals("103")||temp2.equals("105")||temp2.equals("110")||temp2.equals("301")||temp2.equals("302")||temp2.equals("323")||temp2.equals("332")||temp2.equals("334")||temp2.equals("335")||temp2.equals("1406")||temp2.equals("1407")||temp2.equals("1408")||temp2.equals("1409"))
				needyBills.add(temp3);
			if (temp.equals("20"))
				efficiencyBills.add(temp3);
			if (temp2.equals("1600")||temp2.equals("1602")||temp2.equals("1604")||temp2.equals("1605")||temp2.equals("1929"))
				peaceBills.add(temp3);
			if ((dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("race")>=0||dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("racial")>=0||dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("ethnic")>=0)) {
				racialBills.add(temp3);
				racialEqBills.add(temp3);
			}	
			if (temp2.equals("1500")||temp2.equals("1501")||temp2.equals("1502")||temp2.equals("1504")||temp2.equals("1505")||temp2.equals("1507")||temp2.equals("1520")||temp2.equals("1521"))
				businessBills.add(temp3);
			if ((dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("lgbt")>=0||dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("gay")>=0||dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("bisexual")>=0||dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("transgender")>=0))
				lgbtBills.add(temp3);
			if (temp2.equals("1520"))
				corporateBills.add(temp3);
			if (temp.equals("7"))
				environmentBills.add(temp3);
			if (temp2.equals("900"))
				immigrationBills.add(temp3);
			if (temp2.equals("1302")||temp2.equals("101")||temp2.equals("103")||temp2.equals("1406")||dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("low-income")>=0||dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("poor")>=0||dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("poverty")>=0)
				poorBills.add(temp3);
				workBills.add(temp3);
			if (temp.equals("5")||temp2.equals("107")||temp2.equals("110"))
				economicBills.add(temp3);
			if (temp2.equals("1602")||temp2.equals("1606")||temp.equals("18")||temp.equals("19"))
				worldBills.add(temp3);
			if (dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("hard work")>=0||dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("determination")>=0)
				workBills.add(temp3);
			if (dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("gender")>=0||dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("female")>=0)
				genderBills.add(temp3);
			if (temp2.equals("1602")||temp.equals("18")||temp.equals("19"))
				compromiseBills.add(temp3);
			if (dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("gun")>=0||dataSheet.getCell(17,i).getContents().toLowerCase().indexOf("firearm")>=0)
				gunBills.add(temp3);
		}
		
		
		//then write everything into the curSheet from the curBillList
		int r = 0;
		for (int i = 0; i < 17; i++) {
			r = 0;
			curSheet = sheetList[i];
			curBillList = billList.get(i);
			for (int k = 0; k < curBillList.size(); k++) {
				temp = curBillList.get(k);
				tempArr = temp.split(";");
				if (tempArr.length<3)
					continue;
				y = Integer.parseInt(tempArr[0]);
				label = new Label(0,r,tempArr[0]);
				curSheet.addCell(label);
				label = new Label(1,r,tempArr[1]);
				curSheet.addCell(label);
				label = new Label(2,r,tempArr[2]);
				curSheet.addCell(label);
				temp = findBillType(y);
				label = new Label(3,r,temp);
				curSheet.addCell(label);
				if (temp.equals("s"))
					y = y-6536;
				else if (temp.equals("hres"))
					y = y-10084;
				else if (temp.equals("sres"))
					y = y-11041;
				else if (temp.equals("hconres"))
					y = y-11683;
				else if (temp.equals("sconres"))
					y = y-11866;
				else if (temp.equals("hjres"))
					y = y-11924;
				else if (temp.equals("sjres"))
					y = y-12032;
				y++;
				label = new Label(4,r,y+"");
				curSheet.addCell(label);
				r++;
			}
		
		}
		
		out.write();
		out.close();
	}	
}	
