import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class HorseBarn {
	
	private Deque<String> warCries;
	
	public HorseBarn(){
		warCries = new ArrayDeque<>(initWarcries());
	}
	
	public Horse getHorse(CyclicBarrier barrier, int distance, HorseRace race, PrinterThread outputHelper){
		Horse horse = new Horse(barrier, distance, race, outputHelper);
		horse.setWarCry(getWarcry());
		return horse;
	}
	
	private List<String> initWarcries(){
		List<String> temp = Arrays.asList("BANKAI!", "AHOO AHOO!","Eat My Dust!","BOOOOYAAA!!","IKUZE!!",
				 			"ARRGHH!","TIGIRIG TIG TIG!","HYAAA!!","MAAAWW!","For the Wolf!","I'M BATMAN");
		Collections.shuffle(temp);
		
		return temp;
	}
	
	private String getWarcry(){
		if(warCries.isEmpty()){
			warCries.addAll(initWarcries());
		}
		return warCries.pop();
	}
	
	
}
