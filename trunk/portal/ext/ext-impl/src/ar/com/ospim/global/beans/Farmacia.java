package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.farmaciaOspim.beans.ItemFarmaciaTotal;
import ar.com.ospim.global.beans.ColegioFarmacia;
import ar.com.ospim.global.beans.Seccional;

public class Farmacia {
	private int id_farmacia;
	private String camara;
	private String farmacia;
	private String codigo;
	private String calle;
	private String telefono;
	private String codigoFarmacia;
	private String codigoFarmaciaMandataria;
	private String destino;
	private Empresa empresa;
	private BigDecimal porcDescuento;
	private String baseDto; //0 OSP+AMT //1 PVP
	private String cheque_a_nombre_de;
	private String cbu;
	private String emailCBU;
    private Date fechaBaja ;
    private int idSeccional;
    private String codigoColegio ;
    private ColegioFarmacia colegio;
    private Seccional seccional;
    private Domicilio domicilio;
    
	public Farmacia() {
		super();
	}
	
	public Farmacia(String  calle, String telefono, String  codigo, String  cuit,
			String   camara, String farmaciaDesc, String  codigofarmacia, 
			BigDecimal porcedesc , String  sucursal , int id_seccional
			, String nombreSeccional , String nombreColegio , String codColegio ,String baseDescuento , String codigoFarmaciaMandataria,
			int provincia , int localidad) {
		
		super();
		
		this.calle=calle;
		this.telefono=telefono;
		this.codigo=codigo;
		this.codigoFarmacia=codigofarmacia;
		this.codigoFarmaciaMandataria = codigoFarmaciaMandataria  ;
		this.camara= camara ; 
		this.farmacia=farmaciaDesc;
		this.setBaseDto(baseDescuento);
	    this.setPorcDescuento(porcedesc);		
		this.setEmpresa(new Empresa(cuit,sucursal));
		this.colegio= new ColegioFarmacia(codColegio,nombreColegio );
		
		
		Domicilio domicilio=new Domicilio();
		domicilio.setProvinciaId(provincia); 
		domicilio.setLocalidadId(localidad);		
		domicilio.setCalle(this.calle );
		domicilio.setTelefono(telefono != null ? telefono : "");
		this.setDomicilioDefault(domicilio);
		
	    Seccional secc = new Seccional(id_seccional, nombreSeccional);
		this.setSeccional(secc);
	}
	
	public Farmacia(String codigoPrestador, String nroFarmacia) {
		super();
		
		this.codigo = codigoPrestador;
		this.codigoFarmacia = nroFarmacia;
	}
	
	public Farmacia(int idFarmacia, String descFarmacia) {
		super();
		this.id_farmacia = idFarmacia;
		this.farmacia = descFarmacia;
	}

	public Farmacia(Integer idSerialFarm, String codFarm, String descripcion) {
		super();
		this.id_farmacia = idSerialFarm;
		this.codigoFarmacia = codFarm;
		this.farmacia = descripcion;
		
	}
	
	public String getCamara() {
		return camara;
	}

	public void setCamara(String camara) {
		this.camara = camara;
	}

	public String getFarmacia() {
		return farmacia;
	}

