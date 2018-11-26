import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Main {

	public static void main(String[] args) throws IOException, RowsExceededException, WriteException, BiffException{
		System.out.println("Type 'B' or 'T' or 'M' or 'R' or 'RE' or 'TE'");
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
		
		if (response.equalsIgnoreCase("B")) {
			b.go(start, end); //bills can go from 1 to 12073
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("M")) {
			m.go(start, end); //members can go from 1 to 547
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("T")) {
			t.go(start,end); //bills can go from 1 to 12043 (due to reserved bills probably)
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("R")) {
			r.go(start, end); 
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("RE")) {
			re.go(start,end);
			System.out.println("Done!");
		}
		else if (response.equalsIgnoreCase("TE")) {
			te.go(start,end);
			System.out.println("Done!");
		}
		else {
			System.out.println("Invalid input. Goodbye!");
		}
		
		scan.close();
		
	}

}
