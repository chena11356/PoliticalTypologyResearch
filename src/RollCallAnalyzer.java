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

public class RollCallAnalyzer extends Analyzer {
	
	//format of bill info strings
	//billType (hr, s, etc.), billNumber (not index), and 1 or -1 stance (keep consistent)
	//note: have a strict definition of bill relevance and a loose definition and a normal definition and analyze each
	//note: SENATE ROLL CALL VOTES SHOULD BE ANALYZED TOO (like hr.2)
	
	public String[] needyBills = {
			"hr;2;-1", "hr;3762;1", "s;12;1", "hr;24;1", "hr;30;1", "s;30;1", "s;31;-1", "hr;33;1", "s;38;1", "s;123;1", "hr;132;1", "s;141;-1", "hr;143;1", "s;167;-1", "s;203;1", "hr;3381;-1", "hr;842;-1", "hr;775;-1", "hr;928;1", "hr;1624;1", "hr;546;-1", "hr;1342;-1", "hr;2050;-1", "hr;3308;-1", "hr;1516;-1", "hr;2654;-1", "hr;2400;1", "hr;3742;-1", "s;804;-1", "s;1099;1", "s;339;1", "s;522;-1", "s;1012;-1", "s;298;-1", "s;183;1", "s;539;-1", "s;264;1", "s;313;-1", "s;1532;-1", "s;2148;-1", "s;1512;-1", "s;1016;1", "hr;251;-1", "hr;596;1", "s;336;1", "s;857;-1", "s;314;-1", "hr;578;-1"
	};
	public String[] efficiencyBills= {
			"hr;50;-1", "hr;185;-1", "hr;427;-1", "hr;5063;-1", "hr;712;-1", "hr;3438;-1", "hr;5226;-1", "hr;5053;-1", "hr;4885;-1", "hr;4890;-1", "hr;27;-1", "hr;4956;-1", "hr;5499;-1", "hr;304;1", "hr;4585;1", "hr;4730;-1", "hr;3635;1", "hr;2775;1", "s;1944;-1", "s;168;-1", "s;1378;-1", "s;2035;1", "s;280;-1", "hr;598;-1", "hr;1155;-1", "s;1150;-1", "s;226;-1","hr;1732;-1"
	};
	public String[] peaceBills= {
			"hr;3460;-1", "hr;3461;1", "hr;4534;-1", "s;28;1", "hr;1534;1", "s;1789;1", "s;1188;-1", "s;1265;-1"
	};
	public String[] racialBills= {
			"hr;1557;-1", "hr;1933;-1", "s;1056;-1", "hr;4539;-1", "hr;4603;-1", "s;1177;-1", "hres;616;-1", "hr;3231;-1", "hr;2875;-1", "hr;4754;-1", "sres;373;-1", "s;3053;-1", "s;2548;-1", "s;3168;-1"
	};
	public String[] businessBills= {
			"hr;766;-1", "hr;1675;1", "hr;2289;-1", "s;812;1", "hr;4854;1", "hr;37;1", "hr;3791;1", "hr;1210;1", "hr;650;1", "hr;766;1", "hr;4498;1", "hr;6392;1", "hr;2745;1", "hr;5424;1", "hr;3192;1", "hr;2357;1", "hr;685;1", "hr;2896;1", "hr;6100;1", "s;1711;1", "s;1491;1", "s;214;-1", "s;2760;-1"
	};
	public String[] lgbtBills= {
			"hr;1706;-1", "s;2765;-1", "hr;4475;-1", "s;3360;-1", "hr;5373;-1", "hres;561;-1", "s;3134;-1", "hres;549;-1", "sres;511;-1", "hconres;38;-1", "s;302;-1"
	};
	public String[] corporateBills= {
			"hr;2745;1", "s;2102;-1", "hr;4016;1", "hr;5125;-1", "hr;494;-1", "hr;1098;-1", "hr;415;-1", "s;198;-1"
	};
	public String[] environmentBills= {
			"hr;1029;-1", "hr;1030;-1", "hr;2042;-1", "s;330;1", "hr;3797;-1", "hr;4775;-1", "s;1140;-1", "hr;2406;-1", "hr;1732;-1", "hr;348;-1", "hr;4557;-1", "hr;594;-1", "hr;3880;-1", "hr;239;1", "hr;4715;-1", "hr;2494;1", "hr;746;1", "hr;3546;1", "hr;2016;1", "hr;1284;1", "hr;2920;1", "hr;1388;-1", "hr;1548;1", "hr;1482;1", "s;2821;1", "s;2659;-1", "s;751;-1", "s;405;-1", "s;1500;-1", "hres;540;1", "hr;1901;-1"
	};
	public String[] immigrationBills= {
			"hr;213;-1", "hr;3009;1", "hr;4038;1", "s;534;1", "s;2146;1", "s;2193;1", "hr;5207;-1", "hr;3573;1", "hr;4798;-1", "hr;3314;1", "hr;1019;-1", "hr;5654;1", "hr;2922;-1", "hr;4537;1", "hr;3011;1", "hr;2033;-1", "hr;3999;1", "hr;4032;1", "hr;5224;1", "hr;1148;1", "hr;4197;1", "hr;5816;1", "hr;1147;1", "hr;191;1", "hr;2942;1", "hr;1153;1", "s;2337;-1", "s;1300;-1", "s;1032;1", "s;153;-1", "s;686;1", "s;1842;1", "s;3124;1"
	};
	public String[] poorBills= {
			"hr;251;1", "hr;1270;-1", "hr;3700;1", "hr;5587;1", "s;1177;1", "s;2082;1", "hr;5525;-1", "hr;6416;1", "hr;6394;1", "hr;5985;1", "hr;24;-1", "hr;1655;1", "hr;2962;1", "hr;1142;1", "hr;2411;1", "hr;2721;1", "s;522;1", "s;2921;1", "s;1012;1", "s;264;-1", "s;3083;1", "s;1193;1", "s;2677;1", "s;1380;1", "s;1716;1", "s;1833;1", "s;1966;1", "s;2962;1", "s;2292;-1"
	};
	public String[] economicBills= {
			"hr;1260;-1", "hr;2150;-1", "s;1105;1", "hr;3514;-1", "hr;4773;1", "hr;5719;-1", "hr;3222;-1", "hr;612;1", "hr;1655;-1", "hr;3071;-1", "hr;188;-1", "hr;1893;1", "hr;3862;-1", "hr;3164;-1", "hr;2103;-1", "hr;5894;-1", "s;2707;1", "s;1150;-1", "s;1874;-1", "s;2042;-1", "s;391;1", "s;1772;-1", "s;161;-1", "s;2697;-1", "s;1785;1", "s;1127;-1"
	};
	public String[] racialEqBills= {
			"hr;1557;1", "hr;1933;1", "s;1056;1", "hr;4539;1", "hr;4603;1", "s;1177;1", "hres;616;1", "hr;3231;1", "hr;2875;1", "hr;4754;1", "sres;373;1", "s;3053;1", "s;2548;1", "s;3168;1"
	};
	public String[] worldBills= {
			"hr;1150;-1", "hr;1567;-1", "s;1252;-1", "hr;2740;-1", "hr;3706;-1", "hr;4939;-1", "s;269;-1", "hr;2368;-1", "hr;5732;-1", "hr;624;-1", "hr;2847;-1", "hr;5474;-1", "hr;4481;-1", "hr;1111;-1", "hr;1340;-1", "s;553;-1", "s;2645;-1", "s;1911;-1", "s;2551;-1", "s;302;-1", "s;677;-1", "s;2152;-1", "s;1933;-1", "s;1789;-1", "s;452;-1", "s;3256;-1", "s;3106;-1", "s;2231;-1", "s;284;-1", "s;3478;-1"
	};
	public String[] workBills= {
			"hr;251;1", "hr;1270;-1", "hr;3700;1", "hr;5587;1", "s;524;1", "s;1177;1", "hr;3406;1", "hr;1260;1", "hr;2150;1", "s;1105;-1", "s;2042;1", "s;1874;1", "hr;3514;1", "hr;4773;-1", "hr;5719;1", "hr;3222;1", "hr;612;-1", "hr;1655;1", "hr;3071;1", "hr;188;1", "hr;1893;-1", "hr;3862;1", "hr;3164;1", "hr;2103;1", "hr;5894;1", "s;2707;-1", "s;1150;1", "s;391;-1", "s;1772;1", "s;161;1", "s;2697;1", "s;1785;-1", "s;1127;1"
	};
	public String[] genderBills= {
			"hr;1356;1", "hr;4755;1", "hr;5332;1", "hr;5686;1", "hres;364;1", "sres;462;1", "s;2487;1", "s;3147;1", "hr;2100;1", "hr;4603;1", "hres;746;1", "hr;5272;1", "s;3256;1", "s;3053;1", "s;2200;1"
	};
	public String[] compromiseBills= {
			"hr;757;1", "hr;2740;1", "s;2040;1", "hr;4923;1", "hr;4514;1", "hr;664;-1", "hr;1150;1", "hr;5732;1", "hr;825;1", "s;2531;1", "hr;1112;1", "hr;1111;-1", "hr;5094;1", "hr;4927;-1", "s;1238;1", "s;2551;1", "s;302;1", "s;3414;1"
	};
	public String[] gunBills= {
			"hr;224;-1", "hr;2406;1", "s;1473;-1", "hr;4269;-1", "hres;467;-1", "hres;694;-1", "s;405;1", "sres;478;-1", "hr;5671;-1"
	};
	public String[] testBills = {
			"hr;224;-1"
	};
	
