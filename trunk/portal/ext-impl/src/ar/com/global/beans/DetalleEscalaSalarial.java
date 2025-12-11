package ar.com.global.beans;

import java.util.Date;
import java.util.List;



public class DetalleEscalaSalarial {
		private Date fechaDesde;
		private List<TablaEscalaSalarial> escalaSalarial;

		public Date getFechaDesde() {
			return fechaDesde;
		}

		public void setFechaDesde(Date fechaDesde) {
			this.fechaDesde = fechaDesde;
		}

		public List<TablaEscalaSalarial> getEscalaSalarial() {
			return escalaSalarial;
		}

		public void setEscalaSalarial(List<TablaEscalaSalarial> escalaSalarial) {
			this.escalaSalarial = escalaSalarial;
		}
	}