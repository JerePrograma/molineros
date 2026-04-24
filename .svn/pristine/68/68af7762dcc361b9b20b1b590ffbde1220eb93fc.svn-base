package ar.com.ospim.webservice.hoteles;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.axis.AxisFault;


public class TestCliente {

	public static void main(String[] args) {
		
		
		try {
			
			SincronizarHotelesServiceLocator service = new SincronizarHotelesServiceLocator();
			SincronizarHotelesSoapBindingStub clienteWS = new SincronizarHotelesSoapBindingStub(new java.net.URL(service.getSincronizarHotelesAddress()),service);
			Calendar fecha=  Calendar.getInstance();

			
//////////////////////////
//////////////////////////
		//  RECIBOS	
			
 			List<ReciboHotelWS>recibos = new ArrayList<ReciboHotelWS>();
 
			ReciboHotelWS recibo = new ReciboHotelWS();
			recibo.setSucursal("00030");
			recibo.setNumero("1");
			recibo.setTotal(15D);
			
			
			List<ReciboIngresoWS> ingresos1 = new ArrayList<ReciboIngresoWS>();
			ReciboIngresoWS ri1= new ReciboIngresoWS();
			ri1.setImporte(15D);
			ri1.setFecha(fecha);
			ri1.setTipoIngreso("Efectivo");
			
			ingresos1.add(ri1);
			
			recibo.setIngresos((ReciboIngresoWS[]) ingresos1.toArray(new ReciboIngresoWS[ingresos1.size()]));
			
			recibos.add(recibo);
			
			
			ReciboHotelWS recibo1 = new ReciboHotelWS();
			recibo1.setSucursal("00030");
			recibo1.setNumero("2");
			recibo1.setTotal(20D);
//-------			
			List<ReciboIngresoWS> ingresos2 = new ArrayList<ReciboIngresoWS>();
			ReciboIngresoWS ri2= new ReciboIngresoWS();
			ri2.setImporte(10D);
			ri2.setFecha(fecha);
			ri2.setTipoIngreso("Efectivo");
			ingresos2.add(ri2);
			
			
			ReciboIngresoWS ri3= new ReciboIngresoWS();
			ri3.setImporte(10D);
			ri3.setFecha(fecha);
			ri3.setTipoIngreso("Transferencia Bancaria");
			ri3.setOperacionNro("2020091800");
			ri3.setBancoId(7);
			ri3.setCuentaBancariaId(77);
			ingresos2.add(ri3);
			
			recibo1.setIngresos((ReciboIngresoWS[]) ingresos2.toArray(new ReciboIngresoWS[ingresos2.size()]));
			
			recibos.add(recibo1);
			
			ReciboHotelWS[] rr = (ReciboHotelWS[]) recibos.toArray(new ReciboHotelWS[recibos.size()]);
			
			
			rr=clienteWS.sincronizarRecibos(rr );
			
			for(ReciboHotelWS r:rr) {
				System.out.println("Recibo Nro "+ r.getNumero() +"   ---   Fecha  Proceso " +r.getFechaProceso().getTime());
				System.out.println(r.getError());
			}
			
////////////////////////////
////////////////////////////
////////////////////////////			
	// FACTURAS		
/*			
			List<FacturaWS>facturas = new ArrayList<FacturaWS>();
			FacturaWS factura1 = new FacturaWS();
			factura1.setFecha(fecha);
			factura1.setNumero("1");
			factura1.setSucursal("00030");
			factura1.setLetra("A");
			factura1.setImporteTotal(new BigDecimal(121));
			factura1.setImporteNeto(new BigDecimal(100));
			factura1.setIva(new BigDecimal(21));
			factura1.setPresentaForm8001(false);
			factura1.setTotalExento(BigDecimal.ZERO);
			factura1.setTipo("FCP");
			ClienteHotelWS cliente = new ClienteHotelWS();
			cliente.setApellido("PEREZ");
			cliente.setNombre("JUAN");
			cliente.setTipo("AFILIADO");
			cliente.setCuil("12345678901");
			cliente.setDocumentoNro("1234");
			factura1.setCliente(cliente);
			
			List<FacturaDetalleWS>detalle1 = new ArrayList<FacturaDetalleWS>();
			FacturaDetalleWS d1 = new FacturaDetalleWS();
			d1.setConcepto(1);
			d1.setPrecio(new BigDecimal(30));
			detalle1.add(d1);
			
			FacturaDetalleWS d2 = new FacturaDetalleWS();
			d2.setConcepto(1);
			d2.setPrecio(new BigDecimal(70));
			detalle1.add(d2);
			factura1.setDetalles((FacturaDetalleWS[]) detalle1.toArray(new FacturaDetalleWS[detalle1.size()]));
			
			List<FacturaIngresoWS> fingresos1 = new ArrayList<FacturaIngresoWS>();
			FacturaIngresoWS fri1= new FacturaIngresoWS();
			fri1.setImporte(15D);
			fri1.setFecha(fecha);
			fri1.setTipoIngreso("Efectivo");
			
			fingresos1.add(fri1);
			
			factura1.setIngresos((FacturaIngresoWS[]) fingresos1.toArray(new FacturaIngresoWS[fingresos1.size()]));
			
	        facturas.add(factura1);
			
			FacturaWS[] ff = (FacturaWS[]) facturas.toArray(new FacturaWS[facturas.size()]);
			
			
			ff=clienteWS.sincronizarFacturas(ff );
			
			for(FacturaWS r:ff) {
				System.out.println("Factura Nro "+ r.getNumero() +"   ---   Fecha  Proceso " +r.getFechaProceso().getTime());
				System.out.println(r.getError());
			}
			
*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
		

	}

}
