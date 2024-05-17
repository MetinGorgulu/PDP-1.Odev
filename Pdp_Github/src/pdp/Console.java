package pdp;

import java.io.IOException;
/**
*
* Metin G�rg�l�  /  metin.gorgulu@ogr.sakarya.edu.tr
* 05.04.2024
* <p>
* Bu s�n�f console daki yaz�lar� sildiren clear fonksiyonu bar�nd�r�.
* </p>
*/
public class Console {
	
	/*
	 * bu fonksiyon consoledaki yaz�lari sildirir. Java n�n 
	 * ta��nabilirli�i %100 olmas�ndan dolay� bu komut derleyicide
	 * yaz�lar� silmez ama cmd console unda �al���r.
	 */
	public static void clear(){
		try {
			if(System.getProperty("os.name").contains("Windows")) {
				new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
			}else {
				Runtime.getRuntime().exec("clear");	
			}
			
		}catch (IOException | InterruptedException ex) {
		}
	}

}
