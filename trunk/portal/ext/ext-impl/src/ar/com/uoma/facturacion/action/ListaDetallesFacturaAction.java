package ar.com.uoma.facturacion.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.util.StringUtils;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaDetalle;
import ar.com.uoma.facturacion.Producto;

/**
 * @author SVA
 */

public class ListaDetallesFacturaAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(ListaDetallesFacturaAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		List<Producto> productos = (ArrayList<Producto>) session.getAttribute(WebKeysUOMA.PRODUCTOS_EN_SESSION);
		Factura factura = (Factura) session.getAttribute(WebKeysUOMA.FACTURA_EN_EDICION);
		
		Producto aux = null;
		ArrayList<FacturaDetalle> detalles = null;
		
		_log.debug("Agregando detalle a la factura");
			
		renderRequest.setAttribute("esEdicion", "esEdicion");
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);

		if(cmd.equalsIgnoreCase(Constants.ADD)) {
			int codigo = ParamUtil.getInteger(renderRequest, "codigo");
			String condIva = ParamUtil.getString(renderRequest, "condIva");
			String clienteTipo = ParamUtil.getString(renderRequest, "clienteTipo");
			String descripcion = ParamUtil.getString(renderRequest, "descripcion");
			String _precio = ParamUtil.getString(renderRequest, "precio","0.00");
			BigDecimal precio = new BigDecimal(_precio);
			precio.setScale(2, RoundingMode.HALF_EVEN);
			
			Producto pr = new Producto();
			pr.setId(codigo);
			pr.setDescripcion(descripcion);
					
			aux = buscarDatosProducto(productos, pr);
			pr.setDebitoCredito(aux.getDebitoCredito());
			
			if(condIva.equalsIgnoreCase(WebKeysUOMA.CATEGORIAS_IVA[0][0])) { // Resp Inscripto
//				pr.setIva(precio.multiply(new BigDecimal("0.79"))); // no será 0.21 ?
//				pr.setPrecioUnitario(precio.subtract(pr.getIva()) );
				pr.setIva(precio.multiply(new BigDecimal("0.21"))); 
				pr.setPrecioUnitario(precio.subtract(pr.getIva()) );
			}else {
				pr.setPrecioUnitario(precio);	
			}
			
			//		me aseguro sea un numero negativo para no confundir con IDs de BD
			Random r = new Random(System.currentTimeMillis());
			int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
			if(idAux > 0){
				idAux = (-1)*idAux;
			}
			
			FacturaDetalle fd = new FacturaDetalle();
			fd.setEstado(FacturaDetalle.ESTADOS.ALTA);
			fd.setId(idAux);
			fd.setDetalle(pr);
			fd.setPrecio(precio);
			
			if(factura.getDetalles() == null) {
				detalles = new ArrayList<FacturaDetalle>();
				detalles.add(fd);
				factura.setDetalle(detalles);
			}else {
				factura.getDetalles().add(fd);
			}
			if(StringUtils.checkEmpty(clienteTipo)) {
				clienteTipo = Cliente.TIPOS_CLIENTE.EMPRESA.name();   // Asumo esto porque para el Afiliado o Invitado lo mando como lo que es...
			}			
				
			Cliente cli = new Cliente();
			if(factura.getCliente()!=null) {
				cli = factura.getCliente();
			}
			cli.setCategoriaIVA(condIva);
			cli.setTipo(Cliente.TIPOS_CLIENTE.valueOf(clienteTipo));
			factura.setCliente(cli);
			
//			VALIDAR EL ITEM REPETIDO

		}
		
		if(cmd.equalsIgnoreCase(Constants.DELETE)) {
			
			int idDetalle = ParamUtil.getInteger(renderRequest, "idDetalle");
			int pos = 0;
			boolean encontro = false;
			
			while (!encontro) {
				FacturaDetalle fd = factura.getDetalles().get(pos);
				
				
				if(fd.getId() == idDetalle) {
					encontro=true;
					factura.getDetalles().remove(pos);
				}
				
				pos++;
				
			}			
		}
		
		if(cmd.equalsIgnoreCase(Constants.CANCEL)) {
			
			List<FacturaDetalle> listDetalles = factura.getDetalles();
			if (listDetalles != null && !listDetalles.isEmpty()){
				factura.getDetalles().removeAll(listDetalles);	
			}
		}
		
		
		if(cmd.equalsIgnoreCase("recalcular")) {
			String condIva = ParamUtil.getString(renderRequest, "condIva");
			String clienteTipo = "";
			Integer ct = ParamUtil.getInteger(renderRequest, "clienteTipo");
			
			if(ct==null || ct ==0) {
//				ct = 0;
				clienteTipo="AFILIADO";
				condIva="CS";
			}
				
			if(ct==1){
				clienteTipo="EMPRESA";
			}else{
				clienteTipo="AFILIADO";
			}
			
			BigDecimal precio = BigDecimal.ZERO;
			precio.setScale(2, RoundingMode.HALF_EVEN);
			if(factura.getCliente()!=null) {
				factura.getCliente().setCategoriaIVA(condIva);
				factura.getCliente().setTipo(Cliente.TIPOS_CLIENTE.valueOf(clienteTipo));
			}
			if(factura.getDetalles() != null) {
				for(FacturaDetalle d:factura.getDetalles()) {
					precio=d.getPrecio();
					if(condIva.equalsIgnoreCase(WebKeysUOMA.CATEGORIAS_IVA[0][0])) { // Resp Inscripto
						d.getDetalle().setIva( precio.multiply(new BigDecimal("0.21")));
						d.getDetalle().setPrecioUnitario(precio.subtract(d.getDetalle().getIva()));
					}else {
						d.getDetalle().setPrecioUnitario(precio);
					}
				}
			}	
			factura.recalcularImportes();
			session.removeAttribute(WebKeysUOMA.FACTURA_EN_EDICION);
			session.setAttribute(WebKeysUOMA.FACTURA_EN_EDICION,factura);
			return mapping.findForward(getForward(renderRequest,"portlet.uoma.facturacion_detalle"));

		}
		
		factura.recalcularImportes();
		
		return mapping.findForward(getForward(renderRequest,"portlet.uoma.facturacion_detalle"));
	}

	private Producto buscarDatosProducto(List<Producto> productos, Producto prodSel) {
		
		boolean encontro = false;
		int pos = 0;
		Producto prAux = null;
		while (!encontro) {
			prAux = productos.get(pos);
			if(prAux.equals(prodSel) ) {
				encontro = true;
			}
			pos++;
		}
		
		return prAux;
	}
	
//	private boolean validaProfesionEspecialidad(ProfesionPrestador prof, EspecialidadPrestador esp, SubEspecialidadPrestador subEsp, 
//								ArrayList<ProfesionPrestador> listaProf) throws ProfesionEspecialidadSubEspecPrestadorException{
//		
//		boolean result = true;
//		for (Iterator<ProfesionPrestador> iterator = listaProf.iterator(); iterator.hasNext();) {
//			ProfesionPrestador _profPrest =  iterator.next();
//			EspecialidadPrestador _espePrest = _profPrest.getEspecialidades().get(0);
//			SubEspecialidadPrestador _subEspePrestador = _espePrest.getSubEspecialidades().get(0);
//			
//			if(_profPrest.getIdProfesion() == prof.getIdProfesion() 
//				&& _espePrest.getIdEspecialidad() == esp.getIdEspecialidad()
//				&& _subEspePrestador.getId() == subEsp.getId()){
//				
//				result = false;
//				throw new ProfesionEspecialidadSubEspecPrestadorException();
//			}
//		}
//
//		return result;
//	}
		
}