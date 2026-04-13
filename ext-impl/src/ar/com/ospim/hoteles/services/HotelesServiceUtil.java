package ar.com.ospim.hoteles.services;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.persistence.PermissionUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;

import ar.com.ospim.farmaciaOspim.beans.ArchivoMedEspecial;
import ar.com.ospim.farmaciaOspim.beans.ItemFarmaciaTotal;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.DetalleDesglose;
import ar.com.uoma.facturacion.FacturaIngreso;
import ar.com.ospim.farmaciaOspim.beans.TiposDeVentas;
import ar.com.ospim.farmaciaOspim.exceptions.ImposibleBorrarFarmaciaOspimException;
import ar.com.ospim.farmaciaOspim.exceptions.ImposibleCerrarVademecumFarmaciaOspimException;
import ar.com.ospim.global.beans.Farmacia;
import ar.com.ospim.hoteles.beans.Consumo;
import ar.com.ospim.hoteles.beans.Habitacion;
import ar.com.ospim.hoteles.beans.Mesa;
import ar.com.ospim.hoteles.beans.Personal;
import ar.com.ospim.hoteles.beans.Prestamo;
import ar.com.ospim.hoteles.beans.PrestamoCuota;
import ar.com.ospim.hoteles.beans.ProductoCategoria;
import ar.com.ospim.hoteles.beans.ProductoConfiteria;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.hoteles.beans.Reserva;
import ar.com.ospim.procesaArchivos.beans.ArchivoVademecum;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.ArchivoDesglose;

	
	public class HotelesServiceUtil {
		

	private static HotelesServiceImpl instance = null;
	
	private static Log _log = LogFactoryUtil
			.getLog(HotelesServiceUtil.class);

	private static HotelesServiceImpl getInstance() {
		if (null == instance) {
			instance = new HotelesServiceImpl();
		}
		return instance;
	}
	
	public static List<Habitacion>  getHabitaciones(String codHotel, String grupo ) throws SystemException {
		return getInstance().getHabitaciones(codHotel, grupo);
	}
    
	public static List<Mesa>  getMesas(String codHotel, String grupo ) throws SystemException {
		return getInstance().getMesas(codHotel, grupo);
	}
	
	public static List<Mesa>  getPersonalByMesas(String codHotel, Integer mesa ) throws SystemException {
		return getInstance().getPersonalByMesas(codHotel, mesa);
	}
	
	public static List<Mesa>  getMesasByPersonal(String codHotel, Integer personal ) throws SystemException {
		return getInstance().getMesasByPersonal(codHotel, personal);
	}
	
	public static List<Habitacion>  getGrupos (String codHotel) throws SystemException {
		List<Habitacion> list=getInstance().getHabitaciones(codHotel,null );
		Map<String,Habitacion> map = new HashMap<String,Habitacion>();
		for(Habitacion h:list) {
			Habitacion hab = map.get(h.getGrupo());
			if(hab==null) map.put(h.getGrupo(), h);
		}
		
		List ret = new ArrayList<Habitacion>();
		for (String key : map.keySet()) {
	       ret.add(new Habitacion(0,null,key));
	    }
		return ret;
	}
	
	public static List<ProductoCategoria>  getProductosCategorias(String codHotel) throws SystemException {
		return getInstance().getProductosCategorias(codHotel);
	}
	
	public static List<ProductoCategoria>  getProductosCategoriasHabilitados(String codHotel,String tipo) throws SystemException {
		
		List<ProductoCategoria> cats= new ArrayList<ProductoCategoria>();
		
		for(ProductoCategoria p:getInstance().getProductosCategorias(codHotel)) {
			if(p.getAplicaA().contains(tipo)) {
				cats.add(p);
			}
		}
		
		return cats;
	}
	
    public static List<ProductoConfiteria>  getProductos(String codHotel,String categoria,String producto) throws SystemException {
		return getInstance().getProductos(codHotel,categoria,producto);
	}
    
    public static Integer actualizarConsumos(String codHotel,String tipo,String producto,Integer cantidad,String unidadId,Integer personalId,String usuario) throws SystemException {
    	return getInstance().actualizarConsumos(codHotel,tipo,producto,cantidad,unidadId,personalId,usuario);
    }
    
    public static Integer eliminarConsumos(String codHotel,String tipo,String producto,String unidadId,String usuario) throws SystemException {
    	return getInstance().eliminarConsumos(codHotel,tipo,producto,unidadId,usuario);
    }
    
    public static Integer cambiarEstado(String codHotel,String tipo,String producto,String unidadId,String usuario,String estado) throws SystemException {
    	return getInstance().cambiarEstado(codHotel,tipo,producto,unidadId,usuario,estado);
    }
    
    public static List<Consumo>  getConsumos(String codHotel,String tipo,String unidad) throws SystemException {
		return getInstance().getConsumos(codHotel,tipo,unidad);
	}
	
    public static String  getConsumosHTML(String codHotel,String tipo,String unidad,ThemeDisplay themeDisplay) throws SystemException {
    	DecimalFormat df = new DecimalFormat("#.00");
    	String consumosHTML="";
    	 List<Consumo>consumos=getInstance().getConsumos(codHotel,tipo,unidad);
    	 consumosHTML="<table>";
 
/*    	 
 		 consumosHTML += "<thead><tr>" + 
 				"<th></th>" +
 				"<th>Código</th>" + 
 				"<th>Descripción</th>" + 
 				"<th>Cantidad</th>" +
 				"<th>Precio</th>" +
 				"<th>Total</th>" +
 				"</tr></thead>";
*/ 		 
 		 consumosHTML+="<tbody>";
 				
 		 for(Consumo c:consumos) {
 			
 			consumosHTML +="<tr>";
 			consumosHTML +="<td>"; //Boton elimnar
 			
 			consumosHTML +="<input type='image' id='consumo_"+codHotel+"_"+c.getProducto().getCodigo()+ "' onclick='eliminar_producto(this)'" +
 					       "src='/html/images/subtrac_32.png'/>";
 			
// 			  "src='" +themeDisplay.getPathThemeImages() +"/common/subtrac_32.png'/>"; 			
 			
 			
 			consumosHTML +="</td>"; 
 			
 			/*
 			consumosHTML +="<td>";
 			consumosHTML += c.getProducto().getCodigo();
 			consumosHTML +="</td>";
 			*/
 			consumosHTML +="<td>";
 			consumosHTML += c.getProducto().getDescripcion();
 			consumosHTML +="</td>";
 			
 			consumosHTML +="<td align='right'>";
 			consumosHTML += c.getCantidad();
 			consumosHTML +="</td>";
 			
 			consumosHTML +="<td align='right'>";
 			consumosHTML += df.format(c.getPrecio());
 			consumosHTML +="</td>";
 			
 			consumosHTML +="<td align='right'>";
 			consumosHTML += df.format(c.getPrecio()*c.getCantidad());
 			consumosHTML +="</td>";
 			
 			consumosHTML +="</tr>";
 			
 		 }
 		
 		 consumosHTML +=  "</tbody></table>";

    	 
		 return consumosHTML;
	}
    
    
    
    public static Integer  updateMesa(Mesa mesa,String usr) throws SystemException {
		return getInstance().updateMesa(mesa,usr);
	}
    
    public static Integer  deleteMesa(Mesa mesa,String usr) throws SystemException {
		return getInstance().deleteMesa(mesa,usr);
	}
    
    public static Mesa  getMesasByNro(String codHotel, Integer nroMesa ) throws SystemException {
    	Mesa mesa = new Mesa();
		List<Mesa> mesas = getInstance().getMesas(codHotel,null);
		
		for(Mesa m:mesas) {
			if(m.getNumero()==nroMesa ) {
				mesa=m;
				break;
			}
		}
		return mesa;
	}
    
    public static Integer  updateHabitacion(Habitacion habitacion,String usr) throws SystemException {
		return getInstance().updateHabitacion(habitacion,usr);
	}
    
    public static Integer  deleteHabitacion(Habitacion habitacion,String usr) throws SystemException {
		return getInstance().deleteHabitacion(habitacion,usr);
	}
    
    public static Habitacion  getHabitacionesByNro(String codHotel, Integer nroHabitacion ) throws SystemException {
    	Habitacion habitacion = new Habitacion();
		List<Habitacion> habitaciones = getInstance().getHabitaciones(codHotel,null);
		
		for(Habitacion m:habitaciones) {
			if(m.getNumero()==nroHabitacion ) {
				habitacion=m;
				break;
			}
		}
		return habitacion;
	}

    public static String  updateCategoria(ProductoCategoria categoria,String usr) throws SystemException {
		return getInstance().updateCategoria(categoria,usr);
	}
    
    public static String  deleteCategoria(ProductoCategoria categoria,String usr) throws SystemException {
		return getInstance().deleteCategoria(categoria,usr);
	}
    
    public static ProductoCategoria  getCategoriaByCodigo(String codHotel, String codigo ) throws SystemException {
    	ProductoCategoria categoria = new ProductoCategoria();
		List<ProductoCategoria> categorias = getInstance().getProductosCategorias(codHotel);
		for(ProductoCategoria m:categorias) {
			if(m.getCodigo().equalsIgnoreCase(codigo) ) {
				categoria=m;
				break;
			}
		}
		return categoria;
	}
    
    public static ProductoConfiteria  getProductoByCodigo(String codHotel, String codigo ) throws SystemException {
    	List<ProductoConfiteria> productos = getProductos(codHotel, null, codigo);
		return productos.get(0);
	}

    public static String  updateProducto(ProductoConfiteria producto,String usr) throws SystemException {
		return getInstance().updateProducto(producto,usr);
	}
    
    public static String  deleteProducto(ProductoConfiteria producto,String usr) throws SystemException {
		return getInstance().deleteProducto(producto,usr);
	}
    
    public static List<Personal>  getPersonal(String codHotel,String categoria,Integer id) throws SystemException {
		return getInstance().getPersonal(codHotel,categoria,id);
	}
    
    public static Personal  getPersonalById(String codHotel, Integer codigo ) throws SystemException {
    	List<Personal> personal = getPersonal(codHotel, null, codigo);
		return personal.get(0);
	}
    
    public static Integer  updatePersonal(Personal personal,String usr) throws SystemException {
		return getInstance().updatePersonal(personal,usr);
	}
    
    public static Integer  deletePersonal(Personal personal,String usr) throws SystemException {
		return getInstance().deletePersonal(personal,usr);
	}
    
    public static Integer  deleteMesasAsignadasPersonal(String hotel,Integer personalId) throws SystemException {
		return getInstance().deleteMesasAsignadasPersonal(hotel, personalId);
	}
    
    public static Integer  insertMesasAsignadasPersonal(String hotel,Integer personalId,Integer mesaId,String usr) throws SystemException {
		return getInstance().insertMesasAsignadasPersonal(hotel, personalId,mesaId,usr);
	}
    
    public static List<Reserva>  getReservasActivas(Integer anio,Date fecha) throws SystemException {
		return getInstance().getReservasActivas(anio, fecha);
	}
    
    public static Integer insertOrdenConsumo(String hotel,String tipo, Integer mesa, Integer personal,Integer reserva,String usr,String nroFactura) throws SystemException {
		return getInstance().insertOrdenConsumo(hotel, tipo, mesa, personal,reserva, usr,nroFactura);
	}
    
    public static Integer deleteConsumosActivos(String hotel,String tipo, Integer mesa) throws SystemException {
		return getInstance().deleteConsumosActivos(hotel, tipo, mesa);
	}
    
    public static Integer  getTotalConsumosPorReserva(Integer idReseva ) throws SystemException {
		return getInstance().getTotalConsumosPorReserva(idReseva);
	}
    
    public static Integer  getTotalReserva(Integer anio , Integer idReseva ) throws SystemException {
		return getInstance().getTraeTotalReserva(anio, idReseva);
	}
    
    public static List<Reserva>  getReservasByFechaFin(String codHotel,Integer anio,Integer reserva,String habitacion,Date fechaDde,Date fechaHta) throws SystemException {
		return getInstance().getReservasByFechaFin(codHotel, anio, reserva,habitacion,fechaDde,fechaHta);
	}
    
    public static List<Consumo> getUltimoConsumoAsignadoHabitacion(String codHotel  , String  anio, Integer mesaid, String habitacionid) throws SystemException{
    	return getInstance().getUltimoConsumoAsignadoHabitacion(codHotel, anio, mesaid, habitacionid);
    }
    
    public static Reserva  getReservaById(String codHotel,Integer anio,Integer reserva) throws SystemException {
		return getInstance().getReservaById(codHotel, anio, reserva);
	}
    
    public static Long  updateRecibo(Recibo recibo,String usr) throws Exception {
		return getInstance().updateRecibo(recibo,usr);
	}
    
    public static List<Recibo>  getRecibos(String sucursal,Long id,Date fechaDde,Date fechaHta,String clienteNombre, String clienteDoc,Integer estado) throws SystemException {
		return getInstance().getRecibos(sucursal, id, fechaDde,fechaHta,clienteNombre,clienteDoc,estado);
	}
    
    public static Recibo  getReciboByNro(String codHotel,Long idRecibo,boolean estoyEnCentral) throws SystemException {
    	Recibo recibo=null;
    	List<Recibo>list = getRecibos(codHotel,idRecibo,null,null,null,null,null);
    	if(!list.isEmpty()) {
    	  recibo=list.get(0);
    	  recibo.setTotalAnterior(recibo.getTotal());
    	  if(recibo.getReserva()!=null) {
    		  if(!estoyEnCentral) {
    		    Reserva reserva = getReservaById(codHotel,recibo.getReserva().getAnio(),recibo.getReserva().getIdReserva());
    		    recibo.setReserva(reserva);
    		  } 
    	  }
    	  List<FacturaIngreso> ingresos=getInstance().getReciboIngresos(codHotel, idRecibo, null);
    	  recibo.setIngresos(ingresos);
    	}
 		return recibo;
	}
    
    public static Long  anulaRecibo(Recibo recibo,String usr,boolean estoyEnCentral) throws SystemException {
		return getInstance().anulaRecibo(recibo,usr,estoyEnCentral);
	}
    
    public static List<Recibo>  getRecibosPendientesSincronizar() throws SystemException {
    	List<Recibo>recibos=getInstance().getRecibosPendientesSincronizar();
    	for(Recibo r:recibos) {
    		List<FacturaIngreso> ingresos=getInstance().getReciboIngresos(r.getSucursal(),r.getNumero(), null);
    		r.setIngresos(ingresos);
    	}
		return recibos;
	}
    
    public static Long  registraProcesoTransferenciaCentralRecibo(Recibo recibo) throws SystemException {
		return getInstance().registraProcesoTransferenciaCentralRecibo(recibo);
	}
    
    public static Long  aprobarRecibos(List<Recibo> recibos) throws SystemException {
		return getInstance().aprobarRecibos(recibos);
	}
    
    public static List<Recibo>  getReciboByReserva(String codHotel,Integer idReserva,Integer anio) throws SystemException {
    	List<Recibo>list = getInstance().getRecibosByReserva(codHotel,idReserva,anio);
    	return list;
	}
    
    public static Long  updatePrestamo(Prestamo prestamo,String usr) throws SystemException {
		return getInstance().updatePrestamo(prestamo,usr);
	}
    
    public static List<Prestamo>  getListaPrestamos(Prestamo filtro) throws SystemException {
		return getInstance().getListaPrestamos(filtro);
	}
    
    public static Prestamo  getPrestamoById(Long idPrestamo) throws SystemException {
    	Prestamo filtro =new Prestamo();
    	filtro.setId(idPrestamo);
    	List<Prestamo>lista = new ArrayList<Prestamo>();
    	lista=getListaPrestamos(filtro);
    	Prestamo ret =null;
    	if(!lista.isEmpty()) {
    		ret=lista.get(0);
    		List<PrestamoCuota>cuotas=getInstance().getPrestamoCuotas(filtro);
    		ret.setCuotas(cuotas);
    	}
 		return ret;
	}
    
    public static Long  updatePrestamoImagen(Prestamo prestamo,String tipo,String  usr) throws SystemException {
		return getInstance().updatePrestamoImagen(prestamo,tipo,usr);
	}
    
    public static Long  deletePrestamoImagen(Prestamo prestamo,String tipo,String  usr) throws SystemException {
		return getInstance().deletePrestamoImagen(prestamo,tipo,usr);
	}
    
    public static Integer  deletePrestamo(Prestamo prestamo,String usr) throws SystemException {
		return getInstance().deletePrestamo(prestamo,usr);
	}
    
    public static List<ar.com.ospim.tesoreria.beans.Recibo>  getPrestamoPagos(Long idPrestamo,Integer entidad,Date fechaCCHasta) throws SystemException {
		return getInstance().getPrestamoPagos(idPrestamo,entidad,fechaCCHasta);
	}
    
    public static Long  updateReciboRetencion(Recibo recibo,String usr) throws Exception {
		return getInstance().updateReciboRetencion(recibo,usr);
	}
}
