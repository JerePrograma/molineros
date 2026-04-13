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
import ar.com.ospim.global.beans.FinanciacionTurismo;
import ar.com.ospim.global.beans.Ingreso;
import ar.com.ospim.global.beans.TarjetaDebitoCredito;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.hoteles.beans.Reserva;
import ar.com.ospim.webservice.hoteles.ClienteHotelWS;
import ar.com.ospim.webservice.hoteles.FacturaDetalleWS;
import ar.com.ospim.webservice.hoteles.FacturaIngresoWS;
import ar.com.ospim.webservice.hoteles.FacturaWS;
import ar.com.ospim.webservice.hoteles.ReciboHotelWS;
import ar.com.ospim.webservice.hoteles.ReciboIngresoWS;
import ar.com.ospim.webservice.hoteles.SincronizarHotelesServiceLocator;
import ar.com.ospim.webservice.hoteles.SincronizarHotelesSoapBindingStub;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaDetalle;
import ar.com.uoma.facturacion.FacturaIngreso;
import ar.com.uoma.facturacion.services.FacturacionServiceUtil;

public class SincronizaFacturas extends AgendadoJava implements Serializable {

	
	private static final long serialVersionUID = 7517615672718612505L;
	private static Log logger = LogFactoryUtil.getLog(SincronizaFacturas.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		
		
		try{
			rac = ReportesServiceUtil.getConfiguracion();
			
			File configDir = new File(System.getProperty("catalina.base"), "conf");
			File configFile = new File(configDir, "liferay_schedulers.properties");
			
			InputStream stream = new FileInputStream(configFile);
			
			Properties props = new Properties();
			props.load(stream);
			String urlServicio = props.getProperty("hoteles_transferencia_url_service");
			SincronizarHotelesServiceLocator service = new SincronizarHotelesServiceLocator();
			if(urlServicio==null || "".equalsIgnoreCase(urlServicio)) {
				urlServicio=service.getSincronizarHotelesAddress();
			}
			
			SincronizarHotelesSoapBindingStub clienteWS = new SincronizarHotelesSoapBindingStub(new java.net.URL(urlServicio),service);
			
			List<Factura> list = new ArrayList<Factura>();
			
			
			try {
				list = FacturacionServiceUtil.getFacturasPendientesSincronizar();
				List<FacturaWS>facturas=new ArrayList<FacturaWS>();
				
				for(Factura r:list) {
					FacturaWS rws= new FacturaWS();
					
					Factura factura = FacturacionServiceUtil.getFactura(r.getId());
					rws.setId(new Long(r.getId()));
					
					ClienteHotelWS cliente = new ClienteHotelWS();
					
					cliente.setApellido(factura.getCliente().getApellido());
					cliente.setCategoriaIVA(factura.getCliente().getCategoriaIVA());
					cliente.setCuil(factura.getCliente().getCuil());
					cliente.setCuilTitular(factura.getCliente().getCuilTitular());
					cliente.setCuit(factura.getCliente().getCuit());
					cliente.setCuitSucursal(factura.getCliente().getSucursal());
					cliente.setDocumentoNro(factura.getCliente().getDocumentoNro());
					cliente.setDocumentoTipo(factura.getCliente().getDocumentoTipo());
					cliente.setInte(factura.getCliente().getInte());
					cliente.setNombre(factura.getCliente().getNombre());
					cliente.setObservaciones(factura.getCliente().getObservaciones());
					cliente.setRazonSocial(factura.getCliente().getRazonSocial());
					cliente.setTipo(factura.getCliente().getTipo().name());
					rws.setCliente(cliente);
					
					rws.setCae(r.getCae());
					Calendar cal = Calendar.getInstance();
					cal.setTime(r.getFecha());
					rws.setFecha(cal);
					
					Calendar calCae = Calendar.getInstance();
					calCae.setTime(r.getFechaCae());
					rws.setFechaCae(calCae);
					
					rws.setImporteNeto(factura.getImporteNeto());
					rws.setImporteTotal(factura.getImporteTotalCalculado());
					
					rws.setIva(factura.getIva());
					rws.setLetra(factura.getLetra());
					rws.setNumero(factura.getNumero());
					rws.setObservaciones(factura.getObservaciones());
					rws.setPresentaForm8001(factura.isPresentaForm8001());
					rws.setSucursal(factura.getSucursal());
					rws.setTipo(factura.getTipo());
					rws.setTotalExento(factura.getTotalExento());
					
					List<FacturaDetalleWS>detalles= new ArrayList<FacturaDetalleWS>();
					
					for(FacturaDetalle de:factura.getDetalles()) {
						FacturaDetalleWS dWS=new FacturaDetalleWS();
						dWS.setConcepto(de.getDetalle().getId());
						dWS.setPrecio(de.getPrecio());
						detalles.add(dWS);
					}
					rws.setDetalles((FacturaDetalleWS[]) detalles.toArray(new FacturaDetalleWS[detalles.size()]));
					
					
					List<FacturaIngresoWS>ingresos =new ArrayList<FacturaIngresoWS>();
					
					for(FacturaIngreso fi:factura.getIngresos()) {
						
						Ingreso i =  fi.getIngreso();
						
						FacturaIngresoWS riWS = new FacturaIngresoWS();
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
						}else if (i.getTipo().equalsIgnoreCase("FinanciacionTurismo") ) {
							riWS.setTipoIngreso("FinanciacionTurismo");
							riWS.setTransferenciaTipo(FinanciacionTurismo.TURISMO);
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
					rws.setIngresos((FacturaIngresoWS[]) ingresos.toArray(new FacturaIngresoWS[ingresos.size()]));
					
                    List<ReciboHotelWS>recibos= new ArrayList<ReciboHotelWS>();
					
					for(Recibo rec:factura.getRecibosAdelantos()) {
						ReciboHotelWS rWS=new ReciboHotelWS();
						rWS.setSucursal(rec.getSucursal());
						rWS.setNumero(rec.getNumero().toString());
						rWS.setTotal(rec.getTotal());
						recibos.add(rWS);
					}
					rws.setRecibosAdelantos((ReciboHotelWS[]) recibos.toArray(new ReciboHotelWS[recibos.size()]));
					
					facturas.add(rws);
					
				}
				
				FacturaWS[] rr = (FacturaWS[]) facturas.toArray(new FacturaWS[facturas.size()]);

				if(rr.length>0) {
				  rr=clienteWS.sincronizarFacturas(rr);
					
				  for(FacturaWS r:rr) {
					
					if(r.getError()==null) {
						if(r.getFechaProceso()!=null) {
						  FacturacionServiceUtil.registraProcesoTransferenciaCentralFactura(new Long(r.getId()),r.getIdCentral(),
								  r.getFechaProceso().getTime());
						}  
					}else {
						logger.debug("Sincronizo " + r.getSucursal() + " - " + r.getNumero()+"--->"+r.getError());
					}
				  }
				}  
			} catch (SystemException e) {
				   logger.debug("Error al generar Sincronizacion Facturas Hotel"+ e);
			}
						
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de Sincroniza Facturas Hotel");
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
