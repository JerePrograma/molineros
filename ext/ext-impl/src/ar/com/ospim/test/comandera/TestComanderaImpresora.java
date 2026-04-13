package ar.com.ospim.test.comandera;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;
import javax.print.PrintService;

import com.escpos.EscPos;
import com.escpos.EscPosConst;
import com.escpos.Style;
import com.escpos.image.BitonalThreshold;
import com.escpos.image.EscPosImage;
import com.escpos.image.RasterBitImageWrapper;
import com.output.PrinterOutputStream;

public class TestComanderaImpresora {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		PrintService printService = PrinterOutputStream.getPrintServiceByName("Comandera");
		PrinterOutputStream printerOutputStream = null;

		
		try {
			printerOutputStream = new PrinterOutputStream(printService);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EscPos escpos = null;
		
		
		try {
			
			
			escpos = new EscPos(printerOutputStream);
			
			Style styleLeft = new Style();
			styleLeft.setFontSize(Style.FontSize._1, Style.FontSize._1);
			styleLeft.setJustification(EscPosConst.Justification.Left_Default);
			
			Style styleRight = new Style();
			styleRight.setFontSize(Style.FontSize._1, Style.FontSize._1);
			styleRight.setJustification(EscPosConst.Justification.Right);
			
			Style styleCenter = new Style();
			//styleCenter.setFontSize(Style.FontSize._1, Style.FontSize._1);
			styleCenter.setJustification(EscPosConst.Justification.Center);
			
			Style styleBoldCenter = new Style();
			styleBoldCenter.setFontSize(Style.FontSize._1, Style.FontSize._1);
			styleBoldCenter.setJustification(EscPosConst.Justification.Left_Default);
			styleBoldCenter.setBold(true);
			//
		   RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
	       BufferedImage  githubBufferedImage = ImageIO.read(new File("/Users/30DeJunioConfiteria/Downloads/LogoHOTEL_30_DE_JUNIO.jpg"));
  	       EscPosImage escposImage = new EscPosImage(githubBufferedImage, new BitonalThreshold()); 
	        // print smile image...
		 		
		
			
			imageWrapper.setJustification(EscPosConst.Justification.Center);
			escpos.write(imageWrapper,escposImage );
			escpos.feed(1);

			//Datos del punto de venta
			escpos.writeLF(styleLeft , "PETROLFE SA");
			escpos.writeLF(styleLeft , "C.U.I.T.  Nro.: 30-69613100-3");
			escpos.writeLF(styleLeft , "Ing. Brutos: IB 0083581-01");
			escpos.writeLF(styleLeft , "Domicilio: AV. SAN 14N 2901");
			escpos.writeLF(styleLeft , "C.A.B.A. -CP(1232)");
			escpos.writeLF(styleLeft , "Inicio de Actividades 01/12/1998");
			escpos.writeLF(styleLeft , "IVA RESPONSABLE INSCRIPTO");
			//Datos del punto de venta
			
			lineSeparator(escpos, styleCenter);
			//items
			escpos.writeLF(styleLeft , "4,8150 u. x 47.7900");
			//escpos.feed(1);
			escpos.writeLF(styleLeft , "SUPER");
			//escpos.feed(1);
			escpos.writeLF(styleLeft , genString("4,815 L $47,790" , "(21)[13,52]", "230,11"));
			//items
			escpos.feed(1);
			
			escpos.write(styleBoldCenter , genString("TOTAL " , "23000,11"));  
			
			
			
			escpos.feed(6);
			escpos.cut(EscPos.CutMode.FULL);
			escpos.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

	private static void lineSeparator(EscPos escpos , Style style ) throws UnsupportedEncodingException, IOException{
		escpos.feed(1);
		escpos.writeLF(style, "---------------------------------------------");
		escpos.feed(1);
	}
	/*Formatea en dos columnas  */
	private static String genString(String col1 , String col2) {
		String out = String.format( "%-1s %40s", col1, col2);		
	    return out;
	}
	/*Formatea en tres columnas  */
	private static String genString(String col1 , String col2, String col3) {
		String out = String.format( "%1s %15s %15s", col1, col2, col3);		
	    return out;
	}
}
