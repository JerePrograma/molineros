package ar.com.ospim.farmacia.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.farmaciaOspim.beans.ItemMedicacionTotal;

/**
 * @author Federico Brachi
 * @version 1.0
 * @created 14-Jul-2010 12:25:06 p.m.
 */
public class Medicamento implements Comparable<Medicamento> {
	private int id_medicamento;
	private int troquel;
	private int registro;
	private double cantidad;
	private Date fecha;
	private String nombre;
	private String presentacion;
	private String laboratorio;
	private String accion;
	private String droga;
	private BigDecimal precio_al_publico;
	private BigDecimal cober_ospim;//porc ospim
	private BigDecimal cober_amtima;//porc amtima
	private BigDecimal monto_cober_ospim;
	private BigDecimal monto_cober_amtima;
	private BigDecimal cober_sssalud;//porc sssalud
	private BigDecimal precio_ospim;
	private BigDecimal total_medicamento;
	private BigDecimal total_cobertura;
	private BigDecimal total;
	private boolean pmo;
	private int nro_receta;
	//private String profesional;	
	private String baja; //length 1 en la base
	private String cod_barra;
	private int unidades;
	private String tamanio; //length 1 en la base
	private String heladera; //length 1 en la base
	private String sifar; //length 1 en la base
	private String baja_especial; //length 1 en la base
    private Date fecha_baja;
    private Date periodo ;
    private boolean manualDat; 
    private String tipoVenta;
    private String iva;
    
	private static Log _log = LogFactoryUtil.getLog(Medicamento.class);

			
	public Medicamento() {
	}
	
	public Medicamento(int id_medicamento, int troquel, int registro, String nombre,
			String presentacion, String laboratorio, String accion,
			String droga, BigDecimal precio, BigDecimal cober_ospim,
			BigDecimal cober_amtima, BigDecimal porc_sssalud,
			BigDecimal precio_ospim, String cod_barras) {
		this.id_medicamento = id_medicamento;
		this.troquel = troquel;
		this.registro = registro;
		this.nombre = nombre;
		this.presentacion = presentacion;
		this.laboratorio = laboratorio;
		this.accion = accion;
		this.droga = droga;
		this.precio_al_publico = precio;
		this.cober_ospim = cober_ospim;
		this.cober_amtima = cober_amtima;
		this.total_cobertura = (cober_ospim != null ? cober_ospim : BigDecimal.ZERO).add(cober_amtima != null ? cober_amtima : BigDecimal.ZERO);
		this.cober_sssalud = porc_sssalud;
		this.precio_ospim = precio_ospim;
		this.pmo = precio_ospim == null || precio_ospim.doubleValue() == 0 ? false : true;
		this.cod_barra = cod_barras;
	}
	
	public Medicamento(BigDecimal precio, BigDecimal monto_cober_ospim_p,
			BigDecimal monto_cober_amtima_p, String nombre) {		
		this.precio_al_publico = precio;
		this.monto_cober_ospim = monto_cober_ospim_p;
		this.monto_cober_amtima = monto_cober_amtima_p;		
		this.nombre=nombre;
	}

