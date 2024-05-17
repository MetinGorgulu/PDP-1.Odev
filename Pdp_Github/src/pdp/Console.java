package pdp;

import java.io.IOException;
/**
*
* Metin Görgülü  /  metin.gorgulu@ogr.sakarya.edu.tr
* 05.04.2024
* <p>
* Bu sýnýf console daki yazýlarý sildiren clear fonksiyonu barýndýrý.
* </p>
*/
public class Console {
	
	/*
	 * bu fonksiyon consoledaki yazýlari sildirir. Java nýn 
	 * taþýnabilirliði %100 olmasýndan dolayý bu komut derleyicide
	 * yazýlarý silmez ama cmd console unda çalýþýr.
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
