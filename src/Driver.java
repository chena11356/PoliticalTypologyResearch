import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Driver {

	public static void main(String[] args) throws IOException, RowsExceededException, WriteException, BiffException{
		System.out.println("Type 'B' or 'T' or 'M' or 'R' or 'RE' or 'TE' or 'TY' or 'C' or 'BT' or 'DR' or 'TEST'");
		Scanner scan = new Scanner(System.in);
		String response = scan.nextLine();
		
		System.out.println("Start? (inclusive)");
		int start = scan.nextInt();
		System.out.println("End? (inclusive)");
		int end = scan.nextInt();
		
		BillAnalyzer b = new BillAnalyzer();
		MemberAnalyzer m = new MemberAnalyzer();
		TopicAnalyzer t = new TopicAnalyzer();
		RollCallAnalyzer r = new RollCallAnalyzer();
		RelevanceAnalyzer re = new RelevanceAnalyzer();
		TextAnalyzer te = new TextAnalyzer();
		TypologyAnalyzer ty = new TypologyAnalyzer();
		CosponsorshipAnalyzer c = new CosponsorshipAnalyzer();
		BillTypologyAnalyzer bt = new BillTypologyAnalyzer();
		DataReformatter dr = new DataReformatter();
		
		if (response.equalsIgnoreCase("B")) {
			b.go(start, end); //bills can go from 1 to 12072
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("M")) { //NOTE: START MANUALLY REVIEWING AT 150
			m.go(start, end); //members can go from 1 to 547
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("T")) {
			t.go(start,end); //bills can go from 1 to 12043 (due to reserved bills probably)
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("R")) {
			r.go(start, end);  //bills by topic can go from 1 to 17 (18 if you include the test)
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("RE")) {
			re.go(start,end); //bills can go from 1 to 12072
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("TE")) {
			te.go(start,end); //bills can go from 1 to 12072 (redundant)
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("TY")) {
			ty.go(start, end); //members can go from 1 to 547
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("C")) {
			c.go(start, end); //members can go from 1 to 547
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("BT")) {
			bt.go(start, end); //bills can go from 1 to 12072
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("DR")) {
			dr.go(start, end); //members can go from 1 to 547
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("TEST")) {
			System.out.println(m.findTypology(-1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, 1, 1, -1, "Democrat"));
			System.out.println(m.findTypology(-1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, 1, 1, -1, "Republican"));
			System.out.println(m.findTypology(-1, -1, 1, -1, -1, -1, 1, 1, -1, 1, 1, 1, 1, -1, 1, -1, "Democrat"));
			System.out.println(m.findTypology(1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, -1, -1, 1, "Republican"));
			System.out.println(m.findTypology(1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, -1, -1, 1, "Democrat"));
			System.out.println("Done!");
		}
		else {
			System.out.println("Invalid input. Goodbye!");
		}
		
		scan.close();
		
	}

}