	public Medicamento(int id_medicamento, int troquel, int registro, String nombre,
			String presentacion, String laboratorio, String accion,
			String droga, BigDecimal precio, BigDecimal cober_ospim,
			BigDecimal cober_amtima, BigDecimal cober_sss,  BigDecimal precio_ospim, 
			BigDecimal monto_ospim, BigDecimal monto_amtima, double cantidad, int nro_receta, String profesional, Date fecha_receta, String cod_barras) {
		this.id_medicamento = id_medicamento;
		this.troquel = troquel;
		this.registro = registro;
		this.nombre = nombre;
		this.presentacion = presentacion;
		this.laboratorio = laboratorio;
		this.accion = accion;
		this.droga = droga;
		this.precio_al_publico = precio;
		this.cober_ospim = cober_ospim;
		this.cober_amtima = cober_amtima;
		this.cober_sssalud = cober_sss;
		this.precio_ospim = precio_ospim;		
		this.monto_cober_ospim=monto_ospim;
		this.monto_cober_amtima=monto_amtima;
		this.cantidad=cantidad;
		this.pmo = precio_ospim == null ? false : true;
		this.nro_receta=nro_receta;
		//this.profesional=profesional;
		this.fecha=fecha_receta;
		this.total_medicamento=precio;
		this.total_cobertura=new BigDecimal(cober_ospim.doubleValue()+cober_amtima.doubleValue());
		this.total=new BigDecimal(total_medicamento.doubleValue()-total_cobertura.doubleValue());
		this.cod_barra = cod_barras;
	}

	
	public Medicamento(int idMedicamento, int troquel, int registro, String nombre,
			String presentacion, String laboratorio, String accion,
			String droga, BigDecimal precio,Date fecha , Date fechaPeriodo, String codBarras, String presentacionActiva,
			String tipoventa , String iva , boolean manualDat){
		
		this.id_medicamento = idMedicamento;
		this.troquel = troquel;
		this.registro = registro;
		this.nombre = nombre;
		this.presentacion = presentacion;
		this.laboratorio = laboratorio;
		this.accion = accion;
		this.droga = droga;
		this.precio_al_publico = precio;
		this.setFecha(fecha );
		this.setPeriodo(fechaPeriodo);
		this.cod_barra = codBarras;
		this.baja=presentacionActiva;		 
		this.tipoVenta=tipoventa ;
		this.iva=iva;
		this.manualDat=manualDat;
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

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPresentacion() {
		return presentacion != null ? presentacion : "";
	}

	public void setPresentacion(String presentacion) {
		this.presentacion = presentacion;
	}

	public String getLaboratorio() {
		return laboratorio != null ? laboratorio : "";
	}

	public void setLaboratorio(String laboratorio) {
		this.laboratorio = laboratorio;
	}


	public boolean getManualDat () {
		return manualDat  ;
	}

	public void setManualDat(boolean manualDat) {
		this.manualDat  = manualDat;
	}
	
	public String getAccion() {
		return accion != null ? accion : "";
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public Date getFecha() {
		return fecha;
	}
	
	public String getFechaAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return null!=fecha?sdf.format(fecha):"";
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public Date getPeriodo () {
		return periodo ;
	}
	
	public void setPeriodo (Date fecha) {
		this.periodo = fecha;
	}
	
	public Date getFecha_baja() {
		return fecha_baja;
	}
	
	public void setFecha_baja(Date fecha) {
		this.fecha_baja = fecha;
	}
	
	public BigDecimal getPrecio() {
		return precio_al_publico;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio_al_publico = precio;
	}

	public BigDecimal getCober_ospim() {
		return cober_ospim;
	}

	public void setCober_ospim(BigDecimal coberOspim) {
		cober_ospim = coberOspim;
	}

	public BigDecimal getCober_amtima() {
		return cober_amtima;
	}

	public void setCober_amtima(BigDecimal coberAmtima) {
		cober_amtima = coberAmtima;
	}

	public BigDecimal getPrecio_ospim() {
		return precio_ospim;
	}

	public void setPrecio_ospim(BigDecimal precioOspim) {
		precio_ospim = precioOspim;
	}

	public boolean isPmo() {
		return pmo;
	}

	public void setPmo(boolean pmo) {
		this.pmo = pmo;
	}

	public BigDecimal getCober_sssalud() {
		return cober_sssalud;
	}

	public void setCober_sssalud(BigDecimal coberSssalud) {
		cober_sssalud = coberSssalud;
	}

	public String getDroga() {
		return droga;
	}

	public void setDroga(String droga) {
		this.droga = droga;
	}

	public BigDecimal getMonto_cober_ospim() {
		return monto_cober_ospim;
	}

	public void setMonto_cober_ospim(BigDecimal montoCoberOspim) {
		monto_cober_ospim = montoCoberOspim;
	}

	public BigDecimal getMonto_cober_amtima() {
		return monto_cober_amtima;
	}

	public void setMonto_cober_amtima(BigDecimal montoCoberAmtima) {
		monto_cober_amtima = montoCoberAmtima;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public int compareTo(Medicamento o) {
		if (this.registro == o.getRegistro())
			return 0;
		else if ((this.registro != o.getRegistro()))
			return 1;
		else
			return -1;
	}

	public int getNro_receta() {
		return nro_receta;
	}

	public void setNro_receta(int nroReceta) {
		nro_receta = nroReceta;
	}

//	public String getProfesional() {
//		return profesional;
//	}
//
//	public void setProfesional(String profesional) {
//		this.profesional = profesional;
//	}

	public BigDecimal getTotal_medicamento() {
		return total_medicamento;
	}

	public void setTotal_medicamento(BigDecimal totalMedicamento) {
		total_medicamento = totalMedicamento;
	}

	public BigDecimal getTotal_cobertura() {
		return total_cobertura;
	}

	public void setTotal_cobertura(BigDecimal totalCobertura) {
		total_cobertura = totalCobertura;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public int getId_medicamento() {
		return id_medicamento;
	}

	public void setId_medicamento(int idMedicamento) {
		id_medicamento = idMedicamento;
	}
	
	public String getId_medicamentoAsString() {
		return String.valueOf(id_medicamento);
	}
	
	/**
	 * @return the baja
	 */
	public String getBaja() {
		return baja;
	}

	/**
	 * @param baja the baja to set
	 */
	public void setBaja(String baja) {
		this.baja = baja;
	}

	/**
	 * @return the cod_barra
	 */
	public String getCod_barra() {
		return cod_barra;
	}

	/**
	 * @param codBarra the cod_barra to set
	 */
	public void setCod_barra(String codBarra) {
		cod_barra = codBarra;
	}

	/**
	 * @return the unidades
	 */
	public int getUnidades() {
		return unidades;
	}

	/**
	 * @param unidades the unidades to set
	 */
	public void setUnidades(int unidades) {
		this.unidades = unidades;
	}

	/**
	 * @return the tamanio
	 */
	public String getTamanio() {
		return tamanio;
	}

	/**
	 * @param tamanio the tamanio to set
	 */
	public void setTamanio(String tamanio) {
		this.tamanio = tamanio;
	}

	/**
	 * @return the heladera
	 */
	public String getHeladera() {
		return heladera;
	}

	/**
	 * @param heladera the heladera to set
	 */
	public void setHeladera(String heladera) {
		this.heladera = heladera;
	}

	/**
	 * @return the sifar
	 */
	public String getSifar() {
		return sifar;
	}

	/**
	 * @param sifar the sifar to set
	 */
	public void setSifar(String sifar) {
		this.sifar = sifar;
	}

	/**
	 * @return the baja_especial
	 */
	public String getBaja_especial() {
		return baja_especial;
	}

	/**
	 * @param bajaEspecial the baja_especial to set
	 */
	public void setBaja_especial(String bajaEspecial) {
		baja_especial = bajaEspecial;
	}

	public static Medicamento getMapping(ResultSet rs, String prefix) throws SQLException {
		Medicamento medicamento = new Medicamento();
		medicamento.setId_medicamento(rs.getInt(prefix+"id_medicamento"));
		medicamento.setTroquel(rs.getInt(prefix+"troquel"));
		medicamento.setPresentacion(rs.getString(prefix+"presentacion"));
		medicamento.setNombre(rs.getString(prefix+"nombre"));
		medicamento.setLaboratorio(rs.getString(prefix+"laboratorio"));
//		m.monto_ioma,m.norma_ioma,m.cober_ioma,
		medicamento.setPrecio(rs.getBigDecimal(prefix+"precio"));//precio al público
		medicamento.setFecha(rs.getDate(prefix+"fecha"));
//		m.controlado,m.importado,m.tipo_venta,m.iva,m.cod_dto_pami,m.cod_lab,
		medicamento.setRegistro(rs.getInt(prefix+"nro_registro"));
//		m.nro_registro,
		medicamento.setBaja(rs.getString(prefix+"baja"));		  
		medicamento.setCod_barra(rs.getString(prefix+"cod_barra"));
		medicamento.setUnidades(rs.getInt(prefix+"unidades"));
		medicamento.setTamanio(rs.getString(prefix+"tamanio"));
		medicamento.setHeladera(rs.getString(prefix+"heladera"));
		medicamento.setSifar(rs.getString(prefix+"sifar"));
		medicamento.setBaja_especial(rs.getString(prefix+"baja_especial"));		
		medicamento.setAccion(rs.getString(prefix+"accion"));
		medicamento.setDroga(rs.getString(prefix+"droga"));
//		datos vademecum
		medicamento.setCober_ospim(rs.getBigDecimal(prefix+"porc_ospim"));
		medicamento.setCober_amtima(rs.getBigDecimal(prefix+"porc_amtima"));
		medicamento.setCober_sssalud(rs.getBigDecimal(prefix+"porc_sssalud"));		
		medicamento.setPrecio_ospim(rs.getBigDecimal(prefix+"pmoe_n"));
		medicamento.setPmo(medicamento.getPrecio_ospim() == null ? false : true);		
		return medicamento;
	}
	
	public static Medicamento getMappingOspim(ResultSet rs, String prefix) throws SQLException {
	
		Medicamento medicamento = new Medicamento();
		medicamento.setId_medicamento(rs.getInt(prefix+"id_medicamento"));
		medicamento.setTroquel(rs.getInt(prefix+"troquel"));
		medicamento.setRegistro(rs.getInt(prefix+"nro_registro"));		
		medicamento.setNombre(rs.getString(prefix+"nombre"));
		medicamento.setPresentacion(rs.getString(prefix+"presentacion"));
		medicamento.setLaboratorio(rs.getString(prefix+"laboratorio"));
		medicamento.setAccion(rs.getString(prefix+"accion"));
		medicamento.setDroga(rs.getString(prefix+"droga"));
		medicamento.setPrecio(rs.getBigDecimal(prefix+"precio"));
		medicamento.setPeriodo(rs.getDate(prefix+"periodo"));
		medicamento.setFecha_baja(rs.getDate(prefix+"fecha_baja"));
		medicamento.setCod_barra(rs.getString(prefix+"cod_barra"));
		medicamento.setManualDat(rs.getBoolean(prefix+"manual_dat"));
		
		return medicamento;
	}

	
	public static ItemMedicacionTotal getMappingOspimTotal(ResultSet rs, String prefix) throws SQLException {
		
		ItemMedicacionTotal  medicamento = new ItemMedicacionTotal ();
		medicamento.setId_medicamento(rs.getInt(prefix+"id_medicamento"));
		medicamento.setTroquel(rs.getInt(prefix+"troquel"));
		medicamento.setRegistro(rs.getInt(prefix+"nro_registro"));		
		medicamento.setNombre(rs.getString(prefix+"nombre"));
		medicamento.setPresentacion(rs.getString(prefix+"presentacion"));
		medicamento.setLaboratorio(rs.getString(prefix+"laboratorio"));
		medicamento.setAccion(rs.getString(prefix+"accion"));
		medicamento.setDroga(rs.getString(prefix+"droga"));
		medicamento.setPrecio(rs.getBigDecimal(prefix+"precio"));
		medicamento.setPeriodo(rs.getDate(prefix+"periodo"));
		medicamento.setFecha_baja(rs.getDate(prefix+"fecha_baja"));
		medicamento.setCod_barra(rs.getString(prefix+"cod_barra"));
		medicamento.setManualDat(rs.getBoolean(prefix+"manual_dat"));
		medicamento.setTotal_registros(rs.getInt(prefix+"canttotal"));;
		return medicamento;
	}
	
	public static Medicamento getMappingOspimEdita(ResultSet rs, String prefix) throws SQLException {
		
		Medicamento medicamento = new Medicamento();
		medicamento.setId_medicamento(rs.getInt(prefix+"id_medicamento"));
		medicamento.setTroquel(rs.getInt(prefix+"troquel"));
		medicamento.setRegistro(rs.getInt(prefix+"nro_registro"));		
		medicamento.setNombre(rs.getString(prefix+"nombre"));
		medicamento.setPresentacion(rs.getString(prefix+"presentacion"));
		medicamento.setLaboratorio(rs.getString(prefix+"laboratorio"));
		medicamento.setAccion(rs.getString(prefix+"accion"));
		medicamento.setDroga(rs.getString(prefix+"droga"));
		medicamento.setPrecio(rs.getBigDecimal(prefix+"precio"));
		medicamento.setPeriodo(rs.getDate(prefix+"periodo"));
		medicamento.setFecha_baja(rs.getDate(prefix+"fecha_baja"));
		medicamento.setCod_barra(rs.getString(prefix+"cod_barra")==null?"":rs.getString(prefix+"cod_barra")); 
		medicamento.setManualDat(rs.getBoolean(prefix+"manual_dat"));
		medicamento.setTipoVenta(rs.getString(prefix+"tipoventa")==null?"":rs.getString(prefix+"tipoventa"));
		medicamento.setFecha(rs.getDate(prefix+"fecha"));
		medicamento.setIva(rs.getString(prefix+"iva")==null?"":rs.getString(prefix+"iva")); 
		medicamento.setBaja(rs.getString(prefix+"baja"));
		
		return medicamento;
	}
	
	public String getIva() {
		return iva;
	}

	public void setIva(String iva) {
		this.iva = iva;
	}

	public String getTipoVenta() {
		return tipoVenta;
	}
	
	public int getTipoVentaInt() {
		/*int valor ;
		if (tipoVenta.trim().equals("") || tipoVenta==null  ){
			valor=0;
		}else{
			valor= Integer.valueOf(tipoVenta);
		}
		return ( valor);*/
		return (tipoVenta.trim().equals("") || tipoVenta==null  ?0:Integer.valueOf(tipoVenta)) ;
	}

	public void setTipoVenta(String tipoVenta) {
		this.tipoVenta = tipoVenta;
	}
	
}