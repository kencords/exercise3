import java.util.InputMismatchException;
import java.util.Scanner;

public class InputHelper {
	
	private static Scanner input = new Scanner(System.in);
	
	public static int getPositiveNum(String name){
		int num = 0, condition = name.equals("Track Distance")? 20 : 1;
		while(true){
			System.out.print("Enter input for " + name + ": ");
			try{
				num = input.nextInt();
				if(num < condition){
					System.out.println("Invalid input! " + name + " must be greater than " + condition);
					continue;
				}
				
				return num;
			}catch(NumberFormatException | InputMismatchException ex){
				System.out.println("Invalid numeric input!");
				continue;
			}
			finally{
				input.nextLine();
			}
		}
	}
}
