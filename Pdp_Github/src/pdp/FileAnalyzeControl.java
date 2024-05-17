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
* Metin G�rg�l�  /  metin.gorgulu@ogr.sakarya.edu.tr
* 05.04.2024
* <p>
* Bu s�n�f bir File Listesi parametre alarak olu�turulur. �devde bulunmas� istenen 
* Javadoc Sat�r Say�s�, Comment Sat�r Say�s�, Code Sat�r Say�s�, T�m Sat�r Say�s�,
* Fonksiyon Say�s� ve Yorum Sapma Y�zdesini bulma fonksiyonlar� bu s�n�fta yer al�r.
* print() fonksiyonu ile �nce istenilen de�erler tek tek bulunur ve ekrana yazd�r�l�r.
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
			 * �stenilen Sat�r ve fonksiyon say�lar�n� buldurur.
			 * Daha sonra bunlar� belirli d�zene g�re Console a yazd�r�r.
			 */

			System.out.println("S�n�f               : " + file.getName());
			System.out.println("Javadoc Sat�r Say�s�: " + javadoc);
			System.out.println("Yorum Sat�r Say�s�  : " + comment);
			System.out.println("Kod Sat�r Say�s�    : " + codeLine);
			System.out.println("LOC                 : " + loc);
			System.out.println("Fonksiyon Say�s�    : " + fun);
			if (undefiniteNumber) {
				System.out.println("Yorum Sapma Y�zdesi : % Belirsiz");
			} else {
				String deviation = String.format("%.2f", deviationOfComment);
				System.out.println("Yorum Sapma Y�zdesi : % " + deviation);
			}

			System.out.println("-----------------------------------------------");
		}
	}

	private double calculatorDeviationOfComment() {
		if (!(fun == 0)) {
			
			undefiniteNumber = false; //E�er Fonksiyon say�s� 0 ise Herhangi Bir say� 0 a b�l�nemeyece�i i�in Belirsiz Say� olur.
			
			double YG, YH, result, docNumber, commentNumber, funNumber, codeLineNumber;
			docNumber = (double) javadoc;
			commentNumber = (double) comment;
			funNumber = (double) fun;
			codeLineNumber = (double) codeLine;
			//��lem hatas� olmamas� i�in b�t�n say�lar double a d�n��t�r�l�r ve i�lemler yap�l�r.
			
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
			 * Matematiksel algoritma i�erisinde sadece fonksiyon say�s� 0 oldu�unda
			 * sonu� belirsiz ��kmaktad�r.
			 */
			if(!(YH == 0.0 )) {
				result = ((100 * YG) / YH) - 100;

				return result;
			}
			
		} 
		undefiniteNumber = true; //E�er Fonksiyon say�s� 0 ise Herhangi Bir say� 0 a b�l�nemeyece�i i�in Belirsiz Say� olur.
		return 0.0;
	}

	private int LOC(File file) {
		try {
			BufferedReader reader = Files.newBufferedReader(file.toPath());
			
			@SuppressWarnings("unused")
			String line;
			
			int locCount = 0;
			
			/*
			 * Kod i�erisindeki fark etmeksizin b�t�n Sat�r Say�s�n� bulur.
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
			 * �nce Kod ��erisindeki b�t�n javadoc k�sm�n� ve " " tan�mlamalar�n� ��kart�r.
			 * Javadoc ve " " tan�mlamalar� i�erisindeki yorum sat�rlar�n� da saymak istiyorsak
			 * yukar�daki kod par�as�n� kald�rabiliriz.
			 */
			
			/*
			 * Bu sayede hem Javadoc yorum sat�rlar� i�erisinde hem de String "" tan�mlamas� i�erisinde kalan k�s�mlar
			 * ��kar�larak o k�s�mlar i�erisinde kalan sat�rlar�n e�le�me ihtimalini ortadan kald�r�r.
			 */
			

			StringReader stringReader = new StringReader(text);
			reader = new BufferedReader(stringReader);

			Pattern firspattern = Pattern.compile(".*/\\*.*"); // /* Yorum sat�r ba�lang�c�
			Matcher firstmatcher;

			Pattern lastPattern = Pattern.compile(".*\\*/.*"); // */ Yorum sat�r biti�i
			Matcher lastMatcher;

			Pattern normalPattern = Pattern.compile(".*//.*"); //  // Yorum sat�r�
			Matcher normalMatcher;

			boolean firstStepInComment = false;
			String tempLine = "";
			/*
			 * T�m yorum sat�rlar�n� bulur.
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
			 * �nce Kod ��erisindeki b�t�n javadoc ve yorum Sat�rlar� k�sm�n� ve " " tan�mlamalar�n�
			 * ��kart�r. Javadoc ve " " tan�mlamalar� i�erisindeki yorum sat�rlar�n� da saymak istiyorsak
			 * yukar�daki kod par�as�n� kald�rabiliriz.
			 */
			
			/*
			 * Bu sayede hem yorum sat�rlar� i�erisinde hem de String "" tan�mlamas� i�erisinde kalan k�s�mlar
			 * ��kar�larak o k�s�mlar i�erisinde kalan sat�rlar�n e�le�me ihtimalini ortadan kald�r�r.
			 */
			
			String modifiers = "(public|private|protected|static|final|default|abstract|synchronized)";
			
			Pattern funPattern = Pattern.compile("(" + modifiers + "\\s+)(.+)(\\((.)*\\)(\\s*.*)(\\{))");
			Matcher funMatcher;
			funMatcher = funPattern.matcher(text);
			
			/*
			 * Kod i�erisindeki b�t�n fonksiyon tan�mlamalar�n� bulur.
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
			 * Kod i�erisindeki b�t�n yorum sat�rlar�n� siler geriye sadece kod Sat�rlar� kal�r.
			 */
			
			/*
			 * Bu sayede hem yorum sat�rlar� i�erisinde hem de String "" tan�mlamas� i�erisinde kalan k�s�mlar
			 * ��kar�larak o k�s�mlar i�erisinde kalan sat�rlar�n e�le�me ihtimalini ortadan kald�r�r.
			 */

			Pattern codePattern = Pattern.compile("(?!\\s).+");
			Matcher codeMatcher;
			codeMatcher = codePattern.matcher(text);
			
			/*
			 * Sadece kod Sat�rlar� kalan kod i�erisinde ka� tane kod sat�r� oldu�unu bulur.
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
			 * Kod i�erisinde kar���kl�k ya�anmamas� i�in JavaDoc harici Di�er Yorumlar� ve
			 * String Tan�mlamas� i�indeki ("") b�t�n tan�mlamalar� yok eder.
			 */
			
			StringReader stringReader = new StringReader(text);
			reader = new BufferedReader(stringReader);
			
			Pattern starterPattern = Pattern.compile(".*/\\*\\*((?!/).*)");
			Matcher starterMatcher ;
			
			Pattern lastPattern = Pattern.compile(".*\\*\\/.*");
			Matcher lastMatcher ;
			
			/*
			 * kod i�erisinde /** ile ba�latan (* /) ile biten aral�ktaki b�t�n sat�rlar� getirir.
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
