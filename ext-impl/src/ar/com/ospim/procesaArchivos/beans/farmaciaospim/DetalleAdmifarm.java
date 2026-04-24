package ar.com.ospim.procesaArchivos.beans.farmaciaospim;

import java.sql.Date;
import java.sql.ResultSet;

public class DetalleAdmifarm {

    private Date   hasta;
    private String cod_plan;
    private String desc_plan;
    private String dni_benef;
    private String nombre_benef;
    private String fecha;
    private String dispensa;
    private String tipo_matricula;
    private String matricula;
    private String profesional;
    private String registro;
    private String troquel;
    private String nombre_comercial;
    private String pot;
    private String accion;
    private String principio;
    private String nro_lote;
    private String orden;
    private String receta;
    private String nro_item;
    private Double env;
    private Double precio_unitario;
    private Double pvp;
    private Double porcentaje;
    private Double entidad;
    private Double porc_bonif;
    private Double imp_bonif;
    private Double imp_neto;
    private String cod_farmacia;
    private String farmacia;
    private String localidad;
    private String provincia;
    private String region;
    private String laboratorio;
    private String autorizacion;
    private Integer id_cabecera;
    private String pmi;
    private Double monto_ospim;
    private Double monto_uoma;
    private Double monto_amtima;
    private String plan;
    private String inte;
    private String id_ospim;
    private String id_uoma;
    private String id_amtima;
    private String id_seccional;
    private String seccional;
    private String comentario;
    private String cuil_titular;
    private String cuit_farmacia;

    public static DetalleAdmifarm getMapping(ResultSet rs) throws Exception {
        DetalleAdmifarm d = new DetalleAdmifarm();

        d.hasta           = rs.getDate("hasta");
        d.cod_plan        = rs.getString("cod_plan");
        d.desc_plan       = rs.getString("desc_plan");
        d.dni_benef       = rs.getString("dni_benef");
        d.nombre_benef    = rs.getString("nombre_benef");
        d.fecha           = rs.getString("fecha");
        d.dispensa        = rs.getString("dispensa");
        d.tipo_matricula  = rs.getString("tipo_matricula");
        d.matricula       = rs.getString("matricula");
        d.profesional     = rs.getString("profesional");
        d.registro        = rs.getString("registro");
        d.troquel         = rs.getString("troquel");
        d.nombre_comercial= rs.getString("nombre_comercial");
        d.pot             = rs.getString("pot");
        d.accion          = rs.getString("accion");
        d.principio       = rs.getString("principio");
        d.nro_lote        = rs.getString("nro_lote");
        d.orden           = rs.getString("orden");
        d.receta          = rs.getString("receta");
        d.nro_item        = rs.getString("nro_item");
        d.env             = rs.getDouble("env");
        d.precio_unitario = rs.getDouble("precio_unitario");
        d.pvp             = rs.getDouble("pvp");
        d.porcentaje      = rs.getDouble("porcentaje");
        d.entidad         = rs.getDouble("entidad");
        d.porc_bonif      = rs.getDouble("porc_bonif");
        d.imp_bonif       = rs.getDouble("imp_bonif");
        d.imp_neto        = rs.getDouble("imp_neto");
        d.cod_farmacia    = rs.getString("cod_farmacia");
        d.farmacia        = rs.getString("farmacia");
        d.localidad       = rs.getString("localidad");
        d.provincia       = rs.getString("provincia");
        d.region          = rs.getString("region");
        d.laboratorio     = rs.getString("laboratorio");
        d.autorizacion    = rs.getString("autorizacion");
        d.id_cabecera     = rs.getInt("id_cabecera");
        d.pmi             = rs.getString("pmi");
        d.monto_ospim     = rs.getDouble("monto_ospim");
        d.monto_uoma      = rs.getDouble("monto_uoma");
        d.monto_amtima    = rs.getDouble("monto_amtima");
        d.plan            = rs.getString("plan");
        d.inte            = rs.getString("inte");
        d.id_ospim        = rs.getString("id_ospim");
        d.id_uoma         = rs.getString("id_uoma");
        d.id_amtima       = rs.getString("id_amtima");
        d.id_seccional    = rs.getString("id_seccional");
        d.seccional       = rs.getString("seccional");
        d.comentario      = rs.getString("comentario");
        d.cuil_titular    = rs.getString("cuil_titular");
        d.cuit_farmacia   = rs.getString("cuit_farmacia");

        return d;
    }

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public String getCod_plan() {
		return cod_plan;
	}

	public void setCod_plan(String cod_plan) {
		this.cod_plan = cod_plan;
	}

	public String getDesc_plan() {
		return desc_plan;
	}

	public void setDesc_plan(String desc_plan) {
		this.desc_plan = desc_plan;
	}

	public String getDni_benef() {
		return dni_benef;
	}

	public void setDni_benef(String dni_benef) {
		this.dni_benef = dni_benef;
	}

	public String getNombre_benef() {
		return nombre_benef;
	}

