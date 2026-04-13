package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.liferay.portal.kernel.util.StringUtil;

import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.Provincia;

public class FichaBoletaPortal {

	private String descripcion;
	private String cuenta_sucursal;
	private int cod_sucursal_nacion;
	private String nombre_suc_nacion;
	private Date fecha_recauda;
	private Date fecha_rendicion;
	private Date periodo_cod_barras;
	private String cuit;
	private int nro_boleta_portal_emple;
	private String razon_soc;
	private BigDecimal importe;
	private BigDecimal nro_cheque;
	private String estado_cheque;
	private String nroacta;
	private String observacion;

	// para todas las empreasas
	private String empresa_cuit;
	// private String razonSoc;
	private String camara;
	private String fecha_ing;
	private String categoriasalarial;
	private BigDecimal remuneracion;
	private BigDecimal aportesocialuoma;
	private BigDecimal articulo46;
	private BigDecimal cuotaamtima;
	private BigDecimal cuotasocialuoma;
	private BigDecimal cuotausufructo;
	private BigDecimal adherenteamtima;
	private String apellido;
	private String cuil_titular;
	private BigDecimal remuneracion2;
	private double importenoremunerativo;
	
	
	private int totalSocialUoma; 
	private int totalCuotaUoma;  
	private int totalUsufructo; 
	private int totalArt46; 
	private int totalAmtima; 
	private int totalAdhAmtima;
	private int totalDeclarada;
	
	private int totalOS;
	
	private int cantidad;
	private BigDecimal capital;
	private BigDecimal interes;
	private BigDecimal ajusteCapital;
	private BigDecimal ajusteInteres;
	private BigDecimal remuneracionOS;
	private String entidadBoleta;
	private int reporteCantDDJJFinales;
	private int reporteCantDDJJ;
	private int empresasActivas;
	
	private int nro_secuendia_ddjj_portal_emple;
	private PlanCuentas cuenta;
	private PlanCuentas cuentaDevengado;
	private Integer tipoBoleta;
	
	private Domicilio domicilio;
	
	private Long boletaId;
	private Date intencionPago;
	private Date vencimiento;
	private String tipoBoletaStr;
	private String empresa_sucursal;
	private String nroMovimiento;
	private String codBarras;
	

	public static FichaBoletaPortal getMapingFiltrada(ResultSet rs)
			throws SQLException {
		FichaBoletaPortal rdsab = new FichaBoletaPortal();

		rdsab.setDescripcion(rs.getString("descripcion"));
		rdsab.setCuenta_sucursal(rs.getString("Cuenta_sucursal"));
		rdsab.setCod_sucursal_nacion(rs.getInt("cod_sucursal_nacion"));
		rdsab.setNombre_suc_nacion(rs.getString("nombre_suc_nacion"));
		rdsab.setFecha_recauda(rs.getDate("fecha_recauda"));
		rdsab.setPeriodo_cod_barras(rs.getDate("periodo_cod_barras"));
		rdsab.setCuit(rs.getString("cuit"));
		rdsab.setNro_boleta_portal_emple(rs.getInt("nro_boleta_portal_emple"));
		rdsab.setRazon_soc(rs.getString("razon_soc"));
		rdsab.setImporte(rs.getBigDecimal("importe"));
		rdsab.setNro_cheque(rs.getBigDecimal("nro_cheque"));
		rdsab.setEstado_cheque(rs.getString("estado_cheque"));
		rdsab.setNroacta(rs.getString("nroacta"));
		rdsab.setObservacion(rs.getString("observacion"));
		rdsab.setFecha_rendicion(rs.getDate("fecha_rendicion"));
		rdsab.setTipoBoleta(rs.getInt("tipo_boleta_nro"));
		try {
			rdsab.setNroMovimiento(StringUtil.valueOf(rs.getLong("nro_movimiento")));
		}catch(Exception e) {}
		return rdsab;
	}

