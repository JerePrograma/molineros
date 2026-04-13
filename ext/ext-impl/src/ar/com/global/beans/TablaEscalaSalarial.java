package ar.com.global.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;



public class TablaEscalaSalarial {
	private static final long serialVersionUID = -8123166802555856035L;

	private Date fechaDesde;
	private int antiguedadDesde;
	private BigDecimal catA;
	private BigDecimal catB;
	private BigDecimal catC;
	private BigDecimal catD;
	private BigDecimal catE;
	private BigDecimal catF;

	
	private Camara camara;
	
	public static TablaEscalaSalarial getMapping(ResultSet rs)
			throws SQLException {
		TablaEscalaSalarial tabla= new TablaEscalaSalarial();
		
		tabla.setFechaDesde(rs.getDate("fechadesde"));
		tabla.setCamara(TablaEscalaSalarial.Camara.valueOf(rs.getString("camara")));
		tabla.setAntiguedadDesde(rs.getInt("antiguedaddesde"));
		tabla.setCatA(rs.getBigDecimal("cata"));
		tabla.setCatB(rs.getBigDecimal("catb"));
		tabla.setCatC(rs.getBigDecimal("catc"));
		tabla.setCatD(rs.getBigDecimal("catd"));
		tabla.setCatE(rs.getBigDecimal("cate"));
		tabla.setCatF(rs.getBigDecimal("catf"));
		
		return tabla;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public int getAntiguedadDesde() {
		return antiguedadDesde;
	}

	public void setAntiguedadDesde(int antiguedadDesde) {
		this.antiguedadDesde = antiguedadDesde;
	}

	public BigDecimal getCatA() {
		return catA;
	}

	public void setCatA(BigDecimal catA) {
		this.catA = catA;
	}

	public BigDecimal getCatB() {
		return catB;
	}

	public void setCatB(BigDecimal catB) {
		this.catB = catB;
	}

	public BigDecimal getCatC() {
		return catC;
	}

	public void setCatC(BigDecimal catC) {
		this.catC = catC;
	}

	public BigDecimal getCatD() {
		return catD;
	}

	public void setCatD(BigDecimal catD) {
		this.catD = catD;
	}

	public BigDecimal getCatE() {
		return catE;
	}

	public void setCatE(BigDecimal catE) {
		this.catE = catE;
	}

	public BigDecimal getCatF() {
		return catF;
	}

	public void setCatF(BigDecimal catF) {
		this.catF = catF;
	}

	public Camara getCamara() {
		return camara;
	}

	public void setCamara(Camara camara) {
		this.camara = camara;
	}

	public enum Camara {
		FAIM, CAENA, CEPA
	}
}