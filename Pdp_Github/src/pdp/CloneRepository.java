package pdp;

import java.io.File;
import java.io.IOException;

/**
*
* Metin Görgülü  /  metin.gorgulu@ogr.sakarya.edu.tr
* 05.04.2024
* <p>
* Sýnýf Oluþturulurken gönderilen Url adresindeki Github projesini 
* belirtilen Foldername adýnda bir klasör oluþturarak içerisine klonlayan
* ve daha sonrasýnda da içerisine klonlanan klasörü silen fonksiyonlar bulunan sýnýf.
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
		     * Alýnan Url Adresindeki Repository belirtilen klasörün içerisine klonlanýr.
		     */
		    int exitValue = process.exitValue();

		    if (exitValue == 0) {
		        // Klonlama baþarýlý oldu
		        return true;
		    } else {
		        // Klonlama baþarýsýz oldu, hatalý URL
		    	System.out.println("--------------------------------------------");
		        System.out.println("Hatalý bir URL girdiniz.");
		        
		    }
		} catch (IOException | InterruptedException e) {
		    e.printStackTrace();
		}
		return false;
	}

	private void deleteFolder(File folder) {
		
		/*
		 * Girilen Klasör içerisindeki tüm dosyalarý sildirir.
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
		 * klonlanan projenin bulunduðu klasörü tamamen sildirme fonksiyonu.
		 */
		File folder = new File(System.getProperty("user.dir") + "\\" + folderName);
		if (folder.exists()) {
			deleteFolder(folder);
		}
	}
}
