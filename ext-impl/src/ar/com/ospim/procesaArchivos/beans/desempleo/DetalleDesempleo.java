package ar.com.ospim.procesaArchivos.beans.desempleo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetalleDesempleo {
	private String claveDesempleo;
	private String finPago;
	private int codParen;
	private int tipoDoc;
	private int nroDoc;
	private String provEmi;
	private String cuil;
	private Date fechaNac;
	private String apeNombre;
	private Date fechaVig;
	private String sexo;
	private Date fechaIniRel;
	private Date fechaCese;
	private int codOS;
	private Date fechaProceso;
	private String cuilTitular;
	
	public DetalleDesempleo(String line) throws ParseException{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2=new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat sdf3=new SimpleDateFormat("yyMM");
		this.claveDesempleo=line.substring(0,13);
		System.out.println("CLAVE: "+this.claveDesempleo);
		this.finPago=line.substring(14,15);
		System.out.println("finPAgo: "+this.finPago);
		this.codParen=Integer.parseInt(line.substring(16,18));
		System.out.println("codParen: "+this.codParen);
		this.tipoDoc=Integer.parseInt(line.substring(19,21));
		System.out.println("tipoDoc: "+this.tipoDoc);
		this.nroDoc=Integer.parseInt(line.substring(22,30));
		System.out.println("nroDoc: "+this.nroDoc);
		this.provEmi=line.substring(31,34);
		System.out.println("provEmi: "+this.provEmi);
		this.cuil=line.substring(35,46);		
		System.out.println("cuil: "+this.cuil);
		System.out.println("fechaNac: "+line.substring(47,55));
		this.fechaNac=sdf.parse(line.substring(47,55));
		System.out.println("fechaNac: "+this.fechaNac);
		this.apeNombre=line.substring(56,84);
		System.out.println("apeNombre: "+this.apeNombre);
		System.out.println("fechaVig: "+line.substring(85,95));
		this.fechaVig=sdf2.parse(line.substring(85,95));
		System.out.println("fechaVig: "+this.fechaVig);
		this.sexo=line.substring(96,97);
		System.out.println("sexo: "+this.sexo);
		System.out.println("fechaIniRel: "+line.substring(98,102));
		if(!line.substring(98,102).equals("0000")){
			this.fechaIniRel=sdf3.parse(line.substring(98,102));
		}		
		System.out.println("fechaIniRel: "+this.fechaIniRel);
		this.fechaCese=sdf3.parse(line.substring(103,107));
		System.out.println("fechaCese: "+this.fechaCese);
		this.codOS=Integer.parseInt(line.substring(108,114));
		System.out.println("CodOS: "+this.codOS);
		this.fechaProceso=sdf.parse(line.substring(115,123));
		System.out.println("fechaProceso: "+this.fechaProceso);
		this.cuilTitular=line.substring(124,135);	
		System.out.println("cuil titular: "+this.cuilTitular);
	}
	
	public String toString(){
		StringBuffer sbf=new StringBuffer();
		sbf.append("Clave Desempleo: "+this.claveDesempleo);
		sbf.append("\nFin Pago: "+this.finPago);
		sbf.append("\nCod Paren: "+this.codParen);
		sbf.append("\nTipoDoc: "+this.tipoDoc);
		sbf.append("\nNroDoc: "+this.nroDoc);
		sbf.append("\nProvEmi: "+this.provEmi);
		sbf.append("\nCuil: "+this.cuil);
		sbf.append("\nFechaNac: "+this.fechaNac);
		sbf.append("\nApeNombre: "+this.apeNombre);
		sbf.append("\nFechaVig: "+this.fechaVig);
		sbf.append("\nSexo: "+this.sexo);
		sbf.append("\nFechaIniRela: "+this.fechaIniRel);
		sbf.append("\nFechaCese: "+this.fechaCese);
		sbf.append("\nCodOs: "+this.codOS);
		sbf.append("\nFechaProce: "+this.fechaProceso);
		sbf.append("\nCuil: "+this.cuilTitular);
		return sbf.toString();
	}
	
	public String getClaveDesempleo() {
		return claveDesempleo;
	}
	public void setClaveDesempleo(String claveDesempleo) {
		this.claveDesempleo = claveDesempleo;
	}
	public String getFinPago() {
		return finPago;
	}
	public void setFinPago(String finPago) {
		this.finPago = finPago;
	}
	public int getCodParen() {
		return codParen;
	}
	public void setCodParen(int codParen) {
		this.codParen = codParen;
	}
	public int getTipoDoc() {
		return tipoDoc;
	}
	public void setTipoDoc(int tipoDoc) {
		this.tipoDoc = tipoDoc;
	}
	public int getNroDoc() {
		return nroDoc;
	}
	public void setNroDoc(int nroDoc) {
		this.nroDoc = nroDoc;
	}
	public String getProvEmi() {
		return provEmi;
	}
	public void setProvEmi(String provEmi) {
		this.provEmi = provEmi;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	public Date getFechaNac() {
		return fechaNac;
	}
	public void setFechaNac(Date fechaNac) {
		this.fechaNac = fechaNac;
	}
	public String getApeNombre() {
		return apeNombre;
	}
	public void setApeNombre(String apeNombre) {
		this.apeNombre = apeNombre;
	}
	public Date getFechaVig() {
		return fechaVig;
	}
	public void setFechaVig(Date fechaVig) {
		this.fechaVig = fechaVig;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public Date getFechaIniRel() {
		return fechaIniRel;
	}
	public void setFechaIniRel(Date fechaIniRel) {
		this.fechaIniRel = fechaIniRel;
	}
	public Date getFechaCese() {
		return fechaCese;
	}
	public void setFechaCese(Date fechaCese) {
		this.fechaCese = fechaCese;
	}
	public int getCodOS() {
		return codOS;
	}
	public void setCodOS(int codOS) {
		this.codOS = codOS;
	}
	public Date getFechaProceso() {
		return fechaProceso;
	}
	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}
	public String getCuilTitular() {
		return cuilTitular;
	}
	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}
	
	
}
