import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class Horse implements Runnable{
	
	public static List<Horse> raceCompetitors;
	
	private static int counter = 0;
	private static CyclicBarrier raceBarrier;
	private static Random rand = new Random();
	private final int id;
	private String warCry;
	private int trackDistance, curDistance;
	private double startTime, timeMarker;
	private HorseRace race;
	private PrinterThread printer;
	private boolean boost = false;


	public Horse(CyclicBarrier barrier, int trackDistance, HorseRace race, PrinterThread printer){
		Horse.raceBarrier = barrier;
		this.id = ++Horse.counter;
		this.trackDistance = trackDistance;
		this.curDistance = 0;
		this.race = race;
		this.printer = printer;
	}
	
	public synchronized boolean isFinished(){
		return curDistance >= trackDistance;
	}
	
	public int getCurrentDistance(){
		return curDistance;
	}
	
	public double getEndTime(){
		return timeMarker;
	}

	public double getStartTime(){
		return startTime;
	}
	
	public String getWarCry(){
		return warCry;	
	}
	
	public void setWarCry(String warCry){
		this.warCry = warCry;
		System.out.println(warCry + ", " + this.toString() + " has joined the race!");
	}

	@Override
	public void run() {
		try {
			toGate();
			Horse.raceBarrier.await();
			startRunning();
			Horse.raceBarrier.await();
		} catch (Exception e){
			System.out.println("Oops! The track is wet, we can't continue the race");
			e.printStackTrace();
		}
	}

	@Override
	public String toString(){
		return "Horse " + id;
	}

	private synchronized String gallop(){
		int delta = rand.nextInt(10) + (boost? 11 : 1);
		curDistance += delta;
		String msg = this.toString() + " ran " + delta + " meter(s), distance remaining = " 
					+ (!isFinished()? trackDistance - curDistance : 0) + (boost? " BOOST!!!" : "");
		timeMarker = System.nanoTime();
		this.boost = false;
		return (isFinished()? (msg.toUpperCase() + " " + warCry + " " + this.toString() + " HAS FINISHED THE RACE!!!")
				: msg);
	}

	private boolean goFaster(){
		try{
			return Horse.raceCompetitors.get(0) == this && curDistance != Horse.raceCompetitors.get(1).getCurrentDistance();
		}catch(Exception e){
			return true;
		}
	}

	private void startRunning() throws Exception{
		startTime = System.nanoTime();
		while(!isFinished()){
			boost = goFaster();
			printer.addToQueue(">[" + PrinterThread.TIMEFORMAT.format(Date.from(Instant.now())) + "] " + gallop());
			TimeUnit.MILLISECONDS.sleep(500);
		}
	}

	private void toGate() throws Exception{
		double startToGate = System.nanoTime();
		int toGateTime = rand.nextInt(10) + 1;
		System.out.println(this + " is " + toGateTime + " seconds away from the gate");
		TimeUnit.SECONDS.sleep(toGateTime);
		System.out.println("> It took " + ((System.nanoTime() - startToGate) / 1.0E9) + " seconds for " + this.toString() + " to arrive at the gate");
	}

}
