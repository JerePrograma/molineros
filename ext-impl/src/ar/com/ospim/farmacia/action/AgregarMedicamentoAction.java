package ar.com.ospim.farmacia.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.farmacia.WebKeysFarmacia;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.beans.ReintegroMedicamentoItem;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="AgregarMedicamentoAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Agrega medicamentos a una lista de medicamentos
 * 
 * @author Federico Brachi
 * 
 */
public class AgregarMedicamentoAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(AgregarMedicamentoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.farmacia.medicamento.result.search");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		List<ReintegroMedicamentoItem> medicamentos = (ArrayList<ReintegroMedicamentoItem>) session
				.getAttribute(WebKeysFarmacia.REINTEGRO_PRESTACIONES_EN_EDICION);
		
		

		String accion = ParamUtil.getString(renderRequest, "accion", "");
		int id = ParamUtil.getInteger(renderRequest, "id_prestacion", 0);
		
		if (null == medicamentos) {
			medicamentos =  new ArrayList<ReintegroMedicamentoItem>();
		}
		if (accion.equals("borrar") ){
			for (ReintegroMedicamentoItem med : medicamentos) {
				if (id == med.getId()) {
					if (med.getId() > 0) {
						med.setDelete(true);
					} else {
						medicamentos.remove(med);
					}
					break;
				}
			}
		} else if (accion.equals("borrarAll")) {
//			for (ReintegroMedicamentoItem med : medicamentos) {
					medicamentos.clear();
//					medicamentos.remove(med);
//			}
		} else {  //editar o añadir
			double cantidad = ParamUtil.getDouble(renderRequest, "cantidad");
			int troquel = ParamUtil.getInteger(renderRequest, "troquel");
			
			int idReclamoPRestacional=ParamUtil.getInteger(renderRequest, "idReclamoPrestacional");
			int idPrestacionReclamo=ParamUtil.getInteger(renderRequest, "idPrestacionReclamo");
			
			
			boolean pmo = ParamUtil.getBoolean(renderRequest, "pmo");
			int registro = ParamUtil.getInteger(renderRequest, "registro");
			String nombre = ParamUtil.getString(renderRequest, "nombre");
			String presentacion = ParamUtil.getString(renderRequest,
					"presentacion");
			String laboratorio = ParamUtil.getString(renderRequest,
					"laboratorio");
			String coberOspim = ParamUtil.getString(renderRequest,
					"cober_ospim", "0");
			
			String coberPrestadora = ParamUtil.getString(renderRequest,
					"cober_prestadora", "0");
			
			String coberImesa = ParamUtil.getString(renderRequest,
					"cober_imesa", "0");
			
			
			String coberAmtima = ParamUtil.getString(renderRequest,
					"cober_amtima", "0");
			String porcSSS = ParamUtil.getString(renderRequest, "porc_sss", "0");
			String porcOspim = ParamUtil.getString(renderRequest, "porc_ospim", "0");
			String porcAmtima = ParamUtil.getString(renderRequest,
					"porc_amtima", "0");
			String precioPub = ParamUtil.getString(renderRequest, "precio_pub", "0");
			String precioOspim = ParamUtil.getString(renderRequest,
					"precio_ospim", "0");
			int nro_receta = ParamUtil.getInteger(renderRequest, "receta");
			int id_medicamento= ParamUtil.getInteger(renderRequest, "id_medicamento");
			String fecha_medic_receta_string = ParamUtil.getString(renderRequest,"fecha_medic_receta");
			Date fecha_medic_receta=null;
			if (null != fecha_medic_receta_string && !fecha_medic_receta_string.trim().equals("")) {
				fecha_medic_receta = DateUtils.parse(fecha_medic_receta_string, "dd/MM/yyyy");
			}
			String accionTerapeutica = ParamUtil.getString(renderRequest, "accion_t");
			String droga = null;
			Date fecha_receta=null;
			String fecha_receta_string=ParamUtil.getString(renderRequest,"fecha_receta");
			String cod_barras= ParamUtil.getString(renderRequest,"cod_barras");
			String porcentaje = ParamUtil.getString(renderRequest,"porcentaje", "0");
			
			if (null != fecha_receta_string && !fecha_receta_string.trim().equals("")) {
				fecha_receta = DateUtils.parse(fecha_receta_string, "dd/MM/yyyy");
			}


			SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
			String comproFechaDia = ParamUtil.getString(renderRequest, "diaComprobante");
			String comproFechaMes = ParamUtil.getString(renderRequest, "mesComprobante");
			String comproFechaAnio = ParamUtil.getString(renderRequest, "anioComprobante");
		
			Date fechaComprobante = null;
			try {
				fechaComprobante = formatoDeFecha.parse(comproFechaDia + "/"
						+ (Integer.parseInt(comproFechaMes) + 1) + "/" + comproFechaAnio);
			} catch (Exception e) {
				fechaComprobante = null;
			}
			
			
			String comprobanteTipo=ParamUtil.getString(renderRequest,"comprobanteTipo");			
			String comprobanteSuc=ParamUtil.getString(renderRequest,"comprobanteSuc");
			String comprobanteLetra=ParamUtil.getString(renderRequest,"comprobanteLetra");
			String comprobanteNro=ParamUtil.getString(renderRequest,"comprobanteNro");
			String importeCompro=ParamUtil.getString(renderRequest,"importeCompro" ,"0");
			String cuitEntidad=ParamUtil.getString(renderRequest,"cuit_entidad");
			String sucursalEntidad=ParamUtil.getString(renderRequest,"sucursal_entidad");
			
			


			
			String fechaPrestacionDia = ParamUtil.getString(renderRequest, "fechaPrestacionDia");
			String fechaPrestacionMes = ParamUtil.getString(renderRequest, "fechaPrestacionMes");
			String fechaPrestacionAnio = ParamUtil.getString(renderRequest, "fechaPrestacionAnio");
		
			Date fechaPrestacion = null;
			try {
				fechaPrestacion = formatoDeFecha.parse(fechaPrestacionDia + "/"
						+ (Integer.parseInt(fechaPrestacionMes) + 1) + "/" + fechaPrestacionAnio);
			} catch (Exception e) {
				fechaPrestacion = null;
			}
			
			
			
			
			ReintegroMedicamentoItem medicamento = new ReintegroMedicamentoItem();
			Medicamento item = new Medicamento(id_medicamento, troquel, registro, nombre,
					presentacion, laboratorio, accionTerapeutica, droga,
					new BigDecimal(precioPub), new BigDecimal(0),
					new BigDecimal(0), new BigDecimal(0),
					new BigDecimal(0), new BigDecimal(coberOspim),
					new BigDecimal(coberAmtima), cantidad, nro_receta,
					"", fecha_receta, cod_barras);
			
			//añade medicamento
			medicamento.setId(id);
			medicamento.setMedicamento(item);
			medicamento.setNumeroReceta(nro_receta);
			medicamento.setFechaReceta(fecha_medic_receta);
			medicamento.setCantidad(cantidad);
			medicamento.setPrecio_al_publico(new BigDecimal(precioPub));
			medicamento.setTotalCobertura(new BigDecimal (porcentaje));
			medicamento.setImporteCoberturaOspim(new BigDecimal(coberOspim));
			medicamento.setImporteCoberturaAmtima(new BigDecimal(coberAmtima));
			medicamento.setImporteCoberturaPrestadora(new BigDecimal(coberPrestadora));
			medicamento.setImporteCoberturaImesa(new BigDecimal(coberImesa));
			BigDecimal tot = medicamento.getImporteCoberturaAmtima().add(medicamento.getImporteCoberturaOspim());
			tot= tot.add(medicamento.getImporteCoberturaPrestadora()).add(medicamento.getImporteCoberturaImesa());
			medicamento.setTotal(tot.multiply(new BigDecimal(medicamento.getCantidad())));
//			medicamento.setTotal((medicamento.getImporteCoberturaAmtima().add(medicamento.getImporteCoberturaOspim()).multiply(new BigDecimal(medicamento.getCantidad()))));
			medicamento.setPrecio_ospim(medicamento.getPrecio_al_publico().multiply(new BigDecimal(medicamento.getCantidad())));
		// datos del reclamo asociado al medicamento 	
			medicamento.setIdReclamoPrestacional(idReclamoPRestacional);
			medicamento.setIdPrestacionReclamo(idPrestacionReclamo);
			
			medicamento.setFechaComprobante(fechaComprobante);
			medicamento.setComproaDebitarTipo(comprobanteTipo);

			if(!"OTR".equals(medicamento.getComproaDebitarTipo())){
				int sucu = Integer.valueOf(comprobanteSuc);
				medicamento.setComproaDebitarSucursal( String.format("%05d",sucu));
				medicamento.setComproaDebitarLetra(comprobanteLetra);
			}
			int compro = Integer.valueOf(comprobanteNro);				
			medicamento.setComproaDebitarNumero( String.format("%08d",compro));
			
			
			medicamento.setCuitEntidad(cuitEntidad);
			medicamento.setSucursalEntidad(sucursalEntidad);
			medicamento.setImporteComprobante(new BigDecimal(importeCompro));
			medicamento.setFechaPrestacion(fechaPrestacion);
			
		
			
			if(id == 0) {
				//generar siguiente id negativo, porque id <= 0 quiere decir que es nuevo
				medicamento.setId(generarSiguienteIdNegativo(medicamentos));
			}
			else {
				//si es viejo debo borrar la versión anterior
				for (ReintegroMedicamentoItem med : medicamentos) {
					if (id == med.getId()) {
						medicamentos.remove(med);						
						break;
					}
				}
				medicamento.setEdit(true);
			}	
			medicamentos.add(medicamento);			
		}			

		session.removeAttribute(WebKeysFarmacia.REINTEGRO_PRESTACIONES_EN_EDICION);
		session.removeAttribute("total_precio_pub");
		session.removeAttribute("total_cobertura");
		session.setAttribute(WebKeysFarmacia.REINTEGRO_PRESTACIONES_EN_EDICION,
				medicamentos);
		session.setAttribute("total_precio_pub",
				ReintegroActionUtil.getPrecioPublicoTotal(medicamentos).toString());
		session.setAttribute("total_cobertura",
				ReintegroActionUtil.getImporteTotal(medicamentos).toString());
		
		return mapping.findForward("portlet.farmacia.medicamento.result.search");
	}
	
	public int generarSiguienteIdNegativo(List<ReintegroMedicamentoItem> medicamentos){
		int siguiente = -1;
		for (ReintegroMedicamentoItem med : medicamentos) {
			if (med.getId() < 0) {
					siguiente--;				
			}
		}
		return siguiente;
	}
}