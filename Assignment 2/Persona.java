import java.util.concurrent.*;


public class Persona implements Runnable {
	public int minDelay = 0;
	public int maxDelay = 1000;
	public int id;

	public Persona(int id) {
        this.id = id;
    }

	@Override
	public void run() {
		//System.out.printf("Persona %d: è allo sportello %s \n", id,Thread.currentThread().getName());
		int delay = ThreadLocalRandom.current().nextInt(minDelay, maxDelay + 1);
	    try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
	    	System.err.println("Interruzione su sleep.");
	    }
	    System.out.printf("Persona %d: ha liberato lo sportello\n", id);
	}
}
