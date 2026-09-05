package ar.com.ospim.afiliados.reportes.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.util.StringUtils;

public class BusquedaReportePadronFiltro implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7660154674673535562L;
	
	private String cuit;
	private String sucursal;
	private String razonSocial;
	private int edadInicial;
	private int edadFinal;
	private String categoriaUoma;
	private int tituyfliares;
	private String titularesYFliares;
	private Integer parentescoId;
	private String parentescoDesc;
	
	private String idsMotivoBaja;
	private String descMotivoBaja;
	
	private String codigosSeccional;
	private String descSeccional;
	
	private String codigosProvincia;
	private String descProvincia;
	
	private String codigosLocalidad;
	private String descLocalidad;
	
	private String idsTercerizadora;
	private String descTercerizadora;
	
	private String codigosPlan;
	private String descPlan;
	
	private String codigosAportes;
	private String descAportes;
	
	private int tipoBusqueda;
	private String descBusqueda;	
	
	private Date fechaDesde;
	private Date fechaHasta;

	private Date fechaNacimIni;
	private Date fechaNacimFin;
	
	private boolean totalesPorTercerizadora;
	private boolean totalesPorSeccional;
	private boolean totalesPorPlan;
	private boolean totalesPorEmpresa;
	private boolean totalesPorEntidad;
	
	private String proyecto;
	
	private boolean vistaPrevencion;
	
	private boolean vistaAdmifarm;
	
