package ar.com.ospim.afiliados.beans;

import java.util.Date;

import ar.com.ospim.util.DateUtils;

/**
 * @author Administrador
 * @version 1.0
 * @created 29-Jul-2010 11:34:23 a.m.
 */
public class AfiDocumentacion {	
	private Documento documento;
	private Date fecha_ingre;
	private Date fecha_baja;	
	private Afiliado afiliado;
	private int id;
	private String codigoCUD;
	
	public AfiDocumentacion(){}
	
	public AfiDocumentacion(String cuil_titular, int inte, int id_doc,String descrip, Date fecha_ingreso,Date fecha_egreso, int id){
		Afiliado afiliado=new Afiliado(cuil_titular,inte);
		this.afiliado=afiliado;
		this.documento=new Documento(id_doc,descrip);
		this.fecha_ingre=fecha_ingreso;
		this.fecha_baja=fecha_egreso;				
		this.id=id;
	}

	public AfiDocumentacion(String cuil_titular, int inte, int id_doc,String descrip, Date fecha_ingreso,Date fecha_egreso, int id,String codigoCUD){
		Afiliado afiliado=new Afiliado(cuil_titular,inte);
		this.afiliado=afiliado;
		this.documento=new Documento(id_doc,descrip);
		this.fecha_ingre=fecha_ingreso;
		this.fecha_baja=fecha_egreso;				
		this.id=id;
		this.codigoCUD=codigoCUD;
	}
	
	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public Date getFecha_ingre() {
		return fecha_ingre;
	}

	public String getFecha_ingreAsString() {
		return fecha_ingre!=null?DateUtils.format(fecha_ingre,"dd/MM/yyyy"):"";
	}
	
	public void setFecha_ingre(Date fechaIngre) {
		fecha_ingre = fechaIngre;
	}

	public Date getFecha_baja() {
		return fecha_baja;
	}
	
	public String getFecha_bajaAsString() {
		return fecha_baja!=null?DateUtils.format(fecha_baja,"dd/MM/yyyy"):"";
	}

	public void setFecha_baja(Date fechaBaja) {
		fecha_baja = fechaBaja;
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodigoCUD() {
		return codigoCUD;
	}

	public void setCodigoCUD(String codigoCUD) {
		this.codigoCUD = codigoCUD;
	}

		
	  
}