	public static FichaBoletaPortal getMapingTodasEmpresas(ResultSet rs)
			throws SQLException {
		FichaBoletaPortal rdsab = new FichaBoletaPortal();

		rdsab.setEmpresa_cuit(rs.getString("empresa_cuit"));
		rdsab.setRazon_Soc(rs.getString("razon_Soc"));
		rdsab.setCamara(rs.getString("camara"));
		rdsab.setPeriodo_cod_barras(rs.getDate("periodo"));
		try {//No vienen en el consolidado.
			rdsab.setFecha_ing(rs.getString("fecha_ing"));
			rdsab.setCategoriasalarial(rs.getString("categoriasalarial"));
			rdsab.setApellido(rs.getString("apellido"));
			rdsab.setCuil_titular(rs.getString("cuil_titular"));
			rdsab.setRemuneracion2(rs.getBigDecimal("remuneracion2"));
			Domicilio dom=new Domicilio();
			dom.setPlanta(rs.getString("planta"));
			dom.setCalle(rs.getString("calle"));
			dom.setNumero(rs.getString("numero"));
			dom.setPiso(rs.getString("piso"));
			dom.setDepto(rs.getString("depto"));
			dom.setPostal_codi(rs.getString("postal_codi"));
			dom.setTelefono(rs.getString("telefono"));
			Localidad loc=new Localidad();
			loc.setDescripcion(rs.getString("localidad"));
			dom.setLocalidad(loc);
			Provincia prov=new Provincia();
			prov.setDescripcion(rs.getString("provincia"));
			dom.setProvincia(prov);
			rdsab.setDomicilio(dom);
			
		} catch (Exception e) {
		}
		rdsab.setRemuneracion(rs.getBigDecimal("remuneracion"));
		rdsab.setAportesocialuoma(rs.getBigDecimal("aportesocialuoma"));
		rdsab.setArticulo46(rs.getBigDecimal("articulo46"));
		rdsab.setCuotaamtima(rs.getBigDecimal("cuotaamtima"));
		rdsab.setCuotasocialuoma(rs.getBigDecimal("cuotasocialuoma"));
		rdsab.setCuotausufructo(rs.getBigDecimal("cuotausufructo"));
		rdsab.setAdherenteamtima(rs.getBigDecimal("adherenteamtima"));
		rdsab.setImportenoremunerativo(rs.getDouble("importenoremunerativo"));
		rdsab.setTotalSocialUoma(rs.getInt("cant_aporte_social_uoma"));
		rdsab.setTotalCuotaUoma(rs.getInt("cant_cuota_soc_uoma"));
		rdsab.setTotalUsufructo(rs.getInt("cant_usufructo"));
		rdsab.setTotalArt46(rs.getInt("cant_art_46"));
		rdsab.setTotalAmtima(rs.getInt("cant_cuota_amtima"));
		rdsab.setTotalAdhAmtima(rs.getInt("cant_adh_amtima"));
		rdsab.setTotalDeclarada(rs.getInt("cant_total_declarada"));
		return rdsab;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCuenta_sucursal() {
		return cuenta_sucursal;
	}

	public void setCuenta_sucursal(String cuenta_sucursal) {
		this.cuenta_sucursal = cuenta_sucursal;
	}

	public int getCod_sucursal_nacion() {
		return cod_sucursal_nacion;
	}

	public void setCod_sucursal_nacion(int cod_sucursal_nacion) {
		this.cod_sucursal_nacion = cod_sucursal_nacion;
	}

	public String getNombre_suc_nacion() {
		return nombre_suc_nacion;
	}

	public void setNombre_suc_nacion(String nombre_suc_nacion) {
		this.nombre_suc_nacion = nombre_suc_nacion;
	}

	public Date getFecha_recauda() {
		return fecha_recauda;
	}
	
	public String getFecha_recaudaAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fecha_recauda!=null?sdf.format(fecha_recauda):"";		
	}

	public void setFecha_recauda(Date fecha_recauda) {
		this.fecha_recauda = fecha_recauda;
	}

	public Date getPeriodo_cod_barras() {
		return periodo_cod_barras;
	}
	
