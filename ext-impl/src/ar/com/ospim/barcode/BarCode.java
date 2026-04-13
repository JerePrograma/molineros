package ar.com.ospim.barcode;

import java.io.File;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;



/**
 * 
 * @author Kaesar ALNIJRES
 * 
 */

public class BarCode {

	
	public static void main(String args[]){	
		generarCodAmtima();
		//generarCodUOMA(6);
	}
	
	public static void generarCodUOMA(int tipoBoleta){
		//A get a File reference to save the bar code image
		File file = null;
		try {
			//B create the bar code using a String (your data)
			StringBuilder sb= new StringBuilder();
			int verif=0;
			//4 primeras posiciones para el cod. convenio					
			//7 posiciones para Nro de cuenta
			if(tipoBoleta==2){
				file= new File("/home/sistemas-01/Uoma Doc/barcodeUOMA0Barb.jpg");
				sb.append("5783");
				sb.append("7978154");
				//CUIT
				sb.append("27349756078");
				//PERIODO
				sb.append("122011");
				//ID DDJJ
				sb.append("01");
				//ID BOLETA
				sb.append("0006");
				//Importe
				sb.append("00000333333");					
			}else if(tipoBoleta==3){
				file= new File("/home/sistemas-01/Uoma Doc/barcodeUSUFBarb.jpg");
				sb.append("5783");				
				sb.append("7978154");				
				//CUIT
				sb.append("27349756078");
				//PERIODO
				sb.append("122011");
				//ID DDJJ
				sb.append("01");
				//ID BOLETA
				sb.append("0006");
				//Importe
				sb.append("00000333333");				
			}else if(tipoBoleta==4){
				file= new File("/home/sistemas-01/Uoma Doc/barcodeFallecBarb.jpg");
				sb.append("5785");
				sb.append("7873211");
				//CUIT
				sb.append("27349756078");
				//PERIODO
				sb.append("122011");
				//ID DDJJ
				sb.append("01");
				//ID BOLETA
				sb.append("0006");
				//Importe
				sb.append("00000333333");				
			}else if(tipoBoleta==5){
				file=new File("/home/sistemas-01/Uoma Doc/barcodeSolidarioBarb.jpg");
				sb.append("5784");	
				sb.append("7909011");	
				//CUIT
				sb.append("27349756078");
				//PERIODO
				sb.append("122011");
				//ID DDJJ
				sb.append("01");
				//ID BOLETA
				sb.append("0006");
				//Importe
				sb.append("00000333333");				
			}else if(tipoBoleta==6){
				file=new File("/home/sistemas-01/Uoma Doc/barcodeOSPIMBarb.jpg");
				sb.append("5782");
				sb.append("7984846");
				//CUIT
				sb.append("27349756078");
				//PERIODO
				sb.append("122011");
				//ID DDJJ
				sb.append("01");
				//ID BOLETA
				sb.append("0006");
				//Importe
				sb.append("00000333333");				
			}
			
			
			sb.append(String.valueOf(tipoBoleta));				
										
			for(int i=0;i<sb.length();i++){
				verif+=Integer.parseInt(String.valueOf(sb.charAt(i)));
			}
			
			verif=verif*13;
			//2 últimas verificador
			sb.append((verif)%7);
			Barcode barCode = BarcodeFactory.createCode128B(sb.toString());
			barCode.setBarWidth(1);
			barCode.setBarHeight(2);
			 //3 save the generated bar code to the above file as jpeg image
			BarcodeImageHandler.saveJPEG(barCode, file);
		} catch (OutputException e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BarcodeException e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void generarCodAmtima(){
		//A get a File reference to save the bar code image
		File file =  new File("/home/sistemas-01/barcodeAMTIMA26032012-UOMC.jpg");
		try {
			//B create the bar code using a String (your data)
			StringBuilder sb= new StringBuilder();
			int verif=0;
			//4 primeras posiciones para el cod. convenio
			sb.append("5652");			
			//7 posiciones para Nro de cuenta
			sb.append("5953810");
			//CUIT
			sb.append("30531143852");
			//PERIODO
			sb.append("032012");
			//ID DDJJ
			sb.append("01");
			//ID BOLETA
			sb.append("0021");
			//Importe
			sb.append("00010090025");
			//Tipo Boleta AMTIMA
			sb.append("1");			
			
			for(int i=0;i<sb.length();i++){
				verif+=Integer.parseInt(String.valueOf(sb.charAt(i)));
			}
			
			verif=verif*13;
			//último dígito verificador
			sb.append((verif)%7);
			Barcode barCode = BarcodeFactory.createCode128B(sb.toString());
			barCode.setBarWidth(1);
			barCode.setBarHeight(2);
			 //3 save the generated bar code to the above file as jpeg image
			BarcodeImageHandler.saveJPEG(barCode, file);
		} catch (OutputException e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BarcodeException e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
