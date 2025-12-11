/**
 */

package ar.com.ospim.afiliados.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.DetalleDiscapacidad;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="ActionUtil.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class ActionUtil {

	public static void getAfiliadoEntry(HttpServletRequest request)
			throws Exception {

		String cuil_titular = ParamUtil.getString(request, "cuil_titular");
		int inte = ParamUtil.getInteger(request, "inte");

		Afiliado afiliadoEntry = null;

		if (cuil_titular != null && cuil_titular.length() > 0) {
			afiliadoEntry = EditarAfiliadoServiceUtil.getAfiliadoEntry(
					cuil_titular, inte);
		}
		request.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION,
				afiliadoEntry);
	}

	// TODO FIXME
	public static Afiliado getAfiliadoActivoXCuil(String cuil) {
		Afiliado afiliado = null;
		if (cuil != null && cuil.length() > 0) {
			try {
				afiliado = EditarAfiliadoServiceUtil.getAfiliadoXCuil(cuil);
			} catch (Exception e) {
				afiliado = null;
			}
		}
		return afiliado;
	}
	
	public static Afiliado getAfiliadoActivoXCuilInte(String cuil) {
		Afiliado afiliado = null;
		if (cuil != null && cuil.length() > 0) {
			try {
				afiliado = EditarAfiliadoServiceUtil.getAfiliadoXCuilInte(cuil);
			} catch (Exception e) {
				afiliado = null;
			}
		}
		return afiliado;
	}

	public static Afiliado getAfiliadoInclusoDadoBajaByCuilInte(
			String cuil_titular, int inte) {
		Afiliado afiliadoEntry = null;
		if (StringUtils.checkNotEmpty(cuil_titular)) {
			try {
				
				afiliadoEntry = EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuil_titular, inte);
				// Añade datos de la opcion superintendencia
				DetalleOpcionesSS detOpcSS = EditarAfiliadoServiceUtil.buscarOpcionSssPorCuil(afiliadoEntry!=null?afiliadoEntry.getCuil():"");
				afiliadoEntry.setDetalleOpcionSs(detOpcSS);
		        
			} catch (Exception e) {
				afiliadoEntry = null;
			}
		}
		return afiliadoEntry;
	}

	public static Afiliado getAfiliadoDadoBajaByCuilInte(String cuil_titular,
			int inte) throws Exception {
		Afiliado afiliadoEntry = null;
		if (cuil_titular != null && cuil_titular.length() > 0) {
			afiliadoEntry = EditarAfiliadoServiceUtil.getAfiliadoDadoBaja(
					cuil_titular, inte);
		}
		return afiliadoEntry;
	}

	public static void getAfiliadoEntry(ActionRequest actionRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		getAfiliadoEntry(request);
	}

	public static void getAfiliadoEntry(RenderRequest renderRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(renderRequest);

		getAfiliadoEntry(request);
	}

	public static void getAfiliadoTitularEntry(HttpServletRequest request)
			throws Exception {

		String cuil_titular = ParamUtil.getString(request, "cuil_titular");

		Afiliado afiliadoEntry = null;

		if (cuil_titular != null && cuil_titular.length() > 0) {
			afiliadoEntry = EditarAfiliadoServiceUtil
					.getAfiliadoTitularEntry(cuil_titular);
		}
		request.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION,
				afiliadoEntry);
	}

	public static void getAfiliadoTitularEntry(ActionRequest actionRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		getAfiliadoTitularEntry(request);
	}

	public static void getAfiliadoTitularEntry(RenderRequest renderRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(renderRequest);

		getAfiliadoTitularEntry(request);
	}

	public static void getAfiliadoEntryInclusoDadoBaja(
			ActionRequest actionRequest) throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		getAfiliadoEntryInclusoDadoBaja(request);
	}

	public static void getAfiliadoEntryInclusoDadoBaja(
			RenderRequest renderRequest) throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(renderRequest);

		getAfiliadoEntryInclusoDadoBaja(request);
	}

	public static void getAfiliadoEntryInclusoDadoBaja(
			HttpServletRequest request) throws Exception {

		String cuil_titular = ParamUtil.getString(request, "cuil_titular");
		int inte = ParamUtil.getInteger(request, "inte");

		Afiliado afiliadoEntry = null;

		if (cuil_titular != null && cuil_titular.length() > 0) {
			afiliadoEntry = EditarAfiliadoServiceUtil
					.getAfiliadoEntryInclusoDadoBaja(cuil_titular, inte);
		}
		request.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION,
				afiliadoEntry);
	}

	public static void getAfiliadoExistente(ActionRequest actionRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		getAfiliadoExistente(request);
	}

	public static void getAfiliadoExistente(RenderRequest renderRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(renderRequest);

		getAfiliadoExistente(request);
	}

	public static void getAfiliadoExistente(HttpServletRequest request)
			throws Exception {

		String nroDoc = ParamUtil.getString(request, "nroDoc");
		String tipo_documento = ParamUtil.getString(request, "documento_tipo");
		
		String diaVig = ParamUtil.getString(request, "diaVig");
		String mesVig = ParamUtil.getString(request, "mesVig");
		String anioVig = ParamUtil.getString(request, "anioVig");
		SimpleDateFormat formatoDeFechaV = new SimpleDateFormat("dd/MM/yyyy");		
		
		Date vigenteFecha = null;
		try {
			vigenteFecha = formatoDeFechaV.parse(diaVig + "/"
					+ (Integer.parseInt(mesVig) + 1) + "/"
					+ anioVig);
		} catch (Exception e) {
			vigenteFecha = null;
		}

		Afiliado afiliadoEntry = null;

		if (nroDoc != null && nroDoc.length() > 0) {
			afiliadoEntry = EditarAfiliadoServiceUtil.getAfiliadoExistente(
					nroDoc, tipo_documento, vigenteFecha);
		}
		request
				.setAttribute(WebKeysAfiliados.AFILIADO_EXISTENTE,
						afiliadoEntry);
	}

	public static void setAfiliadoExistenteSession(RenderRequest renderRequest)
			throws Exception {
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(
				renderRequest).getSession();
		String nroDoc = ParamUtil.getString(renderRequest, "nroDoc");
		String tipo_documento = ParamUtil.getString(renderRequest,
				"documento_tipo");
		
		String diaVig = ParamUtil.getString(renderRequest, "diaVig");
		String mesVig = ParamUtil.getString(renderRequest, "mesVig");
		String anioVig = ParamUtil.getString(renderRequest, "anioVig");
		SimpleDateFormat formatoDeFechaV = new SimpleDateFormat("dd/MM/yyyy");		
		
		Date vigenteFecha = null;
		try {
			vigenteFecha = formatoDeFechaV.parse(diaVig + "/"
					+ (Integer.parseInt(mesVig) + 1) + "/"
					+ anioVig);
		} catch (Exception e) {
			vigenteFecha = null;
		}
		
		Afiliado afiliadoEntry = null;
		if (nroDoc != null && nroDoc.length() > 0) {
			afiliadoEntry = EditarAfiliadoServiceUtil.getAfiliadoExistente(
					nroDoc, tipo_documento, vigenteFecha);
		}
		session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION,
				afiliadoEntry);
	}

	public static void getAfiliadoDadoBaja(HttpServletRequest request)
			throws Exception {

		String cuil_titular = ParamUtil.getString(request, "cuil_titular");
		int inte = ParamUtil.getInteger(request, "inte");

		Afiliado afiliadoEntry = null;

		if (cuil_titular != null && cuil_titular.length() > 0 && inte >= 0) {
			afiliadoEntry = EditarAfiliadoServiceUtil.getAfiliadoDadoBaja(
					cuil_titular, inte);
		}
		request.setAttribute(WebKeysAfiliados.AFILIADO_BAJA, afiliadoEntry);
	}

	public static DetalleDiscapacidad getDetalleDiscapacidad(String cuil_titular, int inte)
			throws Exception {
		DetalleDiscapacidad detalleDiscapacidad = null;

		if (cuil_titular != null && cuil_titular.length() > 0 && inte >= 0) {
			detalleDiscapacidad = EditarAfiliadoServiceUtil.getDetalleDiscapacidad(cuil_titular, inte);
		}
		return detalleDiscapacidad;
	}

	public static void getAfiliadoDadoBaja(ActionRequest actionRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		getAfiliadoDadoBaja(request);
	}

	public static void getAfiliadoDadoBaja(RenderRequest renderRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(renderRequest);

		getAfiliadoDadoBaja(request);
	}
}