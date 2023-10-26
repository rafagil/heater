package app.osmosi.heater.utils;

public class IntervalThread implements Runnable {

    private boolean running = false;
    private final int timeout;
    private final Runnable runnable;

    public IntervalThread(Runnable runnable, int timeout) {
        this.runnable = runnable;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        running = true;
        while(running) {
            runnable.run();
            try { 
				Thread.sleep(timeout);
			} catch(InterruptedException e) {
				running = false;
			}
        }
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
