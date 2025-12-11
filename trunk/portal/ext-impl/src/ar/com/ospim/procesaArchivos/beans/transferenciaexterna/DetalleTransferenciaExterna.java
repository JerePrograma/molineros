package ar.com.ospim.procesaArchivos.beans.transferenciaexterna;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.compass.core.util.backport.java.util.Arrays;

public class DetalleTransferenciaExterna {
	private String codigoOrganismo;
	private String nroExpediente;
	private Date fechaProceso;
	private Date fechaTransferencia;
	private int clasificacionExpediente;
	private BigDecimal importeTotal;
	private int nroCuota;
	private BigDecimal importeTransferencia;
	private String debitoCredito;
	private String periodo;
	private String nroExpedienteOriginal;
	private String codigoHospital;
	private String nroExpedienteAnssal;
	private String observacion;
	private String detalleJuzgado;
	private String detalleSecretaria;
	private String autos;
	private List<String> facturas;

	@SuppressWarnings("unchecked")
	public DetalleTransferenciaExterna(String line) throws ParseException {
		codigoOrganismo = line.substring(0, 4).trim();
		nroExpediente = line.substring(4, 13).trim();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		fechaProceso = sdf.parse(line.substring(13, 23).trim());
		fechaTransferencia = sdf.parse(line.substring(23, 33).trim());
		clasificacionExpediente = Integer.parseInt(line.substring(33, 35)
				.trim());
		importeTotal = new BigDecimal(line.substring(35, 48).trim() + "."
				+ line.substring(48, 50).trim());
		nroCuota = Integer.parseInt(line.substring(50, 54).trim());
		importeTransferencia = new BigDecimal(line.substring(54, 67).trim()
				+ "." + line.substring(67, 69).trim());
		debitoCredito = line.substring(69, 70).trim();
		periodo = line.substring(70, 76).trim();
		nroExpedienteOriginal = line.substring(76, 85).trim();
		codigoHospital = line.substring(85, 93).trim();
		nroExpedienteAnssal = line.substring(93, 123).trim();
		observacion = line.substring(123, 173).trim();
		detalleJuzgado = line.substring(173, 273).trim();
		detalleSecretaria = line.substring(273, 323).trim();
		autos = line.substring(323, 1347).trim();
//		String detalleFactura = line.substring(1347, 1547).trim();
		String detalleFactura = line.substring(1347, line.length()-1).trim();
		if (detalleFactura.length() > 0) {
			
			ArrayList<String> facturasAux = new ArrayList<String>();
			facturas = new ArrayList<String>();
			
			List<String> facturasTemp = Arrays.asList(detalleFactura.split("\\*"));
			
			for (Iterator iterator = facturasTemp.iterator(); iterator.hasNext();) {
				String fc = (String) iterator.next();
				if(!facturasAux.contains(fc)) {
					facturasAux.add(fc);
				}
			}
			
			facturas.addAll(facturasAux);
//			facturas.addAll(Arrays.asList(detalleFactura.split("\\*")));
		}
	}

	public String getCodigoOrganismo() {
		return codigoOrganismo;
	}

	public void setCodigoOrganismo(String codigoOrganismo) {
		this.codigoOrganismo = codigoOrganismo;
	}

	public String getNroExpediente() {
		return nroExpediente;
	}

	public void setNroExpediente(String nroExpediente) {
		this.nroExpediente = nroExpediente;
	}

	public Date getFechaProceso() {
		return fechaProceso;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	public Date getFechaTransferencia() {
		return fechaTransferencia;
	}

	public void setFechaTransferencia(Date fechaTransferencia) {
		this.fechaTransferencia = fechaTransferencia;
	}

	public int getClasificacionExpediente() {
		return clasificacionExpediente;
	}

	public void setClasificacionExpediente(int clasificacionExpediente) {
		this.clasificacionExpediente = clasificacionExpediente;
	}

	public BigDecimal getImporteTotal() {
		return importeTotal;
	}

	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}

	public int getNroCuota() {
		return nroCuota;
	}

	public void setNroCuota(int nroCuota) {
		this.nroCuota = nroCuota;
	}

	public BigDecimal getImporteTransferencia() {
		return importeTransferencia;
	}

	public void setImporteTransferencia(BigDecimal importeTransferencia) {
		this.importeTransferencia = importeTransferencia;
	}

	public String getDebitoCredito() {
		return debitoCredito;
	}