	//array of arrays
	public String[][] billList = {needyBills,efficiencyBills,peaceBills,racialBills,businessBills,lgbtBills,corporateBills,
			environmentBills,immigrationBills,poorBills,economicBills,racialEqBills,worldBills,workBills,genderBills,
			compromiseBills,gunBills, testBills
	};
	
	public Integer[][] tempData;
	
	public RollCallAnalyzer() {
		tempData = new Integer[547][18];
	}
	
	/*public void reinitialize(Workbook data, Sheet dataSheet, Workbook workbook, WritableWorkbook out, WritableSheet sheet) throws BiffException, IOException {
		data = Workbook.getWorkbook(new File("memberData.xls"));
		dataSheet = data.getSheet(0);
		workbook = Workbook.getWorkbook(new File("memberData.xls"));
		out = Workbook.createWorkbook(new File("memberData.xls"), workbook);
		sheet = out.getSheet(0);
	}*/
	
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
		String[] curBillList;
		String[] tempArr;
		String[] tempArr2;
		Document dTemp;
		Element eTemp;
		Elements t;
		
		String lastName;
		String firstName;
		String state;
		String rollCallParty;
		
		int tempScoreValue;
		
		Workbook data = Workbook.getWorkbook(new File("memberData.xls"));
		Sheet dataSheet = data.getSheet(0);
		