	public void setNombre_benef(String nombre_benef) {
		this.nombre_benef = nombre_benef;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getDispensa() {
		return dispensa;
	}

	public void setDispensa(String dispensa) {
		this.dispensa = dispensa;
	}

	public String getTipo_matricula() {
		return tipo_matricula;
	}

	public void setTipo_matricula(String tipo_matricula) {
		this.tipo_matricula = tipo_matricula;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getProfesional() {
		return profesional;
	}

	public void setProfesional(String profesional) {
		this.profesional = profesional;
	}

	public String getRegistro() {
		return registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}

	public String getTroquel() {
		return troquel;
	}

	public void setTroquel(String troquel) {
		this.troquel = troquel;
	}

	public String getNombre_comercial() {
		return nombre_comercial;
	}

	public void setNombre_comercial(String nombre_comercial) {
		this.nombre_comercial = nombre_comercial;
	}

	public String getPot() {
		return pot;
	}

	public void setPot(String pot) {
		this.pot = pot;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public String getPrincipio() {
		return principio;
	}

	public void setPrincipio(String principio) {
		this.principio = principio;
	}

	public String getNro_lote() {
		return nro_lote;
	}

	public void setNro_lote(String nro_lote) {
		this.nro_lote = nro_lote;
	}

	public String getOrden() {
		return orden;
	}

	public void setOrden(String orden) {
		this.orden = orden;
	}

	public String getReceta() {
		return receta;
	}

	public void setReceta(String receta) {
		this.receta = receta;
	}

	public String getNro_item() {
		return nro_item;
	}

	public void setNro_item(String nro_item) {
		this.nro_item = nro_item;
	}

	public Double getEnv() {
		return env;
	}

	public void setEnv(Double env) {
		this.env = env;
	}

	public Double getPrecio_unitario() {
		return precio_unitario;
	}

	public void setPrecio_unitario(Double precio_unitario) {
		this.precio_unitario = precio_unitario;
	}

	public Double getPvp() {
		return pvp;
	}

	public void setPvp(Double pvp) {
		this.pvp = pvp;
	}

	public Double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(Double porcentaje) {
		this.porcentaje = porcentaje;
	}

	public Double getEntidad() {
		return entidad;
	}

	public void setEntidad(Double entidad) {
		this.entidad = entidad;
	}

	public Double getPorc_bonif() {
		return porc_bonif;
	}

	public void setPorc_bonif(Double porc_bonif) {
		this.porc_bonif = porc_bonif;
	}

	public Double getImp_bonif() {
		return imp_bonif;
	}

	public void setImp_bonif(Double imp_bonif) {
		this.imp_bonif = imp_bonif;
	}

	public Double getImp_neto() {
		return imp_neto;
	}

	public void setImp_neto(Double imp_neto) {
		this.imp_neto = imp_neto;
	}

	public String getCod_farmacia() {
		return cod_farmacia;
	}

	public void setCod_farmacia(String cod_farmacia) {
		this.cod_farmacia = cod_farmacia;
	}

	public String getFarmacia() {
		return farmacia;
	}

	public void setFarmacia(String farmacia) {
		this.farmacia = farmacia;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getLaboratorio() {
		return laboratorio;
	}

	public void setLaboratorio(String laboratorio) {
		this.laboratorio = laboratorio;
	}

	public String getAutorizacion() {
		return autorizacion;
	}

	public void setAutorizacion(String autorizacion) {
		this.autorizacion = autorizacion;
	}

	public Integer getId_cabecera() {
		return id_cabecera;
	}

	public void setId_cabecera(Integer id_cabecera) {
		this.id_cabecera = id_cabecera;
	}

	public String getPmi() {
		return pmi;
	}

	public void setPmi(String pmi) {
		this.pmi = pmi;
	}

	public Double getMonto_ospim() {
		return monto_ospim;
	}

	public void setMonto_ospim(Double monto_ospim) {
		this.monto_ospim = monto_ospim;
	}

	public Double getMonto_uoma() {
		return monto_uoma;
	}

	public void setMonto_uoma(Double monto_uoma) {
		this.monto_uoma = monto_uoma;
	}

	public Double getMonto_amtima() {
		return monto_amtima;
	}

	public void setMonto_amtima(Double monto_amtima) {
		this.monto_amtima = monto_amtima;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getInte() {
		return inte;
	}

	public void setInte(String inte) {
		this.inte = inte;
	}

	public String getId_ospim() {
		return id_ospim;
	}

	public void setId_ospim(String id_ospim) {
		this.id_ospim = id_ospim;
	}

	public String getId_uoma() {
		return id_uoma;
	}

	public void setId_uoma(String id_uoma) {
		this.id_uoma = id_uoma;
	}

	public String getId_amtima() {
		return id_amtima;
	}

	public void setId_amtima(String id_amtima) {
		this.id_amtima = id_amtima;
	}

	public String getId_seccional() {
		return id_seccional;
	}

	public void setId_seccional(String id_seccional) {
		this.id_seccional = id_seccional;
	}

	public String getSeccional() {
		return seccional;
	}

	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getCuil_titular() {
		return cuil_titular;
	}

	public void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}

	public String getCuit_farmacia() {
		return cuit_farmacia;
	}

	public void setCuit_farmacia(String cuit_farmacia) {
		this.cuit_farmacia = cuit_farmacia;
	} 
}
