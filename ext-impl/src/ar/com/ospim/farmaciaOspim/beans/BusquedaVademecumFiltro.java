package ar.com.ospim.farmaciaOspim.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liferay.portal.kernel.util.ParamUtil;

public class BusquedaVademecumFiltro implements Serializable {
	
	private static final long serialVersionUID = 7660154674673345362L;
	
	private int troquel;
	private int registro;
	private Date periodo ;
	private Date fecha;
	private String droga;
	private String nombre;
	private String presentacion;
	private String laboratorio;	
    private int pagina;    	
    private boolean todosLosPadrones ;
    private boolean soloInformadosxLaSss;
    private boolean pmiMadre;
    private boolean pmiHijo;
    private boolean aco;
    private boolean vadeGral;
    private boolean padronMolineros ;
    
	private boolean buscaEnHistoricoDeVademecum;
	private boolean buscasoloNuevasAltas;
	
    
	public BusquedaVademecumFiltro() {
		
	}
	
	public BusquedaVademecumFiltro(String nombre ,String presentacion  ,String droga ,String laboratorio ,Integer troquel
			,Integer registro ,Boolean pmiHijo ,	Boolean pmiMadre 
			,Boolean anticonceptivo ,Boolean gral, int pagina , boolean todosLosPadrones, boolean  aco ,boolean vadeGral 
			, boolean soloInformadosxSuper , boolean buscaEnHistoricoVademecum  , boolean  soloNuevasAltas, boolean  padronMolineros )
		 {
		
		this.droga= droga;
		this.laboratorio= laboratorio;
		this.nombre = nombre;
		this.pagina = pagina;
		this.presentacion = presentacion;
		this.registro = registro;
		this.troquel = troquel;
		this.todosLosPadrones = todosLosPadrones;
		this.pmiHijo = pmiHijo;
		this.pmiMadre = pmiMadre;
		this.aco = aco;
		this.vadeGral = vadeGral;
		this.soloInformadosxLaSss= soloInformadosxSuper ;
		this.buscaEnHistoricoDeVademecum = buscaEnHistoricoVademecum;
		this.buscasoloNuevasAltas= soloNuevasAltas;
		this.padronMolineros=padronMolineros ;
		
	}

	public BusquedaVademecumFiltro(boolean pmiHijo,boolean pmiMadre	, 
			boolean todosLosPadrones, boolean  aco ,boolean vadeGral, boolean molineros )
		 {
		this.todosLosPadrones = todosLosPadrones;
		this.pmiHijo = pmiHijo;
		this.pmiMadre = pmiMadre;
		this.aco = aco;
		this.vadeGral = vadeGral;
		this.padronMolineros= molineros ;
	}
	
	public boolean isBuscasoloNuevasAltas() {
		return buscasoloNuevasAltas;
	}

	public void setBuscasoloNuevasAltas(boolean buscasoloNuevasAltas) {
		this.buscasoloNuevasAltas = buscasoloNuevasAltas;
	}

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

	public int getTroquel() {
		return troquel;
	}

	public void setTroquel(int troquel) {
		this.troquel = troquel;
	}

	public int getRegistro() {
		return registro;
	}

	public void setRegistro(int registro) {
		this.registro = registro;
	}
	public Date getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public String getDroga() {
		return droga;
	}
	public void setDroga(String droga) {
		this.droga = droga;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getPresentacion() {
		return presentacion;
	}
	public void setPresentacion(String presentacion) {
		this.presentacion = presentacion;
	}
	public String getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(String laboratorio) {
		this.laboratorio = laboratorio;
	}

    
	public boolean isPmiMadre() {
		return pmiMadre;
	}

	public void setPmiMadre(boolean pmiMadre) {
		this.pmiMadre = pmiMadre;
	}

	public boolean isPmiHijo() {
		return pmiHijo;
	}

	public void setPmiHijo(boolean pmiHijo) {
		this.pmiHijo = pmiHijo;
	}

	public boolean isAco() {
		return aco;
	}

	public void setAco(boolean aco) {
		this.aco = aco;
	}

	public boolean isVadeGral() {
		return vadeGral;
	}

	public void setVadeGral(boolean vadeGral) {
		this.vadeGral = vadeGral;
	}

	public boolean isTodosLosPadrones() {
		return todosLosPadrones;
	}

	public void setTodosLosPadrones(boolean todosLosPadrones) {
		this.todosLosPadrones = todosLosPadrones;
	}
	
	public boolean isSoloInformadosxSss() {
		return soloInformadosxLaSss;
	}

	public void setSoloInformadosxSss(boolean soloInformados ) {
		this.soloInformadosxLaSss= soloInformados ;
	}
	
	public boolean isBuscaEnHistoricoDeVademecum() {
			return buscaEnHistoricoDeVademecum;
	}

	public void setBuscaEnHistoricoDeVademecum(boolean buscaEnHistoricoDeVademecum) {
			this.buscaEnHistoricoDeVademecum = buscaEnHistoricoDeVademecum;
	}
	
	public boolean isPadronMolineros() {
		return padronMolineros;
	}

	public void setPadronMolineros(boolean padronMolineros) {
		this.padronMolineros = padronMolineros;
	}

	public String getPadronDescripcionFiltros(){
		
		
		String descripcion = "";		
		
		descripcion +=  (pmiMadre?"Padrón Pmi Madre / ":"");
		descripcion +=  (pmiHijo?"Padrón Pmi Hijo / ":"");
		descripcion +=  (aco?"Padrón Anticonceptivos / ":"");
		descripcion +=  (vadeGral?"Padrón General / ":"");
		descripcion +=  (padronMolineros ?"Padrón Molineros / ":"");
		descripcion +=  (todosLosPadrones ?"Padrón Vademecum / ":"");
		descripcion = descripcion.equals("")?"Vademecum":descripcion.substring(0,descripcion.length()-2 );
		return descripcion;
		
	}
	
    public String getDescripcionTipoPadron(){
		String descripcionTipo = "";
		descripcionTipo +=  (pmiMadre?"Pmi Madre/":"");
		descripcionTipo +=  (pmiHijo?"Pmi Hijo/":"");
		descripcionTipo +=  (aco?"Anticonceptivos/":"");
		descripcionTipo +=  (vadeGral?"General/":"");
		descripcionTipo +=  (padronMolineros ?"Molineros/":"");
		descripcionTipo +=  (todosLosPadrones ?"Vademecum/":"");
		descripcionTipo = descripcionTipo.equals("")?"Vademecum":descripcionTipo.substring(0,descripcionTipo.length()-1 );
		return descripcionTipo;
	}
}
