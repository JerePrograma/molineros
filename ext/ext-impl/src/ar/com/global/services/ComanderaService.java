package ar.com.global.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.print.PrintService;

import org.apache.log4j.Logger;

import com.escpos.EscPos;
import com.escpos.EscPosConst;
import com.escpos.Style;
import com.escpos.image.BitonalThreshold;
import com.escpos.image.EscPosImage;
import com.escpos.image.RasterBitImageWrapper;
import com.output.PrinterOutputStream;

public class ComanderaService {
	
  private static final String nameDevice = "Comandera";
  private static File logo;
  private static List<String>cabecera;
  private static List<String>cuerpo;
  private static List<String>pie;
  private static Logger _log = Logger.getLogger(ComanderaService.class);
  
  public String getNameDevice() {
	return nameDevice;
  }
  
//  public void setNameDevice(String nameDevice) {
//	this.nameDevice = nameDevice;
//  }

  public List<String> getCabecera() {
	return cabecera;
  }
  
  public void setCabecera(List<String> cabecera) {
	this.cabecera = cabecera;
  }
  
  public List<String> getCuerpo() {
	return cuerpo;
  }
  
  public void setCuerpo(List<String> cuerpo) {
	this.cuerpo = cuerpo;
  }
  
  public List<String> getPie() {
	return pie;
  }

  public void setPie(List<String> pie) {
	this.pie = pie;
  }
	
  
  public static File getLogo() {
	return logo;
  }

  public static void setLogo(File logo) {
	ComanderaService.logo = logo;
  }

  public static void imprimirTicketFactura() {
	   PrintService printService = PrinterOutputStream.getPrintServiceByName(nameDevice);
	   PrinterOutputStream printerOutputStream = null;
	  
	   try {
			printerOutputStream = new PrinterOutputStream(printService);
		} catch (IOException e) {
			_log.error(e);
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
			styleCenter.setJustification(EscPosConst.Justification.Center);
			
			Style styleBoldCenter = new Style();
			styleBoldCenter.setFontSize(Style.FontSize._1, Style.FontSize._1);
			styleBoldCenter.setJustification(EscPosConst.Justification.Left_Default);
			styleBoldCenter.setBold(true);
			try {
			  RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
		      BufferedImage  githubBufferedImage = ImageIO.read(logo);
		      EscPosImage escposImage = new EscPosImage(githubBufferedImage, new BitonalThreshold()); 
	        	
			  imageWrapper.setJustification(EscPosConst.Justification.Center);
		 	  escpos.write(imageWrapper,escposImage );
			  escpos.feed(1);
			}catch(Exception e) {
				_log.debug(e);
//				e.printStackTrace();
			}
			
			//CABECERA
			 for(String s :cabecera) {
				  escpos.writeLF(styleLeft ,s);
			 }
			
//			lineSeparatorHead(escpos, styleCenter);
			
			//CUERPO
			 for(String s :cuerpo) {
				  escpos.writeLF(styleLeft ,s);
//				  lineSeparator(escpos, styleCenter);
			 }
			 
			 //PIE
			 escpos.feed(4);
			 
			for(String s :pie) {
			   escpos.writeLF(styleLeft ,s);
//			   lineSeparator(escpos, styleCenter);
			}
			
			escpos.feed(6);
			escpos.cut(EscPos.CutMode.FULL);
			escpos.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			_log.error(e);
		} catch (IOException e) {
			e.printStackTrace();
			_log.error(e);
		}
		
  }
  
  
  private static void lineSeparator(EscPos escpos , Style style ) throws UnsupportedEncodingException, IOException{
	escpos.feed(1);
	escpos.writeLF(style, "---------------------------------------------");
	escpos.feed(1);
  }
  
  private static void lineSeparatorHead(EscPos escpos , Style style ) throws UnsupportedEncodingException, IOException{
		escpos.feed(1);
		escpos.writeLF(style, "_____________________________________________");
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
  
  private static void printCabecera() {
	 
	  
  }
  
}


