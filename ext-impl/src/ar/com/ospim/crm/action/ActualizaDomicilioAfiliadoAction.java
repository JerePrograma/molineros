package ar.com.ospim.crm.action;

import java.util.List;
import java.util.Objects;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceImpl;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.TelefonoServiceUtil;
import ar.com.ospim.crm.WebKeysCrm;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ActualizaDomicilioAfiliadoAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil
			.getLog(ActualizaDomicilioAfiliadoAction.class);
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
	}
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		User user = PortalUtil.getUser(PortalUtil.getHttpServletRequest(renderRequest));
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD); 
		String cuilTitular = ParamUtil.getString(renderRequest, "cuil_titular");
		int inte = ParamUtil.getInteger(renderRequest, "inte");
		String email = ParamUtil.getString(renderRequest, "email"); 
		String emailOriginal = ParamUtil.getString(renderRequest, "email_original");

		boolean emailModificado =
			    (StringUtils.checkNotEmpty(email) || StringUtils.checkNotEmpty(emailOriginal)) &&
			    !email.trim().equalsIgnoreCase(emailOriginal.trim());
		
		int idDomicilio, idProvincia, idLocalidad;
		String calle, numero, piso, dpto, codigo_postal, barrio, 
			   cod_area_telefono, telefono, cod_area_laboral, telefono_laboral, 
			   cod_area_celular, celular;
		
		List<Domicilio> domicilios = null;
		Domicilio domicilio = null;
		
		if(!StringUtils.checkEmpty(cmd)){
			
			try{
				if(cmd.equalsIgnoreCase(Constants.VIEW)){
					domicilios = BusquedaAfiliadoServiceUtil.buscarDomiciliosAfiliado(cuilTitular, 0);			
					if (domicilios != null && !domicilios.isEmpty()) {
					    domicilio = domicilios.get(0);
					    renderRequest.setAttribute(WebKeysCrm.CRM_AFILIADO_DOMICILIO, domicilio);
					}
					Afiliado afi = EditarAfiliadoServiceUtil.getAfiliadoEntry(cuilTitular, inte);
					int idPar = (afi != null) ? afi.getId_parentesco() : -1;

					String emailDb = (afi != null) ? afi.getEmail() : null;

					boolean esTitularConyConc = (idPar == 0 || idPar == 1 || idPar == 2);

					if (!esTitularConyConc) {
					    Afiliado titular = EditarAfiliadoServiceUtil.getAfiliadoEntry(cuilTitular, 0);
					    if (titular != null && StringUtils.checkNotEmpty(titular.getEmail())) {
					        emailDb = titular.getEmail();
					    }
					}

					renderRequest.setAttribute(WebKeysCrm.CRM_AFILIADO_EMAIL, emailDb);

					
				}
				if(cmd.equalsIgnoreCase(Constants.SAVE)){
					idDomicilio = ParamUtil.getInteger(renderRequest, "id_domicilio");
					idProvincia = ParamUtil.getInteger(renderRequest, "id_provincia");
					idLocalidad = ParamUtil.getInteger(renderRequest, "id_localidad");
					calle = ParamUtil.getString(renderRequest, "calle");
					numero = ParamUtil.getString(renderRequest, "numero");
					piso = ParamUtil.getString(renderRequest, "piso");
					dpto = ParamUtil.getString(renderRequest, "departamento");
					codigo_postal = ParamUtil.getString(renderRequest, "codigo_postal");
					barrio = ParamUtil.getString(renderRequest, "barrio");
					//cod_area_telefono = ParamUtil.getString(renderRequest, "cod_area_telefono");
					//telefono = ParamUtil.getString(renderRequest, "telefono");
					//cod_area_laboral = ParamUtil.getString(renderRequest, "cod_area_laboral");
					//telefono_laboral = ParamUtil.getString(renderRequest, "telefono_laboral");
					//cod_area_celular = ParamUtil.getString(renderRequest, "cod_area_celular");
					//celular = ParamUtil.getString(renderRequest, "celular");

					email = ParamUtil.getString(renderRequest, "email");
				
					/*
					if(cod_area_telefono.equalsIgnoreCase("0")){
						cod_area_telefono = "";
					}
					if(cod_area_laboral.equalsIgnoreCase("0")){
						cod_area_laboral = "";
					}
					if(cod_area_celular.equalsIgnoreCase("0")){
						cod_area_celular= "";
					}
					if(telefono.equalsIgnoreCase("0")){
						telefono= "";
					}
					if(telefono_laboral.equalsIgnoreCase("0")){
						telefono_laboral= "";
					}
					if(celular.equalsIgnoreCase("0")){
						celular= "";
					}
					*/
					domicilio = new Domicilio();
					
					domicilio.setId_domicilio(idDomicilio);
					domicilio.setBarrio(barrio);
					domicilio.setCalle(calle);
					//domicilio.setCelular(celular);
					//domicilio.setCod_area_celular(cod_area_celular);
					//domicilio.setCod_area_tel_laboral(cod_area_laboral);
					//domicilio.setCod_area_telefono(cod_area_telefono);
					domicilio.setDepto(dpto);
					domicilio.setLocalidadId(idLocalidad);
					domicilio.setNumero(numero);
					domicilio.setPiso(piso);
					domicilio.setPostal_codi(codigo_postal);
					domicilio.setProvinciaId(idProvincia);
					//domicilio.setTel_laboral(telefono_laboral);
					//domicilio.setTelefono(telefono);
					domicilio.setDomi_tipo("P");
					domicilio.setDomi_val("0");
					
				    Domicilio domicilioActual = BusquedaAfiliadoServiceUtil.buscarDomiciliosAfiliado(cuilTitular, 0).get(0);				   
				    
				    boolean domicilioModificado =
				    	    !((domicilioActual.getCalle() == null ? "" : domicilioActual.getCalle().trim().toUpperCase())
				    	        .equals(domicilio.getCalle() == null ? "" : domicilio.getCalle().trim().toUpperCase()))
				    	    ||
				    	    !((domicilioActual.getNumero() == null ? "" : domicilioActual.getNumero().trim().toUpperCase())
				    	        .equals(domicilio.getNumero() == null ? "" : domicilio.getNumero().trim().toUpperCase()))
				    	    ||
				    	    !((domicilioActual.getPiso() == null ? "" : domicilioActual.getPiso().trim().toUpperCase())
				    	        .equals(domicilio.getPiso() == null ? "" : domicilio.getPiso().trim().toUpperCase()))
				    	    ||
				    	    !((domicilioActual.getDepto() == null ? "" : domicilioActual.getDepto().trim().toUpperCase())
				    	        .equals(domicilio.getDepto() == null ? "" : domicilio.getDepto().trim().toUpperCase()))
				    	    ||
				    	    !((domicilioActual.getPostal_codi() == null ? "" : domicilioActual.getPostal_codi().trim().toUpperCase())
				    	        .equals(domicilio.getPostal_codi() == null ? "" : domicilio.getPostal_codi().trim().toUpperCase()))
				    	    ||
				    	    !((domicilioActual.getBarrio() == null ? "" : domicilioActual.getBarrio().trim().toUpperCase())
				    	        .equals(domicilio.getBarrio() == null ? "" : domicilio.getBarrio().trim().toUpperCase())); //||
				            //!java.util.Objects.equals(domicilioActual.getCod_area_telefono(), domicilio.getCod_area_telefono()) ||
				            //!java.util.Objects.equals(domicilioActual.getTelefono(), domicilio.getTelefono()) ||
				            //!java.util.Objects.equals(domicilioActual.getCod_area_celular(), domicilio.getCod_area_celular()) ||
				            //!java.util.Objects.equals(domicilioActual.getCelular(), domicilio.getCelular()) ||
				            //!java.util.Objects.equals(domicilioActual.getCod_area_tel_laboral(), domicilio.getCod_area_tel_laboral()) ||
				            //!java.util.Objects.equals(domicilioActual.getTel_laboral(), domicilio.getTel_laboral());

				    
				    if (domicilioModificado && emailModificado) {				        
				        Connection con = null;
				        PreparedStatement stmt = null;
				        CallableStatement stmt2 = null;
				        try {
				            con = ConnectionHelper.getConnection();
				            
				            // 1. Inserta en afi_estados_histo
				            String sql2 = "{call actualiza_afi_estados_histo(?, ?, ?, ?)}";
				            stmt2 = con.prepareCall(sql2);
				            stmt2.setString(1, cuilTitular);
				            stmt2.setInt(2, inte);
				            stmt2.setString(3, user.getScreenName());
				            stmt2.setString(4, "MOD");
				            stmt2.execute();
				            
				            // 2. Inserta fila en afi_domicilio y afi_estados_histo y actualiza email del afiliado
				            String sql = "{call actualiza_domicilio_afiliado3(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
				            stmt2 = con.prepareCall(sql);
				            stmt2.setString(1, cuilTitular);
				            stmt2.setInt(2, inte);
				            stmt2.setString(3, domicilio.getDomi_tipo());
				            stmt2.setString(4, domicilio.getCalle().toUpperCase());
				            stmt2.setString(5, domicilio.getPiso());
				            stmt2.setString(6, domicilio.getDepto().toUpperCase());
				            stmt2.setString(7, domicilio.getPostal_codi());
				            stmt2.setString(8, domicilio.getBarrio().toUpperCase());
				            stmt2.setString(9, domicilio.getDomi_val());
				            stmt2.setInt(10, domicilio.getProvinciaId());
				            stmt2.setInt(11, domicilio.getLocalidadId());
				            stmt2.setString(12, domicilio.getNumero());
				            //stmt2.setString(13, domicilio.getCod_area_telefono());
				            //stmt2.setString(14, domicilio.getTelefono());
				            //stmt2.setString(15, domicilio.getCod_area_celular());
				            //stmt2.setString(16, domicilio.getCelular());
				            //stmt2.setString(17, domicilio.getCod_area_tel_laboral());
				            //stmt2.setString(18, domicilio.getTel_laboral());
				            
				            stmt2.setNull(13, Types.VARCHAR); // cod_area_telefono
				            stmt2.setNull(14, Types.VARCHAR); // telefono
				            stmt2.setNull(15, Types.VARCHAR); // cod_area_celular
				            stmt2.setNull(16, Types.VARCHAR); // celular
				            stmt2.setNull(17, Types.VARCHAR); // cod_area_tel_laboral
				            stmt2.setNull(18, Types.VARCHAR); // tel_laboral
				            
				            if (StringUtils.checkNotEmpty(email)) {
				                stmt2.setString(19, email.toLowerCase());
				            } else {
				                stmt2.setNull(19, Types.VARCHAR);
				            }

				            stmt2.setInt(20, domicilio.getId_domicilio());
				            stmt2.setString(21, user.getScreenName());				            				            
				            stmt2.executeUpdate();
				            
				         	
				        } catch (Exception e) {
				            _log.error("Error al actualizar solo el email", e);
				            throw new SystemException(e);
				        } finally {
				            ConnectionHelper.cerrar(stmt, con);
				        }
				    } else if (emailModificado) {
				        Connection con = null;
				        PreparedStatement stmt = null;
				        CallableStatement stmt2 = null;
				        try {
				            con = ConnectionHelper.getConnection();
				            
				            // 1. Inserta en afi_estados_histo
				            String sql2 = "{call actualiza_afi_estados_histo(?, ?, ?, ?)}";
				            stmt2 = con.prepareCall(sql2);
				            stmt2.setString(1, cuilTitular);
				            stmt2.setInt(2, inte);
				            stmt2.setString(3, user.getScreenName());
				            stmt2.setString(4, "MOD");
				            stmt2.execute();
				            
				            // 2. Actualiza solo el email del afiliado, y la modi_fecha de la tabla afi_telefono
				            String sql = "{call actualiza_solo_email_afiliado(?,?,?,?)}";
				            stmt = con.prepareCall(sql);
				            stmt.setString(1, cuilTitular);
				            stmt.setInt(2, inte);
				            stmt.setString(3, email.toLowerCase());
				            stmt.setString(4, user.getScreenName());
				            stmt.execute();
				            
				         	
				        } catch (Exception e) {
				            _log.error("Error al actualizar solo el email", e);
				            throw new SystemException(e);
				        } finally {
				            ConnectionHelper.cerrar(stmt, con);
				        }
				    } else if(domicilioModificado){
				        EditarAfiliadoServiceUtil.actualizaDomicilio2(cuilTitular, inte, domicilio, email, user.getScreenName());				        			    	
				    }
				    
				    boolean telefonoActualizado = false;

				    String codAreaTel = ParamUtil.getString(renderRequest, "cod_area_telefono");
				    String numeroTel  = ParamUtil.getString(renderRequest, "telefono");
				    String codAreaCel = ParamUtil.getString(renderRequest, "cod_area_celular");
				    String numeroCel  = ParamUtil.getString(renderRequest, "celular");

				    Connection con = null;
				    try {
				        con = ConnectionHelper.getConnection();
				        List<Telefono> actuales = TelefonoServiceUtil.getTelefonos(cuilTitular, inte);

				        final String TIPO_FIJO = "F";
				        final String TIPO_CEL  = "C";

				        //TELEFONO FIJO
				        Telefono telFijoActual = null;
				        for (Telefono t : actuales) {
				            if (TIPO_FIJO.equalsIgnoreCase(t.getTipo())) {
				                telFijoActual = t;
				                break;
				            }
				        }

				        Telefono telFijo = (telFijoActual != null) ? telFijoActual : new Telefono();
				        telFijo.setTipo(TIPO_FIJO);
				        telFijo.setCodigoArea(codAreaTel);
				        telFijo.setNumero(numeroTel);

				        if (telFijo.getId() > 0) {
				            _log.info("Procesando teléfono FIJO existente id=" + telFijo.getId());
				            TelefonoServiceUtil.actualizaTelefono(con, cuilTitular, inte, telFijo, user.getScreenName());
				            telefonoActualizado = true;
				        } else {
				            // si está vacío no se inserta nada, pero si tiene número nuevo, sí
				            if (StringUtils.checkNotEmpty(numeroTel)) {
				                _log.info("Insertando FIJO nuevo");
				                TelefonoServiceUtil.insertaTelefono(con, cuilTitular, inte, telFijo, user.getScreenName());
				                telefonoActualizado = true;
				            } else {
				                _log.info("No hay FIJO nuevo ni existente, nada que insertar");
				            }
				        }

				        //CELULAR
				        Telefono telCelActual = null;
				        for (Telefono t : actuales) {
				            if (TIPO_CEL.equalsIgnoreCase(t.getTipo())) {
				                telCelActual = t;
				                break;
				            }
				        }

				        Telefono telCel = (telCelActual != null) ? telCelActual : new Telefono();
				        telCel.setTipo(TIPO_CEL);
				        telCel.setCodigoArea(codAreaCel);
				        telCel.setNumero(numeroCel);

				        if (telCel.getId() > 0) {
				            _log.info("Procesando CELULAR existente id=" + telCel.getId());
				            TelefonoServiceUtil.actualizaTelefono(con, cuilTitular, inte, telCel, user.getScreenName());
				            telefonoActualizado = true;
				        } else {
				            // si está vacío no se inserta nada, pero si tiene número nuevo, sí
				            if (StringUtils.checkNotEmpty(numeroCel)) {
				                _log.info("Insertando CELULAR nuevo");
				                TelefonoServiceUtil.insertaTelefono(con, cuilTitular, inte, telCel, user.getScreenName());
				                telefonoActualizado = true;
				            } else {
				                _log.info("No hay celular nuevo ni existente, nada que insertar");
				            }
				        }

				    } catch (Exception e) {
				        _log.error("Error guardando/bajando teléfonos del afiliado " + cuilTitular + "/" + inte, e);
				    } finally {
				        ConnectionHelper.cerrar(null, con);
				    }

				    
				    //Si no hubo cambios en domicilio, email ni teléfono
				    if (!domicilioModificado && !telefonoActualizado) {
				        
				        CallableStatement stmt2 = null;
				        try {
				            con = ConnectionHelper.getConnection();
				            //Trae los teléfonos del afiliado para obtener el id del primero
				            List<Telefono> telefonos = TelefonoServiceUtil.getTelefonos(cuilTitular, inte);
				            int idTelefono = !telefonos.isEmpty() ? telefonos.get(0).getId() : 0;

				            //actualiza modi_fecha de afi_domicilio y afi_teléfono
				            String sql = "{call actualiza_afi_domicilio_verificado(?,?,?)}";
				            stmt2 = con.prepareCall(sql);
				            stmt2.setInt(1, idDomicilio);
				            stmt2.setInt(2, idTelefono);
				            stmt2.setString(3, user.getScreenName());
				            
				            _log.info("actualiza_afi_domicilio_verificado con id_domicilio=" + idDomicilio + " id_telefono=" + idTelefono);

				            stmt2.execute();

				        } catch (Exception e) {
				            _log.error("Error al actualizar modi_fecha en domicilio/telefono", e);
				            throw new SystemException(e);
				        } finally {
				            ConnectionHelper.cerrar(stmt2, con);
				        }
				    }
				}
			
			}catch (Exception e) {
				setForward(renderRequest, "portlet.afiliados.error");
			}	
			
		}else{
			setForward(renderRequest, "portlet.afiliados.error");
		}

		return mapping.findForward(getForward(renderRequest, "portlet.crm.actualiza.domicilio.popup"));
	}

}
