package ar.com.ospim.procesaArchivos.beans;

import java.math.BigDecimal;
import java.text.ParseException;

public class DetalleSuma {  // Solo se utiliza para SUMA
	
	
	public static int longCodigoOOSS = 6;
	public static int longPeriodo = 6;
	public static int longCantBenef = 7;
	public static int longImporteTransferido = 15;	
	public static int longCapita = 15;	
	public static int longArt2incA = 15;	
	public static int longArt2incB = 15;	
	public static int longArt2incC = 15;	
	public static int longArt3 = 15;	
	public static int longTotalSubsidio = 15;	
//	public static int longAreaReservada = 26;
	
	
	private String codigoOOSS;
	private String periodo;
	private int cantidadBeneficiarios;
	private BigDecimal importeTransferido;	
	private BigDecimal capita;	
	private BigDecimal art2incA;	
	private BigDecimal art2incB;	
	private BigDecimal art2incC;	
	private BigDecimal art3;	
	private BigDecimal totalSubsidio;	
//	private String areaReservada;
	
	
	public DetalleSuma(String line) throws ParseException{
		
		int posicion = 0, finalArchivo = 126;
		
		this.codigoOOSS    = line.substring(posicion,longCodigoOOSS);
		posicion += longCodigoOOSS;
		this.periodo       = line.substring(posicion, posicion+longPeriodo);
		posicion += longPeriodo;
		this.cantidadBeneficiarios = Integer.parseInt(line.substring(posicion,posicion+longCantBenef));
		posicion += longCantBenef;
		this.importeTransferido = new BigDecimal(line.substring(posicion, posicion+longImporteTransferido) );
		posicion += longImporteTransferido;
		this.capita = new BigDecimal(line.substring(posicion, posicion+longCapita) );
		posicion += longCapita;
		this.art2incA = new BigDecimal(line.substring(posicion, posicion+longArt2incA-2)+"."+line.substring(posicion+longArt2incA-2, posicion+longArt2incA) );
		posicion += longArt2incA;
		this.art2incB = new BigDecimal(line.substring(posicion, posicion+longArt2incB-2)+"."+line.substring(posicion+longArt2incB-2, posicion+longArt2incB) );
		posicion += longArt2incB;
		this.art2incC = new BigDecimal(line.substring(posicion, posicion+longArt2incC-2)+"."+line.substring(posicion+longArt2incC-2, posicion+longArt2incC) );
		posicion += longArt2incC;
		this.art3 = new BigDecimal(line.substring(posicion, posicion+longArt3-2)+"."+line.substring(posicion+longArt3-2, posicion+longArt3) );
		posicion += longArt3;
		this.totalSubsidio = new BigDecimal(line.substring(finalArchivo-longTotalSubsidio,finalArchivo-3)+"."+line.substring(finalArchivo-3,finalArchivo-1));  // Esto es porque el archivo esta diferente de la definición
		
	}
	
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("COD OOSS: "+this.codigoOOSS);
		sb.append("\nPERIODO: "+this.periodo);
		sb.append("\nCANT.BENEF.: "+this.cantidadBeneficiarios);
		sb.append("\nIMPORTE SUBSIDIO: "+this.totalSubsidio);

		return sb.toString();
	}

	public static int getLongCodigoOOSS() {
		return longCodigoOOSS;
	}

	public static void setLongCodigoOOSS(int longCodigoOOSS) {
		DetalleSuma.longCodigoOOSS = longCodigoOOSS;
	}

	public static int getLongPeriodo() {
		return longPeriodo;
	}

	public static void setLongPeriodo(int longPeriodo) {
		DetalleSuma.longPeriodo = longPeriodo;
	}

	public static int getLongCantBenef() {
		return longCantBenef;
	}

	public static void setLongCantBenef(int longCantBenef) {
		DetalleSuma.longCantBenef = longCantBenef;
	}

	public static int getLongImporteTransferido() {
		return longImporteTransferido;
	}

	public static void setLongImporteTransferido(int longImporteTransferido) {
		DetalleSuma.longImporteTransferido = longImporteTransferido;
	}

	public static int getLongCapita() {
		return longCapita;
	}

	public static void setLongCapita(int longCapita) {
		DetalleSuma.longCapita = longCapita;
	}

	public static int getLongArt2incA() {
		return longArt2incA;
	}

	public static void setLongArt2incA(int longArt2incA) {
		DetalleSuma.longArt2incA = longArt2incA;
	}

	public static int getLongArt2incB() {
		return longArt2incB;
	}

	public static void setLongArt2incB(int longArt2incB) {
		DetalleSuma.longArt2incB = longArt2incB;
	}

	public static int getLongArt2incC() {
		return longArt2incC;
	}

	public static void setLongArt2incC(int longArt2incC) {
		DetalleSuma.longArt2incC = longArt2incC;
	}

	public static int getLongArt3() {
		return longArt3;
	}

	public static void setLongArt3(int longArt3) {
		DetalleSuma.longArt3 = longArt3;
	}

	public static int getLongTotalSubsidio() {
		return longTotalSubsidio;
	}

	public static void setLongTotalSubsidio(int longTotalSubsidio) {
		DetalleSuma.longTotalSubsidio = longTotalSubsidio;
	}

	public String getCodigoOOSS() {
		return codigoOOSS;
	}

	public void setCodigoOOSS(String codigoOOSS) {
		this.codigoOOSS = codigoOOSS;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public int getCantidadBeneficiarios() {
		return cantidadBeneficiarios;
	}

	public void setCantidadBeneficiarios(int cantidadBeneficiarios) {
		this.cantidadBeneficiarios = cantidadBeneficiarios;
	}

	public BigDecimal getImporteTransferido() {
		return importeTransferido;
	}

	public void setImporteTransferido(BigDecimal importeTransferido) {
		this.importeTransferido = importeTransferido;
	}

	public BigDecimal getCapita() {
		return capita;
	}

	public void setCapita(BigDecimal capita) {
		this.capita = capita;
	}

	public BigDecimal getArt2incA() {
		return art2incA;
	}

	public void setArt2incA(BigDecimal art2incA) {
		this.art2incA = art2incA;
	}

	public BigDecimal getArt2incB() {
		return art2incB;
	}

	public void setArt2incB(BigDecimal art2incB) {
		this.art2incB = art2incB;
	}

	public BigDecimal getArt2incC() {
		return art2incC;
	}

	public void setArt2incC(BigDecimal art2incC) {
		this.art2incC = art2incC;
	}

	public BigDecimal getArt3() {
		return art3;
	}

	public void setArt3(BigDecimal art3) {
		this.art3 = art3;
	}

	public BigDecimal getTotalSubsidio() {
		return totalSubsidio;
	}

	public void setTotalSubsidio(BigDecimal totalSubsidio) {
		this.totalSubsidio = totalSubsidio;
	}
	
}
