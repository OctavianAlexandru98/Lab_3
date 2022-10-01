import java.util.concurrent.*;

public class UfficioPostale {
    private int numSportelli;
    private int numPersoneSecondaSala;
    private int numPersone;
    private int terminationDelay;
    private BlockingQueue<Persona> codaPersonePrimaSala;
    private BlockingQueue<Runnable> codaPersoneSecondaSala;
    private ExecutorService pool;

    public UfficioPostale(int numSportelli, int numPersoneSecondaSala) {
        this.numSportelli = numSportelli;
        this.numPersoneSecondaSala = numPersoneSecondaSala;
        this.terminationDelay = 5000;
        this.codaPersonePrimaSala = new LinkedBlockingQueue<Persona>();
        this.codaPersoneSecondaSala = new ArrayBlockingQueue<Runnable>(this.numPersoneSecondaSala);
        this.pool = new ThreadPoolExecutor(this.numSportelli, this.numSportelli, this.terminationDelay,
                TimeUnit.MILLISECONDS, this.codaPersoneSecondaSala);

    }

    public static void main(String[] args) {
        UfficioPostale ufficio = new UfficioPostale(4, 10);
        // faccio arrivare 100 persone nell'ufficio postale
        ufficio.GestorePersoneInArrivo(100);

        // gestisco le persone che si trovano all'interno dell'ufficio
        ufficio.GestorePersonePrimaSala();

        ufficio.chiudiUfficio();
    }

    // questo metodo prende in input il numero di persone da far arrivare
    // nell'ufficio postale
    // queste persone entreranno tutte le prima sala (quella più capiente e senza
    // limiti)
    public void GestorePersoneInArrivo(int numPersone) {
        this.numPersone = numPersone;
        // le persone arrivano nell'ufficio postale e entrano tutte nella prima sala
        for (int i = 1; i <= this.numPersone; i++) {
            this.aggiungiPersonaAllaPrimaCoda(new Persona(i));
        }
    }

    // questo metodo prende le persone che sono presenti nella prima sala e le fa
    // entrare nella seconda
    // al massimo farà entrare "numPersoneSecondaSala" per volta nella seconda sala
    public void GestorePersonePrimaSala() {
        while (true) {
            Persona persona = estraiPersonaDallaPrimaCoda();
            if (persona == null) {
                break;
            }
            serviPersona(persona);
        }
    }

    private void serviPersona(Persona persona) {
        try {
            pool.execute(persona);
        } catch (RejectedExecutionException e) {
            this.sleep(100);
            serviPersona(persona);
        }
    }

    private void aggiungiPersonaAllaPrimaCoda(Persona persona) {
        try {
            this.codaPersonePrimaSala.put(persona);
        } catch (InterruptedException e) {
            System.err.println("La prima sala è piena.");
            System.exit(1);
        }
    }

    private Persona estraiPersonaDallaPrimaCoda() {
        try {
            if(this.codaPersonePrimaSala.isEmpty()){
                System.out.println("La prima sala è vuota.");
                return null;
            }else{
                return this.codaPersonePrimaSala.take();
            }
        } catch (InterruptedException e) {
            System.err.println("La prima sala è vuota.");
            System.exit(1);
            return null;
        }
    }

    private void sleep(long milliSec) {
        try {
            Thread.sleep(milliSec);
        } catch (InterruptedException e) {
            System.err.println("Sono stato interrotto mentre aspettavo.");
        }
    }

    private void chiudiUfficio() {
        this.pool.shutdown();
        try {
            if (!this.pool.awaitTermination(this.terminationDelay, TimeUnit.MILLISECONDS))
                this.pool.shutdownNow();
        } catch (InterruptedException e) {
            this.pool.shutdownNow();
        }
    }

}
