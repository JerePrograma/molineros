package ar.com.uoma.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class Paritaria implements Serializable {
	private static Log _log = LogFactoryUtil.getLog(Paritaria.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date fechaAltaParitaria;
	private String camara;

	private String catA;
	private String catB;
	private String catC;
	private String catD;
	private String catE;
		
	private String catJornalesA;
	private String catJornalesB;
	private String catJornalesC;
	private String catJornalesD;
	private String catJornalesE;
	
	private List<EscalaSueldosBasicos> escalaSueldosBasicos;
	private List<EscalaSueldosBasicos> escalaSueldosBasicosJornales;
	
		
	public String getCatA() {
		return catA;
	}

	public void setCatA(String catA) {
		this.catA = catA;
	}

	public String getCatB() {
		return catB;
	}

	public void setCatB(String catB) {
		this.catB = catB;
	}

	public String getCatC() {
		return catC;
	}

	public void setCatC(String catC) {
		this.catC = catC;
	}

	public String getCatD() {
		return catD;
	}

	public void setCatD(String catD) {
		this.catD = catD;
	}

	public String getCatE() {
		return catE;
	}

	public void setCatE(String catE) {
		this.catE = catE;
	}

	public String getCatJornalesA() {
		return catJornalesA;
	}

	public void setCatJornalesA(String catJornalesA) {
		this.catJornalesA = catJornalesA;
	}

	public String getCatJornalesB() {
		return catJornalesB;
	}

	public void setCatJornalesB(String catJornalesB) {
		this.catJornalesB = catJornalesB;
	}

	public String getCatJornalesC() {
		return catJornalesC;
	}

	public void setCatJornalesC(String catJornalesC) {
		this.catJornalesC = catJornalesC;
	}

	public String getCatJornalesD() {
		return catJornalesD;
	}

	public void setCatJornalesD(String catJornalesD) {
		this.catJornalesD = catJornalesD;
	}

	public String getCatJornalesE() {
		return catJornalesE;
	}

	public void setCatJornalesE(String catJornalesE) {
		this.catJornalesE = catJornalesE;
	}

	public List<EscalaSueldosBasicos> getEscalaSueldosBasicos() {
		return escalaSueldosBasicos;
	}

	public void setEscalaSueldosBasicos(List<EscalaSueldosBasicos> escalaSueldosBasicos) {
		this.escalaSueldosBasicos = escalaSueldosBasicos;
	}

	public List<EscalaSueldosBasicos> getEscalaSueldosBasicosJornales() {
		return escalaSueldosBasicosJornales;
	}

	public void setEscalaSueldosBasicosJornales(List<EscalaSueldosBasicos> escalaSueldosBasicosJornales) {
		this.escalaSueldosBasicosJornales = escalaSueldosBasicosJornales;
	}

	public Date getFechaAltaParitaria() {
		return fechaAltaParitaria;
	}
	
	public void setFechaAltaParitaria(Date fechaAltaParitaria) {
		this.fechaAltaParitaria = fechaAltaParitaria;
	}
	
	public String getCamara() {
		return camara;
	}
	
	public void setCamara(String camara) {
		this.camara = camara;
	}
	
	public static Paritaria getMapping(ResultSet rs, String prefix) {
		Paritaria p = new Paritaria();
		
		try {
			p.setCamara(rs.getString(prefix + "camara"));
			p.setFechaAltaParitaria(rs.getDate(prefix + "fechadesde"));
			
			
		} catch (SQLException e) {
			_log.debug(e);
		}
		return p;
		
	}
	
	
	public static EscalaSueldosBasicos getMappingSuedos(ResultSet rs, String prefix) {
		EscalaSueldosBasicos sueldo = new EscalaSueldosBasicos ();
		
		try {
			sueldo.setAntiguedad(rs.getString(prefix + "antiguedaddesde"));
			sueldo.setPorcentaje(rs.getString(prefix + "porcentaje"));
			sueldo.setCatA(rs.getString(prefix + "cat_a"));
			sueldo.setCatB(rs.getString(prefix + "cat_b"));
			sueldo.setCatC(rs.getString(prefix + "cat_c"));
			sueldo.setCatD(rs.getString(prefix + "cat_d"));
			sueldo.setCatE(rs.getString(prefix + "cat_e"));
			
			
		} catch (SQLException e) {
			_log.error(e);
		}
		return sueldo;
		
	}
	
	
}
