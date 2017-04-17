import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static java.util.stream.Collectors.*;

public class HorseRace {
	
	public static final int GATE_DISTANCE = 10;

	private int horseCount, trackDistance;
	private boolean isRacing = true;
	private List<Horse> horseList;
	private CyclicBarrier raceBarrier;
	private ExecutorService racers;
	private Thread referee;
	private PrinterThread printer = new PrinterThread();
	private HorseBarn barn = new HorseBarn();

	public void start() throws Exception{
		initRace(InputHelper.getPositiveNum("Number of Horses"), InputHelper.getPositiveNum("Track Distance"));
		horseList.forEach(racers::execute);
		printer.startPrinter();
	}

	private List<Horse> getCurrentStanding(){
	    return horseList.stream()
	       				.sorted((horse, horse2) -> Integer.compare(horse.getCurrentDistance(), horse2.getCurrentDistance()))
	       				.collect(toList());
    }
	
	private void initRace(int horseCount, int trackDistance){
		this.trackDistance = trackDistance;
		this.horseCount = horseCount;
		racers = Executors.newFixedThreadPool(this.horseCount);
		raceBarrier = new CyclicBarrier(horseCount, this::barrierAction);
		referee = new Thread(this::checkWinner);
		horseList = new ArrayList<>();
		for(int i=0; i<horseCount; i++){
			horseList.add(barn.getHorse(raceBarrier, trackDistance, this, printer));
		}
		Horse.raceCompetitors = horseList;

		System.out.println("Waiting for all Horses to go to the gate...");
	}
	
	private void barrierAction(){
		if(!isRacing){
            printRanking();
			return;
		}
		System.out.println("All Horses are now at the gate...");
        countDown(3);
	}
	
	private void checkWinner(){
        while (isRacing) {
        	Optional<Horse> optional = horseList.stream()
                				    	        .filter(Horse::isFinished)
                							    .findFirst();
            optional.ifPresent( opVal -> {
            	racers.shutdown();
            	isRacing = false;
            });
            Horse.raceCompetitors = getCurrentStanding();
        }
    }
	
	private void countDown(int count){
		System.out.println("Starting Countdown...");
		for(int i=count; i>0; --i){
			System.out.println(i + "!");
			try{
				TimeUnit.SECONDS.sleep(1);
			}catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		}
		referee.start();
		System.out.println("GOOOO!!!!");
	}
	
	private String getPlace(int n){
		final String[] array = { "TH", "ST", "ND", "RD", "TH", "TH", "TH", "TH", "TH", "TH" };
        switch (n % 100) {
            case 11:
            case 12:
            case 13: {
                return n + "TH";
            }
            default: {
                return n + array[n % 10];
            }
        }
	}

    private void printRanking() {
        final List<Horse> list = getCurrentStanding().stream()
        											 .sorted((horse, horse2) -> Double.compare(horse.getEndTime(), horse2.getEndTime()))
        										     .collect(toList());
        System.out.println("RACE RANKING:");
        for (int i = 0; i < list.size(); ++i) {
            System.out.println(getPlace(list.indexOf(list.get(i)) + 1) + " PLACER: " + list.get(i) + " " + list.get(i).getWarCry() + " (Run Time: " 
            	+ ((list.get(i).getEndTime() - list.get(i).getStartTime())/ 1.0E9) + " seconds)");
        }
    }

	public static void main(String[] args){
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		try{
			new HorseRace().start();
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
}