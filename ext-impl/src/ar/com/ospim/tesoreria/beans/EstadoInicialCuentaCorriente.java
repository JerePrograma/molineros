package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.Empresa;

public class EstadoInicialCuentaCorriente {
	private List<SaldoInicial> saldosIniciales = new ArrayList<SaldoInicial>();
	private Empresa empresa;

	public EstadoInicialCuentaCorriente() {

	}

	public EstadoInicialCuentaCorriente(Date fecha, BigDecimal importe,
			Empresa empresa) {
		super();
		saldosIniciales.add(new SaldoInicial(fecha, importe));
		this.empresa = empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((empresa == null) ? 0 : empresa.hashCode());
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
		EstadoInicialCuentaCorriente other = (EstadoInicialCuentaCorriente) obj;
		if (empresa == null) {
			if (other.empresa != null)
				return false;
		} else if (!empresa.equals(other.empresa))
			return false;
		return true;
	}

	public List<SaldoInicial> getSaldosIniciales() {
		return saldosIniciales;
	}

	public void setSaldosIniciales(List<SaldoInicial> saldosIniciales) {
		this.saldosIniciales = saldosIniciales;
	}

	public static class SaldoInicial implements Comparable<SaldoInicial> {
		private Date fecha;
		private BigDecimal importe;
		
		public SaldoInicial() {
			super();			
		}

		public SaldoInicial(Date fecha, BigDecimal importe) {
			super();
			this.fecha = fecha;
			this.importe = importe;
		}

		public BigDecimal getImporte() {
			return importe;
		}

		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		public Date getFecha() {
			return fecha;
		}

		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

		public int compareTo(SaldoInicial o) {
			return fecha.compareTo(o.fecha);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fecha == null) ? 0 : fecha.hashCode());
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
			SaldoInicial other = (SaldoInicial) obj;
			if (fecha == null) {
				if (other.fecha != null)
					return false;
			} else if (!fecha.equals(other.fecha))
				return false;
			return true;
		}

	}
}