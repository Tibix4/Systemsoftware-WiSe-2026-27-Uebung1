import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

// Das Programm erwartet als Parameter zuerst den Dateipfad, danach eine regular expression zum Datentyp.
// Ich würde empfehlen ".*\.java" zu verwenden
public class Counter_beliebigviele_threads {

	public static void main(String[] args) throws IOException {
		assert (args.length == 2);

		long startTime = System.nanoTime();
        int threa_anzahl = Integer.parseInt(args[2]);
        
		if(args.length != 3) {
			System.out.println("Bitte geben Sie einen Dateipfad und eine regular expression \noder einen Ordnerpfad und eine regular expression  \n und die Anzahl der Threads an.");
			return;
		}
        if(threa_anzahl < 1) {
            System.out.println("Bitte geben Sie eine positive Anzahl von Threads an.");
            return;
        }


		long totalLines = countLinesInAllFiles(args[0], args[1], threa_anzahl);
	
		long endTime = System.nanoTime();

		System.out.println("Anzahl Zeilen: " + totalLines);
		System.out.println("Benötigte Zeit: "
				+ (endTime - startTime) / 1_000_000.0 + " ms");
	}



    public static List<File> getAllFieles (String folder, String regex){
        File[] files = new File(folder).listFiles();
        List<File> goodFilesList = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        for (File file : files) {
            if (file.isFile() && pattern.matcher(file.getName()).matches()) {
                goodFilesList.add(file);
            }
        }
        return goodFilesList;
    }

    public static long countLinesInAllFiles(String folder, String regex, int thread_zahl) throws IOException {
		AtomicLong size = new AtomicLong(0);
		Pattern pattern = Pattern.compile(regex);
		File[] files = new File(folder).listFiles();
        List<File> goodFilesList = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
               goodFilesList.addAll(getAllFieles(file.getAbsolutePath(), regex));
            } else if (file.isFile() && pattern.matcher(file.getName()).matches()) {
                goodFilesList.add(file);
            }
        }
        List<Thread> threads = new ArrayList<>();
        if (thread_zahl > goodFilesList.size()) {
            thread_zahl = goodFilesList.size();
        }else {
            int filesPerThread = goodFilesList.size() / thread_zahl;
        }

		for (int i = 0; i < thread_zahl; i++) {
            for (int k = fielesPerThread, k >= 0; k--) {
               
				Thread thread = new Thread(() -> {
					try {
						size.addAndGet(countLines(file.getAbsolutePath()));
					} catch (IOException e) {
							e.printStackTrace();
					}
				});
				thread.start();
				threads.add(thread);
		} 
            //else if (file.isDirectory()) {
			//	size.addAndGet(countLinesInAllFiles(file.getAbsolutePath(), regex));
			//}
		
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return size.get();
    }

    public static long countLines(String fileName) throws IOException {
		long lines = 0;

         try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while (reader.readLine() != null) {
                lines++;
            }
        }
		return lines;
	}
    

}
