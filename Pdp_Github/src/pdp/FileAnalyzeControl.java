package pdp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
*
* Metin Görgülü  /  metin.gorgulu@ogr.sakarya.edu.tr
* 05.04.2024
* <p>
* Bu sýnýf bir File Listesi parametre alarak oluþturulur. Ödevde bulunmasý istenen 
* Javadoc Satýr Sayýsý, Comment Satýr Sayýsý, Code Satýr Sayýsý, Tüm Satýr Sayýsý,
* Fonksiyon Sayýsý ve Yorum Sapma Yüzdesini bulma fonksiyonlarý bu sýnýfta yer alýr.
* print() fonksiyonu ile önce istenilen deðerler tek tek bulunur ve ekrana yazdýrýlýr.
* </p>
*/
public class FileAnalyzeControl {

	private List<File> fileList;
	private int javadoc;
	private int comment;
	private int codeLine;
	private int loc;
	private int fun;
	private double deviationOfComment;
	private boolean undefiniteNumber;

	public FileAnalyzeControl(List<File> comingfileList) {
		fileList = comingfileList;
	}

	public void print() {
		for (File file : fileList) {

			javadoc = javadocCounter(file);
			comment = commentCounter(file);
			codeLine = codeLineCounter(file);
			loc = LOC(file);
			fun = funCounter(file);
			deviationOfComment = calculatorDeviationOfComment();
			/*
			 * Ýstenilen Satýr ve fonksiyon sayýlarýný buldurur.
			 * Daha sonra bunlarý belirli düzene göre Console a yazdýrýr.
			 */

			System.out.println("Sýnýf               : " + file.getName());
			System.out.println("Javadoc Satýr Sayýsý: " + javadoc);
			System.out.println("Yorum Satýr Sayýsý  : " + comment);
			System.out.println("Kod Satýr Sayýsý    : " + codeLine);
			System.out.println("LOC                 : " + loc);
			System.out.println("Fonksiyon Sayýsý    : " + fun);
			if (undefiniteNumber) {
				System.out.println("Yorum Sapma Yüzdesi : % Belirsiz");
			} else {
				String deviation = String.format("%.2f", deviationOfComment);
				System.out.println("Yorum Sapma Yüzdesi : % " + deviation);
			}

			System.out.println("-----------------------------------------------");
		}
	}

	private double calculatorDeviationOfComment() {
		if (!(fun == 0)) {
			
			undefiniteNumber = false; //Eðer Fonksiyon sayýsý 0 ise Herhangi Bir sayý 0 a bölünemeyeceði için Belirsiz Sayý olur.
			
			double YG, YH, result, docNumber, commentNumber, funNumber, codeLineNumber;
			docNumber = (double) javadoc;
			commentNumber = (double) comment;
			funNumber = (double) fun;
			codeLineNumber = (double) codeLine;
			//Ýþlem hatasý olmamasý için bütün sayýlar double a dönüþtürülür ve iþlemler yapýlýr.
			
			YG = docNumber + commentNumber;
			YG *= 0.8;
			if(!(funNumber == 0.0)) {
				YG /= funNumber;
				YH = codeLineNumber / funNumber;
				YH *= 0.3;
			}else {
				YH = 0.0;
			}
			/*
			 * Matematiksel algoritma içerisinde sadece fonksiyon sayýsý 0 olduðunda
			 * sonuç belirsiz çýkmaktadýr.
			 */
			if(!(YH == 0.0 )) {
				result = ((100 * YG) / YH) - 100;

				return result;
			}
			
		} 
		undefiniteNumber = true; //Eðer Fonksiyon sayýsý 0 ise Herhangi Bir sayý 0 a bölünemeyeceði için Belirsiz Sayý olur.
		return 0.0;
	}

