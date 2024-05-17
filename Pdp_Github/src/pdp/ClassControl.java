package pdp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
*
* Metin G�rg�l�  /  metin.gorgulu@ogr.sakarya.edu.tr
* 05.04.2024
* <p>
* Dosyalar�n .java uzant�l� olan ve i�erisinde class bulunduran
* dosyalar� ay�klayan  fonksiyonlar�n bulundu�u s�n�ft�r.
* </p>
*/

public class ClassControl {
	private List<File> fileList = new ArrayList<>();

	private String folderName;

	public ClassControl(String comingFolderName) {
		folderName = comingFolderName;
	}

	public List<File> fileReader() {
		File folder = new File(System.getProperty("user.dir") + "\\" + folderName);

		/*
		 * Klonlanm�� Klas�r�n i�eri�ini istenenlere g�re ay�klat�r.
		 */
		if (folder.isDirectory()) {

			exploreFolder(folder, fileList);

		}
		return fileList;
	}

	private void exploreFolder(File folder, List<File> fileList) {
		File[] files = folder.listFiles();

		/*
		 * Dosyalar�n uzant�lar�n�n sonunda ".java" olan dosyalar� ay�klar.
		 * Daha sonra containClass fonksiyonun �al��t�r�r ve class olan dosyalar� buldurur.
		 */

		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".java")) {
					if (containsClass(file)) {
						fileList.add(file);
					}

				} else if (file.isDirectory()) {
					exploreFolder(file, fileList);
				}
			}
		}
	}

	private boolean containsClass(File file) {
		try {
			String content = new String(Files.readAllBytes(file.toPath()));
			

			Pattern commentPattern = Pattern.compile("//.*|/\\*(?:.|[\\n\\r])*?\\*/");
			Matcher commentMatcher = commentPattern.matcher(content);
			content = commentMatcher.replaceAll(" ");
			/*
			 * Yorum Sat�rlar�n� ��kart�r.
			 */
			commentPattern = Pattern.compile("\".*\"");
			commentMatcher = commentPattern.matcher(content);
			content = commentMatcher.replaceAll("\"\"");
			/*
			 * "" ifadesi i�erisindeki t�m tan�mlamalar� ��kart�r.
			 */
			
			/*
			 * Bu sayede hem yorum sat�rlar� i�erisinde hem de String "" tan�mlamas� i�erisinde kalan k�s�mlar
			 * ��kar�larak o k�s�mlar i�erisinde kalan sat�rlar�n e�le�me ihtimalini ortadan kald�r�r.
			 */
			
			String modifiers = "(public|private|protected|static|final|default|abstract)\\s+";
			Pattern classPattern = Pattern.compile("("+modifiers+")*(class\\s+\\w+)(\\s*.*\\{)");
			Matcher classMatcher = classPattern.matcher(content);
			/*
			 * class tan�mlamalar�n� e�le�tirir.
			 */
			while (classMatcher.find()) {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
