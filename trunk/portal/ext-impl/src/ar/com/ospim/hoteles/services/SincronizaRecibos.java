package ar.com.ospim.hoteles.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Ingreso;
import ar.com.ospim.global.beans.TarjetaDebitoCredito;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.hoteles.beans.Reserva;
import ar.com.ospim.webservice.hoteles.ReciboHotelWS;
import ar.com.ospim.webservice.hoteles.ReciboIngresoWS;
import ar.com.ospim.webservice.hoteles.SincronizarHotelesServiceLocator;
import ar.com.ospim.webservice.hoteles.SincronizarHotelesSoapBindingStub;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaIngreso;

public class SincronizaRecibos extends AgendadoJava implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -634269632968317190L;
	/**
	 * 
	 */
	private static Log logger = LogFactoryUtil.getLog(SincronizaRecibos.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		
		
		try{
			rac = ReportesServiceUtil.getConfiguracion();
			
			SincronizarHotelesServiceLocator service = new SincronizarHotelesServiceLocator();
			
			File configDir = new File(System.getProperty("catalina.base"), "conf");
			File configFile = new File(configDir, "liferay_schedulers.properties");
			
			InputStream stream = new FileInputStream(configFile);
			
			Properties props = new Properties();
			props.load(stream);
			String urlServicio = props.getProperty("hoteles_transferencia_url_service");
			if(urlServicio==null || "".equalsIgnoreCase(urlServicio)) {
				urlServicio=service.getSincronizarHotelesAddress();
			}
			
			
			SincronizarHotelesSoapBindingStub clienteWS = new SincronizarHotelesSoapBindingStub(new java.net.URL(urlServicio),service);
			

			   
			List<Recibo> list = new ArrayList<Recibo>();
			
			
			try {
				list = HotelesServiceUtil.getRecibosPendientesSincronizar();
				List<ReciboHotelWS>recibos=new ArrayList<ReciboHotelWS>();
				
				for(Recibo r:list) {
					ReciboHotelWS rws= new ReciboHotelWS();
					
					if(r.getCliente()!=null ) {
						
					   if(r.getCliente().getDocumentoNro()!=null) {	
					     rws.setClienteDocumento(r.getCliente().getDocumentoNro());
					   } 
					   if(r.getCliente().getId()>0) { 
					     rws.setClienteId(r.getCliente().getId());
					   } 
					   
					   if(r.getCliente().getRazonSocial()!=null) {
						 rws.setClienteNombre(r.getCliente().getRazonSocial());
					   }
					     
					}
					
					if(r.getReserva()!=null && r.getReserva().getIdReserva()!=null) {
						   rws.setComprobanteTipo("RES");
						   rws.setComprobanteAnio(r.getReserva().getAnio());
						   rws.setComprobanteLetra("");
						   rws.setComprobanteNumero(r.getReserva().getIdReserva().toString());
						   rws.setComprobanteSucursal(r.getSucursal());
					}
					
					if(r.getFactura()!=null && r.getFactura().getNumero()!=null) {
						rws.setComprobanteTipo(r.getFactura().getTipo());
						rws.setComprobanteLetra(r.getFactura().getLetra());
						rws.setComprobanteNumero(r.getFactura().getNumero());
						rws.setComprobanteSucursal(r.getFactura().getSucursal());
					}
					
					rws.setDescripcion(r.getDescripcion());
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(r.getFecha());
					rws.setFecha(cal);
					
					rws.setTotal(r.getTotal());
					rws.setSucursal(r.getSucursal());
					rws.setNumero(r.getNumero().toString());
					
					
					List<ReciboIngresoWS>ingresos =new ArrayList<ReciboIngresoWS>();
					
					for(FacturaIngreso fi:r.getIngresos()) {
						
						Ingreso i =  fi.getIngreso();
						
						ReciboIngresoWS riWS = new ReciboIngresoWS();
						riWS.setTipoIngreso("Efectivo");
						if (i.getTipo().equals("Cheque") || i.getTipo().equalsIgnoreCase("Transferencia Bancaria") || 
								i.getTipo().equalsIgnoreCase("Tarjeta Crédito") ||
								i.getTipo().equalsIgnoreCase("Tarjeta Débito")
								) {
						    riWS.setOperacionNro(i.getNumeroStr());
						}
			
						if (i.getTipo().equals("Cheque")) {
							riWS.setBancoId(i.getBanco().getId_banco());
						} else if (i.getTipo().equalsIgnoreCase("Tarjeta Crédito") ) {
							riWS.setBancoId(i.getBanco().getId_banco());
						} else if (i.getTipo().equalsIgnoreCase("Tarjeta Débito") ) {
							riWS.setBancoId(i.getBanco().getId_banco());
						}
						
						riWS.setImporte(i.getImporte().doubleValue());
						Calendar c1 = Calendar.getInstance();
						c1.setTime(i.getFecha());
						riWS.setFecha(c1);
						
						if (i.getTipo().equals("Cheque")) {
							riWS.setChequeEstado(Cheque.Estado.RECIBIDO);
							riWS.setTipoIngreso("Cheque");
						}
						
						if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") ) {
							riWS.setCuentaBancariaId(i.getCuentaBancaria().getId_cuenta_bcria());
							riWS.setTipoIngreso("Transferencia Bancaria");
							riWS.setTransferenciaTipo(DepositoBancario.ID_TIPO_TRANSFERENCIA);
						} else if (i.getTipo().equalsIgnoreCase("Tarjeta Crédito") ) {
							riWS.setTipoIngreso("Tarjeta Crédito");
							riWS.setTransferenciaTipo(TarjetaDebitoCredito.ID_TIPO_CREDITO);
						} else if (i.getTipo().equalsIgnoreCase("Tarjeta Débito") ) {
							riWS.setTipoIngreso("Tarjeta Débito");
							riWS.setTransferenciaTipo(TarjetaDebitoCredito.ID_TIPO_DEBITO);
						}
						if(i.getTipo().equals("Cheque")){
							riWS.setCuentaBancariaId(i.getCuentaBancaria().getId_cuenta_bcria());
						}
						
						if (i.getTipo().equalsIgnoreCase("Tarjeta Crédito") || i.getTipo().equalsIgnoreCase("Tarjeta Débito")) {
							riWS.setTarjetaEmisor(i.getEmisor());
							riWS.setTarjetaCuotas(i.getCuotas());
						}
						
						riWS.setSucursal(r.getSucursal());
						
						ingresos.add(riWS);
						
					}
					rws.setIngresos((ReciboIngresoWS[]) ingresos.toArray(new ReciboIngresoWS[ingresos.size()]));
					
					recibos.add(rws);
					
				}
				
				ReciboHotelWS[] rr = (ReciboHotelWS[]) recibos.toArray(new ReciboHotelWS[recibos.size()]);
				
				if(rr.length>0) {
				  rr=clienteWS.sincronizarRecibos(rr);
					
				  for(ReciboHotelWS r:rr) {
					
					if(r.getError()==null) {
						if(r.getFechaProceso()!=null) {
						   Recibo rec = new Recibo();
						   rec.setSucursal(r.getSucursal());
						   rec.setNumero(Long.parseLong(r.getNumero()));
						   rec.setFechaProceso(r.getFechaProceso().getTime());
						   HotelesServiceUtil.registraProcesoTransferenciaCentralRecibo(rec);
						}  
					}else {
						logger.debug("Sincronizo " + r.getSucursal() + " - " + r.getNumero()+"--->"+r.getError());
					}
				  }
				}  
				
			} catch (SystemException e) {
				   logger.debug("Error al generar Sincronizacion Recibos Hotel");
			}
						
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de Sincroniza Recibos Hotel");
		} catch (NumberFormatException e) {
			logger.error(e);
		} catch (SystemException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		
		
		
	}

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}

	    
}