	private int LOC(File file) {
		try {
			BufferedReader reader = Files.newBufferedReader(file.toPath());
			
			@SuppressWarnings("unused")
			String line;
			
			int locCount = 0;
			
			/*
			 * Kod içerisindeki fark etmeksizin bütün Satýr Sayýsýný bulur.
			 */

			while ((line = reader.readLine()) != null) {
				locCount++;
			}

			reader.close();

			return locCount;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;

	}

	private int commentCounter(File file) {
		StringBuilder codeBuilder = new StringBuilder();
		try {
			BufferedReader reader = Files.newBufferedReader(file.toPath());
			String line;
			boolean inComment = false;
			int commentCount = 0;

			while ((line = reader.readLine()) != null) {
				codeBuilder.append(line).append("\n");
			}
			String text = codeBuilder.toString();

			Pattern deletePattern = Pattern.compile("\\/\\*\\*[^/](?:.|[\\r\\n])*?\\*\\/");
			Matcher deleteMatcher = deletePattern.matcher(text);
			text = deleteMatcher.replaceAll("");
			deletePattern = Pattern.compile("\".*\"");
			deleteMatcher = deletePattern.matcher(text);
			text = deleteMatcher.replaceAll("");
			
			/*
			 * Önce Kod Ýçerisindeki bütün javadoc kýsmýný ve " " tanýmlamalarýný çýkartýr.
			 * Javadoc ve " " tanýmlamalarý içerisindeki yorum satýrlarýný da saymak istiyorsak
			 * yukarýdaki kod parçasýný kaldýrabiliriz.
			 */
			
			/*
			 * Bu sayede hem Javadoc yorum satýrlarý içerisinde hem de String "" tanýmlamasý içerisinde kalan kýsýmlar
			 * çýkarýlarak o kýsýmlar içerisinde kalan satýrlarýn eþleþme ihtimalini ortadan kaldýrýr.
			 */
			

			StringReader stringReader = new StringReader(text);
			reader = new BufferedReader(stringReader);

			Pattern firspattern = Pattern.compile(".*/\\*.*"); // /* Yorum satýr baþlangýcý
			Matcher firstmatcher;

			Pattern lastPattern = Pattern.compile(".*\\*/.*"); // */ Yorum satýr bitiþi
			Matcher lastMatcher;

			Pattern normalPattern = Pattern.compile(".*//.*"); //  // Yorum satýrý
			Matcher normalMatcher;

			boolean firstStepInComment = false;
			String tempLine = "";
			/*
			 * Tüm yorum satýrlarýný bulur.
			 */
			
			while ((line = reader.readLine()) != null) {
				line = line.trim();

				firstmatcher = firspattern.matcher(line);
				if (firstmatcher.matches()&&!(inComment)&& !(line.startsWith("//"))) {
					firstStepInComment = true;
					inComment = true;
					tempLine = line;
				}
				if (inComment) {
					lastMatcher = lastPattern.matcher(line);
					if (lastMatcher.matches()) {
						if (firstStepInComment) {
							commentCount++;
							
						}
						inComment = false;
					}
					if (tempLine != line) {
						firstStepInComment = false;
					}
					if(inComment&& !firstStepInComment) {
					commentCount++;
					}
					
				}else {
					normalMatcher = normalPattern.matcher(line);
					if (normalMatcher.matches()) {
						commentCount++;
					}
				}
				

			}
			
			

			reader.close();

			return commentCount;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private int funCounter(File file) {
		StringBuilder codeBuilder = new StringBuilder();
		try {
			int funCount = 0;
			BufferedReader reader = Files.newBufferedReader(file.toPath());
			String line;

			while ((line = reader.readLine()) != null) {
				codeBuilder.append(line).append("\n");
			}
			String text = codeBuilder.toString();

			Pattern deletePattern = Pattern.compile("//.*|/\\*(?:.|[\\n\\r])*?\\*/");
			Matcher deleteMatcher = deletePattern.matcher(text);
			text = deleteMatcher.replaceAll("");
			deletePattern = Pattern.compile("\".*\"");
			deleteMatcher = deletePattern.matcher(text);
			text = deleteMatcher.replaceAll("");
			
			/*
			 * Önce Kod Ýçerisindeki bütün javadoc ve yorum Satýrlarý kýsmýný ve " " tanýmlamalarýný
			 * çýkartýr. Javadoc ve " " tanýmlamalarý içerisindeki yorum satýrlarýný da saymak istiyorsak
			 * yukarýdaki kod parçasýný kaldýrabiliriz.
			 */
			
			/*
			 * Bu sayede hem yorum satýrlarý içerisinde hem de String "" tanýmlamasý içerisinde kalan kýsýmlar
			 * çýkarýlarak o kýsýmlar içerisinde kalan satýrlarýn eþleþme ihtimalini ortadan kaldýrýr.
			 */
			
			String modifiers = "(public|private|protected|static|final|default|abstract|synchronized)";
			
			Pattern funPattern = Pattern.compile("(" + modifiers + "\\s+)(.+)(\\((.)*\\)(\\s*.*)(\\{))");
			Matcher funMatcher;
			funMatcher = funPattern.matcher(text);
			
			/*
			 * Kod içerisindeki bütün fonksiyon tanýmlamalarýný bulur.
			 */

			
			while (funMatcher.find()) {
				funCount++;

			}

			reader.close();

			return funCount;

		} catch (Exception e) {
		}
		return 0;
	}

	private int codeLineCounter(File file) {
		try {
			String text = new String(Files.readAllBytes(file.toPath()));

			int codeLineCount = 0;

			Pattern deletePattern ;
			Matcher deleteMatcher ;
			deletePattern = Pattern.compile("\".*\"");
			deleteMatcher = deletePattern.matcher(text);
			text = deleteMatcher.replaceAll("");
			deletePattern = Pattern.compile("//.*|/\\*(?:.|[\\n\\r])*?\\*/");
			deleteMatcher = deletePattern.matcher(text);
			text = deleteMatcher.replaceAll("");
			
			/*
			 * Kod içerisindeki bütün yorum satýrlarýný siler geriye sadece kod Satýrlarý kalýr.
			 */
			
			/*
			 * Bu sayede hem yorum satýrlarý içerisinde hem de String "" tanýmlamasý içerisinde kalan kýsýmlar
			 * çýkarýlarak o kýsýmlar içerisinde kalan satýrlarýn eþleþme ihtimalini ortadan kaldýrýr.
			 */

			Pattern codePattern = Pattern.compile("(?!\\s).+");
			Matcher codeMatcher;
			codeMatcher = codePattern.matcher(text);
			
			/*
			 * Sadece kod Satýrlarý kalan kod içerisinde kaç tane kod satýrý olduðunu bulur.
			 */

			while (codeMatcher.find()) {
				codeLineCount++;
			}
			return codeLineCount;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private int javadocCounter(File file) {
		StringBuilder codeBuilder = new StringBuilder();
		try {
			BufferedReader reader = Files.newBufferedReader(file.toPath());
			String line;
			boolean inJavadoc = false;
			int javadocLineCount = 0;
			
			
			while ((line = reader.readLine()) != null) {
				codeBuilder.append(line).append("\n");
			}
			String text = codeBuilder.toString();

			Pattern deletePattern ;
			Matcher deleteMatcher ;
			deletePattern = Pattern.compile("\".*\"");
			deleteMatcher = deletePattern.matcher(text);
			text = deleteMatcher.replaceAll("");
			
			deletePattern = Pattern.compile("//.*|\\/\\*[^\\*/](?:.|[\\r\\n])*?\\*\\/");
			deleteMatcher = deletePattern.matcher(text);
			text = deleteMatcher.replaceAll("");
			
			/*
			 * Kod içerisinde karýþýklýk yaþanmamasý için JavaDoc harici Diðer Yorumlarý ve
			 * String Tanýmlamasý içindeki ("") bütün tanýmlamalarý yok eder.
			 */
			
			StringReader stringReader = new StringReader(text);
			reader = new BufferedReader(stringReader);
			
			Pattern starterPattern = Pattern.compile(".*/\\*\\*((?!/).*)");
			Matcher starterMatcher ;
			
			Pattern lastPattern = Pattern.compile(".*\\*\\/.*");
			Matcher lastMatcher ;
			
			/*
			 * kod içerisinde /** ile baþlatan (* /) ile biten aralýktaki bütün satýrlarý getirir.
			 */
			String tempLine = "";
			boolean firstStepInJavadoc = false;

			while ((line = reader.readLine()) != null) {
				line = line.trim();

				starterMatcher = starterPattern.matcher(line);
				if (starterMatcher.matches()&&!inJavadoc) {
					firstStepInJavadoc = true;
					inJavadoc = true;
					tempLine = line;
				}
				if (inJavadoc) {
					lastMatcher = lastPattern.matcher(line);
					if (lastMatcher.matches()) {
						if (firstStepInJavadoc) {
							javadocLineCount++;
							
						}
						inJavadoc = false;
					}
					if (tempLine != line) {
						firstStepInJavadoc = false;
					}
					if(inJavadoc&& !firstStepInJavadoc) {
					javadocLineCount++;
					}
					
				}
				

			}
			
			

			reader.close();

			return javadocLineCount;
		} catch (IOException e) {
			e.printStackTrace();

		}
		return 0;

	}

}