		Workbook workbook = Workbook.getWorkbook(new File("memberData.xls"));
		out = Workbook.createWorkbook(new File("memberData.xls"), workbook);
		sheet = out.getSheet(0);
		
		//first put all the data from the dataSheet into tempData
		for (int i = 0; i < 547; i++) {
			for (int k = 0; k < 18; k++) {
				tempData[i][k] = Integer.parseInt(dataSheet.getCell(13+k,i).getContents());
			}
		}
		
		for (int i = start-1; i < end; i++) {
			printProgress(start, end, i);
			curBillList = billList[i];
			for (String billInfo : curBillList) {
				//create temporary array of length 3 with billType, billNumber, and 1 or -1 stance (keep consistent)
				tempArr = billInfo.split(";");
				//connect to bill website
				if (tempArr[0].equals("hr"))
					curPage = "https://www.congress.gov/bill/114th-congress/house-bill/"+tempArr[1];
				else if (tempArr[0].equals("s"))
					curPage = "https://www.congress.gov/bill/114th-congress/senate-bill/"+tempArr[1];
				else if (tempArr[0].equals("hres"))
					curPage = "https://www.congress.gov/bill/114th-congress/house-resolution/"+tempArr[1];
				else if (tempArr[0].equals("sres"))
					curPage = "https://www.congress.gov/bill/114th-congress/senate-resolution/"+tempArr[1];
				else if (tempArr[0].equals("hjres"))
					curPage = "https://www.congress.gov/bill/114th-congress/house-joint-resolution/"+tempArr[1];
				else if (tempArr[0].equals("sjres"))
					curPage = "https://www.congress.gov/bill/114th-congress/senate-joint-resolution/"+tempArr[1];
				else if (tempArr[0].equals("hconres"))
					curPage = "https://www.congress.gov/bill/114th-congress/house-concurrent-resolution/"+tempArr[1];
				else if (tempArr[0].equals("sconres"))
					curPage = "https://www.congress.gov/bill/114th-congress/senate-concurrent-resolution/"+tempArr[1];
				else {
					System.out.println("Invalid URL while trying to connect to URL of "+billInfo);
					System.exit(1);
				}
				d = Jsoup.connect(curPage+"/cosponsors").timeout(100000).get();
				
				//get bill sponsors and change score
				if (d.html().indexOf("Sponsor:")>=0) {
					temp = d.html().substring(d.html().indexOf("Sponsor:"));
					temp = temp.substring(temp.indexOf("href"));
					temp = temp.substring(temp.indexOf(">")+6, temp.indexOf("]")+1);
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
					lastName = temp.substring(0,temp.indexOf(",")).trim();
					firstName = temp.substring(temp.indexOf(",")+2,temp.indexOf("[")).trim();
					state = temp.substring(temp.indexOf("["),temp.indexOf("]")).split("-")[1];
					memberIndex = -3;
					//now search for correct member
					for (int w = 0; w < 547; w++){
						if (dataSheet.getCell(0,w).getContents().indexOf(lastName)>=0&&dataSheet.getCell(0,w).getContents().indexOf(firstName)>=0&&stateAbbreviation(dataSheet.getCell(2,w).getContents().trim()).indexOf(state)>=0) {
							memberIndex = w;
							break;
						}	
					}
					if (memberIndex!=-3) {
						//found member, now change their stance score accordingly
						tempScoreValue = tempData[memberIndex][i];
						try {
							tempScoreValue += 3*Integer.parseInt(tempArr[2]);
						}
						catch (ArrayIndexOutOfBoundsException ar) {
							System.out.println("Array index out of bounds as sponsor for member: "+lastName+" "+firstName+" "+state+" as sponsor of bill: "+billInfo);
						}
						tempData[memberIndex][i] = tempScoreValue;
						/*
						label = new Label(13+i,memberIndex,tempScoreValue+"");
						sheet.addCell(label);*/
						/*out.write();
						out.close();
						reinitialize(data, dataSheet, workbook, out, sheet);*/
					}
					else {
						//did not find member, deal with this
						System.out.println("Could not find member: "+lastName+ " "+firstName+" "+state+" as sponsor of bill: "+billInfo);
					}
				}
				
				//then iterate through all the cosponsors of the bill, if any, and change their score
				if (d.html().indexOf("No cosponsors")<0) {
				
					temp = d.getElementById("main").html();
					dTemp = Jsoup.parseBodyFragment(temp); //makes the div part a temp document
					t = dTemp.getElementsByTag("a"); //gives the td elements of the overview
					//each a element's text looks like --> Rep. Fitzpatrick, Michael G. [R-PA-8]&#42
					for (Element legislatorInfo : t) {
						memberIndex = -3;
						temp2 = legislatorInfo.text().trim().substring(5);
						//make sure the jr. and  sr. and iii don't mess things up
						if (temp2.indexOf("Johnson, Henry C")>=0) {
							temp2 = "Johnson, Henry C. \"Hank\" [D-GA-4]";
						}
						if (temp2.indexOf("Rogers, Mike")>=0) {
							temp2 = "Rogers, Mike [R-AL-3]";
						}
						if (temp2.indexOf("Hunter, Duncan")>=0) {
							temp2 = "Hunter, Duncan [R-CA-50]";
						}
						if (temp2.indexOf("Kirk, Mark")>=0) {
							temp2 = "Kirk, Mark [R-IL]";
						}
						if (temp2.indexOf(", Jr.")>=0) {
							temp2 = removeSegment(temp2,", Jr.");
						}
						if (temp2.indexOf(", Sr.")>=0) {
							temp2 = removeSegment(temp2,", Sr.");
						}
						if (temp2.indexOf(", III")>=0) {
							temp2 = removeSegment(temp2,", III");
						}
						try {
							lastName = temp2.substring(0,temp2.indexOf(",")).trim();
						}
						catch (StringIndexOutOfBoundsException st) {
							System.out.println("Could not find last name of "+temp2+" as cosponsor for "+billInfo);
							continue;
						}
						firstName = temp2.substring(temp2.indexOf(",")+2,temp2.indexOf("[")).trim();
						state = temp2.substring(temp2.indexOf("["),temp2.indexOf("]")).split("-")[1];
						//now search for correct member
						for (int w = 0; w < 547; w++){
							if (dataSheet.getCell(0,w).getContents().indexOf(lastName)>=0&&dataSheet.getCell(0,w).getContents().indexOf(firstName)>=0&&stateAbbreviation(dataSheet.getCell(2,w).getContents().trim()).indexOf(state)>=0) {
								memberIndex = w;
								break;
							}	
						}
						if (memberIndex!=-3) {
							//found member, now change their stance score accordingly
							tempScoreValue = tempData[memberIndex][i];
							try {
								tempScoreValue += 2*Integer.parseInt(tempArr[2]);
							}
							catch (ArrayIndexOutOfBoundsException ar) {
								System.out.println("Array index out of bounds as cosponsor for member: "+lastName+" "+firstName+" "+state+" as sponsor of bill: "+billInfo);
							}
							tempData[memberIndex][i] = tempScoreValue;
							/*
							label = new Label(13+i,memberIndex,tempScoreValue+"");
							sheet.addCell(label);*/
							/*out.write();
							out.close();
							reinitialize(data, dataSheet, workbook, out, sheet);*/
						}
						else {
							//did not find member, deal with this
							System.out.println("Could not find member: "+legislatorInfo.text()+" as cosponsor of bill: "+billInfo);
						}
					}
				}
				
				//now iterate through all the roll call votes, if any, and change scores
				d = Jsoup.connect(curPage+"/actions").timeout(100000).get();
				temp = d.html();
				if (temp.indexOf("Roll no.")>=0) {
					temp = temp.substring(temp.indexOf("http://clerk.house.gov/evs"));
					temp = temp.substring(0, temp.indexOf(".xml")+4);
					//so now temp is the url for the roll call votes
					dd = Jsoup.connect(temp).parser(Parser.xmlParser()).get();
					t = dd.getElementsByTag("recorded-vote");
					for (Element vote : t) {
						temp2 = vote.html();
						if (temp2.indexOf("uac")>=0) {
							temp2 = temp2.substring(temp2.indexOf("uac"));
						}
						else {
							if (temp2.indexOf(" un")==-1) {
								System.out.println("Roll call vote for "+billInfo+" failed to contain unaccented: "+temp2);
								continue;
							}
							temp2 = temp2.substring(temp2.indexOf(" un")+1);
						}
						temp2 = temp2.substring(temp2.indexOf("=")+2);
						if (temp2.indexOf("par")==-1) {
							System.out.println("Roll call vote for "+billInfo+" failed to contain party: "+temp2);
							continue;
						}
						tempName = temp2.substring(0,temp2.indexOf("par")-2); 
						if (tempName.indexOf("(")>=0)
							tempName = tempName.substring(0,tempName.indexOf("(")-1);
						if (tempName.indexOf(",")>=0) {
							lastName = tempName.substring(0,tempName.indexOf(",")).trim();
							firstName = tempName.substring(tempName.indexOf(",")+1).trim();
						}
						else {
							lastName = tempName;
							firstName = "N/A";
						}
						//E. B. Johnson is Eddie Bernice Johnson
						if (firstName.indexOf("E. B.")>=0) {
							firstName = "Eddie";
							lastName = "Johnson";
						}
						if (temp.indexOf("sate")>=0) {
							temp2 = temp2.substring(temp2.indexOf("sate"));
						}
						else {
							if (temp2.indexOf(" sta")==-1) {
								System.out.println("Roll call vote for "+billInfo+" failed to contain state: "+temp2);
								continue;
							}
							temp2 = temp2.substring(temp2.indexOf(" sta")+1);
						}
						if (temp2.indexOf("=")>=0&&temp.indexOf("roe")>=0) {
							state = temp2.substring(temp2.indexOf("=")+2,temp2.indexOf("roe")-2);
						}
						else {
							if (temp2.indexOf("=")==-1||temp2.indexOf("role")==-1) {
								System.out.println("Roll call vote for "+billInfo+" failed to contain = or role: "+temp2);
								continue;
							}
							state = temp2.substring(temp2.indexOf("=")+2,temp2.indexOf("role")-2);
						}
						try {
							tempVote = temp2.substring(temp2.indexOf("<v")+6,temp2.indexOf("</v"));
						}
						catch(StringIndexOutOfBoundsException se) {
							System.out.println("Couldn't find <vote> and </vote> for "+billInfo+": "+temp2);
							continue;
						}
						//now search for correct member
						memberIndex = -3;
						for (int w = 0; w < 547; w++){
							if (firstName.equals("N/A")) {
								if (dataSheet.getCell(0,w).getContents().indexOf(lastName)>=0&&stateAbbreviation(dataSheet.getCell(2,w).getContents().trim()).indexOf(state)>=0) {
									memberIndex = w;
									break;
								}	
							}
							else {
								if (dataSheet.getCell(0,w).getContents().indexOf(lastName)>=0&&dataSheet.getCell(0,w).getContents().indexOf(firstName)>=0&&stateAbbreviation(dataSheet.getCell(2,w).getContents().trim()).indexOf(state)>=0) {
									memberIndex = w;
									break;
								}	
							}
						}
						if (memberIndex!=-3) {
							//found member, now change their stance score accordingly
							tempScoreValue = tempData[memberIndex][i];
							if (tempVote.indexOf("Aye")>=0||tempVote.indexOf("Yes")>=0||tempVote.indexOf("Yea")>=0)
								tempScoreValue += Integer.parseInt(tempArr[2]);
							if (tempVote.indexOf("No")>=0||tempVote.indexOf("Nay")>=0)
								tempScoreValue -= Integer.parseInt(tempArr[2]);
							tempData[memberIndex][i] = tempScoreValue;
							/*
							label = new Label(13+i,memberIndex,tempScoreValue+"");
							sheet.addCell(label);*/
							//reinitialize(data, dataSheet, workbook, out, sheet);
						}
						else {
							//did not find member, deal with this
							System.out.println("Could not find member: "+firstName+" "+lastName+" "+state);
							System.out.println("As house roll call vote of bill: "+billInfo+" "+tempVote);
						}
					}
				}
				temp = d.html();
				if (temp.indexOf("http://www.senate.gov/legislative/LIS/roll_call_lists")>=0) {
					temp = temp.substring(temp.indexOf("http://www.senate.gov/legislative/LIS/roll_call_lists"));
					temp = temp.substring(0, temp.indexOf(">")-1);
					while (temp.indexOf("amp;")>=0) {
						temp = removeSegment(temp,"amp;");
					}
					//so now temp is the url for the roll call votes
					dd = Jsoup.connect(temp).timeout(100000).get();
					temp = dd.html();
					temp = temp.substring(temp.indexOf("YEAs ---"));
					temp = temp.substring(temp.indexOf("contenttext")+13);
					temp = temp.substring(0,temp.indexOf("span"));
					temp = temp.substring(0,temp.lastIndexOf("<br>"));
					//now temp is all the yea votes, separated by <br>s
					tempArr2 = temp.split("<br>");
					for (int m = 0; m < tempArr2.length; m++) {
						tempName = tempArr2[m];
						lastName = tempName.substring(0,tempName.indexOf("(")-1);
						tempName = tempName.substring(tempName.indexOf("(")+1);
						rollCallParty = tempName.substring(0,tempName.indexOf("-"));
						if (rollCallParty.indexOf("D")>=0) {
							rollCallParty = "Dem";
						}
						else if (rollCallParty.indexOf("R")>=0) {
							rollCallParty = "Rep";
						}
						else {
							rollCallParty = "Ind";
						}
						tempName = tempName.substring(tempName.indexOf("-")+1);
						state = tempName.substring(0,tempName.indexOf(")"));
						tempVote = "Yea";
						
						//now search for correct member
						memberIndex = -4;
						for (int w = 0; w < 547; w++){
							if (dataSheet.getCell(0,w).getContents().indexOf(lastName)>=0&&stateAbbreviation(dataSheet.getCell(2,w).getContents().trim()).indexOf(state)>=0&&dataSheet.getCell(11,w).getContents().indexOf("S")>=0&&dataSheet.getCell(1,w).getContents().indexOf(rollCallParty)>=0) {
								memberIndex = w;
								break;
							}	
						}
						if (memberIndex!=-4) {
							//found member, now change their stance score accordingly
							tempScoreValue = tempData[memberIndex][i];
							//if (tempVote.indexOf("Aye")>=0||tempVote.indexOf("Yes")>=0||tempVote.indexOf("Yea")>=0)
							tempScoreValue += Integer.parseInt(tempArr[2]);
							tempData[memberIndex][i] = tempScoreValue;
							/*
							if (tempVote.indexOf("No")>=0||tempVote.indexOf("Nay")>=0)
								tempScoreValue -= Integer.parseInt(tempArr[2]);*/
							/*
							label = new Label(13+i,memberIndex,tempScoreValue+"");
							sheet.addCell(label);*/
							//reinitialize(data, dataSheet, workbook, out, sheet);
						}
						else {
							//did not find member, deal with this
							System.out.println("Could not find member: "+lastName+" "+rollCallParty+" "+state);
							System.out.println("As house roll call yea vote of bill: "+billInfo);
						}
					}
					
					//now go through the nay votes
					temp = dd.html();
					try {
						temp = temp.substring(temp.indexOf("NAYs ---"));
					}
					catch (StringIndexOutOfBoundsException st) {
						System.out.println("No nay votes for senate for bill: "+billInfo);
						continue;
					}
					temp = temp.substring(temp.indexOf("contenttext")+13);
					temp = temp.substring(0,temp.indexOf("span"));
					temp = temp.substring(0,temp.lastIndexOf("<br>"));
					//now temp is all the nay votes, separated by <br>s
					tempArr2 = temp.split("<br>");
					for (int m = 0; m < tempArr2.length; m++) {
						tempName = tempArr2[m];
						lastName = tempName.substring(0,tempName.indexOf("(")-1);
						tempName = tempName.substring(tempName.indexOf("(")+1);
						rollCallParty = tempName.substring(0,tempName.indexOf("-"));
						if (rollCallParty.indexOf("D")>=0) {
							rollCallParty = "Dem";
						}
						else if (rollCallParty.indexOf("R")>=0) {
							rollCallParty = "Rep";
						}
						else {
							rollCallParty = "Ind";
						}
						tempName = tempName.substring(tempName.indexOf("-")+1);
						state = tempName.substring(0,tempName.indexOf(")"));
						tempVote = "Nay";
						
						//now search for correct member
						memberIndex = -4;
						for (int w = 0; w < 547; w++){
							if (dataSheet.getCell(0,w).getContents().indexOf(lastName)>=0&&stateAbbreviation(dataSheet.getCell(2,w).getContents().trim()).indexOf(state)>=0&&dataSheet.getCell(11,w).getContents().indexOf("S")>=0&&dataSheet.getCell(1,w).getContents().indexOf(rollCallParty)>=0) {
								memberIndex = w;
								break;
							}	
						}
						if (memberIndex!=-4) {
							//found member, now change their stance score accordingly
							tempScoreValue = tempData[memberIndex][i];
							//if (tempVote.indexOf("No")>=0||tempVote.indexOf("Nay")>=0)
							tempScoreValue -= Integer.parseInt(tempArr[2]);
							tempData[memberIndex][i] = tempScoreValue;
							/*
							label = new Label(13+i,memberIndex,tempScoreValue+"");
							sheet.addCell(label);*/
							//reinitialize(data, dataSheet, workbook, out, sheet);
						}
						else {
							//did not find member, deal with this
							System.out.println("Could not find member: "+lastName+" "+rollCallParty+" "+state);
							System.out.println("As house roll call nay vote of bill: "+billInfo);
						}
					}
					
				}
			}
			
			
			
		}
		for (int i = 0; i < 547; i++) {
			for (int k = 0; k < 18; k++) {
				label = new Label(13+k,i,tempData[i][k]+"");
				sheet.addCell(label);
			}
		}
		out.write();
		out.close();
	}	
}
