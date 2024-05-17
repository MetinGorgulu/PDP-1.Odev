package pdp;

import java.io.File;
import java.io.IOException;

/**
*
* Metin G�rg�l�  /  metin.gorgulu@ogr.sakarya.edu.tr
* 05.04.2024
* <p>
* S�n�f Olu�turulurken g�nderilen Url adresindeki Github projesini 
* belirtilen Foldername ad�nda bir klas�r olu�turarak i�erisine klonlayan
* ve daha sonras�nda da i�erisine klonlanan klas�r� silen fonksiyonlar bulunan s�n�f.
* </p>
*/

public class CloneRepository {
	private String repoUrl;
	private String folderName;

	public CloneRepository(String comingRepoUrl, String comingFolderName) {
		repoUrl = comingRepoUrl;
		folderName = comingFolderName;
	}

	public boolean cloneRepository() {
		try {
		    ProcessBuilder processBuilder = new ProcessBuilder("git", "clone", repoUrl, folderName);
		    Process process = processBuilder.start();
		    process.waitFor();
		    
		    /*
		     * Al�nan Url Adresindeki Repository belirtilen klas�r�n i�erisine klonlan�r.
		     */
		    int exitValue = process.exitValue();

		    if (exitValue == 0) {
		        // Klonlama ba�ar�l� oldu
		        return true;
		    } else {
		        // Klonlama ba�ar�s�z oldu, hatal� URL
		    	System.out.println("--------------------------------------------");
		        System.out.println("Hatal� bir URL girdiniz.");
		        
		    }
		} catch (IOException | InterruptedException e) {
		    e.printStackTrace();
		}
		return false;
	}

	private void deleteFolder(File folder) {
		
		/*
		 * Girilen Klas�r i�erisindeki t�m dosyalar� sildirir.
		 */
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					deleteFolder(file);
				}
			}
		}
		folder.delete();
	}

	public void deleteFiles() {
		
		/*
		 * klonlanan projenin bulundu�u klas�r� tamamen sildirme fonksiyonu.
		 */
		File folder = new File(System.getProperty("user.dir") + "\\" + folderName);
		if (folder.exists()) {
			deleteFolder(folder);
		}
	}
}
