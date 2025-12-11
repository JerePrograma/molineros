package ar.com.ospim.hoteles.action;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.global.services.ComanderaService;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaDetalle;
import ar.com.uoma.facturacion.services.FacturacionServiceUtil;

public class HotelesFacturarByComanderaAction extends JSONAction {

	private static Log _log = LogFactoryUtil.getLog(HotelesFacturarByComanderaAction.class);
	
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String resultado = "{}";
		
		
		String idEjemplar = req.getParameter("id_ejemplar");
		String mostrarRazSoc = req.getParameter("mostrarRazonSoc");
		
		Integer idFactura= ParamUtil.getInteger(req, "id_factura");
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		NumberFormat format0D = new DecimalFormat("##########0");
		NumberFormat forCantidad = new DecimalFormat("###0");
		NumberFormat format2D = new DecimalFormat("$#0.00");
		Integer copias= ParamUtil.getInteger(req, "copias");
		
		try {
			 _log.debug("Facturando desde confitería: factura ID: " + idFactura );
			
			 Factura factura = FacturacionServiceUtil.getFactura(idFactura);
			
			 _log.debug("Factura recuperada " + factura.getTipo() + " " + factura.getLetra() + " " + factura.getNumero() + "" );
			
			 Cliente datosCabecera = FacturacionServiceUtil.getConfiguracionPtoVta(factura.getSucursal());
						
			 String pathLogo = TraeListasServiceUtil.getSystemConfig("LOGO_HOTEL");
			 
			 _log.debug("Path Logo: " + pathLogo);
					 
			 File logo = new File(pathLogo);
			 
			 _log.debug("Logo: " + logo.getAbsolutePath()  + " " + logo.getAbsoluteFile() );
			 
			 List<String> cb = new ArrayList<String>();
			 List<String> cn = new ArrayList<String>();
			 List<String> pie = new ArrayList<String>();
			 String linea="";
			 Date fechaInicioAct =formatoDeFechas.parse(datosCabecera.getDocumentoTipo());
			 cb.add(datosCabecera.getRazonSocial());
			 cb.add(datosCabecera.getDomicilio().getCalle());
			 cb.add(datosCabecera.getDomicilio().getTelefono());
			 cb.add(datosCabecera.getCategoriaIVA());
			 cb.add("C.U.I.T: 30-533114385-6                      ");
			 cb.add("Ingresos Brutos: "+datosCabecera.getDocumentoNro());
			 cb.add("Inicio Actividades: "+ formatoDeFechas.format(fechaInicioAct));
			 cb.add("_____________________________________________");
			 
			 cb.add("");
			 String razonSocial="Razón Social: ";
			 String cuit="Cuit: ";
			 String domicilio="Domicilio: ";
			 String condIva ="I.V.A: ";
			 
			 _log.debug("Cliente: " + factura.getCliente().getApellido() + ", " + factura.getCliente().getNombre());

			 
			 if(factura.getCliente().getCuit()!=null && !factura.getCliente().getCuit().isEmpty()) {
				 cuit+=factura.getCliente().getCuit()==null?"":factura.getCliente().getCuit();
				 razonSocial+= factura.getCliente().getRazonSocial().length()>45?factura.getCliente().getRazonSocial().substring(0,45):
					          factura.getCliente().getRazonSocial();
			 }else {
				 cuit+=factura.getCliente().getCuil();
				 razonSocial+= (factura.getCliente().getApellido()+" " +
			 factura.getCliente().getNombre()).length()>45?(factura.getCliente().getApellido()+" " +
					 factura.getCliente().getNombre()).substring(0,45):
					 (factura.getCliente().getApellido()+" " +
			                    			 factura.getCliente().getNombre());
			 }
			 
			 if(factura.getCliente().getDomicilio()!=null) {
			    domicilio+= factura.getCliente().getDomicilio().getCalle().trim()+" "+
			 factura.getCliente().getDomicilio().getNumero()+ " " +
			 factura.getCliente().getDomicilio().getDepto() + " " +
			 factura.getCliente().getDomicilio().getOficina() + " "+
					 factura.getCliente().getDomicilio().getLocalidadAsString();
			 }
			 if("RI".equalsIgnoreCase(factura.getCliente().getCategoriaIVA())) {
				 condIva+="Responsable Inscripto" ;
			 }else if("CS".equalsIgnoreCase(factura.getCliente().getCategoriaIVA())) {
				 condIva+="Consumidor Final" ; 
			 }else {
				condIva+="Exento" ; 
			 }
			 
			 cb.add("Fecha: " + formatoDeFechas.format(factura.getFecha()));
			 cb.add(razonSocial);
			 if(domicilio.length()>45){
				cb.add(domicilio.substring(0,45));
				cb.add(domicilio.substring(45));
			 }else {
				 cb.add(domicilio);
			 }
			 cb.add(cuit);
			 cb.add(condIva);
			 
			 cb.add("Factura Nro: "+factura.getLetra()+" " + factura.getSucursal()+"-"+ String.format("%08d",Integer.parseInt(factura.getNumero())));
			 
			//                   123456789-123456789-123456789-123456789-12345  
			         
			 cb.add("_____________________________________________");
			 ComanderaService comand= new ComanderaService();
			 comand.setLogo(logo);
			 comand.setCabecera(cb);
			 int xx=0;
			 String productoStr ="";
			 for(FacturaDetalle d: factura.getDetalles()) {
				 
				 productoStr= d.getDetalle().getDescripcion();
				 if(d.getDetalle().getDescripcion().length()>23) {
					 productoStr= d.getDetalle().getDescripcion().substring(0,23);
				 }
				 productoStr = String.format("%-23s", productoStr);
			 
			 linea= productoStr +  String.format("%20s",format2D.format(d.getPrecio()));
				 cn.add(linea);
			 }
			 
			 cn.add("_____________________________________________");
			 
			 if("A".equals(factura.getLetra())) {
			 cn.add("Neto   :"+String.format("%35s",format2D.format( factura.getImporteNeto())));
			 cn.add("Iva 21%:"+String.format("%35s",format2D.format(factura.getIva())));
			 cn.add("Exento :"+String.format("%35s",format2D.format(factura.getImporteExento())));
			 cn.add("");
			 }	 
			 cn.add("Total  :"+String.format("%35s",format2D.format(factura.getImporteTotalCalculado())));
				
			 
			 comand.setCuerpo(cn);
			 
			 pie.add("_____________________________________________");
			 pie.add( String.format("%-45s", "CAE: "+factura.getCae()));
			 pie.add("Fecha Vto CAE: "+formatoDeFechas.format(factura.getFechaCae()));
			 comand.setPie(pie);
			 
			 _log.debug("imprimiendo copias: " + copias );
			 
			 for(int i=0;i<copias;i++) {
			       comand.imprimirTicketFactura();
			 } 
						 
			 _log.debug("Fin impresión comandera");
			 
		 }catch (NumberFormatException e) {
			_log.error(e);	
		 }catch (Exception e) {
			_log.error(e);
		 }

				
		 resultado = "{}";
			
		 return resultado;
	}
}