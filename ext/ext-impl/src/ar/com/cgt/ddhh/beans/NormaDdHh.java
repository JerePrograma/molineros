package ar.com.cgt.ddhh.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;



/**
 * @author Sergio Valentini
 * @version 1.0
 * @created 10-06-2013 05:27:49 p.m.
 */
public class NormaDdHh implements Serializable {
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 6282750233948950289L;
	
	  private Integer id;
	  private String sistema;
	  private String numero;
	  private String fuenteDependencia;
	  private String autor;
	  private Date fecha;
	  private String lugar;
	  private String resumen;
	  private String contenido;
	  private String link;
	  private String sigla;
	  private String incLegisNac;
	  private Date alta_fecha;
	  private String alta_usr;
	  private Date modi_fecha;
	  private String modi_usr;
	  private Date baja_fecha;
	  private String baja_usr;
	  private TemasNormasDDHH tema;
	  private TiposNormasDDHH tipo;
	
	public NormaDdHh(){
		super();
	}
	
	public NormaDdHh(Integer id, String sistema,String numero, String fuenteDependencia,
			String autor,Date fecha, String lugar, String resumen, String contenido, String link, String sigla, String incLegNac){
		
		super();
		this.id=id;
		this.sistema=sistema;
		this.numero=numero;
		this.fuenteDependencia=fuenteDependencia;
		this.autor=autor;
		this.fecha=fecha;
		this.lugar=lugar;
		this.resumen=resumen;
		this.contenido=contenido;
		this.link=link;
		this.sigla=sigla;
		this.incLegisNac=incLegNac;
		

	}

	public static NormaDdHh getMapping(ResultSet rs)
			throws SQLException {
		
		NormaDdHh ndh = new NormaDdHh();
		
		ndh.setId(rs.getInt("id"));
		ndh.setSistema(rs.getString("sistema"));
		ndh.setNumero(rs.getString("numero"));
		ndh.setFuenteDependencia(rs.getString("fuente_dependencia"));
		ndh.setAutor(rs.getString("autor"));
		ndh.setFecha(rs.getDate("fecha"));
		ndh.setLugar(rs.getString("lugar"));
		ndh.setResumen(rs.getString("resumen"));
		ndh.setContenido(rs.getString("contenido"));
		ndh.setLink(rs.getString("link"));
		ndh.setSigla(rs.getString("sigla"));
		ndh.setIncLegisNac(rs.getString("inc_legis_nac"));
		return ndh;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getFuenteDependencia() {
		return fuenteDependencia;
	}

	public void setFuenteDependencia(String fuenteDependencia) {
		this.fuenteDependencia = fuenteDependencia;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public String getResumen() {
		return resumen;
	}

	public void setResumen(String resumen) {
		this.resumen = resumen;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getIncLegisNac() {
		return incLegisNac;
	}

	public void setIncLegisNac(String incLegisNac) {
		this.incLegisNac = incLegisNac;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String alta_usr) {
		this.alta_usr = alta_usr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modi_fecha) {
		this.modi_fecha = modi_fecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modi_usr) {
		this.modi_usr = modi_usr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String baja_usr) {
		this.baja_usr = baja_usr;
	}

	public TemasNormasDDHH getTema() {
		return tema;
	}

	public void setTema(TemasNormasDDHH tema) {
		this.tema = tema;
	}

	public TiposNormasDDHH getTipo() {
		return tipo;
	}

	public void setTipo(TiposNormasDDHH tipo) {
		this.tipo = tipo;
	}
	
}
