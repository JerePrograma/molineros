package ar.com.ospim.crm.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.util.StringUtils;

public class DocumentoLegalCRMTotal extends DocumentoLegalCRM {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2007176745102445298L;

	private int totalRegistros;
	
	public DocumentoLegalCRMTotal (){	
		super();
	}	

	public static DocumentoLegalCRMTotal getMapping(String prefix, ResultSet rs) throws SQLException{
		
		String prefiMotivo = "mot_";
		String prefiTipoReclamo = "tipo_";
		String prefiAfi = "afi_" ;
		String prefiNoAfi = "noafi_" ;
		
		DocumentoLegalCRMTotal dlcrm = new DocumentoLegalCRMTotal();
		Afiliado afi = new Afiliado();
		NoAfiliado noAfi = new NoAfiliado();
		
		String dni = null;
		String cuilTitu = null;

//		ccrm.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		dlcrm.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		dlcrm.setAltaSector(rs.getString(prefix + "alta_sector"));
		dlcrm.setAltaUsr(rs.getString(prefix + "alta_usr"));
		dlcrm.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		dlcrm.setBajaUsr(rs.getString(prefix + "baja_usr"));
		dlcrm.setDescripcion(rs.getString(prefix + "descripcion"));
		dlcrm.setDescripcionSolucion(rs.getString(prefix + "descripcion_solucion"));
		dlcrm.setExpediente(rs.getString(prefix + "expediente"));
		dlcrm.setFechaAvisoAlEstudio(rs.getDate(prefix + "fecha_aviso_estudio"));
		dlcrm.setFechaContactoPSOM(rs.getDate(prefix + "fecha_contacto_ps_om"));
		dlcrm.setFechaNotificacion(rs.getDate(prefix + "fecha_notificacion"));
		dlcrm.setFechaRespuesta(rs.getDate(prefix + "fecha_respuesta"));
		dlcrm.setFechaVencimiento(rs.getDate(prefix + "fecha_vencimiento"));
		dlcrm.setImporteReclamado(rs.getBigDecimal(prefix +"importe_reclamado"));
		dlcrm.setId(rs.getInt(prefix + "id"));
//		ccrm.setModiFecha(rs.getDate(prefix + "modi_fecha"));
		dlcrm.setModiFecha(rs.getTimestamp(prefix + "modi_fecha"));
		dlcrm.setModiSector(rs.getString(prefix + "modi_sector"));
		dlcrm.setModiUsr(rs.getString(prefix + "modi_usr"));

		dlcrm.setTipo(new TipoReclamo(rs.getInt(prefiTipoReclamo + "id_tipo_contacto"), rs.getString(prefiTipoReclamo + "descripcion")));
		dlcrm.setMotivo(new MotivoContacto(rs.getInt(prefiMotivo + "id_motivo"), rs.getString(prefiMotivo + "descripcion") ) );
		dlcrm.setRadicacion(rs.getString(prefix + "radicacion"));
		dlcrm.setResolucion(rs.getString(prefix + "resolucion"));
		dlcrm.setTramiteNumero(rs.getInt(prefix + "tramite_nro"));
		dlcrm.setTotalRegistros(rs.getInt("total_registros_v"));
		
		cuilTitu = rs.getString(prefiAfi + "cuil_titular");
		if(!StringUtils.checkEmpty(cuilTitu)){
			
			afi.setCuil_titular(rs.getString(prefiAfi + "cuil_titular"));
			afi.setInte(rs.getInt(prefiAfi + "inte"));
			afi.setApellido(rs.getString(prefiAfi + "apellido"));
			afi.setNombre(rs.getString(prefiAfi + "nombre"));
			afi.setDocumento_tipo(rs.getString(prefiAfi + "documento_tipo"));
			afi.setDocu_numero(rs.getString(prefiAfi + "docu_numero"));
			afi.setId_ospim(rs.getInt(prefiAfi + "id_ospim"));
			afi.setId_amtima(rs.getInt(prefiAfi + "id_amtima"));
			
			afi.setSeccional(new Seccional(rs.getInt(prefiAfi + "id"), rs.getString(prefiAfi + "descripcion")));
	  
			Plan plan= new Plan(rs.getInt(prefiAfi + "id_plan"),rs.getString(prefiAfi + "plan"));
			afi.setUltimo_plan(plan);
	//		afi.setIngre_fecha(rs.getDate("ingre_fecha"));
			afi.setBaja_fecha(rs.getDate(prefiAfi + "baja_fecha"));
			afi.setId_uoma(rs.getInt(prefiAfi + "id_uoma"));
			afi.setVigen_fecha(rs.getDate(prefiAfi + "vigen_fecha"));
			try{
				afi.setEmail(rs.getString(prefiAfi + "email"));
				Domicilio afiDom = new Domicilio();
				afiDom.setTelefono(rs.getString(prefiAfi + "telefonos"));
				afi.setDomicilioDefault(afiDom); 
				
			}catch (Exception e) {}
			dlcrm.setAfiliado(afi);
		}
		
		try{
			dni = rs.getString(prefiNoAfi + "documento_numero");
		}catch (Exception e) {
			dni = null;
		}	
		if(!StringUtils.checkEmpty(dni)){
			
			noAfi.setDocumentoTipo(rs.getString(prefiNoAfi + "documento_tipo") );
			noAfi.setDocumentoNumero(rs.getString(prefiNoAfi + "documento_numero") );
			noAfi.setApellido(rs.getString(prefiNoAfi + "apellido") );
			noAfi.setNombre(rs.getString(prefiNoAfi + "nombre") );
			noAfi.setTelefono(rs.getString(prefiNoAfi + "telefono") );
			noAfi.setEmail(rs.getString(prefiNoAfi + "email") );
			noAfi.setAltaFecha(rs.getTimestamp(prefiNoAfi + "alta_fecha") );
			noAfi.setAltaUsr(rs.getString(prefiNoAfi + "alta_usr") );
			
			dlcrm.setNoAfiliado(noAfi);
		}
		
		
		return dlcrm;
	}
	
	
//	public String getAltaSector() {
//		
//		UserGroup group = null;
//		try {
//			group = UserGroupUtil.findByPrimaryKey(Long.parseLong(altaSector)); // puede venir o numero o descrip del sector
//		} catch (NoSuchUserGroupException e) {
//			logger.error(e);
//		} catch (NumberFormatException e) {
//			//logger.error(e);
//			return altaSector;
//		} catch (SystemException e) {
//			logger.error(e);
//		}
//		return group!=null?group.getDescription():"no encontrado";
//	}
//
//	public String getModiSector() {
//		UserGroup group = null;
//		try {
//			group = UserGroupUtil.findByPrimaryKey(Long.parseLong(modiSector)); // puede venir o numero o descrip del sector
//		} catch (NoSuchUserGroupException e) {
//			logger.error(e);
//		} catch (NumberFormatException e) {
//			//logger.error(e);
//			return modiSector;
//		} catch (SystemException e) {
//			logger.error(e);
//		}
//		return group!=null?group.getDescription():"no encontrado";
//	}

	public int getTotalRegistros() {
		return totalRegistros;
	}

	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}

	
}