	public void setPeriodo_cod_barras(Date periodo_cod_barras) {
		this.periodo_cod_barras = periodo_cod_barras;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public int getNro_boleta_portal_emple() {
		return nro_boleta_portal_emple;
	}

	public void setNro_boleta_portal_emple(int nro_boleta_portal_emple) {
		this.nro_boleta_portal_emple = nro_boleta_portal_emple;
	}

	public String getRazon_soc() {
		return razon_soc;
	}

	public void setRazon_soc(String razon_soc) {
		this.razon_soc = razon_soc;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getNro_cheque() {
		return nro_cheque;
	}

	public void setNro_cheque(BigDecimal nro_cheque) {
		this.nro_cheque = nro_cheque;
	}

	public String getEstado_cheque() {
		return estado_cheque;
	}

	public void setEstado_cheque(String estado_cheque) {
		this.estado_cheque = estado_cheque;
	}

	public String getNroacta() {
		return nroacta;
	}

	public void setNroacta(String nroacta) {
		this.nroacta = nroacta;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public final String getCamara() {
		return camara;
	}

	public final String getFecha_ing() {
		return fecha_ing;
	}

	public final String getCategoriasalarial() {
		return categoriasalarial;
	}

	public final BigDecimal getRemuneracion() {
		return remuneracion;
	}

	public final BigDecimal getAportesocialuoma() {
		return aportesocialuoma;
	}

	public final BigDecimal getArticulo46() {
		return articulo46;
	}

	public final BigDecimal getCuotaamtima() {
		return cuotaamtima;
	}

	public final BigDecimal getCuotasocialuoma() {
		return cuotasocialuoma;
	}

	public final BigDecimal getCuotausufructo() {
		return cuotausufructo;
	}

	public final BigDecimal getAdherenteamtima() {
		return adherenteamtima;
	}

	public final String getApellido() {
		return apellido;
	}

	public final String getCuil_titular() {
		return cuil_titular;
	}

	public final void setCamara(String camara) {
		this.camara = camara;
	}

	public final void setFecha_ing(String fecha_ing) {
		this.fecha_ing = fecha_ing;
	}

	public final void setCategoriasalarial(String categoriasalarial) {
		this.categoriasalarial = categoriasalarial;
	}

	public final void setRemuneracion(BigDecimal remuneracion) {
		this.remuneracion = remuneracion;
	}

	public final void setAportesocialuoma(BigDecimal aportesocialuoma) {
		this.aportesocialuoma = aportesocialuoma;
	}

	public final void setArticulo46(BigDecimal articulo46) {
		this.articulo46 = articulo46;
	}

	public final void setCuotaamtima(BigDecimal cuotaamtima) {
		this.cuotaamtima = cuotaamtima;
	}

	public final void setCuotasocialuoma(BigDecimal cuotasocialuoma) {
		this.cuotasocialuoma = cuotasocialuoma;
	}

	public final void setCuotausufructo(BigDecimal cuotausufructo) {
		this.cuotausufructo = cuotausufructo;
	}

	public final void setAdherenteamtima(BigDecimal adherenteamtima) {
		this.adherenteamtima = adherenteamtima;
	}

	public final void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public final void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}

	public BigDecimal getRemuneracion2() {
		return remuneracion2;
	}

	public void setRemuneracion2(BigDecimal remuneracion2) {
		this.remuneracion2 = remuneracion2;
	}

	public double getImportenoremunerativo() {
		return importenoremunerativo;
	}

	public void setImportenoremunerativo(double importenoremunerativo) {
		this.importenoremunerativo = importenoremunerativo;
	}

	public final String getEmpresa_cuit() {
		return empresa_cuit;
	}

	public final String getRazon_Soc() {
		return razon_soc;
	}

	public final void setEmpresa_cuit(String empresa_cuit) {
		this.empresa_cuit = empresa_cuit;
	}

	public final void setRazon_Soc(String razon_Soc) {
		this.razon_soc = razon_Soc;
	}
	
	public final String getPeriodoAsString(){
		SimpleDateFormat sdf=new SimpleDateFormat("MM/yyyy");
		return periodo_cod_barras!=null?sdf.format(periodo_cod_barras):"";
	}

	public int getTotalSocialUoma() {
		return totalSocialUoma;
	}

	public void setTotalSocialUoma(int totalSocialUoma) {
		this.totalSocialUoma = totalSocialUoma;
	}

	public int getTotalCuotaUoma() {
		return totalCuotaUoma;
	}

	public void setTotalCuotaUoma(int totalCuotaUoma) {
		this.totalCuotaUoma = totalCuotaUoma;
	}

	public int getTotalUsufructo() {
		return totalUsufructo;
	}

	public void setTotalUsufructo(int totalUsufructo) {
		this.totalUsufructo = totalUsufructo;
	}

	public int getTotalArt46() {
		return totalArt46;
	}

	public void setTotalArt46(int totalArt46) {
		this.totalArt46 = totalArt46;
	}

	public int getTotalAmtima() {
		return totalAmtima;
	}

	public void setTotalAmtima(int totalAmtima) {
		this.totalAmtima = totalAmtima;
	}

	public int getTotalAdhAmtima() {
		return totalAdhAmtima;
	}

	public void setTotalAdhAmtima(int totalAdhAmtima) {
		this.totalAdhAmtima = totalAdhAmtima;
	}

	public int getTotalDeclarada() {
		return totalDeclarada;
	}

	public void setTotalDeclarada(int totalDeclarada) {
		this.totalDeclarada = totalDeclarada;
	}

	public void setImportenoremunerativo(Double importenoremunerativo) {
		this.importenoremunerativo = importenoremunerativo;
	}
	
	
	
	public int getTotalOS() {
		return totalOS;
	}

	public void setTotalOS(int totalOS) {
		this.totalOS = totalOS;
	}

	public BigDecimal getRemuneracionOS() {
		return remuneracionOS;
	}

	public void setRemuneracionOS(BigDecimal remuneracionOS) {
		this.remuneracionOS = remuneracionOS;
	}
	
	
	public Date getFecha_rendicion() {
		return fecha_rendicion;
	}

	public void setFecha_rendicion(Date fecha_rendicion) {
		this.fecha_rendicion = fecha_rendicion;
	}
	public String getFecha_rendicionAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fecha_rendicion !=null?sdf.format(fecha_rendicion ):"";		
	}
	
	

	public Domicilio getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	public static HashMap<String, List<FichaBoletaPortal>> getHashMapBoletaPortal(List<FichaBoletaPortal> lista){
		HashMap<String, List<FichaBoletaPortal>> hm=new HashMap<String, List<FichaBoletaPortal>>();		
		for(FichaBoletaPortal repo:lista){			
			List <FichaBoletaPortal> temp=hm.get(repo.getEmpresa_cuit());			
			if(temp==null){				
				temp=new ArrayList<FichaBoletaPortal>();
				temp.add(repo);
			}else{
				temp.add(repo);				
			}
			hm.put(repo.getEmpresa_cuit(),temp);
		}
		return hm;		
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public BigDecimal getCapital() {
		return capital;
	}

	public void setCapital(BigDecimal capital) {
		this.capital = capital;
	}

	public BigDecimal getInteres() {
		return interes;
	}

	public void setInteres(BigDecimal interes) {
		this.interes = interes;
	}

	public BigDecimal getAjusteCapital() {
		return ajusteCapital;
	}

	public void setAjusteCapital(BigDecimal ajusteCapital) {
		this.ajusteCapital = ajusteCapital;
	}

	public BigDecimal getAjusteInteres() {
		return ajusteInteres;
	}

	public void setAjusteInteres(BigDecimal ajusteInteres) {
		this.ajusteInteres = ajusteInteres;
	}

	public String getEntidadBoleta() {
		return entidadBoleta;
	}

	public void setEntidadBoleta(String entidadBoleta) {
		this.entidadBoleta = entidadBoleta;
	}

	public int getReporteCantDDJJFinales() {
		return reporteCantDDJJFinales;
	}

	public void setReporteCantDDJJFinales(int reporteCantDDJJFinales) {
		this.reporteCantDDJJFinales = reporteCantDDJJFinales;
	}

	public int getReporteCantDDJJ() {
		return reporteCantDDJJ;
	}

	public void setReporteCantDDJJ(int reporteCantDDJJ) {
		this.reporteCantDDJJ = reporteCantDDJJ;
	}

	public int getEmpresasActivas() {
		return empresasActivas;
	}

	public void setEmpresasActivas(int empresasActivas) {
		this.empresasActivas = empresasActivas;
	}

	public int getNro_secuendia_ddjj_portal_emple() {
		return nro_secuendia_ddjj_portal_emple;
	}

	public void setNro_secuendia_ddjj_portal_emple(int nro_secuendia_ddjj_portal_emple) {
		this.nro_secuendia_ddjj_portal_emple = nro_secuendia_ddjj_portal_emple;
	}

	public PlanCuentas getCuenta() {
		return cuenta;
	}

	public void setCuenta(PlanCuentas cuenta) {
		this.cuenta = cuenta;
	}

	public Integer getTipoBoleta() {
		return tipoBoleta;
	}

	public void setTipoBoleta(Integer tipoBoleta) {
		this.tipoBoleta = tipoBoleta;
	}

	public PlanCuentas getCuentaDevengado() {
		return cuentaDevengado;
	}

	public void setCuentaDevengado(PlanCuentas cuentaDevengado) {
		this.cuentaDevengado = cuentaDevengado;
	}

	public Long getBoletaId() {
		return boletaId;
	}

	public void setBoletaId(Long id) {
		this.boletaId = id;
	}

	public Date getIntencionPago() {
		return intencionPago;
	}

	public void setIntencionPago(Date intencionPago) {
		this.intencionPago = intencionPago;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public String getTipoBoletaStr() {
		return tipoBoletaStr;
	}

	public void setTipoBoletaStr(String tipoBoletaStr) {
		this.tipoBoletaStr = tipoBoletaStr;
	}

	public String getEmpresa_sucursal() {
		return empresa_sucursal;
	}

	public void setEmpresa_sucursal(String empresa_sucursal) {
		this.empresa_sucursal = empresa_sucursal;
	}

	public String getNroMovimiento() {
		return nroMovimiento;
	}

	public void setNroMovimiento(String nroMovimiento) {
		this.nroMovimiento = nroMovimiento;
	}

	public String getCodBarras() {
		return codBarras;
	}

	public void setCodBarras(String codBarras) {
		this.codBarras = codBarras;
	}
	
	
	
}