//	private int pagina;
//	private int registrosTotal;
//	private final int registrosPorPagina = 50;
	
	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public int getEdadInicial() {
		return edadInicial;
	}

	public void setEdadInicial(int edadInicial) {
		this.edadInicial = edadInicial;
	}

	public int getEdadFinal() {
		return edadFinal;
	}

	public void setEdadFinal(int edadFinal) {
		this.edadFinal = edadFinal;
	}

	public String getCategoriaUoma() {
		return categoriaUoma;
	}

	public void setCategoriaUoma(String categoriaUoma) {
		this.categoriaUoma = categoriaUoma;
	}

	public int getTituyfliares() {
		return tituyfliares;
	}

	public void setTituyfliares(int tituyfliares) {
		this.tituyfliares = tituyfliares;
	}

	public String getTitularesYFliares() {
		return titularesYFliares;
	}

	public void setTitularesYFliares(String titularesYFliares) {
		this.titularesYFliares = titularesYFliares;
	}

	public Integer getParentescoId() {
		return parentescoId;
	}

	public void setParentescoId(Integer parentescoId) {
		this.parentescoId = parentescoId;
	}

	public String getParentescoDesc() {
		return parentescoDesc;
	}

	public void setParentescoDesc(String parentescoDesc) {
		this.parentescoDesc = parentescoDesc;
	}

	public String getIdsMotivoBaja() {
		return idsMotivoBaja;
	}

	public void setIdsMotivoBaja(String idsMotivoBaja) {
		this.idsMotivoBaja = idsMotivoBaja;
	}

	public String getDescMotivoBaja() {
		return descMotivoBaja;
	}

	public void setDescMotivoBaja(String descMotivoBaja) {
		this.descMotivoBaja = descMotivoBaja;
	}

	public String getCodigosSeccional() {
		return codigosSeccional;
	}

	public void setCodigosSeccional(String codigosSeccional) {
		this.codigosSeccional = codigosSeccional;
	}

	public String getDescSeccional() {
		return descSeccional;
	}

	public void setDescSeccional(String descSeccional) {
		this.descSeccional = descSeccional;
	}

	public String getCodigosProvincia() {
		return codigosProvincia;
	}

	public void setCodigosProvincia(String codigosProvincia) {
		this.codigosProvincia = codigosProvincia;
	}

	public String getDescProvincia() {
		return descProvincia;
	}

	public void setDescProvincia(String descProvincia) {
		this.descProvincia = descProvincia;
	}

	public String getCodigosLocalidad() {
		return codigosLocalidad;
	}

	public void setCodigosLocalidad(String codigosLocalidad) {
		this.codigosLocalidad = codigosLocalidad;
	}

	public String getDescLocalidad() {
		return descLocalidad;
	}

	public void setDescLocalidad(String descLocalidad) {
		this.descLocalidad = descLocalidad;
	}

	public String getIdsTercerizadora() {
		return idsTercerizadora;
	}

	public void setIdsTercerizadora(String idsTercerizadora) {
		this.idsTercerizadora = idsTercerizadora;
	}

	public String getDescTercerizadora() {
		return descTercerizadora;
	}

	public void setDescTercerizadora(String descTercerizadora) {
		this.descTercerizadora = descTercerizadora;
	}

	public String getCodigosPlan() {
		return codigosPlan;
	}

	public void setCodigosPlan(String codigosPlan) {
		this.codigosPlan = codigosPlan;
	}

	public String getDescPlan() {
		return descPlan;
	}

	public void setDescPlan(String descPlan) {
		this.descPlan = descPlan;
	}

	public String getCodigosAportes() {
		return codigosAportes;
	}

	public void setCodigosAportes(String codigosAportes) {
		this.codigosAportes = codigosAportes;
	}

	public String getDescAportes() {
		return descAportes;
	}

	public void setDescAportes(String descAportes) {
		this.descAportes = descAportes;
	}

	public int getTipoBusqueda() {
		return tipoBusqueda;
	}

	public void setTipoBusqueda(int tipoBusqueda) {
		this.tipoBusqueda = tipoBusqueda;
	}

	public String getDescBusqueda() {
		return descBusqueda;
	}

	public void setDescBusqueda(String descBusqueda) {
		this.descBusqueda = descBusqueda;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public boolean isTotalesPorTercerizadora() {
		return totalesPorTercerizadora;
	}

	public void setTotalesPorTercerizadora(boolean totalesPorTercerizadora) {
		this.totalesPorTercerizadora = totalesPorTercerizadora;
	}

	public boolean isTotalesPorSeccional() {
		return totalesPorSeccional;
	}

	public void setTotalesPorSeccional(boolean totalesPorSeccional) {
		this.totalesPorSeccional = totalesPorSeccional;
	}

	public boolean isTotalesPorPlan() {
		return totalesPorPlan;
	}

	public void setTotalesPorPlan(boolean totalesPorPlan) {
		this.totalesPorPlan = totalesPorPlan;
	}

	public boolean isTotalesPorEmpresa() {
		return totalesPorEmpresa;
	}

	public void setTotalesPorEmpresa(boolean totalesPorEmpresa) {
		this.totalesPorEmpresa = totalesPorEmpresa;
	}

	public boolean isTotalesPorEntidad() {
		return totalesPorEntidad;
	}

	public void setTotalesPorEntidad(boolean totalesPorEntidad) {
		this.totalesPorEntidad = totalesPorEntidad;
	}

	public boolean isVistaPrevencion() {
		return vistaPrevencion;
	}

	public void setVistaPrevencion(boolean vistaPrevencion) {
		this.vistaPrevencion = vistaPrevencion;
	}

	public Date getFechaNacimIni() {
		return fechaNacimIni;
	}

	public void setFechaNacimIni(Date fechaNacimIni) {
		this.fechaNacimIni = fechaNacimIni;
	}

	public Date getFechaNacimFin() {
		return fechaNacimFin;
	}

	public void setFechaNacimFin(Date fechaNacimHta) {
		this.fechaNacimFin = fechaNacimHta;
	}
	
	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}
	
	public boolean isVistaAdmifarm() {
	    return vistaAdmifarm;
	}

	public void setVistaAdmifarm(boolean vistaAdmifarm) {
	    this.vistaAdmifarm = vistaAdmifarm;
	}
	
	public String getDescripcionFiltros(){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String descripcion = "";
		
		descripcion += "Tipo Búsqueda: "+ descBusqueda.toUpperCase() + ", Para " + titularesYFliares.toUpperCase() + 
				", Entre Fechas: " + sdf.format(fechaDesde) + " y " +  sdf.format(fechaHasta); 
		
		descripcion += ", Empresa: " + (cuit.length()==0?"TODAS":(cuit+"-"+sucursal+"-"+razonSocial.toUpperCase()));
		
		descripcion += ", Categoría Uoma: " + (categoriaUoma.length()==0?"TODAS":categoriaUoma.toUpperCase());
		
		descripcion += (edadInicial > 0 && edadFinal > 0)?(" ,Entre Edades: "+edadInicial + " y " + edadFinal):
						  (edadInicial > 0 && edadFinal == 0)?(" ,Mayores de: "+edadInicial):
							(edadInicial == 0 && edadFinal > 0)?(" ,Menores de: "+edadFinal):" ,TODAS LAS EDADES";
							
		descripcion += ", Parentesco: " + parentescoDesc.toUpperCase();
		
		descripcion += ", Motivos Baja: " + descMotivoBaja.toUpperCase();

		descripcion += ", Seccional: " + descSeccional.toUpperCase();

		descripcion += ", Provincia: " + descProvincia.toUpperCase();

		descripcion += ", Localidad: " + descLocalidad.toUpperCase();

		descripcion += ", Tercerizadora: " + descTercerizadora.toUpperCase();

		descripcion += ", Plan: " + descPlan.toUpperCase();

		descripcion += ", Aporte: " + descAportes.toUpperCase();

		descripcion += ", Proyecto: " +(StringUtils.checkEmpty(proyecto)?"TODOS":proyecto.toUpperCase());
		
		return descripcion;
		
	}
}
