import java.io.*;
import java.nio.file.*;
import java.util.zip.*;
import java.nio.file.attribute.*;
import java.time.Instant;

/*  Nel path passato come parametro da linea di comando non ci devono essere spazzi nei nomi delle cartelle
    Es. se si vuole zippare il contenuto della caretella "Reti e laboratorio 3" la si deve rinominare in "Reti_e_laboratorio_3" o simili togliendo tutti gli spazzi dal nome
    Funziona anche se invece di una directory viene passato il path di un singolo file
*/
public class ZipDir extends SimpleFileVisitor<Path> {

    private Path sourceDir;
    private ZipOutputStream zos;

    public ZipDir(Path sourceDir,ZipOutputStream zos) {
        this.sourceDir = sourceDir;
        this.zos = zos;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {

        try {
            Path targetFile = sourceDir.relativize(file);

            zos.putNextEntry(new ZipEntry(targetFile.toString()));
            byte[] bytes = Files.readAllBytes(file);
            zos.write(bytes, 0, bytes.length);
            zos.closeEntry();

        } catch (IOException ex) {
            System.err.println(ex);
        }

        return FileVisitResult.CONTINUE;
    }
    
    // Funzione prende in input un Instant e restituisce una stringa dateTime con il seguente formato: YYYY-MM-DD hh:mm:ss:SSSS
    // Quindi la precisione e' al decimo di millisecondo 
    public static String DateTimeToString(Instant dateTime) {
        String stringDateTime=(dateTime.toString().replace('T', ' ')).split("\\.")[0];
        return stringDateTime+":"+(dateTime.toString().replace('T', ' ')).split("\\.")[1].substring(0,4);
    }

    public static void printTreadMessage(String message){
        System.out.println(DateTimeToString(Instant.now()) +"  "+ Thread.currentThread().getName()+" ==> "+message);
    }
    
    //task che dato il path di un file/directory crea un file zip con lo stesso nome
    public static class Zipper implements Runnable {
        public String path;
        public  Zipper(String path){
            this.path = path;
        }
        public void run(){
            Path sourceDir = Paths.get(this.path);
            try {
                String zipFileName = this.path.concat(".zip");
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName));
                Files.walkFileTree(sourceDir, new ZipDir(sourceDir,zos));
                
                zos.close();
            } catch (IOException ex) {
                System.err.println("I/O Error: " + ex);
            }
            //printTreadMessage("Compressione terminata : "+ this.path);
        }
    }

    public static void main(String[] args) {
        String dirPath = args[0];
        File folder = new File(dirPath);
        if (folder.isFile()) {
            Thread thread = new Thread(new Zipper(folder.getPath()));
            thread.start();
        } else {
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                String[] splitedFileName = listOfFiles[i].getName().split("\\.");

                //controllo che il file che sto per comprimere non sia gia compresso
                if(!("zip".equals(splitedFileName[splitedFileName.length-1]))){
                    if(listOfFiles[i].isFile()){
                        Thread thread = new Thread(new Zipper(listOfFiles[i].getPath()));
                        thread.start();
                    } else if (listOfFiles[i].isDirectory()){
                        ZipDir.main(new String[]{listOfFiles[i].getPath()});
                    }    
                }
            }
        }
        //printTreadMessage("Tutti i thread sono stati avviati");
    }
}
