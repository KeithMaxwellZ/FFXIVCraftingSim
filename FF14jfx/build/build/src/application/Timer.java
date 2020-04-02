package application;

public class Timer
{
	private long startTime;
	private long endTime;
	private Status st;
	
	public Timer() {
		startTime = 0;
		endTime = 0;
		st = Status.stopped;
	}
	
	public void startTimer() {
		startTime = System.currentTimeMillis();
		st = Status.started;
	}
	
	public void stopTimer() {
		endTime = System.currentTimeMillis();
		st = Status.stopped;
	}
	
	public double getTime() {
		return (double)(System.currentTimeMillis() - startTime) / 1000.0;
	}
}

enum Status{
	started, stopped
}