	public void setFarmacia(String farmacia) {
		this.farmacia = farmacia;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCodigoFarmacia() {
		return codigoFarmacia;
	}

	public void setCodigoFarmacia(String codigoFarmacia) {
		this.codigoFarmacia = codigoFarmacia;
	}

	public String getCodigoFarmaciaMandataria() {
		return codigoFarmaciaMandataria;
	}

	public void setCodigoFarmaciaMandataria(String codigoFarmacia) {
		this.codigoFarmaciaMandataria= codigoFarmacia;
	}	
	
	public static Farmacia getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	public String getDescripcion(){
		return this.farmacia;
	}
	
	public String getId(){
		return this.codigo;
	}
		
	public BigDecimal getPorcDescuento() {
		return porcDescuento;
	}

	public void setPorcDescuento(BigDecimal porcDescuento) {
		this.porcDescuento = porcDescuento;
	}

	public String getCheque_a_nombre_de() {
		return cheque_a_nombre_de;
	}

	public void setCheque_a_nombre_de(String chequeANombreDe) {
		cheque_a_nombre_de = chequeANombreDe;
	}

	public String getBaseDto() {
		return baseDto;
	}

	public void setBaseDto(String baseDto) {
		this.baseDto = baseDto;
	}

	
	
	public static Farmacia getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Farmacia f = new Farmacia();
		
		f.setId_farmacia(rs.getInt(prefix + "id_farmacia"));
		f.setCamara(rs.getString(prefix + "camara"));
		f.setFarmacia(rs.getString(prefix + "farmacia"));
		f.setEmpresa(new Empresa(rs.getString(prefix + "cuit"), rs
				.getString(prefix + "sucursal"), null));
		f.setCodigo(rs.getString(prefix + "codigo"));
		f.setCalle(rs.getString(prefix + "calle"));
		f.setTelefono(rs.getString(prefix + "telefono"));
		f.setCodigoFarmacia(rs.getString(prefix + "cod_farm"));
		try {
			f.setPorcDescuento(rs.getBigDecimal(prefix + "porc_descuento"));
			f.setBaseDto(rs.getString("base_dto"));
			f.setDestino(rs.getString("destino"));
			f.setCBU(rs.getString("cbu"));
			f.setEmailCBU(rs.getString("email_cbu"));
			f.setCheque_a_nombre_de(rs.getString(prefix + "cheque_a_nombre_de"));
			f.setIdSeccional(rs.getInt(prefix + "id_seccional"));
		} catch (Exception e) {
		}
		
		return f;
	}
	public static Farmacia getMappingColegioSeccional(ResultSet rs, String prefix)
			throws SQLException {
		Farmacia f = new Farmacia();
		
		f.setId_farmacia(rs.getInt(prefix + "id_farmacia"));
		f.setCamara(rs.getString(prefix + "camara"));
		f.setFarmacia(rs.getString(prefix + "farmacia"));
		f.setEmpresa(new Empresa(rs.getString(prefix + "cuit"), rs
		.getString(prefix + "sucursal"), rs.getString(prefix + "farmacia")));
		f.setCodigo(rs.getString(prefix + "codigo"));
		f.setCalle(rs.getString(prefix + "calle"));
		f.setTelefono(rs.getString(prefix + "telefono"));
		f.setCodigoFarmacia(rs.getString(prefix + "cod_farm"));
		try {
			f.setPorcDescuento(rs.getBigDecimal(prefix + "porc_descuento"));
			f.setBaseDto(rs.getString("base_dto")==null?"":rs.getString("base_dto"));
			f.setDestino(rs.getString("destino"));
			f.setCBU(rs.getString("cbu"));
			f.setEmailCBU(rs.getString("email_cbu"));
			f.setCheque_a_nombre_de(rs.getString(prefix + "cheque_a_nombre_de"));
			f.setIdSeccional(rs.getInt(prefix + "id_seccional"));
			f.setBajaFecha(rs.getDate(prefix + "baja_fecha"));	
			f.setCodigoFarmaciaMandataria(rs.getString(prefix + "cod_farm_mandataria"));
			f.setColegio(new ColegioFarmacia(rs.getString("cod_Colegio") ,rs.getString("nombre_Colegio") ) );
		    Seccional secc = new Seccional(rs.getInt("id_seccional") , rs.getString("nombre_seccional"));
			f.setSeccional(secc);			
			Domicilio domicilio=new Domicilio();
			domicilio.setProvinciaId(rs.getInt("provincia")); 
			domicilio.setLocalidadId(rs.getInt("localidad"));		
			domicilio.setCalle(rs.getString(prefix + "Calle"));
			domicilio.setTelefono(rs.getString(prefix + "telefono")!= null ? rs.getString(prefix + "telefono"): "");
			f.setDomicilioDefault(domicilio);
			
		} catch (Exception e) {
		}
		
		return f;
	}

