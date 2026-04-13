package ar.com.ospim.afiliados.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.SituacionLaboralException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.MotivoBaja;
import ar.com.ospim.afiliados.beans.SituacionLaboral;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="GrabarSituacionLaboralAction.java.html"><b><i>View
 * Source</i></b></a>
 * <p>
 * Graba las situaciones laborales
 * 
 * @author Federico Brachi
 * 
 */
public class GrabarSituacionLaboralAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(GrabarSituacionLaboralAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.empleador.view");
	}

	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		_log.debug("Agregando situ laboral a sesion");

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(
				renderRequest).getSession();
		session.removeAttribute("baja_cascada");
		session.removeAttribute("fecha_egreso");
		Afiliado afiliado = null;
		String cuil_titular = null;
		int inte = 0;
		if (session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION) == null) {
			cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
			inte = ParamUtil.getInteger(renderRequest, "inte");
			afiliado = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(
					cuil_titular, inte);
		} else {
			afiliado = (Afiliado) session
					.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
			cuil_titular = afiliado.getCuil_titular();
			inte = afiliado.getInte();
		}
		if (afiliado == null) {
			afiliado = EditarAfiliadoServiceUtil
					.getAfiliadoEntryInclusoDadoBaja(cuil_titular, inte);
			if (afiliado == null) {
				afiliado = new Afiliado();
				afiliado.setCuil_titular(ParamUtil.getString(renderRequest,
						"cuil_titular"));
				afiliado.setInte(ParamUtil.getInteger(renderRequest, "inte"));
			}
		}
		Date fecha_ingreso = null;
		Date fecha_egreso = null;
		Date vieja_fecha_ingreso = null;
		int situ_revista = ParamUtil.getInteger(renderRequest, "situ_revista");
		String revista = ParamUtil.getString(renderRequest,
				"nombre_situ_revista");
		int categoria = ParamUtil.getInteger(renderRequest, "categoria");
		String categoriaString = ParamUtil.getString(renderRequest,
				"nombre_categoria");
		String escala_salarial = ParamUtil.getString(renderRequest,
				"escala_salarial");
		String nombre_empresa = ParamUtil.getString(renderRequest,
				"nombre_empresa");
		String cuit_empleador = renderRequest.getParameter("cuit_empleador");

		boolean baja_cascada = ParamUtil.getBoolean(renderRequest,
				"baja_cascada");
		renderRequest.setAttribute("baja_cascada", baja_cascada);
		session.setAttribute("baja_cascada", baja_cascada);

		int id = ParamUtil.getInteger(renderRequest, "idSituLaboral");

		String sucur = null;
		sucur = ParamUtil.getString(renderRequest, "sucursal");
		if (sucur == null || sucur.trim().equals("")) {
			sucur = "000";
		}
		Empresa empresa = new Empresa(cuit_empleador);
		empresa.setRazon_soc(nombre_empresa);
		empresa.setSucursal(sucur);

		int motivo_baja = ParamUtil.getInteger(renderRequest, "motivo_baja");
		String descrp_motivo_baja = ParamUtil.getString(renderRequest,
				"descrp_motivo_baja");
		if (motivo_baja == 0) {
			descrp_motivo_baja = "";
		}
		MotivoBaja motivoBaja = new MotivoBaja(motivo_baja, descrp_motivo_baja);

		String fecha_ingreso_string = renderRequest
				.getParameter("fechaIngreso");
		String fecha_egreso_string = renderRequest.getParameter("fechaEgreso");
		String vieja_Fecha_Ingreso_string = renderRequest
				.getParameter("fecha_ingreso_vieja");

		List<SituacionLaboral> laboralList = (ArrayList<SituacionLaboral>) session
				.getAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL);

		if (laboralList == null) {
			laboralList = afiliado.getLista_situ_laboral();
		}
		if (laboralList == null) {
			laboralList = new ArrayList<SituacionLaboral>();
		}

		if (null != fecha_ingreso_string) {
			fecha_ingreso = DateUtils.parse(fecha_ingreso_string, "dd/MM/yyyy");
		}

		if (null != vieja_Fecha_Ingreso_string) {
			vieja_fecha_ingreso = DateUtils.parse(vieja_Fecha_Ingreso_string,
					"dd/MM/yyyy");
		}

		if (null != fecha_egreso_string) {
			try {
				fecha_egreso = DateUtils.parse(fecha_egreso_string,
						"dd/MM/yyyy");
			} catch (ParseException e) {
				fecha_egreso = null;
			}
		}
		session.setAttribute("fecha_egreso", fecha_egreso);

		SituacionLaboral situLaboral = new SituacionLaboral(afiliado, empresa,
				fecha_ingreso, fecha_egreso, revista, categoriaString,
				categoria, situ_revista, motivoBaja, escala_salarial);
		situLaboral.setEstado(Constants.ADD);
		Random generator = new Random();
		int r = generator.nextInt() + 100;
		situLaboral.setId(r);

		this.setearSituLaboral(renderRequest, session, cuil_titular, inte,
				afiliado, fecha_ingreso, fecha_egreso, vieja_fecha_ingreso,
				cuit_empleador, baja_cascada, id, sucur, laboralList,
				situLaboral);

		return mapping.findForward("portlet.situlaboral.result.search");
	}

	private void setearSituLaboral(RenderRequest renderRequest,
			HttpSession session, String cuil_titular, int inte,
			Afiliado afiliado, Date fecha_ingreso, Date fecha_egreso,
			Date vieja_fecha_ingreso, String cuit_empleador,
			boolean baja_cascada, int id, String sucur,
			List<SituacionLaboral> laboralList, SituacionLaboral situLaboral)
			throws Exception {
		try {
			if (afiliado != null) {
				if (null != renderRequest.getParameter("borrar")
						&& renderRequest.getParameter("borrar").trim()
								.equals("true")) {
					for (int i = 0; i < laboralList.size(); i++) {
						SituacionLaboral situLaboralViejo = laboralList.get(i);
						boolean isCuil = laboralList.get(i).getAfiliado()
								.getCuil_titular().equals(cuil_titular);
						int intero = laboralList.get(i).getAfiliado().getInte();
						boolean isCuit = laboralList.get(i).getEmpresa()
								.getCuit().equals(cuit_empleador);
						boolean isSucur = laboralList.get(i).getEmpresa()
								.getSucursal().equals(sucur);
						boolean fechaIngreso = laboralList.get(i)
								.getFecha_ingre().equals(fecha_ingreso);
						if (isCuil && intero == inte && isCuit && isSucur
								&& fechaIngreso) {
							if (laboralList.get(i).getEstado() == null) {
								situLaboral.setEstado(Constants.UPDATE);
								Date fecha = new Date();
								situLaboral.setFecha_baja_logica(fecha);
								situLaboral
										.setViejaFechaIngreso(vieja_fecha_ingreso);
								laboralList.set(i, situLaboral);
							} else if (laboralList.get(i).getEstado() != null
									&& situLaboralViejo.getEstado() != null
									&& situLaboralViejo.getEstado().equals(
											Constants.ADD)) {
								laboralList.remove(i);
							} else if ((laboralList.get(i).getEstado() != null
									&& situLaboralViejo.getEstado() != null && situLaboralViejo
									.getEstado().equals(Constants.UPDATE))) {
								situLaboral.setEstado(Constants.UPDATE);
								situLaboral
										.setViejaFechaIngreso(vieja_fecha_ingreso);
								laboralList.set(i, situLaboral);
							}
						}
					}

				} else if (null != renderRequest.getParameter("editar")
						&& renderRequest.getParameter("editar").trim()
								.equals("true")) {
					boolean flag = this.isDateIngreValida(cuit_empleador,
							fecha_ingreso, fecha_egreso, laboralList, id, inte,sucur );
					if (!flag) {
						for (int i = 0; i < laboralList.size(); i++) {
							boolean isCuil = laboralList.get(i).getAfiliado()
									.getCuil_titular().equals(cuil_titular);
							int intero = laboralList.get(i).getAfiliado()
									.getInte();
							boolean isCuit = laboralList.get(i).getEmpresa()
									.getCuit().equals(cuit_empleador);
							boolean isSucur = laboralList.get(i).getEmpresa()
									.getSucursal().equals(sucur);
							boolean isViejaFechaIngreso = laboralList.get(i)
									.getFecha_ingre()
									.equals(vieja_fecha_ingreso);
							if (isCuil && intero == inte && isCuit && isSucur
									&& isViejaFechaIngreso) {
								if (laboralList.get(i).getEstado() == null) {
									situLaboral.setBaja_cascada(baja_cascada);
									situLaboral.setEstado(Constants.UPDATE);
									situLaboral
											.setViejaFechaIngreso(vieja_fecha_ingreso);
									laboralList.set(i, situLaboral);
								} else if (laboralList.get(i).getEstado() == Constants.ADD) {
									situLaboral.setBaja_cascada(baja_cascada);
									situLaboral.setEstado(Constants.ADD);
									laboralList.set(i, situLaboral);
								} else if (laboralList.get(i).getEstado() == Constants.UPDATE) {
									situLaboral.setBaja_cascada(baja_cascada);
									situLaboral.setEstado(Constants.UPDATE);
									situLaboral
											.setViejaFechaIngreso(vieja_fecha_ingreso);
									laboralList.set(i, situLaboral);
								}
							}
						}
					} else {
						throw new SituacionLaboralException();
					}
				} else {
					boolean flag = this.isDateIngreValida(cuit_empleador,
							fecha_ingreso, fecha_egreso, laboralList, id, inte , sucur);
					if ((laboralList != null)
							&& (afiliado.getLista_situ_laboral() != null)) {
						laboralList = afiliado.getLista_situ_laboral();
					}
					if (!flag) {
						situLaboral.setBaja_cascada(baja_cascada);
						laboralList.add(situLaboral);
					} else {
						throw new SituacionLaboralException();
					}
				}
			}
			afiliado.setLista_situ_laboral(laboralList);
			session.setAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL,
					laboralList);
			session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION, afiliado);
		} catch (Exception e) {
			if (e instanceof SituacionLaboralException
					|| e instanceof PrincipalException) {

				SessionErrors.add(renderRequest,
						SituacionLaboralException.class.getName());
			} else {
				throw e;
			}
		}
	}

	private boolean isDateIngreValida(String cuit_empleador,
			Date fecha_ingreso_nueva, Date fecha_egreso_nueva,
			List<SituacionLaboral> laboralList, int id, int inte , String sucursal_empleador) {
		boolean flag = false;
		int i = 0;
		while (!flag && i < laboralList.size()) {
			int idSitus = laboralList.get(i).getId();
			String cuit = null;
			String sucu = null;
			int inteDeLaSitu = laboralList.get(i).getAfiliado().getInte();
			if (laboralList.get(i).getEmpresa().getCuit() != null) {
				cuit = laboralList.get(i).getEmpresa().getCuit();
				sucu = laboralList.get(i).getEmpresa().getSucursal();

			}
			if (cuit.equals(cuit_empleador) && sucu.equals(sucursal_empleador)  
					&& id != idSitus && inte == inteDeLaSitu) {
				Date fecha_ingreso_vieja = laboralList.get(i).getFecha_ingre();
				Date fecha_egreso_vieja = laboralList.get(i).getFecha_baja();

				int compIngreNuevaIngreVieja = 0;
				int compEgreNuevaIngreVieja = 0;
				int compIngreNuevaEgreVieja = 0;
				int compEgreNuevaEgreVieja = 0;

				compIngreNuevaIngreVieja = fecha_ingreso_nueva
						.compareTo(fecha_ingreso_vieja);
				if (fecha_egreso_nueva != null) {
					compEgreNuevaIngreVieja = fecha_egreso_nueva
							.compareTo(fecha_ingreso_vieja);
				}
				if (fecha_egreso_vieja != null) {
					compIngreNuevaEgreVieja = fecha_ingreso_nueva
							.compareTo(fecha_egreso_vieja);
				} else {
					compIngreNuevaEgreVieja = 1;
				}
				if (fecha_egreso_nueva != null && fecha_egreso_vieja != null) {
					compEgreNuevaEgreVieja = fecha_egreso_nueva
							.compareTo(fecha_egreso_vieja);
				} else if (fecha_egreso_nueva != null
						&& fecha_egreso_vieja == null) {
					compEgreNuevaEgreVieja = 1;
				} else if (fecha_egreso_nueva == null
						&& fecha_egreso_vieja != null) {
					compEgreNuevaEgreVieja = -1;
				}
				if (fecha_egreso_vieja == null && fecha_egreso_nueva == null
						&& laboralList.get(i).getFecha_baja_logica() == null) {
					flag = true;
				} else if (compIngreNuevaEgreVieja < 0
						&& fecha_egreso_nueva == null
						&& laboralList.get(i).getFecha_baja_logica() == null) {
					flag = true;
				} else if (compIngreNuevaEgreVieja < 0
						&& compEgreNuevaEgreVieja < 0
						&& (compEgreNuevaIngreVieja > 0 || fecha_egreso_nueva == null)
						&& laboralList.get(i).getFecha_baja_logica() == null) {
					flag = true;
				} else if (compIngreNuevaIngreVieja > 0
						&& compIngreNuevaEgreVieja < 0
						&& laboralList.get(i).getFecha_baja_logica() == null) {
					flag = true;
				} else if (compIngreNuevaIngreVieja >= 0
						&& fecha_egreso_vieja == null
						&& laboralList.get(i).getFecha_baja_logica() == null) {
					flag = true;
				} else if (compIngreNuevaIngreVieja <= 0
						&& compEgreNuevaEgreVieja > 0
						&& fecha_egreso_vieja != null
						&& laboralList.get(i).getFecha_baja_logica() == null) {
					flag = true;
				}
				i++;
			} else {
				i++;
			}
		}
		return flag;
	}
}