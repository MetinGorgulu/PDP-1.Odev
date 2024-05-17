package pdp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
*
* Metin G�rg�l�  /  metin.gorgulu@ogr.sakarya.edu.tr
* 05.04.2024
* <p>
* Program�n Main s�n�f�d�r. Kullan�c�dan Github Url ister ve 
* CloneRepository s�n�f�n� kullanarak Url adresindeki Projeyi
* belirtilen klas�r ad�nda klas�r olu�turup i�erisine klonlar.
* E�er klonlama ba�ar�l� ise ClassConrtol s�n�f� ile gereklilikleri
* (.java uzant� ve class olmas�) kontrol ederek FileAnlayzeControl s�n�f� 
* ile de istenen �zellikleri bularak ekrana yazd�r�r.
* </p>
*/
public class Program {

	public static void main(String[] args) {

		List<File> fileList = new ArrayList<>();
		boolean isCloned = false;

		Console.clear();
		while (true) {
			try {
				System.out.println("L�tfen Bir Github Url Adresi Giriniz:");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				String repoUrl = reader.readLine();
				Console.clear();
				String folderName = "Cloned_Files";
				CloneRepository repository = new CloneRepository(repoUrl, folderName);
				isCloned = repository.cloneRepository();
				/*
				 * Verilen Github Url adresindeki projeyi Projenin bulundu�u 
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
					 * Klonlanan projenin her bir dosyas�n� inceler, .java uzant�l� ve i�erisinde
					 * class bulunan dosyalar� ay�klar. classControl s�n�f�yla FileAnalyzeControl
					 * s�n�f�yla da bulunmas� istenen sat�r ve fonksiyon say�lar� bulunur. 
					 * En sonunda yerel dizinde olu�turulan klon dosya tamamen silinir.
					 */
				} else {
					System.out.println("L�tfen Ge�erli Bir Main Url Adresi Giriniz.");
					System.out.println("--------------------------------------------");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
