import java.text.SimpleDateFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PrinterThread{
	
	public static final SimpleDateFormat TIMEFORMAT = new SimpleDateFormat("H:mm:ss.SSS");
	
	private BlockingQueue<String> outputQueue;
	private boolean isRacing = true;
	private Thread printer;
	
	public PrinterThread(){
		outputQueue = new PriorityBlockingQueue<>(10);
		printer = new Thread(()-> {
			while(isRacing){
				print();
			}
		});
		printer.setDaemon(true);
	}

	public void addToQueue(String msg){
		outputQueue.offer(msg);
	}

	public void startPrinter(){
		printer.start();
	}
	
	private void print(){
		try{
			System.out.println(outputQueue.take());
			TimeUnit.MILLISECONDS.sleep(15);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
