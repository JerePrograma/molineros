package ar.com.ospim.crm.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ar.com.empresas.beans.Contacto;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ContactoCRMTotal extends ContactoCRM {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2022524302602259627L;

	private static Log logger = LogFactoryUtil.getLog(ContactoCRMTotal.class);

	private int totalRegistros;
	
	public ContactoCRMTotal (){	
		super();
	}	
	
	public static ContactoCRMTotal getMapping(String prefix, ResultSet rs) throws SQLException{
		
		String prefiMotivo = "mot_";
		String prefiTipo = "tipo_";
		String prefiCateg = "cate_";
		String prefiAfi = "afi_" ;
		String prefiNoAfi = "noafi_" ;
		String prefiEficacia = "efi_";
		
		ContactoCRMTotal ccrm = new ContactoCRMTotal();
		Afiliado afi = new Afiliado();
		NoAfiliado noAfi = new NoAfiliado();
		
		String dni = null;
		String cuilTitu = null;
		
//		ccrm.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		ccrm.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		ccrm.setAltaSector(rs.getString(prefix + "alta_sector"));
		ccrm.setAltaUsr(rs.getString(prefix + "alta_usr"));
		ccrm.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		ccrm.setBajaUsr(rs.getString(prefix + "baja_usr"));
		ccrm.setComentarioCierre(rs.getString(prefix + "comentario_cierre"));
		ccrm.setDescripcion(rs.getString(prefix + "descripcion"));
		ccrm.setEstado(ESTADOS.valueOf(rs.getString(prefix + "estado")) );
		ccrm.setId(rs.getInt(prefix + "id"));
		ccrm.setIdContacto(rs.getInt(prefix + "id_contacto"));
		ccrm.setIdCrmRelacionado(rs.getInt(prefix + "relacionado_con_id"));
		ccrm.setImportancia(rs.getInt(prefix + "importancia"));
		ccrm.setIncumplimientoDelContrato(rs.getInt(prefix + "incumplimiento_contrato"));
//		ccrm.setModiFecha(rs.getDate(prefix + "modi_fecha"));
		ccrm.setModiFecha(rs.getTimestamp(prefix + "modi_fecha"));
		ccrm.setModiSector(rs.getString(prefix + "modi_sector"));
		ccrm.setModiUsr(rs.getString(prefix + "modi_usr"));

		ccrm.setTipo(new TipoContacto(rs.getInt(prefiTipo + "id_tipo_contacto"), rs.getString(prefiTipo + "descripcion")));
		ccrm.setMotivo(new MotivoContacto(rs.getInt(prefiMotivo + "id_motivo"), rs.getString(prefiMotivo + "descripcion") ) );
		ccrm.setCategoria(new CategoriaContacto(rs.getInt(prefiCateg + "id_categoria"), rs.getString(prefiCateg + "descripcion") ) );
		ccrm.setTotalRegistros(rs.getInt("total_registros_v"));
		ccrm.setEficacia(CRMEficacia.getMapping(prefiEficacia, rs));
		
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
			plan.setId_plan_omint(rs.getInt(prefiAfi + "id_plan_omint"));
			plan.setDescripcionOmint(rs.getString(prefiAfi + "plan_omint"));
			afi.setUltimo_plan(plan);
	//		afi.setIngre_fecha(rs.getDate("ingre_fecha"));
			afi.setBaja_fecha(rs.getDate(prefiAfi + "baja_fecha"));
			afi.setId_uoma(rs.getInt(prefiAfi + "id_uoma"));
			afi.setVigen_fecha(rs.getDate(prefiAfi + "vigen_fecha"));
		
			ccrm.setAfiliado(afi);
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
			
			ccrm.setNoAfiliado(noAfi);
		}
		
		
		Contacto con = null;
		try{
		 if(rs.getInt("contacto_seccional")!=0){
			ContactoElectronico ce = new ContactoElectronico();
			ce.setId(rs.getInt("contacto_seccional"));
			con=new Contacto();
			con.setContacto(ce);
			ccrm.setContactoSeccional(con);
			if(rs.getInt(prefiAfi + "id")!=0 && rs.getString(prefiAfi + "descripcion")!=null){
			   con.setSeccional(new Seccional(rs.getInt(prefiAfi + "id"), rs.getString(prefiAfi + "descripcion")));
			}   
			if( rs.getString(prefiAfi + "apellido")!=null){
		 	   con.setNombreApe(rs.getString(prefiAfi + "apellido"));
			} 
			if( rs.getString(prefiAfi + "nombre")!=null){
			   con.setCargoDescripcion(rs.getString(prefiAfi + "nombre"));	
			}
		 }
		}catch(Exception e){
			ccrm.setContactoSeccional(con);
		}
		
		EdificioSectorUsuarioLiferay ediSecUsu = null;
		try{
			DerivacionSeguimiento ds = new DerivacionSeguimiento();

//			ds.setAltaFecha(rs.getTimestamp("derivacion_alta_fecha"));
			ds.setDerivacionEdificio(rs.getString("derivacion_edificio"));
			ds.setDerivacionSector(rs.getString("derivacion_sector"));
			ds.setDerivacionUsr(rs.getString("derivacion_usr"));
			
			ediSecUsu = new EdificioSectorUsuarioLiferay(ds.getDerivacionEdificio(), ds.getDerivacionSector(), ds.getDerivacionUsr());
				
		}catch (Exception e) {
			ediSecUsu = null;
		}
		ccrm.setDerivacion(ediSecUsu);
		
		return ccrm;
	}
	
	public static ContactoCRMTotal getMappingConSeguimiento(String prefix, ResultSet rs) throws SQLException{

		String prefiSeguim = "seg_";
		ContactoCRMTotal contacto = null;
		EdificioSectorUsuarioLiferay ediSecUsu = null;
		
		contacto = ContactoCRMTotal.getMapping(prefix, rs);
		
		contacto.setSeguimiento(new ArrayList<DerivacionSeguimiento>());
		
		DerivacionSeguimiento ds = DerivacionSeguimiento.getMapping(prefiSeguim, rs);
		
		if(ds.getDerivacionUsr() != null && !ds.getDerivacionUsr().isEmpty()){
			ediSecUsu = new EdificioSectorUsuarioLiferay(ds.getDerivacionEdificio(), ds.getDerivacionSector(), ds.getDerivacionUsr());
			contacto.getSeguimiento().add(ds);
			contacto.setDerivacion(ediSecUsu);
		}
		
		return contacto;
	}

	public int getTotalRegistros() {
		return totalRegistros;
	}

	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}
}
