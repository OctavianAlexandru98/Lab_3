import java.util.concurrent.*;

/**
 * Reti e laboratorio III - A.A. 2022/2023
 * Soluzione dell'esercizio di preparazione all'assignment.
 * 
 * @author Matteo Loporchio
 */
public class Sala {

	public static final int numViaggiatori = 50;
	public static final int numEmettitrici = 5;
	// Numero massimo di persone in attesa.
	public static final int dimCoda = 10;
	// Intervallo di tempo fra l'arrivo di un viaggiatore e l'altro.
	public static final int taskDelay = 50;
	// Tempo massimo di attesa per la terminazione del pool.
	public static final int terminationDelay = 5000;

	public static void main(String[] args) {
		// Creo una coda bloccante per memorizzare i task (viaggiatori) in arrivo.
		BlockingQueue<Runnable> q = new ArrayBlockingQueue<Runnable>(dimCoda);
		// Creo un pool di thread con al massimo `numEmettitrici` thread.
		ExecutorService pool = new ThreadPoolExecutor(
				numEmettitrici, // Numero di thread da mantenere nel pool.
				numEmettitrici, // Numero massimo di thread possibili nel pool.
				terminationDelay, // Tempo di keep-alive per i thread.
				TimeUnit.MILLISECONDS,
				q, // Coda bloccante per i task.
					// Politica di rifiuto di default
					// che solleva una RejectedExecutionException in caso di coda piena.
				new ThreadPoolExecutor.AbortPolicy());
		// Creazione dei task.
		for (int i = 0; i < numViaggiatori; i++) {
			try {
				pool.execute(new Viaggiatore(i));
			}
			// Catturo qui l'eccezione sollevata con la AbortPolicy
			// in caso di coda piena.
			catch (RejectedExecutionException e) {
				System.err.printf("Viaggiatore %d: sala esaurita\n", i);
			}
			// Attendo un intervallo di tempo prima di creare un nuovo task.
			try {
				Thread.sleep(taskDelay);
			} catch (InterruptedException e) {
				System.err.println("Interruzione su sleep.");
				System.exit(1);
			}
		}
		// Terminazione del thread pool. Adottiamo il seguente metodo:
		// 1) Smetto di accettare nuovi task.
		// 2) Successivamente aspetto un certo intervallo di tempo affinché
		// tutti i thread possano terminare.
		// 3) Passato l'intervallo, l'esecuzione del pool viene interrotta
		// immediatamente.
		pool.shutdown();
		try {
			if (!pool.awaitTermination(terminationDelay, TimeUnit.MILLISECONDS))
				pool.shutdownNow();
		} catch (InterruptedException e) {
			pool.shutdownNow();
		}
	}
}
