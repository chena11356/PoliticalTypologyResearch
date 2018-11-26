import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Analyzer {
	
	//removes segment from string
	public String removeSegment(String input, String toRemove) {
		int i1 = input.indexOf(toRemove);
		int i2 = i1 + toRemove.length();
		return input.substring(0, i1)+input.substring(i2);
	}
	
	//turns parties into abbreviation
	public String partyAbbreviation(String input) {
		switch (input) {
		case "Democrat":
			return "D";
		case "Republican":
			return "R";
		case "Independent":
			return "I";
		default:
			return input;
		}
	}
	
	//turns full state name into abbreviation
	public String stateAbbreviation(String input) {
		switch (input) {
		case "Alabama":
			return "AL";
		case "Alaska":
			return "AK";
		case "American Samoa":
			return "AS";
		case "Arizona":
			return "AZ";
		case "Arkansas":
			return "AR";
		case "California":
			return "CA";
		case "Colorado":
			return "CO";
		case "Connecticut":
			return "CT";
		case "Delaware":
			return "DE";
		case "District of Columbia":
			return "DC";
		case "Florida":
			return "FL";
		case "Georgia":
			return "GA";
		case "Guam":
			return "GU";
		case "Hawaii":
			return "HI";
		case "Idaho":
			return "ID";
		case "Illinois":
			return "IL";
		case "Indiana":
			return "IN";
		case "Iowa":
			return "IA";
		case "Kansas":
			return "KS";
		case "Kentucky":
			return "KY";
		case "Louisiana":
			return "LA";
		case "Maine":
			return "ME";
		case "Maryland":
			return "MD";
		case "Marshall Islands":
			return "MH";
		case "Massachusetts":
			return "MA";
		case "Michigan":
			return "MI";
		case "Micronesia":
			return "FM";
		case "Minnesota":
			return "MN";
		case "Mississippi":
			return "MS";
		case "Missouri":
			return "MO";
		case "Montana":
			return "MT";
		case "Nebraska":
			return "NE";
		case "Nevada":
			return "NV";
		case "New Hampshire":
			return "NH";
		case "New Jersey":
			return "NJ";
		case "New Mexico":
			return "NM";
		case "New York":
			return "NY";
		case "North Carolina":
			return "NC";
		case "North Dakota":
			return "ND";
		case "Northern Marianas":
			return "MP";
		case "Ohio":
			return "OH";
		case "Oklahoma":
			return "OK";
		case "Oregon":
			return "OR";
		case "Palau":
			return "PW";
		case "Pennsylvania":
			return "PA";
		case "Puerto Rico":
			return "PR";
		case "Rhode Island":
			return "RI";
		case "South Carolina":
			return "SC";
		case "South Dakota":
			return "SD";
		case "Tennessee":
			return "TN";
		case "Texas":
			return "TX";
		case "Utah":
			return "UT";
		case "Vermont":
			return "VT";
		case "Virginia":
			return "VA";
		case "Virgin Islands":
			return "VI";
		case "Washington":
			return "WA";
		case "West Virginia":
			return "WV";
		case "Wisconsin":
			return "WI";
		case "Wyoming":
			return "WY";
		default:
			return input;
		}
	}
	
	//finds member's typology given scores
	public String findTypology(int a, int b, int c, int d, int e, int f, int g, int h, int i, int j, int k, int l, int m, int n, int o, int p, String party) {
		//note: assumes no legislators are bystanders
		//democrat typologies
		double solidLiberal = 0.01;
		double opportunityDemocrat = 0.01;
		double disaffectedDemocrat = 0.01;
		double devoutAndDiverse = 0.01;
		//republican typologies
		double newEraEnterpriser = 0.01;
		double marketSkepticRepublican = 0.01;
		double countryFirstConservative = 0.01;
		double coreConservative = 0.01;
		
		//a: more to help needy vs. can't afford to help needy
		if (a<0) {
			solidLiberal *= 85;
			opportunityDemocrat *= 62;
			disaffectedDemocrat *= 72;
			devoutAndDiverse *= 62;
			newEraEnterpriser *= 31;
			marketSkepticRepublican *= 32;
			countryFirstConservative *= 19;
			coreConservative *= 10;
		}
		if (a>0) {
			solidLiberal *= 12;
			opportunityDemocrat *= 33;
			disaffectedDemocrat *= 22;
			devoutAndDiverse *= 34;
			newEraEnterpriser *= 59;
			marketSkepticRepublican *= 58;
			countryFirstConservative *= 70;
			coreConservative *= 83;
		}
		
		
		//b: government wasteful vs. government good
		if (b<0) {
			solidLiberal *= 30;
			opportunityDemocrat *= 41;
			disaffectedDemocrat *= 63;
			devoutAndDiverse *= 49;
			newEraEnterpriser *= 49;
			marketSkepticRepublican *= 69;
			countryFirstConservative *= 71;
			coreConservative *= 89;
		}
		if (b>0) {
			solidLiberal *= 66;
			opportunityDemocrat *= 57;
			disaffectedDemocrat *= 32;
			devoutAndDiverse *= 47;
			newEraEnterpriser *= 47;
			marketSkepticRepublican *= 29;
			countryFirstConservative *= 21;
			coreConservative *= 8;
		}
		
		
		//c: military vs. diplomacy
		if (c<0) {
			solidLiberal *= 4;
			opportunityDemocrat *= 15;
			disaffectedDemocrat *= 14;
			devoutAndDiverse *= 32;
			newEraEnterpriser *= 41;
			marketSkepticRepublican *= 42;
			countryFirstConservative *= 54;
			coreConservative *= 71;
		}
		if (c>0) {
			solidLiberal *= 95;
			opportunityDemocrat *= 80;
			disaffectedDemocrat *= 82;
			devoutAndDiverse *= 66;
			newEraEnterpriser *= 49;
			marketSkepticRepublican *= 42;
			countryFirstConservative *= 25;
			coreConservative *= 13;
		}
		
		/*
		//d: discrimination strong vs. discrimination weak
		if (d<0) {
			solidLiberal *= 91;
			opportunityDemocrat *= 54;
			disaffectedDemocrat *= 63;
			devoutAndDiverse *= 41;
			newEraEnterpriser *= 17;
			marketSkepticRepublican *= 12;
			countryFirstConservative *= 9;
			coreConservative *= 5;
		}
		if (d>0) {
			solidLiberal *= 7;
			opportunityDemocrat *= 37;
			disaffectedDemocrat *= 28;
			devoutAndDiverse *= 47;
			newEraEnterpriser *= 73;
			marketSkepticRepublican *= 75;
			countryFirstConservative *= 76;
			coreConservative *= 80;
		}
		*/
		
		//e: regulation good vs. regulation bad
		if (e<0) {
			solidLiberal *= 96;
			opportunityDemocrat *= 76;
			disaffectedDemocrat *= 39;
			devoutAndDiverse *= 38;
			newEraEnterpriser *= 57;
			marketSkepticRepublican *= 41;
			countryFirstConservative *= 15;
			coreConservative *= 4;
		}
		if (e>0) {
			solidLiberal *= 4;
			opportunityDemocrat *= 21;
			disaffectedDemocrat *= 54;
			devoutAndDiverse *= 55;
			newEraEnterpriser *= 37;
			marketSkepticRepublican *= 53;
			countryFirstConservative *= 73;
			coreConservative *= 89;
		}
		
		
		//f: homosexuality ok vs. homosexuality bad
		if (f<0) {
			solidLiberal *= 99;
			opportunityDemocrat *= 92;
			disaffectedDemocrat *= 79;
			devoutAndDiverse *= 53;
			newEraEnterpriser *= 64;
			marketSkepticRepublican *= 62;
			countryFirstConservative *= 13;
			coreConservative *= 50;
		}
		if (f>0) {
			opportunityDemocrat *= 7;
			disaffectedDemocrat *= 15;
			devoutAndDiverse *= 37;
			newEraEnterpriser *= 28;
			marketSkepticRepublican *= 31;
			countryFirstConservative *= 70;
			coreConservative *= 37;
		}
		
		
		//g: too much profit vs. ok profit
		if (g<0) {
			solidLiberal *= 82;
			opportunityDemocrat *= 55;
			disaffectedDemocrat *= 95;
			devoutAndDiverse *= 50;
			newEraEnterpriser *= 24;
			marketSkepticRepublican *= 89;
			countryFirstConservative *= 47;
			coreConservative *= 10;
		}
		if (g>0) {
			solidLiberal *= 16;
			opportunityDemocrat *= 40;
			disaffectedDemocrat *= 4;
			devoutAndDiverse *= 43;
			newEraEnterpriser *= 68;
			marketSkepticRepublican *= 10;
			countryFirstConservative *= 42;
			coreConservative *= 82;
		}
		
		
		//h: regulation bad vs. regulation good
		if (h<0) {
			solidLiberal *= 0;
			opportunityDemocrat *= 20;
			disaffectedDemocrat *= 28;
			devoutAndDiverse *= 45;
			newEraEnterpriser *= 34;
			marketSkepticRepublican *= 39;
			countryFirstConservative *= 70;
			coreConservative *= 92;
		}
		if (h>0) {
			solidLiberal *= 100;
			opportunityDemocrat *= 79;
			disaffectedDemocrat *= 69;
			devoutAndDiverse *= 48;
			newEraEnterpriser *= 60;
			marketSkepticRepublican *= 57;
			countryFirstConservative *= 17;
			coreConservative *= 1;
		}
		
		
		//i: immigrants good vs. immigrants bad
		if (i<0) {
			solidLiberal *= 99;
			opportunityDemocrat *= 99;
			disaffectedDemocrat *= 83;
			devoutAndDiverse *= 47;
			newEraEnterpriser *= 70;
			marketSkepticRepublican *= 29;
			coreConservative *= 39;
		}
		if (i>0) {
			disaffectedDemocrat *= 11;
			devoutAndDiverse *= 44;
			newEraEnterpriser *= 23;
			marketSkepticRepublican *= 55;
			countryFirstConservative *= 76;
			coreConservative *= 43;
		}
		
		/*
		//j: poor easy vs. poor hard
		if (j<0) {
			opportunityDemocrat *= 28;
			disaffectedDemocrat *= 16;
			devoutAndDiverse *= 10;
			newEraEnterpriser *= 61;
			marketSkepticRepublican *= 75;
			countryFirstConservative *= 58;
			coreConservative *= 83;
		}
		if (j>0) {
			solidLiberal *= 98;
			opportunityDemocrat *= 61;
			disaffectedDemocrat *= 79;
			devoutAndDiverse *= 86;
			newEraEnterpriser *= 23;
			marketSkepticRepublican *= 14;
			countryFirstConservative *= 23;
			coreConservative *= 3;
		}
		*/
		
		//k: economy unfair vs. economy fair
		if (k<0) {
			solidLiberal *= 99;
			opportunityDemocrat *= 67;
			disaffectedDemocrat *= 99;
			devoutAndDiverse *= 55;
			newEraEnterpriser *= 18;
			marketSkepticRepublican *= 94;
			countryFirstConservative *= 41;
			coreConservative *= 21;
		}
		if (k>0) {
			opportunityDemocrat *= 32;
			devoutAndDiverse *= 43;
			newEraEnterpriser *= 75;
			marketSkepticRepublican *= 5;
			countryFirstConservative *= 48;
			coreConservative *= 75;
		}
		
		/*
		//l: equal rights already vs. not yet
		if (l<0) {
			solidLiberal *= 2;
			opportunityDemocrat *= 31;
			disaffectedDemocrat *= 7;
			devoutAndDiverse *= 8;
			newEraEnterpriser *= 48;
			marketSkepticRepublican *= 61;
			countryFirstConservative *= 66;
			coreConservative *= 81;
		}
		if (l>0) {
			solidLiberal *= 98;
			opportunityDemocrat *= 67;
			disaffectedDemocrat *= 92;
			devoutAndDiverse *= 89;
			newEraEnterpriser *= 43;
			marketSkepticRepublican *= 32;
			countryFirstConservative *= 25;
			coreConservative *= 12;
		}
		*/
		
		/*
		//m: foreign vs. domestic
		if (m<0) {
			solidLiberal *= 87;
			opportunityDemocrat *= 76;
			disaffectedDemocrat *= 30;
			devoutAndDiverse *= 22;
			newEraEnterpriser *= 45;
			marketSkepticRepublican *= 22;
			countryFirstConservative *= 19;
			coreConservative *= 50;
		}
		if (m>0) {
			solidLiberal *= 10;
			opportunityDemocrat *= 20;
			disaffectedDemocrat *= 63;
			devoutAndDiverse *= 69;
			newEraEnterpriser *= 48;
			marketSkepticRepublican *= 72;
			countryFirstConservative *= 66;
			coreConservative *= 44;
		}
		*/
		
		/*
		//n: hard work is enough vs. not enough
		if (n<0) {
			solidLiberal *= 25;
			opportunityDemocrat *= 76;
			disaffectedDemocrat *= 43;
			devoutAndDiverse *= 47;
			newEraEnterpriser *= 90;
			marketSkepticRepublican *= 65;
			countryFirstConservative *= 57;
			coreConservative *= 94;
		}
		if (n>0) {
			solidLiberal *= 73;
			opportunityDemocrat *= 22;
			disaffectedDemocrat *= 54;
			devoutAndDiverse *= 48;
			newEraEnterpriser *= 8;
			marketSkepticRepublican *= 34;
			countryFirstConservative *= 36;
			coreConservative *= 4;
		}
		*/
		
		/*
		//o: equal rights already vs. not yet
		if (o<0) {
			solidLiberal *= 3;
			opportunityDemocrat *= 40;
			disaffectedDemocrat *= 17;
			devoutAndDiverse *= 32;
			newEraEnterpriser *= 69;
			marketSkepticRepublican *= 57;
			countryFirstConservative *= 49;
			coreConservative *= 90;
		}
		if (o>0) {
			solidLiberal *= 97;
			opportunityDemocrat *= 57;
			disaffectedDemocrat *= 82;
			devoutAndDiverse *= 64;
			newEraEnterpriser *= 24;
			marketSkepticRepublican *= 40;
			countryFirstConservative *= 43;
			coreConservative *= 8;
		}
		*/
		
		/*
		//p: compromise good vs. compromise bad
		if (p<0) {
			solidLiberal *= 97;
			opportunityDemocrat *= 94;
			disaffectedDemocrat *= 61;
			devoutAndDiverse *= 26;
			newEraEnterpriser *= 56;
			marketSkepticRepublican *= 49;
			countryFirstConservative *= 9;
			coreConservative *= 30;
		}
		if (p>0) {
			solidLiberal *= 2;
			opportunityDemocrat *= 5;
			disaffectedDemocrat *= 35;
			devoutAndDiverse *= 64;
			newEraEnterpriser *= 37;
			marketSkepticRepublican *= 47;
			countryFirstConservative *= 76;
			coreConservative *= 64;
		}
		*/
		
		if (party.indexOf("Dem")>=0) {
			solidLiberal *= 99;
			opportunityDemocrat *= 79;
			disaffectedDemocrat *= 85;
			devoutAndDiverse *= 59;
			newEraEnterpriser *= 22;
			marketSkepticRepublican *= 12;
			countryFirstConservative *= 0;
			coreConservative *= 0;
		}
		if (party.indexOf("Rep")>=0) {
			solidLiberal *= 0;
			opportunityDemocrat *= 15;
			disaffectedDemocrat *= 2;
			devoutAndDiverse *= 27;
			newEraEnterpriser *= 66;
			marketSkepticRepublican *= 75;
			countryFirstConservative *= 95;
			coreConservative *= 97;
		}
		if (newEraEnterpriser>=marketSkepticRepublican&&newEraEnterpriser>=countryFirstConservative&&newEraEnterpriser>=coreConservative&&newEraEnterpriser>=solidLiberal&&newEraEnterpriser>=opportunityDemocrat&&newEraEnterpriser>=disaffectedDemocrat&&newEraEnterpriser>=devoutAndDiverse)
			return "New Era Enterpriser";
		if (marketSkepticRepublican>=newEraEnterpriser&&marketSkepticRepublican>=countryFirstConservative&&marketSkepticRepublican>=coreConservative&&marketSkepticRepublican>=solidLiberal&&marketSkepticRepublican>=opportunityDemocrat&&marketSkepticRepublican>=disaffectedDemocrat&&marketSkepticRepublican>=devoutAndDiverse)
			return "Market Skeptic Republican";
		if (countryFirstConservative>=newEraEnterpriser&&countryFirstConservative>=marketSkepticRepublican&&countryFirstConservative>=coreConservative&&countryFirstConservative>=solidLiberal&&countryFirstConservative>=opportunityDemocrat&&countryFirstConservative>=disaffectedDemocrat&&countryFirstConservative>=devoutAndDiverse) {
			if (coreConservative>=newEraEnterpriser&&coreConservative>=marketSkepticRepublican&&coreConservative>=devoutAndDiverse&&coreConservative>=disaffectedDemocrat&&coreConservative>=opportunityDemocrat) {
				return "Country First Conservative / Extreme";
			}
			else {
				return "Country First Conservative / Not extreme";
			}
		}
		if (coreConservative>=newEraEnterpriser&&coreConservative>=marketSkepticRepublican&&coreConservative>=countryFirstConservative&&coreConservative>=solidLiberal&&coreConservative>=opportunityDemocrat&&coreConservative>=disaffectedDemocrat&&coreConservative>=devoutAndDiverse)
			return "Core Conservative";
		if (solidLiberal>=opportunityDemocrat&&solidLiberal>=disaffectedDemocrat&&solidLiberal>=devoutAndDiverse&&solidLiberal>=newEraEnterpriser&&solidLiberal>=marketSkepticRepublican&&solidLiberal>=countryFirstConservative&&solidLiberal>=coreConservative)
			return "Solid Liberal";
		if (opportunityDemocrat>=solidLiberal&&opportunityDemocrat>=disaffectedDemocrat&&opportunityDemocrat>=devoutAndDiverse&&opportunityDemocrat>=newEraEnterpriser&&opportunityDemocrat>=marketSkepticRepublican&&opportunityDemocrat>=countryFirstConservative&&opportunityDemocrat>=coreConservative)
			return "Opportunity Democrat";
		if (disaffectedDemocrat>=solidLiberal&&disaffectedDemocrat>=opportunityDemocrat&&disaffectedDemocrat>=devoutAndDiverse&&disaffectedDemocrat>=newEraEnterpriser&&disaffectedDemocrat>=marketSkepticRepublican&&disaffectedDemocrat>=countryFirstConservative&&disaffectedDemocrat>=coreConservative)
			return "Disaffected Democrat";
		if (devoutAndDiverse>=solidLiberal&&devoutAndDiverse>=opportunityDemocrat&&devoutAndDiverse>=disaffectedDemocrat&&devoutAndDiverse>=newEraEnterpriser&&devoutAndDiverse>=marketSkepticRepublican&&devoutAndDiverse>=countryFirstConservative&&devoutAndDiverse>=coreConservative)
			return "Devout and Diverse";
		return "N/A";

	}
	
	//checks to see if progress should be printed and prints progress
	public void printProgress(int start, int end, int i) {
		if (i == (int)(start + (9*(end-start)/10)))
			System.out.println("90% complete...");
		else if (i == (int)(start + (8*(end-start)/10)))
			System.out.println("80% complete...");
		else if (i == (int)(start + (7*(end-start)/10)))
			System.out.println("70% complete...");
		else if (i == (int)(start + (6*(end-start)/10)))
			System.out.println("60% complete...");
		else if (i == (int)(start + (5*(end-start)/10)))
			System.out.println("50% complete...");
		else if (i == (int)(start + (4*(end-start)/10)))
			System.out.println("40% complete...");
		else if (i == (int)(start + (3*(end-start)/10)))
			System.out.println("30% complete...");
		else if (i == (int)(start + (2*(end-start)/10)))
			System.out.println("20% complete...");
		else if (i == (int)(start + (1*(end-start)/10)))
			System.out.println("10% complete...");
	}
	
	//removes duplicates from an ArrayList
	public void removeDuplicates(ArrayList<String> input){
		Set<String> hs = new HashSet<>();
		hs.addAll(input);
		input.clear();
		input.addAll(hs);
	}
	
	//checks if given range overlaps with another given range
	public boolean rangeIncludesNumBetween(int start, int end, int b1, int b2) {
		if ((start<=b1&&end>=b1)||(start>=b1&&end<=b2)||(start>=b1&&start<=b2)||(end>=b1&&end<=b2))
			return true;
		else
			return false;
	}
	
	//checks if number is in range
	public boolean numInRange(int n, int a, int b) {
		if (n>=a&&n<=b)
			return true;
		else
			return false;
	}
	
	//removes non-number characters from a string input
	public String removeNonNums(String input) {
		String res = "";
		for (int i = 0; i < input.length(); i++) {
			if (Character.isDigit(input.charAt(i)))
				res+=input.charAt(i);
		}
		return res;
	}
	
	//removes quotation marks from a string input
	public String removeQuotes(String input) {
		String res = "";
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i)!='"')
				res+=input.charAt(i);
		}
		return res;
	}
	
	/*
	public void analyze(String response, ArrayList<String> urls) throws IOException, RowsExceededException, WriteException { //response is "B" or "M", urls is an ArrayList of urls
		WritableWorkbook out;
		WritableSheet sheet;
		Label label;
		Document d; //the page it's on
		int j; //keeps track of which variable it's on
		String temp;
		String temp2;
		Document dTemp;
		Element eTemp;
		Elements t;
		
		int numCo, coDem, coRep, coInd;
		
		int numCongress, yearCongress, //number and year of congress, DONE
		rcVotes, //number of roll call votes, DONE
		amendSubm, //number of amendments submitted
		amendProp, //number of amendments proposed on senate floor
		amendRc, //roll call votes on amendments in Senate
		amendAgree, //amendments agreed to
		amendDem, //number of democrats who sponsored amendment
		amendRep, //number of republicans who sponsored amendment
		amendInd, //number of independents who sponsored amendment
		amendDemCo, amendRepCo, amendIndCo, //above, but for cosponsors
		numCo, //number of cosponsors
		coDem, //number of democrat cosponsors
		coRep, //number of republican cosponsors
		coInd, //number of independent cosponsors
		coNE, //number of cosponsors from ME, NH, VT, MA, CT, RI, NY, NJ, PA
		coMW, //above, but from OH, MI, IN, IL, WI, MO, IA, MN, KS, NE, SD, ND
		coW, //above, but from WA, OR, CA, AZ, NV, ID, MT, WY, UT, CO, NM
		coS, //above, but from TX, OK, AR, LA, AL, MS, TN, KY, FL, GA, SC, NC, VA, WV, DC, MD, DE
		numBillsYear; //number of bills passed the year the bill was proposed
		
		String sponsParty, sponsState, //party and state of sponsor (D / R / I ; state abbreviation) 
		committee1, committee2, //committee, 2 if there are 2 
		billStatus, //introduced, passed house, passed senate, etc.
		chamber, //Senate vs House
		presParty, //party in control of White House at time of passing
		subj; //subject - policy area
		
		double amendDemPercent, //dems who sponsored amendments out of total amendment sponsors, times 100%
		amendRepPercent, //above, but for reps 
		amendIndPercent, //above, but for inds
		amendDemCoPercent, amendRepCoPercent, amendIndCoPercent, //above, but for cosponsors
		partisanship; //how partisan was the bill?
		
		boolean war; //was country at war when bill was proposed?
		
		
		//---------------------------------------------------------//
		//------------------NOW FIND THESE VARIABLES---------------//
		//---------------------------------------------------------//
		
		int tryCount = 0;
		int maxTries = 20;
		
		
		if(response.equalsIgnoreCase("B")) {
			out = Workbook.createWorkbook(new File("billData"+System.currentTimeMillis()+".xls"));
			sheet = out.createSheet("Main", 0);



			for (int i = 0; i < urls.size(); i++) {
				j = 0;
				while (true) {
					try {d = Jsoup.connect(urls.get(i)+"/amendments").timeout(100000).get(); //connect to website
						break;
					} catch (IOException h) {
						i++;
						if (++tryCount == maxTries) throw h;
					}
				}
				

				
				//retrieve numCongress
				t = d.getElementsByTag("title");
				temp = t.first().text(); //gets title
				temp2 = temp.substring(temp.indexOf("-",temp.indexOf("-")+1)+2, temp.indexOf("-",temp.indexOf("-")+1)+7);
				temp2 = removeNonNums(temp2);
				label = new Label(j, i, temp2);
				sheet.addCell(label);
				j++;
				
				
				//retrieve yearCongress
				temp = temp.substring(temp.indexOf("(")+1, temp.indexOf("(")+5);
				label = new Label(j, i, temp);
				sheet.addCell(label);
				j++;
				
				//retrieve sponsParty
				t = d.getElementsByClass("overview_wrapper bill");
				temp = t.first().html(); //gets the whole div
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
				
				//retrieve committee1
				//retrieve committee2
				
				
				//retrieve rcVotes
				temp2 = temp.substring(temp.lastIndexOf(">",temp.length()-3)+1, temp.lastIndexOf(">",temp.length()-3)+4); //gives the party
				temp2 = removeNonNums(temp2);
				if (temp2.equals(""))
					temp2 = "0";
				label = new Label(j,i, temp2);
				sheet.addCell(label);
				j++;
				
				//retrieve billStatus
				t = d.getElementsByClass("hide_fromsighted");
				temp = t.eq(t.size()-2).last().text();
				temp = temp.substring(temp.indexOf("status")+7);
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
				//retrieve amendSubm
				t = d.getElementsByClass("selected");
				temp = t.last().html(); //gets the whole selected li
				if (temp.indexOf("span class=")==-1)
					temp = t.eq(t.size()-2).last().html();
				temp = temp.substring(temp.indexOf("span class="),temp.indexOf("</span"));
				temp = removeNonNums(temp);
				
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
				//retrieve numCo
				while (true) {
					try {d = Jsoup.connect(urls.get(i)+"/cosponsors").timeout(100000).get(); //connect to website
						break;
					} catch (IOException e) {
						i++;
						if (++tryCount == maxTries) throw e;
					}
				}
				
				
				
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
				
				//retrieve coDem
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
				
				//retrieve coRep
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
				
				//retrieve coInd
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
				
				//make coDemPercent
				if (numCo!=0) {
					temp = "" + (double)(coDem)/(coDem+coRep+coInd);
				} else {
					temp = "0";
				}
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
				//make coRepPercent
				if (numCo!=0) {
					temp = "" + (double)(coRep)/(coDem+coRep+coInd);
				} else {
					temp = "0";
				}
				label = new Label(j,i, temp);
				sheet.addCell(label);
				j++;
				
			}
		}
		else {
			out = Workbook.createWorkbook(new File("memberData"+System.currentTimeMillis()+".xls"));
			sheet = out.createSheet("Main", 0);
		}
		

		
		//label = new Label(0, 2, "A label record"); //0 is column 1, 2 is row 2
		//sheet.addCell(label); 
		//Number number = new Number(3, 4, 3.1459); 
		//sheet.addCell(number);
		

		out.write(); 
		out.close();
		
	}*/
	
}
