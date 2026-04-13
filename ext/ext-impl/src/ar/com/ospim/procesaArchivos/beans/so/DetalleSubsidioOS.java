package ar.com.ospim.procesaArchivos.beans.so;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetalleSubsidioOS {
	private String tipoRegistro;
	private String cuit;
	private String cuil;
	private String codigoOS;
	private Date periodo;
	private BigDecimal remuneracionAfectOS;
	private BigDecimal aportesOS;
	private BigDecimal contirbucionOS;
	private BigDecimal subsidio;
	private String obraSocialRel;
	private String indParTot;
	private String debitoCredito;
	private String motivoExcepcion;
	private BigDecimal capita;
	private String hombre0a14;
	private String hombre15a49;
	private String hombre50a64;
	private String hombre65a99;
	private String mujer0a14;
	private String mujer15a49;
	private String mujer50a64;
	private String mujer65a99;

	public DetalleSubsidioOS(String line) throws ParseException {
		SimpleDateFormat sdfPeriodo = new SimpleDateFormat("yyMM");

		tipoRegistro = line.substring(0, 2);
		cuit = line.substring(2, 13);
		cuil = line.substring(14, 24);
		codigoOS = line.substring(24, 30);
		periodo = sdfPeriodo.parse(line.substring(30, 34));
		remuneracionAfectOS = new BigDecimal(line.substring(34, 44) + "."
				+ line.substring(44, 46));
		aportesOS = new BigDecimal(line.substring(46, 56) + "."
				+ line.substring(56, 58));
		contirbucionOS = new BigDecimal(line.substring(58, 68) + "."
				+ line.substring(68, 70));
		if (tipoRegistro.toUpperCase().equals("DE")) {
			debitoCredito = line.substring(89, 90);
		} else {
			subsidio = new BigDecimal(line.subSequence(70, 80) + "."
					+ line.substring(80, 82));
			obraSocialRel = line.substring(82, 88);
			indParTot = line.substring(88, 89);
			debitoCredito = line.substring(89, 90);
			if (debitoCredito != null
					&& debitoCredito.trim().toUpperCase().equals("D")) {
				subsidio = subsidio.negate();
			}
			motivoExcepcion = line.substring(90, 91);
			if (line.length()>103){
				capita = new BigDecimal(line.substring(91, 103));
			}
			if (line.length() > 119){
				hombre0a14 = line.substring(103, 105);
				hombre15a49 = line.substring(105, 107);
				hombre50a64 = line.substring(107, 109);
				hombre65a99 = line.substring(109, 111);
				mujer0a14 = line.substring(111, 113);
				mujer15a49 = line.substring(113, 115);
				mujer50a64 = line.substring(115, 117);
				mujer65a99 = line.substring(117, 119);
			}
		}
	}

	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
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

	public String getCodigoOS() {
		return codigoOS;
	}

	public void setCodigoOS(String codigoOS) {
		this.codigoOS = codigoOS;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public BigDecimal getRemuneracionAfectOS() {
		return remuneracionAfectOS;
	}

	public void setRemuneracionAfectOS(BigDecimal remuneracionAfectOS) {
		this.remuneracionAfectOS = remuneracionAfectOS;
	}

	public BigDecimal getAportesOS() {
		return aportesOS;
	}

	public void setAportesOS(BigDecimal aportesOS) {
		this.aportesOS = aportesOS;
	}

	public BigDecimal getContirbucionOS() {
		return contirbucionOS;
	}

	public void setContirbucionOS(BigDecimal contirbucionOS) {
		this.contirbucionOS = contirbucionOS;
	}

	public BigDecimal getSubsidio() {
		return subsidio;
	}

	public void setSubsidio(BigDecimal subsidio) {
		this.subsidio = subsidio;
	}

	public String getObraSocialRel() {
		return obraSocialRel;
	}

	public void setObraSocialRel(String obraSocialRel) {
		this.obraSocialRel = obraSocialRel;
	}

	public String getIndParTot() {
		return indParTot;
	}

	public void setIndParTot(String indParTot) {
		this.indParTot = indParTot;
	}

	public String getDebitoCredito() {
		return debitoCredito;
	}

	public void setDebitoCredito(String debitoCredito) {
		this.debitoCredito = debitoCredito;
	}

	public String getMotivoExcepcion() {
		return motivoExcepcion;
	}

	public void setMotivoExcepcion(String motivoExcepcion) {
		this.motivoExcepcion = motivoExcepcion;
	}

	public BigDecimal getCapita() {
		return capita;
	}

	public void setCapita(BigDecimal capita) {
		this.capita = capita;
	}

	public String getHombre0a14() {
		return hombre0a14;
	}

	public void setHombre0a14(String hombre0a14) {
		this.hombre0a14 = hombre0a14;
	}

	public String getHombre15a49() {
		return hombre15a49;
	}

	public void setHombre15a49(String hombre15a19) {
		this.hombre15a49 = hombre15a19;
	}

	public String getHombre50a64() {
		return hombre50a64;
	}

	public void setHombre50a64(String hombre50a64) {
		this.hombre50a64 = hombre50a64;
	}

	public String getHombre65a99() {
		return hombre65a99;
	}

	public void setHombre65a99(String hombre65a99) {
		this.hombre65a99 = hombre65a99;
	}

	public String getMujer0a14() {
		return mujer0a14;
	}

	public void setMujer0a14(String mujer0a14) {
		this.mujer0a14 = mujer0a14;
	}

	public String getMujer15a49() {
		return mujer15a49;
	}

	public void setMujer15a49(String mujer15a49) {
		this.mujer15a49 = mujer15a49;
	}

	public String getMujer50a64() {
		return mujer50a64;
	}

	public void setMujer50a64(String mujer50a64) {
		this.mujer50a64 = mujer50a64;
	}

	public String getMujer65a99() {
		return mujer65a99;
	}

	public void setMujer65a99(String mujer65a99) {
		this.mujer65a99 = mujer65a99;
	}

}
