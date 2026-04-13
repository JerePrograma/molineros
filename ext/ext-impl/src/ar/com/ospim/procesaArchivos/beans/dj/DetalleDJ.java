package ar.com.ospim.procesaArchivos.beans.dj;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetalleDJ implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private String codigoObraSocial;
	private Date periodo;
	private String cuit;
	private String cuil;
	private BigDecimal remuneracionAfectOS;
	private BigDecimal importeAdicionalOS;
	private String zona;
	private String cantGrupoFamiliar;
	private String cantAdherentesGrupoFamiliar;
	private Integer secObligacion;
	private String condicionCuil;
	private String situacionCuil;
	private String actividad;
	private Integer modalidad;
	private String codigoSiniestro;
	private BigDecimal aporteAdicionalOS;
	private String versionAplicativo;
	private BigDecimal remuneracionDecreto1273_02;
	private String esposa;
	private BigDecimal excedenteAporteOS;
	private boolean declaroRetenciones;
	private boolean declaro;
	private Date fechaPresentacion;
	private Date fechaProceso;
	private char original;
	private BigDecimal importeBaseContribucionOS;
	
	private BigDecimal aporteBasicoOS;
	private BigDecimal contribucionOS;
	private BigDecimal remuneracionTotal;
	private String obraSocialInformada;

	public DetalleDJ(String line) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfPeriodo = new SimpleDateFormat("yyMM");
		
		Integer version=-1;
		
		try {
			fechaPresentacion = sdf.parse(line.substring(104, 114));
			version=0;
		} catch (Exception e) {}
		
        if(version<0){
        	try {
    			fechaPresentacion = sdf.parse(line.substring(109, 119));
    			version=1;
    		} catch (Exception e) {}
        }
		
        if(version<0){
        	try {
    			fechaPresentacion = sdf.parse(line.substring(126, 136));
    			version=2;
    		} catch (Exception e) {}
        }
		
		/*
		boolean versionNueva = true;
		try {
			fechaPresentacion = sdf.parse(line.substring(109, 119));// 10
		} catch (Exception e) {
			versionNueva = false;
		}
        */
		if (version==1) {
			codigoObraSocial = line.substring(0, 6).trim();// 6
			periodo = sdfPeriodo.parse(line.substring(6, 10));// 4
			cuit = line.substring(10, 21).trim();// 11
			cuil =  line.substring(21, 32).trim();// 11
			remuneracionAfectOS = new BigDecimal(line.substring(32, 42) + "."
					+ line.substring(42, 44));// 12
			importeAdicionalOS = new BigDecimal(line.substring(44, 50) + "."
					+ line.substring(50, 52));// 8
			zona = line.substring(52, 54).trim();// 2
			cantGrupoFamiliar = line.substring(54, 56);// 2
			cantAdherentesGrupoFamiliar = line.substring(56, 58);// 2
			secObligacion = Integer.valueOf(line.substring(58, 61));// 3
			condicionCuil = line.substring(61, 63).trim();// 2
			situacionCuil = line.substring(63, 65).trim();// 2
			actividad = line.substring(67, 68).trim();// 2
			modalidad = Integer.parseInt(line.substring(68, 71));// 3
			codigoSiniestro = line.substring(71, 73).trim();// 2
			aporteAdicionalOS = new BigDecimal(line.substring(73, 79) + "."
					+ line.substring(79, 81));// 8
			versionAplicativo = line.substring(81, 83);// 2
			remuneracionDecreto1273_02 = new BigDecimal(line.substring(83, 92)
					+ "." + line.substring(92, 94));// 9
			esposa = line.substring(94, 95);// 1
			excedenteAporteOS = new BigDecimal(line.substring(95, 105) + "."
					+ line.substring(105, 107));// 12
			declaroRetenciones = line.substring(107, 108).equals("N") ? false
					: true;// 1
			declaro = line.substring(108, 109).equals("N") ? false : true;// 1
			fechaPresentacion = sdf.parse(line.substring(109, 119));// 10
			fechaProceso = sdf.parse(line.substring(119, 129));// 10
			original = line.charAt(129);// 1
			// filler = line.substring(125, 155);// 31
			if (line.length() > 175) {
				String importeParte1 = line.substring(164, 173).trim();
				String importeParte2 = line.substring(173, 175).trim();
				if (importeParte1.equals("")) {
					importeParte1 = "0";
				}
				if (importeParte2.equals("")) {
					importeParte2 = "0";
				}
				importeBaseContribucionOS = new BigDecimal(importeParte1 + "."
						+ importeParte2);// 11
			}
			// line.substring( 164, 199);// 36 3 uso futuro
		} else if(version==0){
			codigoObraSocial = line.substring(0, 6).trim();// 6
			periodo = sdfPeriodo.parse(line.substring(6, 10));// 4
			cuit = line.substring(10, 21).trim();// 11
			cuil = line.substring(21, 32).trim();// 11
			remuneracionAfectOS = new BigDecimal(line.substring(32, 42) + "."
					+ line.substring(42, 44));// 12
			importeAdicionalOS = new BigDecimal(line.substring(44, 50) + "."
					+ line.substring(50, 52));// 8
			zona = line.substring(52, 54).trim();// 2
			cantGrupoFamiliar = line.substring(54, 56);// 2
			cantAdherentesGrupoFamiliar = line.substring(56, 58);// 2
			secObligacion = Integer.valueOf(line.substring(58, 61));// 3
			condicionCuil = line.substring(61, 62).trim();// 1
			situacionCuil = line.substring(62, 63).trim();// 1
			actividad = line.substring(63, 65).trim();// 2
			modalidad = Integer.parseInt(line.substring(65, 68));// 3
			codigoSiniestro = line.substring(68, 70).trim();// 2
			aporteAdicionalOS = new BigDecimal(line.substring(70, 76) + "."
					+ line.substring(76, 78));// 8
			versionAplicativo = line.substring(78, 80);// 2
			remuneracionDecreto1273_02 = new BigDecimal(line.substring(80, 87)
					+ "." + line.substring(87, 89));// 9
			esposa = line.substring(89, 89);// 1
			excedenteAporteOS = new BigDecimal(line.substring(90, 100) + "."
					+ line.substring(100, 102));// 12
			declaroRetenciones = line.substring(102, 103).equals("N") ? false
					: true;// 1
			declaro = line.substring(103, 104).equals("N") ? false : true;// 1
			fechaPresentacion = sdf.parse(line.substring(104, 114));// 10
			fechaProceso = sdf.parse(line.substring(114, 124));// 10
			original = line.charAt(124);// 1
			// filler = line.substring(125, 155);// 31
			if (line.length() >= 164) {
				String importeParte1 = line.substring(156, 162).trim();
				if (importeParte1.equals("")){
					importeParte1 = "0";
				}
				String importeParte2 = line.substring(162, 164).trim();
				if (importeParte2.equals("")){
					importeParte2 = "0";
				}
				importeBaseContribucionOS = new BigDecimal(importeParte1 + "." + importeParte2);// 8
			}
			// line.substring( 164, 199);// 36 3 uso futuro
		}else if(version==2) {
			codigoObraSocial = line.substring(0, 6).trim();// 6
			periodo = sdfPeriodo.parse(line.substring(6, 10));// 4
			cuit = line.substring(10, 21).trim();// 11
			cuil =  line.substring(21, 32).trim();// 11
			remuneracionAfectOS = new BigDecimal(line.substring(32, 44) + "."
					+ line.substring(44, 46));// 14
			importeAdicionalOS = new BigDecimal(line.substring(46, 58) + "."
					+ line.substring(58, 60));// 8
			
			zona = line.substring(60,62).trim();// 2
			cantGrupoFamiliar = line.substring(62, 64);// 2
			cantAdherentesGrupoFamiliar = line.substring(64, 66);// 2
			
			secObligacion = Integer.valueOf(line.substring(67, 69));// 3
			condicionCuil = line.substring(69, 71).trim();// 2
			situacionCuil = line.substring(71, 73).trim();// 2
			actividad = line.substring(73, 76).trim();// 2
			modalidad = Integer.parseInt(line.substring(76, 79));// 3
			codigoSiniestro = line.substring(79, 81).trim();// 2
			aporteAdicionalOS = new BigDecimal(line.substring(81, 93) + "."
					+ line.substring(93, 95));// 8
			versionAplicativo = line.substring(95, 97);// 2
			remuneracionDecreto1273_02 = new BigDecimal(line.substring(97, 109)
					+ "." + line.substring(109, 111));// 9
			esposa = line.substring(111, 112);// 1
			excedenteAporteOS = new BigDecimal(line.substring(112, 122) + "."
					+ line.substring(122, 124));// 12
			declaroRetenciones = line.substring(124, 125).equals("N") ? false
					: true;// 1
			declaro = line.substring(125, 126).equals("N") ? false : true;// 1
			fechaPresentacion = sdf.parse(line.substring(126, 136));// 10
			fechaProceso = sdf.parse(line.substring(136, 146));// 10
			original = line.charAt(146);// 1
			aporteBasicoOS= new BigDecimal(line.substring(147, 156) + "." + line.substring(156, 158));
			contribucionOS= new BigDecimal(line.substring(158, 170) + "." + line.substring(170, 172));
			remuneracionTotal= new BigDecimal(line.substring(172, 184) + "." + line.substring(184, 186));
			obraSocialInformada= line.substring(187, 193);
			
			
			if (line.length() >= 200) {
				String importeParte1 = line.substring(193, 205).trim();
			    if (importeParte1.equals("")){
				    importeParte1 = "0";
			    }
			    String importeParte2 = line.substring(205, 207).trim();
			    if (importeParte2.equals("")){
				    importeParte2 = "0";
			    }
			    importeBaseContribucionOS = new BigDecimal(importeParte1 + "." + importeParte2);// 8
			}   
			
		}
	}

	public String getCodigoObraSocial() {
		return codigoObraSocial;
	}

	public void setCodigoObraSocial(String codigoObraSocial) {
		this.codigoObraSocial = codigoObraSocial;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public BigDecimal getRemuneracionAfectOS() {
		return remuneracionAfectOS;
	}

	public void setRemuneracionAfectOS(BigDecimal remuneracionAfectObraSocial) {
		this.remuneracionAfectOS = remuneracionAfectObraSocial;
	}

	public BigDecimal getImporteAdicionalOS() {
		return importeAdicionalOS;
	}

	public void setImporteAdicionalOS(BigDecimal importeAdicionalOS) {
		this.importeAdicionalOS = importeAdicionalOS;
	}

	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public String getCantGrupoFamiliar() {
		return cantGrupoFamiliar;
	}

	public void setCantGrupoFamiliar(String cantGrupoFamiliar) {
		this.cantGrupoFamiliar = cantGrupoFamiliar;
	}

	public String getCantAdherentesGrupoFamiliar() {
		return cantAdherentesGrupoFamiliar;
	}

	public void setCantAdherentesGrupoFamiliar(
			String cantAdherentesGrupoFamiliar) {
		this.cantAdherentesGrupoFamiliar = cantAdherentesGrupoFamiliar;
	}

	public Integer getSecObligacion() {
		return secObligacion;
	}

	public void setSecObligacion(Integer secObligacion) {
		this.secObligacion = secObligacion;
	}

	public String getCondicionCuil() {
		return condicionCuil;
	}

	public void setCondicionCuil(String condicionCuil) {
		this.condicionCuil = condicionCuil;
	}

	public String getSituacionCuil() {
		return situacionCuil;
	}

	public void setSituacionCuil(String situacionCuil) {
		this.situacionCuil = situacionCuil;
	}

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public Integer getModalidad() {
		return modalidad;
	}

	public void setModalidad(Integer modalidad) {
		this.modalidad = modalidad;
	}

	public String getCodigoSiniestro() {
		return codigoSiniestro;
	}

	public void setCodigoSiniestro(String codigoSiniestro) {
		this.codigoSiniestro = codigoSiniestro;
	}

	public BigDecimal getAporteAdicionalOS() {
		return aporteAdicionalOS;
	}

	public void setAporteAdicionalOS(BigDecimal aporteAdicionalOS) {
		this.aporteAdicionalOS = aporteAdicionalOS;
	}

	public String getVersionAplicativo() {
		return versionAplicativo;
	}

	public void setVersionAplicativo(String versionAplicativo) {
		this.versionAplicativo = versionAplicativo;
	}

	public BigDecimal getRemuneracionDecreto1273_02() {
		return remuneracionDecreto1273_02;
	}

	public void setRemuneracionDecreto1273_02(
			BigDecimal remuneracionDecreto1273_02) {
		this.remuneracionDecreto1273_02 = remuneracionDecreto1273_02;
	}

	public String getEsposa() {
		return esposa;
	}

	public void setEsposa(String esposa) {
		this.esposa = esposa;
	}

	public BigDecimal getExcedenteAporteOS() {
		return excedenteAporteOS;
	}

	public void setExcedenteAporteOS(BigDecimal excedenteAporteOS) {
		this.excedenteAporteOS = excedenteAporteOS;
	}

	public boolean isDeclaroRetenciones() {
		return declaroRetenciones;
	}

	public void setDeclaroRetenciones(boolean declaroRetenciones) {
		this.declaroRetenciones = declaroRetenciones;
	}

	public boolean isDeclaro() {
		return declaro;
	}

	public void setDeclaro(boolean declaro) {
		this.declaro = declaro;
	}

	public Date getFechaPresentacion() {
		return fechaPresentacion;
	}

	public void setFechaPresentacion(Date fechaPresentacion) {
		this.fechaPresentacion = fechaPresentacion;
	}

	public Date getFechaProceso() {
		return fechaProceso;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	public char getOriginal() {
		return original;
	}

	public void setOriginal(char original) {
		this.original = original;
	}

	public BigDecimal getImporteBaseContribucionOS() {
		return importeBaseContribucionOS;
	}

	public void setImporteBaseContribucionOS(
			BigDecimal importeBaseContribucionOS) {
		this.importeBaseContribucionOS = importeBaseContribucionOS;
	}
	
	public BigDecimal getAporteBasicoOS() {
		return aporteBasicoOS;
	}

	public void setAporteBasicoOS(BigDecimal aporteBasicoOS) {
		this.aporteBasicoOS = aporteBasicoOS;
	}

	public BigDecimal getContribucionOS() {
		return contribucionOS;
	}

	public void setContribucionOS(BigDecimal contribucionOS) {
		this.contribucionOS = contribucionOS;
	}

	public BigDecimal getRemuneracionTotal() {
		return remuneracionTotal;
	}

	public void setRemuneracionTotal(BigDecimal remuneracionTotal) {
		this.remuneracionTotal = remuneracionTotal;
	}

	public String getObraSocialInformada() {
		return obraSocialInformada;
	}

	public void setObraSocialInformada(String obraSocialInformada) {
		this.obraSocialInformada = obraSocialInformada;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\ncodigoObraSocial: " + codigoObraSocial);
		str.append("\nperiodo " + periodo);
		str.append("\ncuit " + cuit);
		str.append("\ncuil " + cuil);
		str.append("\nremuneracionAfectOS " + remuneracionAfectOS);
		str.append("\nimporteAdicionalOS " + importeAdicionalOS);
		str.append("\nzona " + zona);
		str.append("\ncantGrupoFamiliar " + cantGrupoFamiliar);
		str.append("\ncantAdherentesGrupoFamiliar "
				+ cantAdherentesGrupoFamiliar);
		str.append("\nsecObligacion " + secObligacion);
		str.append("\ncondicionCuil " + condicionCuil);
		str.append("\nsituacionCuil " + situacionCuil);
		str.append("\nactividad " + actividad);
		str.append("\nmodalidad " + modalidad);
		str.append("\ncodigoSiniestro " + codigoSiniestro);
		str.append("\naporteAdicionalOS " + aporteAdicionalOS);
		str.append("\nversionAplicativo " + versionAplicativo);
		str
				.append("\nremuneracionDecreto1273_02 "
						+ remuneracionDecreto1273_02);
		str.append("\nesposa " + esposa);
		str.append("\nexcedenteAporteOS " + excedenteAporteOS);
		str.append("\ndeclaroRetenciones " + declaroRetenciones);
		str.append("\ndeclaro " + declaro);
		str.append("\nfechaPresentacion " + fechaPresentacion);
		str.append("\nfechaProceso " + fechaProceso);
		str.append("\noriginal " + original);
		str.append("\nimporteBaseContribucionOS " + importeBaseContribucionOS);
		return str.toString();
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
