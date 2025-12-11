/**
 */

package ar.com.ospim.afiliados.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.AfliadoYaTieneConyugeException;
import ar.com.ospim.afiliados.ConyugeNoPuedeSerSolteroException;
import ar.com.ospim.afiliados.DuplicateAfiliadoIdException;
import ar.com.ospim.afiliados.HijoNoPuedeSerCasadoException;
import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.TitularNoPuedeSerSolteroException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.MotivoBaja;
import ar.com.ospim.afiliados.reportes.ReporteHistoricoMovimientosAfiliadoExcel;
import ar.com.ospim.afiliados.services.CredencialesServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.ProcesosCorreoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="CargarIntegranteEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * @modif SVA
 */
public class CargarIntegranteEntryAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		ActionUtil.getAfiliadoTitularEntry(actionRequest);
		
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
		
		Afiliado afiliadoInSession = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
		
		String preCarga = (String) session.getAttribute("pre_carga");
		String idPreAfiliado =  (String) session.getAttribute("id_pre_afiliado");
		
		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				updateAfiliadoEntry(actionRequest, preCarga, idPreAfiliado, cmd);
				
				actionRequest.setAttribute("Exito",
						String.valueOf(afiliadoInSession.getId_ospim()) + "|"
								+ String.valueOf(afiliadoInSession.getId_uoma()) + "|"
								+ String.valueOf(afiliadoInSession.getId_amtima()));
				
				session.removeAttribute("pre_carga");
				session.removeAttribute("id_pre_afiliado");
				session.removeAttribute(WebKeysAfiliados.PREAFILIADO_EN_SESSION);
				
			} else if (cmd.equals(Constants.DELETE)) {
				borraAfiliadoEntry(actionRequest);
				setForward(actionRequest, "portlet.afiliados.view");
			}
		} catch (Exception e) {
			if (e instanceof NoSuchAfiliadoEntryException
					|| e instanceof DuplicateAfiliadoIdException
					|| e instanceof AfliadoYaTieneConyugeException
					|| e instanceof HijoNoPuedeSerCasadoException
					|| e instanceof ConyugeNoPuedeSerSolteroException
					|| e instanceof TitularNoPuedeSerSolteroException) {
				if (e instanceof NoSuchAfiliadoEntryException
						|| e instanceof DuplicateAfiliadoIdException
						|| e instanceof AfliadoYaTieneConyugeException) {
					SessionErrors.add(actionRequest, e.getClass().getName());
				}
				if (e instanceof HijoNoPuedeSerCasadoException) {
					SessionErrors.add(actionRequest, e.getClass().getName());
				}
				if (e instanceof ConyugeNoPuedeSerSolteroException) {
					SessionErrors.add(actionRequest, e.getClass().getName());
				}
				if (e instanceof TitularNoPuedeSerSolteroException) {
					SessionErrors.add(actionRequest, e.getClass().getName());
				}
			} else {
				throw e;
			}
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(
				renderRequest).getSession();
		
		String preCarga = null;
		String idPreAfiliado=null;
		Afiliado afiliado = null,  preAfiliado = null;
		
		this.cargarListas(renderRequest);
		
		try {
			String cuil_titular = ParamUtil.getString(renderRequest,"cuil_titular");
			int inte = ParamUtil.getInteger(renderRequest, "inte");
			
			preCarga = ParamUtil.getString(renderRequest, "pre_carga");
			idPreAfiliado = ParamUtil.getString(renderRequest, "id_pre_afiliado");
			
			afiliado = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(cuil_titular, inte);
			if(afiliado==null){ // busco al titular que me da los id de socio y cuil_titular y domicilio
				afiliado = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(cuil_titular, 0);
			}
			session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION,afiliado);
		
			if(!ar.com.ospim.util.StringUtils.checkEmpty(preCarga) && preCarga.equalsIgnoreCase("true")){
				preAfiliado = EditarAfiliadoServiceUtil.getAfiliadoPreCarga(cuil_titular, inte, Integer.parseInt(idPreAfiliado));
				if(preAfiliado.getTipoOperacion().equalsIgnoreCase(Constants.ADD) ){ //  solo vamos a entrar x el ALta en CargaIntegranteEntryAction
//					cmd = "add";
					session.setAttribute(WebKeysAfiliados.PREAFILIADO_EN_SESSION, preAfiliado);
					
					session.setAttribute("pre_carga", preCarga);
					session.setAttribute("id_pre_afiliado", idPreAfiliado);
				}	
			}		

		} catch (Exception e) {
			if (e instanceof NoSuchAfiliadoEntryException
					|| e instanceof PrincipalException) {
				SessionErrors.add(renderRequest, e.getClass().getName());
				return mapping.findForward("portlet.afiliados.error");
			} else {
				throw e;
			}
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.afiliados.cargar_integrante_entry"));
	}

	@SuppressWarnings("unchecked")
	protected void borraAfiliadoEntry(ActionRequest actionRequest)
			throws Exception {

		String cuil_titular = ParamUtil.getString(actionRequest, "cuil_titular");
		int inte = ParamUtil.getInteger(actionRequest, "inte");
		int motivo_baja = ParamUtil.getInteger(actionRequest, "motivo_baja", -1);
		if (-1 == motivo_baja) {
			throw new PrincipalException("No existe motivo de baja");
		}
		PortletSession portletSession = actionRequest.getPortletSession();

		List<MotivoBaja> motivosBaja = (ArrayList<MotivoBaja>) portletSession
				.getAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		Date baja_fecha = getMesesABaja(motivosBaja, motivo_baja);

		User user = PortalUtil.getUser(actionRequest);

		EditarAfiliadoServiceUtil.borraAfiliadoEntry(cuil_titular, inte,
				motivo_baja, baja_fecha, user.getScreenName());
	}

	private Date getMesesABaja(List<MotivoBaja> motivosBaja, int motivo_baja) {
		int meses = 0;
		for (MotivoBaja mot : motivosBaja) {
			if (mot.getId_motivo_baja() == motivo_baja) {
				meses = mot.getMeses_a_baja();
			}
		}
		Date baja_fecha = DateUtils.anyadeMeses(new Date(), meses);
		return baja_fecha;
	}

	protected void updateAfiliadoEntry(ActionRequest actionRequest,String preCarga, String idPreAfi,
			String command) throws Exception {
		Afiliado afiliado = getAfiliadoIntegranteFromRequest(actionRequest);
		
		User user = PortalUtil.getUser(actionRequest);
		if (command.equals(Constants.ADD)) {
			// Add afiliado entry
			EditarAfiliadoServiceUtil.cargaAfiliadoIntegranteEntry(afiliado, null, preCarga, idPreAfi, user.getScreenName(), null, 0, null);
			
			if (DateUtils.getEdad(afiliado.getNaci_fecha()) == 0  &&
					CredencialesServiceUtil.validarExisteExentoCopago(afiliado.getCuil_titular(), afiliado.getInte()) == 1){
					// es menor a un año
				  CredencialesServiceUtil.insertarCredencial(afiliado.getCuil_titular(), afiliado.getInte(), user.getScreenName());
			}

					
		} else {
			// Update product entry
			EditarAfiliadoServiceUtil.actualizaAfiliadoEntry(afiliado, preCarga, idPreAfi, null,user.getScreenName(), null, null);
		}
		
//		SVA 08/10/2019
//		if(afiliado.getDiscapacitado().equals("1")){
//			this.enviarNovedadsobreAfiliadoDiscapacidad(afiliado.getCuil_titular());
//		}
	}
	
	private Afiliado getAfiliadoIntegranteFromRequest(ActionRequest actionRequest){
		
		Afiliado afi = new Afiliado();
		
		String cuil_titular = ParamUtil.getString(actionRequest, "cuil_titular");
		int inte = ParamUtil.getInteger(actionRequest, "inte");

		String nombre = ParamUtil.getString(actionRequest, "nombre");
		String apellido = ParamUtil.getString(actionRequest, "apellido");
		String sexo = ParamUtil.getString(actionRequest, "sexo");
		String documento_tipo = ParamUtil.getString(actionRequest,"documento_tipo");
		String nroDoc = ParamUtil.getString(actionRequest, "nroDoc");
		String cuil = ParamUtil.getString(actionRequest, "cuil");
		String vigenteFechaMes = ParamUtil.getString(actionRequest,"vigenteFechaMes");
		String vigenteFechaDia = ParamUtil.getString(actionRequest,"vigenteFechaDia");
		String vigenteFechaAnio = ParamUtil.getString(actionRequest,"vigenteFechaAnio");
		SimpleDateFormat formatoDeFechaV = new SimpleDateFormat("dd/MM/yyyy");

		Date vigenteFecha = null;
		try {
			vigenteFecha = formatoDeFechaV.parse(vigenteFechaDia + "/"
					+ (Integer.parseInt(vigenteFechaMes) + 1) + "/"
					+ vigenteFechaAnio);
		} catch (Exception e) {
			vigenteFecha = null;
		}
		String fechaNacimientoMes = ParamUtil.getString(actionRequest,"fechaNacimientoMes");
		String fechaNacimientoDia = ParamUtil.getString(actionRequest,"fechaNacimientoDia");
		String fechaNacimientoAnio = ParamUtil.getString(actionRequest,"fechaNacimientoAnio");
		Date fechaNacimiento = null;
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		try {
			fechaNacimiento = formatoDeFecha.parse(fechaNacimientoDia + "/"
					+ (Integer.parseInt(fechaNacimientoMes) + 1) + "/"
					+ fechaNacimientoAnio);
		} catch (Exception e) {
			fechaNacimiento = null;
		}
//		String estado_civil = ParamUtil.getString(actionRequest, "estado_civil");
//		String parentesco = ParamUtil.getString(actionRequest, "parentesco");
		int estado_civil = ParamUtil.getInteger(actionRequest, "estado_civil");
		int parentesco = ParamUtil.getInteger(actionRequest, "parentesco");
		int nacionalidad = ParamUtil.getInteger(actionRequest, "nacionalidad");
		String discapacitado = ParamUtil.getString(actionRequest,"discapacitado");
		int seccional = ParamUtil.getInteger(actionRequest, "id_seccional");
		int obra_social_ant = ParamUtil.getInteger(actionRequest,"obra_social_ant");
		String obs = ParamUtil.getString(actionRequest, "obs");
//		int provincia = ParamUtil.getInteger(actionRequest, "provincia");
//		int localidad = ParamUtil.getInteger(actionRequest, "localidad");
//		String cod_postal = ParamUtil.getString(actionRequest, "cod_postal");
//		String calle = ParamUtil.getString(actionRequest, "calle");
//		String numero = ParamUtil.getString(actionRequest, "numero");
//		String piso = ParamUtil.getString(actionRequest, "piso");
//		String dpto = ParamUtil.getString(actionRequest, "dpto");
//		String barrio = ParamUtil.getString(actionRequest, "barrio");
//		String telefono = ParamUtil.getString(actionRequest, "telefono");
		int id_uoma = ParamUtil.getInteger(actionRequest, "id_uoma");
		int id_amtima = ParamUtil.getInteger(actionRequest, "id_amtima");
		int id_ospim = ParamUtil.getInteger(actionRequest, "id_ospim");
//		String censo2013 = ParamUtil.getString(actionRequest, "censo2013");
		int id_correspondencia = ParamUtil.getInteger(actionRequest, "numero_correspondencia");
		String tieneAntecJudiciales = ParamUtil.getString(actionRequest, "tiene_antecedentes_judiciales","0");
		String proyecto = ParamUtil.getString(actionRequest, "proyecto",null);
		if(StringUtils.checkEmpty(proyecto)){
			proyecto = null;
		}

		
//		Domicilio domi = new Domicilio();
////		domi.setCelular(celular);
//		domi.setCalle(calle);
//		domi.setNumero(numero);
//		domi.setPiso(piso);
//		domi.setDepto(dpto);
//		domi.setBarrio(barrio);
//		domi.setLocalidadId(localidad);
//		domi.setProvinciaId(provincia);
////		domi.setCod_area_telefono(cod_area_telefono);
////		domi.setTelefono(telefono);
		
		afi.setCuil_titular(cuil_titular);
		afi.setInte(inte);
		afi.setNombre(nombre);
		afi.setApellido(apellido);
		afi.setSexo(sexo);
		afi.setDocumento_tipo(documento_tipo);
		afi.setDocu_numero(nroDoc);
		afi.setCuil(cuil);
		afi.setVigen_fecha(vigenteFecha);
		afi.setNaci_fecha(fechaNacimiento);
		afi.setId_civil_esta(estado_civil);
		afi.setNacionalidad(nacionalidad);
		afi.setDiscapacitado(discapacitado);
		afi.setId_parentesco(parentesco);
		afi.setSeccional(new Seccional(seccional));
		afi.setAnterior_os(obra_social_ant);
		afi.setId_amtima(id_amtima);
		afi.setId_ospim(id_ospim);
		afi.setId_uoma(id_uoma);
		afi.setObservaciones(obs);
//		afi.setCenso2013(Integer.parseInt(censo2013));
		afi.setIdCorrespondencia(id_correspondencia);
		afi.setTieneAntecedentesJudiciales(Integer.valueOf(tieneAntecJudiciales));
		afi.setProyecto(proyecto);
		
		return afi;
	}
	
	private void enviarNovedadsobreAfiliadoDiscapacidad(String cuilTitular){
		
		List<String> destinatarios = ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.CAMBIOS_DISCAPACIDAD);

		HSSFWorkbook wb = ReporteHistoricoMovimientosAfiliadoExcel.generaReporteHistoricoMovimientosAfiliado(cuilTitular, new Date(), new Date());
		
		EnviaEmailsThread.enviarMailDesatendido("Aviso cambios en afiliado", "Grupo fliar: " + cuilTitular, destinatarios, wb, "CambiosGrupoFamiliarIntegrante_"+cuilTitular+".xls");
		
	}
	
	private void cargarListas(RenderRequest renderRequest) throws Exception{
		
		TraeListasServiceUtil.getMotivosBaja(renderRequest);
		
	}
}