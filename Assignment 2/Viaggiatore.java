import java.util.concurrent.ThreadLocalRandom;

/**
 *	Reti e laboratorio III - A.A. 2022/2023
 *	Soluzione dell'esercizio di preparazione all'assignment
 * 
 *	Ogni viaggiatore viene simulato da un task che esegue le seguenti operazioni:
 *	1) Stampare "Viaggiatore {id}: sto acquistando un biglietto";
 *  2) Aspettare per un intervallo di tempo random tra 0 e 1000 ms;
 *  3) Stampare "Viaggiatore {id}: ho acquistato il biglietto".
 *  
 *  @author Matteo Loporchio
 */
public class Viaggiatore implements Runnable {
	// Tempo minimo di attesa per le operazioni del Viaggiatore.
	public final int minDelay = 0;
	// Tempo massimo di attesa per le operazioni del Viaggiatore.
	public final int maxDelay = 1000;
	// Identificativo del Viaggiatore.
	public final int id;
	
	/**
	 *  Costruttore della classe Viaggiatore.
	 *  @param id l'identificativo del viaggiatore
	 */
	public Viaggiatore(int id) {this.id = id;}

	/**
	 *  Il metodo principale del task Viaggiatore,
	 *  contenente la logica del thread.
	 */
	@Override
	public void run() {
		System.out.printf("Viaggiatore %d: sto acquistando un biglietto\n", id);
		// Aspetto un intervallo di tempo compreso fra 0 e 1000 ms (inclusi).
		int delay = ThreadLocalRandom.current().nextInt(minDelay, maxDelay + 1);
	    try {Thread.sleep(delay);}
	    catch (InterruptedException e) {
	    	System.err.println("Interruzione su sleep.");
	    }
	    System.out.printf("Viaggiatore %d: ho acquistato il biglietto\n", id);
	}
}
