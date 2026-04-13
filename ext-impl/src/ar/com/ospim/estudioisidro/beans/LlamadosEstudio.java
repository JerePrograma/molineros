package ar.com.ospim.estudioisidro.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.global.beans.Empresa;

public class LlamadosEstudio implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Llamado> llamados;
	private String cuit;
	private String razon;
	private Empresa empresa;
	private String cartaDoc;
	private String ubicacionCarpeta;
	int cantDeudas;
	private HashMap<Integer, ResumenActaAcuerdo> resumenActaAcuerdo;
	private int cantChequesRechazados;
	private BigDecimal importeChequesRechazados;
	private int cantReemplazadosRechazo;
	private BigDecimal importeReemplazadosRechazo;
	private int cantCanjeadosSinDepo;
	private BigDecimal importeCanjeadosSinDepo;
	private int cantChequesCartera;
	private BigDecimal importeChequesCartera;
	/*private List<ReporteListadoValores> chequesRechazados;
	private List<ReporteListadoValores> chequesReemplazadosRechazo;
	private List<ReporteListadoValores> chequesCanjeadosSinDepo;
	private List<ReporteListadoValores> chequesCartera;*/
	
	

	/*public List<ReporteListadoValores> getChequesRechazados() {
		return chequesRechazados;
	}

	public void setChequesRechazados(List<ReporteListadoValores> chequesRechazados) {
		this.chequesRechazados = chequesRechazados;
	}

	public List<ReporteListadoValores> getChequesReemplazadosRechazo() {
		return chequesReemplazadosRechazo;
	}

	public void setChequesReemplazadosRechazo(
			List<ReporteListadoValores> chequesReemplazadosRechazo) {
		this.chequesReemplazadosRechazo = chequesReemplazadosRechazo;
	}

	public List<ReporteListadoValores> getChequesCanjeadosSinDepo() {
		return chequesCanjeadosSinDepo;
	}

	public void setChequesCanjeadosSinDepo(
			List<ReporteListadoValores> chequesCanjeadosSinDepo) {
		this.chequesCanjeadosSinDepo = chequesCanjeadosSinDepo;
	}

	public List<ReporteListadoValores> getChequesCartera() {
		return chequesCartera;
	}

	public void setChequesCartera(List<ReporteListadoValores> chequesCartera) {
		this.chequesCartera = chequesCartera;
	}*/

	public List<Llamado> getLlamados() {
		return llamados;
	}

	public void setLlamados(List<Llamado> llamados) {
		this.llamados = llamados;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazon() {
		return razon;
	}

	public void setRazon(String razon) {
		this.razon = razon;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getCartaDoc() {
		return cartaDoc;
	}

	public void setCartaDoc(String cartaDoc) {
		this.cartaDoc = cartaDoc;
	}

	public String getUbicacionCarpeta() {
		return ubicacionCarpeta;
	}

	public void setUbicacionCarpeta(String ubicacionCarpeta) {
		this.ubicacionCarpeta = ubicacionCarpeta;
	}

	public int getCantActas(int entidad) {
		if (null != resumenActaAcuerdo && null!= resumenActaAcuerdo.get(entidad)) {
			return resumenActaAcuerdo.get(entidad).getCantActas();
		} else {
			return 0;
		}
	}
	
	public String getSaldoActasAsString(int entidad) {
		if (null != resumenActaAcuerdo && null!= resumenActaAcuerdo.get(entidad) && null!=resumenActaAcuerdo.get(entidad).getSaldoActas() ) {
			NumberFormat formatter = new DecimalFormat("$#0.00");
			return formatter.format(resumenActaAcuerdo.get(entidad).getSaldoActas());
		} else {
			return "";
		}
	}

	public int getCantDeudas() {
		return cantDeudas;
	}

	public void setCantDeudas(int cantDeudas) {
		this.cantDeudas = cantDeudas;
	}

	public int getCantConvenios(int entidad) {
		if (null != resumenActaAcuerdo && null!= resumenActaAcuerdo.get(entidad)) {
			return resumenActaAcuerdo.get(entidad).getCantConvenios();
		} else {
			return 0;
		}
	}
	
	public String getSaldoConveniosAsString(int entidad) {
		if (null != resumenActaAcuerdo && null!= resumenActaAcuerdo.get(entidad) && null!=resumenActaAcuerdo.get(entidad).getSaldoConvenios()) {
			NumberFormat formatter = new DecimalFormat("$#0.00");
			return formatter.format(resumenActaAcuerdo.get(entidad).getSaldoConvenios());
		} else {
			return "";
		}
	}

	public int getCantConvenios() {
		Iterator<Integer> it = resumenActaAcuerdo.keySet().iterator();
		int cont = 0;
		if (null != resumenActaAcuerdo) {
			while (it.hasNext()) {
				Integer key = it.next();
				cont += resumenActaAcuerdo.get(key).getCantConvenios();
			}
		}
		return cont;
	}
	
	public int getCantActas() {
		Iterator<Integer> it = resumenActaAcuerdo.keySet().iterator();
		int cont = 0;
		if (null != resumenActaAcuerdo) {
			while (it.hasNext()) {
				Integer key = it.next();
				cont += resumenActaAcuerdo.get(key).getCantActas();
			}
		}
		return cont;
	}
	
	public int getCantRecibos() {
		Iterator<Integer> it = resumenActaAcuerdo.keySet().iterator();
		int cont = 0;
		if (null != resumenActaAcuerdo) {
			while (it.hasNext()) {
				Integer key = it.next();
				cont += resumenActaAcuerdo.get(key).getCantRecibos();
			}
		}
		return cont;
	}
	
	public int getCantRecibos(int entidad) {
		if (null != resumenActaAcuerdo && null!= resumenActaAcuerdo.get(entidad)) {
			return resumenActaAcuerdo.get(entidad).getCantRecibos();
		} else {
			return 0;
		}
	}
	
	public String getImporteRecibosAsString(int entidad) {
		if (null != resumenActaAcuerdo && null!= resumenActaAcuerdo.get(entidad) && null!= resumenActaAcuerdo.get(entidad).getImporteRecibos()) {
			NumberFormat formatter = new DecimalFormat("$#0.00");
			return formatter.format(resumenActaAcuerdo.get(entidad).getImporteRecibos());
		} else {
			return "";
		}
	}

	public HashMap<Integer, ResumenActaAcuerdo> getResumenActaAcuerdo() {
		return resumenActaAcuerdo;
	}

	public void setResumenActaAcuerdo(
			HashMap<Integer, ResumenActaAcuerdo> resumenActaAcuerdo) {
		this.resumenActaAcuerdo = resumenActaAcuerdo;
	}
	
	

	public int getCantChequesRechazados() {
		return cantChequesRechazados;
	}

	public void setCantChequesRechazados(int cantChequesRechazados) {
		this.cantChequesRechazados = cantChequesRechazados;
	}

	public BigDecimal getImporteChequesRechazados() {
		return importeChequesRechazados;
	}
	
	public String getImporteChequesRechazadosAsString() {
		if (null != importeChequesRechazados) {
			NumberFormat formatter = new DecimalFormat("$#0.00");
			return formatter.format(importeChequesRechazados);
		} else {
			return "";
		}		
	}

	public void setImporteChequesRechazados(BigDecimal importeChequesRechazados) {
		this.importeChequesRechazados = importeChequesRechazados;
	}

	public int getCantReemplazadosRechazo() {
		return cantReemplazadosRechazo;
	}

	public void setCantReemplazadosRechazo(int cantReemplazadosRechazo) {
		this.cantReemplazadosRechazo = cantReemplazadosRechazo;
	}

	public BigDecimal getImporteReemplazadosRechazo() {
		return importeReemplazadosRechazo;
	}
	
	public String getImporteReemplazadosRechazoAsString() {
		if (null != importeReemplazadosRechazo) {
			NumberFormat formatter = new DecimalFormat("$#0.00");
			return formatter.format(importeReemplazadosRechazo);
		} else {
			return "";
		}		
	}

	public void setImporteReemplazadosRechazo(BigDecimal importeReemplazadosRechazo) {
		this.importeReemplazadosRechazo = importeReemplazadosRechazo;
	}

	public int getCantCanjeadosSinDepo() {
		return cantCanjeadosSinDepo;
	}

	public void setCantCanjeadosSinDepo(int cantCanjeadosSinDepo) {
		this.cantCanjeadosSinDepo = cantCanjeadosSinDepo;
	}

	public BigDecimal getImporteCanjeadosSinDepo() {
		return importeCanjeadosSinDepo;
	}
	
	public String getImporteCanjeadosSinDepoAsString() {
		if (null != importeCanjeadosSinDepo) {
			NumberFormat formatter = new DecimalFormat("$#0.00");
			return formatter.format(importeCanjeadosSinDepo);
		} else {
			return "";
		}		
	}

	public void setImporteCanjeadosSinDepo(BigDecimal importeCanjeadosSinDepo) {
		this.importeCanjeadosSinDepo = importeCanjeadosSinDepo;
	}

	public int getCantChequesCartera() {
		return cantChequesCartera;
	}

	public void setCantChequesCartera(int cantChequesCartera) {
		this.cantChequesCartera = cantChequesCartera;
	}

	public BigDecimal getImporteChequesCartera() {
		return importeChequesCartera;
	}
	
	public String getImporteChequesCarteraAsString() {
		if (null != importeChequesCartera) {
			NumberFormat formatter = new DecimalFormat("$#0.00");
			return formatter.format(importeChequesCartera);
		} else {
			return "";
		}		
	}

	public void setImporteChequesCartera(BigDecimal importeChequesCartera) {
		this.importeChequesCartera = importeChequesCartera;
	}



	public class ResumenActaAcuerdo {
		int cantActas;
		BigDecimal importeActas;
		BigDecimal saldoActas;
		int cantConvenios;
		BigDecimal importeConvenios;
		BigDecimal saldoConvenios;
		int cantRecibos;
		BigDecimal importeRecibos;
		
		public ResumenActaAcuerdo() {
		}

		public int getCantActas() {
			return cantActas;
		}

		public void setCantActas(int cantActas) {
			this.cantActas = cantActas;
		}

		public BigDecimal getSaldoActas() {
			return saldoActas;
		}

		public void setSaldoActas(BigDecimal saldo) {
			this.saldoActas = saldo;
		}

		public int getCantConvenios() {
			return cantConvenios;
		}

		public void setCantConvenios(int cantConvenios) {
			this.cantConvenios = cantConvenios;
		}

		public BigDecimal getSaldoConvenios() {
			return saldoConvenios;
		}

		public void setSaldoConvenios(BigDecimal saldoConvenios) {
			this.saldoConvenios = saldoConvenios;
		}

		public BigDecimal getImporteActas() {
			return importeActas;
		}

		public void setImporteActas(BigDecimal importeActas) {
			this.importeActas = importeActas;
		}

		public BigDecimal getImporteConvenios() {
			return importeConvenios;
		}

		public void setImporteConvenios(BigDecimal importeConvenios) {
			this.importeConvenios = importeConvenios;
		}

		public int getCantRecibos() {
			return cantRecibos;
		}

		public void setCantRecibos(int cantRecibos) {
			this.cantRecibos = cantRecibos;
		}

		public BigDecimal getImporteRecibos() {
			return importeRecibos;
		}

		public void setImporteRecibos(BigDecimal importeRecibos) {
			this.importeRecibos = importeRecibos;
		}

	}

}
