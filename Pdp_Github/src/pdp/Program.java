package pdp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
*
* Metin Görgülü  /  metin.gorgulu@ogr.sakarya.edu.tr
* 05.04.2024
* <p>
* Programýn Main sýnýfýdýr. Kullanýcýdan Github Url ister ve 
* CloneRepository sýnýfýný kullanarak Url adresindeki Projeyi
* belirtilen klasör adýnda klasör oluþturup içerisine klonlar.
* Eðer klonlama baþarýlý ise ClassConrtol sýnýfý ile gereklilikleri
* (.java uzantý ve class olmasý) kontrol ederek FileAnlayzeControl sýnýfý 
* ile de istenen özellikleri bularak ekrana yazdýrýr.
* </p>
*/
public class Program {

	public static void main(String[] args) {

		List<File> fileList = new ArrayList<>();
		boolean isCloned = false;

		Console.clear();
		while (true) {
			try {
				System.out.println("Lütfen Bir Github Url Adresi Giriniz:");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				String repoUrl = reader.readLine();
				Console.clear();
				String folderName = "Cloned_Files";
				CloneRepository repository = new CloneRepository(repoUrl, folderName);
				isCloned = repository.cloneRepository();
				/*
				 * Verilen Github Url adresindeki projeyi Projenin bulunduðu 
				 * yerel dosya dizinine klonlar
				 */

				if (isCloned) {
					ClassControl classConrol = new ClassControl(folderName);
					fileList = classConrol.fileReader();
					FileAnalyzeControl fileAnalyze = new FileAnalyzeControl(fileList);
					fileAnalyze.print();
					repository.deleteFiles();
					break;
					/*
					 * Klonlanan projenin her bir dosyasýný inceler, .java uzantýlý ve içerisinde
					 * class bulunan dosyalarý ayýklar. classControl sýnýfýyla FileAnalyzeControl
					 * sýnýfýyla da bulunmasý istenen satýr ve fonksiyon sayýlarý bulunur. 
					 * En sonunda yerel dizinde oluþturulan klon dosya tamamen silinir.
					 */
				} else {
					System.out.println("Lütfen Geçerli Bir Main Url Adresi Giriniz.");
					System.out.println("--------------------------------------------");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
