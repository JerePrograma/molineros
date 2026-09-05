package ar.com.ospim.autorizaciones.action;

import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.afiliados.beans.AfiCuentasBancarias;
import ar.com.ospim.autorizaciones.beans.AfiCuentaBancaria;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacionalCuenta;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPedidoCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalServiceUtil;
import ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil;
import ar.com.ospim.util.StringUtils;

public class CuentaDocumentoHelper {
	
	private static Log logger = LogFactoryUtil                                                              
			.getLog(CuentaDocumentoHelper.class);     
	
	public static ReclamoPrestacional getImagenNombre (ReclamoPrestacional   reclamo ) throws Exception {
		
		List<String> listStrings = new ArrayList<String>();
		listStrings.add("CBU");
		listStrings.add("NOTA AUTORIZACION PAGO");
		
		DynamicQuery dlf =DynamicQueryFactoryUtil.forClass(
				DLFileEntry.class, PortletClassLoaderUtil.getClassLoader());
		
		DLFolder f = DLFolderLocalServiceUtil.getFolder(
	            10136, 0L, "ReclamosPrestacionales");
	    long folderId = f.getFolderId();
		
		Criterion criterion1 = null;
		criterion1 = RestrictionsFactoryUtil.eq("folderId",folderId);
		criterion1=RestrictionsFactoryUtil.and(criterion1,
		RestrictionsFactoryUtil.ilike("title", String.valueOf(reclamo.getCuenta().getIdReclamoPrestacional()) +"%" ));
		
		
		criterion1=RestrictionsFactoryUtil.and(criterion1,
					RestrictionsFactoryUtil.in("description",listStrings));
		
	
		
		dlf.add(criterion1);
		
		
		List<Object> results=DLFolderLocalServiceUtil.dynamicQuery(dlf);
		
		//int total = results.size();
		
		
		for (Object f1 : results) {			
			DLFileEntry fileEntry = (DLFileEntry) f1;
			
			logger.debug("title"  +fileEntry.getDescription());

			
			logger.debug("Name"  +fileEntry.getName());
			
			if ("CBU".equals(fileEntry.getDescription())){
				reclamo.getCuenta().setImagenCBU(fileEntry.getName());
			}
			if ("NOTA AUTORIZACION PAGO".equals(fileEntry.getDescription())){
				reclamo.getCuenta().setImagenNotaAutorizada(fileEntry.getName());
			}
			
		}
		
		return reclamo;

	}
	
	
public static String validaExisteImagen (ReclamoPrestacional   reclamo , String descripcion ) throws Exception {
		
		String outMensaje = null;
		List<String> listStrings = new ArrayList<String>();
		listStrings.add("CBU");
		listStrings.add("NOTA AUTORIZACION PAGO");
		
		DynamicQuery dlf =DynamicQueryFactoryUtil.forClass(
				DLFileEntry.class, PortletClassLoaderUtil.getClassLoader());
		
		DLFolder f = DLFolderLocalServiceUtil.getFolder(
	            10136, 0L, "ReclamosPrestacionales");
	    long folderId = f.getFolderId();
		
		Criterion criterion1 = null;
		criterion1 = RestrictionsFactoryUtil.eq("folderId",folderId);
		criterion1=RestrictionsFactoryUtil.and(criterion1,
		RestrictionsFactoryUtil.ilike("title", String.valueOf(reclamo.getId_reclamo()) +"%" ));
		
		
		criterion1=RestrictionsFactoryUtil.and(criterion1,
					RestrictionsFactoryUtil.in("description",listStrings));
		
		
	
		
		dlf.add(criterion1);
		
		
		List<Object> results=DLFolderLocalServiceUtil.dynamicQuery(dlf);
		
		//int total = results.size();
		
		
		
		for (Object f1 : results) {			
			DLFileEntry fileEntry = (DLFileEntry) f1;
			
			logger.debug("title"  +fileEntry.getDescription());

			
			logger.debug("Name"  +fileEntry.getName());
			
			if (descripcion.equalsIgnoreCase(fileEntry.getDescription())){
				outMensaje = "Ya existe ese comprobante para el reclamo " + " ( " + descripcion + " ) " ;
			}
		
			
		}
		
		return outMensaje;

	}
	




public static String validaImagenPrestacion (ReclamoPrestacional   reclamo  ) throws Exception {
	
	String outMensaje = null;

	
	DynamicQuery dlf =DynamicQueryFactoryUtil.forClass(
			DLFileEntry.class, PortletClassLoaderUtil.getClassLoader());
	
	DLFolder f = DLFolderLocalServiceUtil.getFolder(
            10136, 0L, "ReclamosPrestacionales");
    long folderId = f.getFolderId();
	
	Criterion criterion1 = null;
	criterion1 = RestrictionsFactoryUtil.eq("folderId",folderId);
	criterion1=RestrictionsFactoryUtil.and(criterion1,
	RestrictionsFactoryUtil.ilike("title", String.valueOf(reclamo.getId_reclamo()) +"%" ));
	
	dlf.add(criterion1);
	
	
	List<Object> results=DLFolderLocalServiceUtil.dynamicQuery(dlf);
	
	int total = 0;
	
	for (Object f1 : results) {			
		DLFileEntry fileEntry = (DLFileEntry) f1;
				
		if (!"CBU".equals(fileEntry.getDescription())  &&
				!"NOTA AUTORIZACION PAGO".equals(fileEntry.getDescription())){
			total =  total + 1;
		}

	}
	
	//Nuevo para tomar imagenes de comprobantes portal proveedores
	
	DLFolder f1 = DLFolderLocalServiceUtil.getFolder(10136, 0L, "Comprobantes");
	long folderIdNew=f1.getFolderId();
	
	try{
	  if(total==0) {
         PrestacionesReclamo presta =reclamo.getPrestaciones().get(0);
    
 	
	     String idFacturaImg = presta.getComprobanteCUIT()+"-"+presta.getComprobanteTipo()+"-"+
			presta.getComprobanteLetra()+String.format("%05d",Integer.valueOf(presta.getComprobanteSucursal()))+
			presta.getComprobanteNro()/*+"-"*/;
	   
	     List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"CPBTE");
	    
	     total += list.size() ;
	     if(total==0) {
	    	 List<DLFileEntryImpl>list1 = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"ADJ");
	    	 total+=list1.size();
	     }
	  }
	}catch(Exception e) {}
	//Fin nuevo

    // También considerar documentación proveniente de Compras
    if (total == 0) {
        try {
            RequerimientoCompraReclamoPrestacional relacion =
                    RequerimientoCompraReclamoPrestacionalServiceUtil
                            .getRelacionPorReclamoPrestacional(
                                    reclamo.getId_reclamo()
                            );

            if (relacion != null
                    && relacion.isVinculado()
                    && relacion.getIdReclamoPrestacionalInt() == reclamo.getId_reclamo()
                    && relacion.getIdRequerimientoCompra() > 0) {

                int idRequerimiento =
                        relacion.getIdRequerimientoCompra();

                // Orden médica
                List<RequerimientoCompraPresupuesto> ordenes =
                        BusquedaRequerimientoCompraServiceUtil
                                .listarOrdenesMedicas(idRequerimiento);

                if (ordenes != null) {
                    for (RequerimientoCompraPresupuesto orden : ordenes) {

                        if (orden != null
                                && orden.isActivo()
                                && orden.getIdRequerimiento() != null
                                && orden.getIdRequerimiento().intValue() == idRequerimiento
                                && orden.getTipoDocumento() != null
                                && orden.getTipoDocumento().intValue()
                                == RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_ORDEN_MEDICA
                                && orden.getIdRequerimientoPresupuesto() != null) {

                            total++;
                            break;
                        }
                    }
                }

                // Pedido de cotización
                if (total == 0) {
                    RequerimientoCompraPedidoCotizacion pedido =
                            BusquedaRequerimientoCompraServiceUtil
                                    .getPedidoCotizacionAdjudicado(idRequerimiento);

                    if (pedido != null
                            && pedido.getIdRequerimiento() != null
                            && pedido.getIdRequerimiento().intValue() == idRequerimiento
                            && pedido.getDlFileEntryId() != null
                            && pedido.getDlFileEntryId().longValue() > 0L) {

                        total++;
                    }
                }

                // Presupuesto adjudicado
                if (total == 0) {
                    RequerimientoCompraPresupuesto presupuesto =
                            BusquedaRequerimientoCompraServiceUtil
                                    .getPresupuestoAdjudicado(idRequerimiento);

                    if (presupuesto != null
                            && presupuesto.isActivo()
                            && presupuesto.getIdRequerimiento() != null
                            && presupuesto.getIdRequerimiento().intValue() == idRequerimiento
                            && presupuesto.getTipoDocumento() != null
                            && presupuesto.getTipoDocumento().intValue()
                            == RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_PRESUPUESTO
                            && presupuesto.getIdRequerimientoPresupuesto() != null) {

                        total++;
                    }
                }
            }

        } catch (Exception e) {
            logger.error(
                    "Error validando documentación de Compras para reclamo "
                            + reclamo.getId_reclamo(),
                    e
            );
        }
    }

	if (total == 0){
		outMensaje = "El reclamo debe tener al menos una imagen cargada ";
	}
	
	return outMensaje;

}
	
	
	
	public static ReclamoPrestacionalCuenta getCuenta (AfiCuentaBancaria   afiCuenta ) throws Exception {
		
		
		ReclamoPrestacionalCuenta cuenta = new ReclamoPrestacionalCuenta();
		
		String titular = afiCuenta.getCboTitular();
		cuenta.setId(afiCuenta.getId());
		if("0".equals(titular)){
			cuenta.setCbu(afiCuenta.getCbu()); 
			cuenta.setEmail(afiCuenta.getEmail());
			cuenta.setCuil(afiCuenta.getCuil());
			cuenta.setApellido(afiCuenta.getApellido());
			cuenta.setNombre(afiCuenta.getNombre());
			cuenta.setCmbTitular(titular);
			cuenta.setImagenCBU(afiCuenta.getAdjClaveCBU());
		}else if ("1".equals(titular)){
			cuenta.setCbu(afiCuenta.getCbu()); 
			cuenta.setEmail(afiCuenta.getEmail());
			cuenta.setCuil(afiCuenta.getCuil());
			cuenta.setApellido(afiCuenta.getApellido());
			cuenta.setNombre(afiCuenta.getNombre());
			cuenta.setCmbTitular(titular);
			cuenta.setImagenCBU(afiCuenta.getAdjClaveCBU());
			cuenta.setImagenNotaAutorizada(afiCuenta.getAdjClaveNota());
		}
	
		
		return cuenta;

	}
	
	
	public static String validaCuentaCambioEstado (ReclamoPrestacional   reclamo ) throws Exception {
		
		String msgError =  null; 
		
		
		if (reclamo.getCuenta() == null){
			return msgError = "Debe ingresar una Cuenta";
		}
		
		ReclamoPrestacionalCuenta cuenta = reclamo.getCuenta();
		String titular = cuenta.getCmbTitular();

		if("0".equals(titular)){
			if (cuenta.getCbu() == null || StringUtils.checkEmpty(cuenta.getCbu()) ){
				return msgError = "Debe ingresar el CBU";
			}

			if (cuenta.getEmail() == null || StringUtils.checkEmpty(cuenta.getEmail()) ){
				return msgError = "Debe ingresar el Email";
			}
			
			if (cuenta.getImagenCBU() == null || StringUtils.checkEmpty(cuenta.getImagenCBU()) ){
				return msgError = "Debe agregar un comprobante del CBU";
			}
			
		}else if ("1".equals(titular)){
			if (cuenta.getCbu() == null || StringUtils.checkEmpty(cuenta.getCbu()) ){
				return msgError = "Debe ingresar el CBU";
			}
			if (cuenta.getEmail() == null || StringUtils.checkEmpty(cuenta.getEmail()) ){
				return msgError = "Debe ingresar el Email";
			}
			if (cuenta.getCuil() == null || StringUtils.checkEmpty(cuenta.getCuil()) ){
				return msgError = "Debe ingresar el Cuil";
			}
			if (cuenta.getApellido() == null || StringUtils.checkEmpty(cuenta.getApellido()) ){
				return msgError = "Debe ingresar un Apellido";
			}
		
			if (cuenta.getNombre() == null || StringUtils.checkEmpty(cuenta.getNombre()) ){
				return msgError = "Debe ingresar un Nombre";
			}
			
			if (cuenta.getImagenCBU() == null || StringUtils.checkEmpty(cuenta.getImagenCBU()) ){
				return msgError = "Debe agregar un comprobante del CBU";
			}

			if (cuenta.getImagenNotaAutorizada() == null || StringUtils.checkEmpty(cuenta.getImagenNotaAutorizada()) ){
				return msgError = "Debe agregar la nota autorizante";
			}


		}
	
		
		return msgError;

	}
	
	
	public static ReclamoPrestacionalCuenta getCuentas(AfiCuentasBancarias afiCuenta) throws Exception {

	    ReclamoPrestacionalCuenta cuenta = new ReclamoPrestacionalCuenta();
	    cuenta.setId(afiCuenta.getId());
	    cuenta.setCbu(afiCuenta.getCbu());
	    cuenta.setEmail(afiCuenta.getEmail());
	    cuenta.setCuil(afiCuenta.getCuilCbu());

	    //determinar si es titular o apoderado
	    cuenta.setApellido(afiCuenta.getApellido());
	    cuenta.setNombre(afiCuenta.getNombre());
	    
	    if (afiCuenta.isTitular()) {
	        cuenta.setCmbTitular("0"); //titular
	        cuenta.setImagenCBU(afiCuenta.getFileCbu());
	    } else {
	        cuenta.setCmbTitular("1"); //apoderado
	        cuenta.setImagenCBU(afiCuenta.getFileCbu());
	        cuenta.setImagenNotaAutorizada(afiCuenta.getFileNotaAutorizada());
	    }

	    return cuenta;
	}

	
	
	
}