	public void setDebitoCredito(String debitoCredito) {
		this.debitoCredito = debitoCredito;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getNroExpedienteOriginal() {
		return nroExpedienteOriginal;
	}

	public void setNroExpedienteOriginal(String nroExpedienteOriginal) {
		this.nroExpedienteOriginal = nroExpedienteOriginal;
	}

	public String getCodigoHospital() {
		return codigoHospital;
	}

	public void setCodigoHospital(String codigoHospital) {
		this.codigoHospital = codigoHospital;
	}

	public String getNroExpedienteAnssal() {
		return nroExpedienteAnssal;
	}

	public void setNroExpedienteAnssal(String nroExpedienteAnssal) {
		this.nroExpedienteAnssal = nroExpedienteAnssal;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getDetalleJuzgado() {
		return detalleJuzgado;
	}

	public void setDetalleJuzgado(String detalleJuzgado) {
		this.detalleJuzgado = detalleJuzgado;
	}

	public String getDetalleSecretaria() {
		return detalleSecretaria;
	}

	public void setDetalleSecretaria(String detalleSecretaria) {
		this.detalleSecretaria = detalleSecretaria;
	}

	public String getAutos() {
		return autos;
	}

	public void setAutos(String autos) {
		this.autos = autos;
	}

	public void setFacturas(List<String> facturas) {
		this.facturas = facturas;
	}

	public List<String> getFacturas() {
		return facturas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codigoHospital == null) ? 0 : codigoHospital.hashCode());
		result = prime * result
				+ ((codigoOrganismo == null) ? 0 : codigoOrganismo.hashCode());
		result = prime * result
				+ ((debitoCredito == null) ? 0 : debitoCredito.hashCode());
		result = prime * result
				+ ((detalleJuzgado == null) ? 0 : detalleJuzgado.hashCode());
		result = prime
				* result
				+ ((detalleSecretaria == null) ? 0 : detalleSecretaria
						.hashCode());
		result = prime * result
				+ ((fechaProceso == null) ? 0 : fechaProceso.hashCode());
		result = prime
				* result
				+ ((fechaTransferencia == null) ? 0 : fechaTransferencia
						.hashCode());
		result = prime * result
				+ ((importeTotal == null) ? 0 : importeTotal.hashCode());
		result = prime
				* result
				+ ((importeTransferencia == null) ? 0 : importeTransferencia
						.hashCode());
		result = prime * result + nroCuota;
		result = prime * result
				+ ((nroExpediente == null) ? 0 : nroExpediente.hashCode());
		result = prime
				* result
				+ ((nroExpedienteAnssal == null) ? 0 : nroExpedienteAnssal
						.hashCode());
		result = prime
				* result
				+ ((nroExpedienteOriginal == null) ? 0 : nroExpedienteOriginal
						.hashCode());
		result = prime * result + ((periodo == null) ? 0 : periodo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DetalleTransferenciaExterna other = (DetalleTransferenciaExterna) obj;
		if (codigoHospital == null) {
			if (other.codigoHospital != null)
				return false;
		} else if (!codigoHospital.equals(other.codigoHospital))
			return false;
		if (codigoOrganismo == null) {
			if (other.codigoOrganismo != null)
				return false;
		} else if (!codigoOrganismo.equals(other.codigoOrganismo))
			return false;
		if (debitoCredito == null) {
			if (other.debitoCredito != null)
				return false;
		} else if (!debitoCredito.equals(other.debitoCredito))
			return false;
		if (detalleJuzgado == null) {
			if (other.detalleJuzgado != null)
				return false;
		} else if (!detalleJuzgado.equals(other.detalleJuzgado))
			return false;
		if (detalleSecretaria == null) {
			if (other.detalleSecretaria != null)
				return false;
		} else if (!detalleSecretaria.equals(other.detalleSecretaria))
			return false;
		if (fechaProceso == null) {
			if (other.fechaProceso != null)
				return false;
		} else if (!fechaProceso.equals(other.fechaProceso))
			return false;
		if (fechaTransferencia == null) {
			if (other.fechaTransferencia != null)
				return false;
		} else if (!fechaTransferencia.equals(other.fechaTransferencia))
			return false;
		if (importeTotal == null) {
			if (other.importeTotal != null)
				return false;
		} else if (!importeTotal.equals(other.importeTotal))
			return false;
		if (importeTransferencia == null) {
			if (other.importeTransferencia != null)
				return false;
		} else if (!importeTransferencia.equals(other.importeTransferencia))
			return false;
		if (nroCuota != other.nroCuota)
			return false;
		if (nroExpediente == null) {
			if (other.nroExpediente != null)
				return false;
		} else if (!nroExpediente.equals(other.nroExpediente))
			return false;
		if (nroExpedienteAnssal == null) {
			if (other.nroExpedienteAnssal != null)
				return false;
		} else if (!nroExpedienteAnssal.equals(other.nroExpedienteAnssal))
			return false;
		if (nroExpedienteOriginal == null) {
			if (other.nroExpedienteOriginal != null)
				return false;
		} else if (!nroExpedienteOriginal.equals(other.nroExpedienteOriginal))
			return false;
		if (periodo == null) {
			if (other.periodo != null)
				return false;
		} else if (!periodo.equals(other.periodo))
			return false;
		return true;
	}

}