	public static ItemFarmaciaTotal getMappingFarmaciaTotal(ResultSet rs, String prefix)
			throws SQLException {
		ItemFarmaciaTotal f = new ItemFarmaciaTotal();
		
		f.setId_farmacia(rs.getInt(prefix + "id_farmacia"));
		f.setCamara(rs.getString(prefix + "camara"));
		f.setFarmacia(rs.getString(prefix + "farmacia"));
		f.setEmpresa(new Empresa(rs.getString(prefix + "cuit"), rs
		.getString(prefix + "sucursal"), rs.getString(prefix + "farmacia")));
		f.setCodigo(rs.getString(prefix + "codigo"));
		f.setCalle(rs.getString(prefix + "calle"));
		f.setTelefono(rs.getString(prefix + "telefono"));
		f.setCodigoFarmacia(rs.getString(prefix + "cod_farm"));
		f.setTotal_registros(rs.getInt(prefix+"canttotal"));
		
		try {
			
			f.setPorcDescuento(rs.getBigDecimal(prefix + "porc_descuento"));
			f.setBaseDto(rs.getString("base_dto")==null?"":rs.getString("base_dto"));
			f.setDestino(rs.getString("destino"));
			f.setCBU(rs.getString("cbu"));
			f.setEmailCBU(rs.getString("email_cbu"));
			f.setCheque_a_nombre_de(rs.getString(prefix + "cheque_a_nombre_de"));
			f.setBajaFecha(rs.getDate(prefix + "baja_fecha"));	
			f.setCodigoFarmaciaMandataria(rs.getString(prefix + "cod_farm_mandataria"));
			f.setColegio(new ColegioFarmacia(rs.getString("cod_Colegio") ,rs.getString("nombre_Colegio") ) );
		    Seccional secc = new Seccional(rs.getInt("id_seccional") , rs.getString("nombre_seccional"));
			f.setSeccional(secc);
			Domicilio domicilio=new Domicilio();
			domicilio.setProvinciaId(rs.getInt("provincia")); 
			domicilio.setLocalidadId(rs.getInt("localidad"));		
			domicilio.setCalle(rs.getString(prefix + "Calle"));
			domicilio.setTelefono(rs.getString(prefix + "telefono")!= null ? rs.getString(prefix + "telefono"): "");
			f.setDomicilioDefault(domicilio);
			
		} catch (Exception e) {
		}
		
		return f;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result
				+ ((codigoFarmacia == null) ? 0 : codigoFarmacia.hashCode());
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
		Farmacia other = (Farmacia) obj;
		if (codigo == null) {
			if (other.codigo != null){
				return false;
			}
		} else if (!codigo.equals(other.codigo)){
			return false;
		}
		if (codigoFarmacia == null) {
			if (other.codigoFarmacia != null){
				return false;
			}
		} else if (!codigoFarmacia.equals(other.codigoFarmacia)){
			return false;
		}
		return true;
	}

	public int getId_farmacia() {
		return id_farmacia;
	}

	public void setId_farmacia(int id_farmacia) {
		this.id_farmacia = id_farmacia;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public String getCBU() {
		return cbu;
	}

	public void setCBU(String cbu) {
		this.cbu = cbu;
	}

	public String getEmailCBU() {
		return emailCBU;
	}

	public void setEmailCBU(String emailCBU) {
		this.emailCBU = emailCBU;
	}

	public String getCodigoColegio() {
		return codigoColegio;
	}

	public void setCodigoColegio(String codigo ) {
		this.codigoColegio = codigo ;
	}
	 
	
	public Date getBajaFecha() {
		return fechaBaja;
	}

	public void setBajaFecha(Date bajaFecha) {
	  this.fechaBaja= bajaFecha;
	}

	void setIdSeccional(int seccionalId){
		this.idSeccional = seccionalId;
	}
	
	int getSeccionalId(){
		return this.idSeccional;
	}

	public void setColegio(ColegioFarmacia datosColegio){
		colegio = datosColegio;
	}
	
	public ColegioFarmacia getColegio(){
		return colegio;
	}
	
	public void setSeccional (Seccional datosSeccional ){
		seccional= datosSeccional ;
	}
	
	public Seccional  getSeccional (){
		return seccional;
	}
	
	public Domicilio getDomicilioDefault() {
		return domicilio = domicilio == null ? new Domicilio() : domicilio;
	}

	public Domicilio setDomicilioDefault(Domicilio domicilioDatos) {
		domicilio= new Domicilio();
		return domicilio = domicilioDatos;
	}
}
