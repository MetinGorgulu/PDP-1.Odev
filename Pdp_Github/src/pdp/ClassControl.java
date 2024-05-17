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
* Metin Görgülü  /  metin.gorgulu@ogr.sakarya.edu.tr
* 05.04.2024
* <p>
* Dosyalarýn .java uzantýlý olan ve içerisinde class bulunduran
* dosyalarý ayýklayan  fonksiyonlarýn bulunduðu sýnýftýr.
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
		 * Klonlanmýþ Klasörün içeriðini istenenlere göre ayýklatýr.
		 */
		if (folder.isDirectory()) {

			exploreFolder(folder, fileList);

		}
		return fileList;
	}

	private void exploreFolder(File folder, List<File> fileList) {
		File[] files = folder.listFiles();

		/*
		 * Dosyalarýn uzantýlarýnýn sonunda ".java" olan dosyalarý ayýklar.
		 * Daha sonra containClass fonksiyonun çalýþtýrýr ve class olan dosyalarý buldurur.
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
			 * Yorum Satýrlarýný Çýkartýr.
			 */
			commentPattern = Pattern.compile("\".*\"");
			commentMatcher = commentPattern.matcher(content);
			content = commentMatcher.replaceAll("\"\"");
			/*
			 * "" ifadesi içerisindeki tüm tanýmlamalarý çýkartýr.
			 */
			
			/*
			 * Bu sayede hem yorum satýrlarý içerisinde hem de String "" tanýmlamasý içerisinde kalan kýsýmlar
			 * çýkarýlarak o kýsýmlar içerisinde kalan satýrlarýn eþleþme ihtimalini ortadan kaldýrýr.
			 */
			
			String modifiers = "(public|private|protected|static|final|default|abstract)\\s+";
			Pattern classPattern = Pattern.compile("("+modifiers+")*(class\\s+\\w+)(\\s*.*\\{)");
			Matcher classMatcher = classPattern.matcher(content);
			/*
			 * class tanýmlamalarýný eþleþtirir.
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
