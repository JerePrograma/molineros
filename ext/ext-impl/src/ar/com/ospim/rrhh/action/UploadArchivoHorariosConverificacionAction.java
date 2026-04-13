package ar.com.ospim.rrhh.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.procesaArchivos.exception.ErrorProcesandoArchivosHorariosException;
import ar.com.ospim.rrhh.beans.RegistroAcceso;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class UploadArchivoHorariosConverificacionAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(UploadArchivoHorariosConverificacionAction.class);

	private static ProcesaArchivoHorarios serviceUtil = new ProcesaArchivoHorarios();
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
		
		List<String> erroresImportaHorarios = null;
		int edificioOrigen = 0;
		User user = PortalUtil.getUser(actionRequest);
		
		try {
			
			edificioOrigen = ParamUtil.getInteger(actionRequest, "origen");
			
			String fileNameImportaHorarios = uploadReq
					.getFileName("importa_horarios");
			
			if (fileNameImportaHorarios != null) {

				if (fileNameImportaHorarios != null
						&& !fileNameImportaHorarios.trim().equals("")) {

					File file = uploadReq.getFile("importa_horarios");
					FileInputStream fileInput = new FileInputStream(file);
					erroresImportaHorarios = procesarFile(actionRequest,
							fileInput, "de Horarios", edificioOrigen, user);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
		if ((null != erroresImportaHorarios && !erroresImportaHorarios.isEmpty())) {
			
			if (null != erroresImportaHorarios
					&& !erroresImportaHorarios.isEmpty()) {
				ErrorProcesandoArchivosHorariosException epame = new ErrorProcesandoArchivosHorariosException();
				SessionErrors.add(actionRequest, epame.getClass().getName());
			} else {
				ErrorProcesandoArchivosHorariosException e = new ErrorProcesandoArchivosHorariosException();
				SessionErrors.add(actionRequest, e.getClass().getName());
			}
		}
		
		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		}
		actionRequest.setAttribute("origenEdificioRRHH", edificioOrigen);
		
		setForward(actionRequest, "portlet.rrhh.importar_horarios");
	}

	private List<String> procesarFile(ActionRequest actionRequest,
			FileInputStream file, String tipoArchivo, int origenEdificio, User user) throws IOException {
		
		List<String> errores = new ArrayList<String>();
		
		try {
			Scanner scanner = new Scanner(file, "UTF-8");
//			1 grabamos todo como "E" entrada.
			serviceUtil.procesarArchivoImportaHorariosConVerificacion(scanner, origenEdificio, user);
			
//			2 corremos un proceso que se encarga de analizar si existe una salida correspondiente a ese dia o al siguiente 
//			y ajuste el registro anterior
			serviceUtil.verificaLosDiques();
			
		} catch (Exception e) {
			logger.debug("Error al procesar archivo " + tipoArchivo, e);
			errores.add(tipoArchivo);
		}		

		return errores;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		List<RegistroAcceso> fichadas = null;
		
		if(StringUtils.checkNotEmpty(cmd)){
			if(cmd.equalsIgnoreCase(Constants.SEARCH)){
//				fichadas = serviceUtil.buscarUltimasLecturasAccesoLosDiques();
			
//				renderRequest.setAttribute("FICHADAS_LOS_DIQUES", fichadas);
			}else if (cmd.equalsIgnoreCase(Constants.UPDATE)){
				corregirFichadas(renderRequest);
//				fichadas = serviceUtil.buscarUltimasLecturasAccesoLosDiques();
//				renderRequest.setAttribute("FICHADAS_LOS_DIQUES", fichadas);
			}
			fichadas = serviceUtil.buscarUltimasLecturasAccesoLosDiques();
			renderRequest.setAttribute("FICHADAS_LOS_DIQUES", fichadas);
			
 			return mapping.findForward(getForward(renderRequest, "portlet.rrhh.verificar_horarios_los_diques"));
		}
		return mapping.findForward(getForward(renderRequest, "portlet.rrhh.importar_horarios"));
	}
	
	private void corregirFichadas(RenderRequest renderRequest) throws PortalException, SystemException{
		
		User user = PortalUtil.getUser(renderRequest);
		
		Integer cantidadFichadas = ParamUtil.getInteger(renderRequest, "cantidadFichadas"); 
		int id = 0;
		String tipoRegistro="";
		Boolean modificado = false;
		
		for (int i = 0; i < cantidadFichadas; i++) {
			modificado = ParamUtil.getBoolean(renderRequest, "modificado_"+i,false);
			
			if(modificado){
				
				id = ParamUtil.getInteger(renderRequest, "id_"+i);
				tipoRegistro = ParamUtil.getString(renderRequest, "tipo_registro_"+i); 
				
				logger.info("entro al corregirFichadas id:" + id + " tipo registro: "+ tipoRegistro + " usuario: "+ user.getScreenName());
				
				serviceUtil.corrigeFichadaLosDiques(id, tipoRegistro, user);
			}
		}
		
	}

}