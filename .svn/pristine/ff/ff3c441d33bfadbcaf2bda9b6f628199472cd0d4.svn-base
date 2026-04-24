package ar.com.ospim.tesoreria.action;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;
import org.postgresql.util.PSQLException;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.procesaArchivos.ProcesaArchivos;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.BalanceSumasYSaldos;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;
import ar.com.ospim.tesoreria.reportes.ReporteContabilidadBalanceSumasSaldosExcel;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

public class AsientosEspecialesAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		String tipo=actionRequest.getParameter("tipo");
		
		if("E".equalsIgnoreCase(tipo)) {
			
			
			String entidad="O";
			Integer entidadNro=WebKeysGlobal.OSPIM;
			List<String>errores = new ArrayList<String>();
			
			if(actionResponse.getNamespace().equals("_FAR_1_")){
				entidad="A";
				entidadNro=WebKeysGlobal.AMTIMA;
			}else if(actionResponse.getNamespace().equals("_UOM_1_")){
				entidad="U";
				entidadNro=WebKeysGlobal.UOMA;
		   }
			
		   HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();

		   UploadPortletRequest uploadReq = null;
		   try {
		      uploadReq = PortalUtil
					.getUploadPortletRequest(actionRequest);
		   }catch(Exception xx) {}   
		
		   Asiento asiento = (Asiento) session.getAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION); 
		   if(asiento==null) {		
			   asiento= new Asiento();
		   }
		   String htaString = actionRequest.getParameter("ejercicio_hasta");
		   String ddString = actionRequest.getParameter("ejercicio_desde");
		   String ejercicio = actionRequest.getParameter("ejercicio");
		   if (StringUtils.isNotBlank(ejercicio) && entidadNro!=WebKeysGlobal.AMTIMA) {
				ddString = "01/08/" + Integer.valueOf(ejercicio.split("-")[0]);
				htaString = "31/07/" + Integer.valueOf(ejercicio.split("-")[1]);
		   }
		   
		   if (StringUtils.isNotBlank(ejercicio) && entidadNro==WebKeysGlobal.AMTIMA) {
				ddString = "01/07/" + Integer.valueOf(ejercicio.split("-")[0]);
				htaString = "30/06/" + Integer.valueOf(ejercicio.split("-")[1]);
		   }
		   
		   Date fecha = null;
		   SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		   fecha=sdf.parse(ddString);
		   actionRequest.setAttribute("ejercicio_desde",ddString);
		   actionRequest.setAttribute("ejercicio_hasta",htaString);
		   actionRequest.setAttribute("ejercicio",ejercicio);
		   /*
			if(fecha==null){
				String ejercicio_seleccionado=(String)session.getAttribute("ejercicio_seleccionado");
				if(ejercicio_seleccionado!=null && !ejercicio_seleccionado.trim().equals("")){
					Calendar fechaCal=Calendar.getInstance();
					fechaCal.set(Calendar.YEAR,Integer.parseInt(ejercicio_seleccionado.substring(0, 4)));
					fechaCal.set(Calendar.MONTH,7);
					fechaCal.set(Calendar.DAY_OF_MONTH,1);
					fecha=fechaCal.getTime();
				}
			}
            */
			if(fecha==null){
				fecha=new Date();
			}
			actionRequest.setAttribute("planCuentas",
					TraeListasServiceUtil.getPlanCuentasImputables(fecha, entidadNro));
		
		   try {
			if(uploadReq!=null && uploadReq.getFileName("archivo")!=null) {
			   String fileName = uploadReq.getFileName("archivo").toLowerCase();
			   Boolean proceso=false;
			   if (fileName != null) {
				File zip = uploadReq.getFile("archivo");
				if ( fileName.endsWith(".xls")) {
					proceso=true;
					List<PlanCuentas>pcs=(List<PlanCuentas>) actionRequest.getAttribute("planCuentas");
					List<Detalle> det = new ProcesaArchivos().procesarAsientoXLS(actionRequest, zip,fileName,entidad,pcs);
					asiento.setDetalle(det);
				}	
			   }
			}
			session.setAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION,asiento);
		  } catch (Exception e) {
			actionRequest.setAttribute("asiento", asiento);
			SessionErrors.add(actionRequest, e.getClass().getName());
			if(e.getClass().getName().contains("OldExcelFormatException")) {
			   errores.add("Error en Versión de Excel - Debe ser 97-2003 ");
			}
			if(errores.size()>0) {
			   actionRequest.setAttribute("errores", errores);
			}   
		  }

		  if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		  }
		
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		
		PortletSession portletSession = renderRequest.getPortletSession();
		
		Asiento asiento = null;
		
		String htaString = renderRequest.getParameter("ejercicio_hasta");
		String ddString = renderRequest.getParameter("ejercicio_desde");
		String ejercicio = renderRequest.getParameter("ejercicio");
		String fechaString = renderRequest.getParameter("fecha");
		String descripcion = renderRequest.getParameter("descripcion");
	
		if (StringUtils.isNotBlank(ejercicio) && entidad!=WebKeysGlobal.AMTIMA) {
			ddString = "01/08/" + Integer.valueOf(ejercicio.split("-")[0]);
			htaString = "31/07/" + Integer.valueOf(ejercicio.split("-")[1]);
		}
		if (StringUtils.isNotBlank(ejercicio) && entidad==WebKeysGlobal.AMTIMA) {
			ddString = "01/07/" + Integer.valueOf(ejercicio.split("-")[0]);
			htaString = "30/06/" + Integer.valueOf(ejercicio.split("-")[1]);
		}

		Date fecha = null;//new Date();
		if(fecha==null){
			String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
			if(ejercicio_seleccionado!=null && !ejercicio_seleccionado.trim().equals("")){
				Calendar fechaCal=Calendar.getInstance();
				fechaCal.set(Calendar.YEAR,Integer.parseInt(ejercicio_seleccionado.substring(0, 4)));
				fechaCal.set(Calendar.MONTH,7);
				fechaCal.set(Calendar.DAY_OF_MONTH,1);
				fecha=fechaCal.getTime();
			}
		}
		if(fecha==null){
			fecha=new Date();
		}
		
		renderRequest.setAttribute("planCuentas",
				TraeListasServiceUtil.getPlanCuentasImputables(fecha, entidad));
		List<String> errores = new ArrayList<String>();
		renderRequest.setAttribute("errores",errores);
		
		String cmd = renderRequest.getParameter("cmd");
		if(cmd==null) cmd = ParamUtil.get(renderRequest, "cmd","");
		if(cmd!=null) {
			
			if("newApertura".equals(cmd)) {
				 asiento = new Asiento();
				 asiento.setDetalle(new ArrayList<Detalle>());
				 session.setAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION,asiento);
				 return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.asientos_apertura_cierre"));
			}else if("clean".equals(cmd)) {
				 if(asiento==null) asiento=new Asiento();
				 asiento.setDetalle(new ArrayList<Detalle>());
				 session.setAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION,asiento);
				 return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.editar_asientos_apertura_cierre_search_result"));
			} else if("precargar".equals(cmd)) {
				String tipo = ParamUtil.getString(renderRequest,"tipo");
				asiento=(Asiento) session.getAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION);
				
				String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
				asiento.setEjercicioDesdeString(ddString);
				asiento.setEjercicioHastaString(htaString);
				if(fechaString!=null) {
				   asiento.setFechaString(fechaString);
				}
				
				if(ejercicio_seleccionado!=null && !ejercicio_seleccionado.trim().equals("")){
					Calendar fechaCal=Calendar.getInstance();
					fechaCal.set(Calendar.YEAR,Integer.parseInt(ejercicio_seleccionado.substring(0, 4)));
					fechaCal.set(Calendar.MONTH,7);
					fechaCal.set(Calendar.DAY_OF_MONTH,1);
					fecha=fechaCal.getTime();
				}
				
				
				Calendar desdeEjercicio = DateUtils.getDesdeEjercicio(renderRequest, entidad);
				Calendar hastaEjercicio = DateUtils.getHastaEjercicio(renderRequest, entidad);
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				desdeEjercicio.setTime(sdf.parse(ddString));
				hastaEjercicio.setTime(sdf.parse(htaString));
				asiento.setDetalle(new ArrayList<Detalle>());
				if("A".equals(tipo)) {
					desdeEjercicio.add(Calendar.YEAR, -1);
					hastaEjercicio.add(Calendar.YEAR, -1);
					List<PlanCuentas>pcs=(List<PlanCuentas>) renderRequest.getAttribute("planCuentas");
					List<Asiento>lcierre =AsientoServiceUtil.buscarAsientoCierre(desdeEjercicio.getTime(), hastaEjercicio.getTime(), entidad);
					 if(lcierre==null || lcierre.size()==0) {
						   errores.add("Verifique que exista un asiento de cierre del Ejercicio Anterior Generado por este proceso");
						   renderRequest.setAttribute("errores",errores);
					}else if(lcierre.size()>1) {
						    errores.add("Existe más de un asiento de cierre del Ejercicio Anterior. Elimine uno");
						   renderRequest.setAttribute("errores",errores);
					}else {
						String[] rubros=null;
						String[] ctaCambio=null;
						if(entidad==WebKeysGlobal.OSPIM) {
							rubros =TraeListasServiceUtil.getSystemConfig("RUBROS_APERTURA_OSPIM").split(";");
							ctaCambio =TraeListasServiceUtil.getSystemConfig("CUENTAS_CAMBIO_APERTURA_OSPIM").split(";");
						}else if(entidad==WebKeysGlobal.AMTIMA) {
							rubros =TraeListasServiceUtil.getSystemConfig("RUBROS_APERTURA_AMTIMA").split(";");
							ctaCambio =TraeListasServiceUtil.getSystemConfig("CUENTAS_CAMBIO_APERTURA_AMTIMA").split(";");
						}if(entidad==WebKeysGlobal.UOMA) {
							rubros =TraeListasServiceUtil.getSystemConfig("RUBROS_APERTURA_UOMA").split(";");
							ctaCambio =TraeListasServiceUtil.getSystemConfig("CUENTAS_CAMBIO_APERTURA_UOMA").split(";");
						}	
						
					    List<String> listaRubros = Arrays.asList(rubros);
					    Integer pase=0;
					    String cBus="";
						Asiento ac=lcierre.get(0);
						for(Detalle d:ac.getDetalle()) {
							for(String s:listaRubros) {
							   if(s.trim().equals(d.getCuenta().getNumero().substring(0,s.trim().length()))) {
							      Detalle dNew = new Detalle();
							      PlanCuentas cuenta=d.getCuenta();
							      for(int i=0;i<=ctaCambio.length-1;i++) {
							    	  if(ctaCambio[i].contains(cuenta.getNumero())) {
							    		  cBus=ctaCambio[i].split("==")[1];
							    		  for(PlanCuentas p :pcs) {
							    			  if(cBus.trim().equals(p.getNumero())) {
							    				  cuenta=p;
							    				  break;
							    			  }
							    		  }
							    		  break; 
							    	  }
							      }
							      dNew.setCuenta(cuenta);
							      dNew.setDebe(d.getHaber());
							      dNew.setHaber(d.getDebe());
							      dNew.setPase(++pase);
							      dNew.setId(pase);
							      asiento.getDetalle().add(dNew);
							      break;
						       }
							}
						}
					}
				}else if("C".equals(tipo)) {
				   List<BalanceSumasYSaldos> balanceSumasYSaldos = AsientoServiceUtil
						.buscarBalanceSumasYSaldos(desdeEjercicio.getTime(),
								hastaEjercicio.getTime(), true,
								true, false, entidad);

				   List<BalanceSumasYSaldos> saldosIniciales = null;
				    saldosIniciales = BalanceSumasYSaldos
						.buildBalanceFromAsientos(AsientoServiceUtil
								.buscarAsientosConDetalle(desdeEjercicio.getTime(),
										hastaEjercicio.getTime(), 1, 1, true, true, entidad));
				   mergearCuentas(balanceSumasYSaldos, saldosIniciales);
				   Collections.sort(balanceSumasYSaldos);
				   asiento.setDetalle(new ArrayList<Detalle>());
				   Integer pase=0;
				   Integer pos=0;
				   List<PlanCuentas>pcs=(List<PlanCuentas>) renderRequest.getAttribute("planCuentas");
				   for(BalanceSumasYSaldos b :balanceSumasYSaldos) {
					Detalle d = new Detalle();
					d.setPase(++pase);
					d.setId(pase);
					pos=pcs.indexOf(new PlanCuentas(b.getNumeroCuenta(),b.getDescripcionCuenta()));
					if(pos!=-1) {
						d.setCuenta(pcs.get(pos));	
					}
					BigDecimal sdo =b.getDebe().subtract(b.getHaber());
					if(sdo.compareTo(BigDecimal.ZERO)>0 ) {
							d.setDebe(BigDecimal.ZERO);
							d.setHaber(sdo);	
					}else {
							d.setDebe(sdo.abs());
							d.setHaber(BigDecimal.ZERO);
					}
					if(sdo.compareTo(BigDecimal.ZERO)!=0)
					     asiento.getDetalle().add(d);
					
				   }
				   if(asiento.getDetalle()==null || asiento.getDetalle().size()==0) {
					   
					   errores.add("Verifique que no exista un asiento de cierre. Los saldos de las cuentas estan en cero");
					   
					   renderRequest.setAttribute("errores",errores);
				   }
				   
				}else if("I".equals(tipo)) {
					
					
					List<BalanceSumasYSaldos> balanceSumasYSaldos = AsientoServiceUtil
							.buscarBalanceSumasYSaldos(desdeEjercicio.getTime(),
									hastaEjercicio.getTime(), true,
									true, false, entidad);

					   List<BalanceSumasYSaldos> saldosIniciales = null;
					    saldosIniciales = BalanceSumasYSaldos
							.buildBalanceFromAsientos(AsientoServiceUtil
									.buscarAsientosConDetalle(desdeEjercicio.getTime(),
											hastaEjercicio.getTime(), 1, 1, true, true, entidad));
					   mergearCuentas(balanceSumasYSaldos, saldosIniciales);
					   Set<String> filtroCuentas = new HashSet<String>();				   
					   ReporteContabilidadBalanceSumasSaldosExcel.ajustarCuentas(balanceSumasYSaldos,desdeEjercicio.getTime(),hastaEjercicio.getTime(),desdeEjercicio.getTime(),hastaEjercicio.getTime(),filtroCuentas,entidad);
					   Collections.sort(balanceSumasYSaldos);
					   
					   asiento.setDetalle(new ArrayList<Detalle>());
					   Integer pase=0;
					   Integer pos=0;
					   List<PlanCuentas>pcs=(List<PlanCuentas>) renderRequest.getAttribute("planCuentas");
					   
					   for(BalanceSumasYSaldos b :balanceSumasYSaldos) {
						Detalle d = new Detalle();
						d.setPase(++pase);
						d.setId(pase);
						pos=pcs.indexOf(new PlanCuentas(b.getNumeroCuenta(),b.getDescripcionCuenta()));
						if(pos!=-1) {
							d.setCuenta(pcs.get(pos));	
						}
						BigDecimal sdo =b.getDebeAjustado().subtract(b.getHaberAjustado());
						if(sdo.compareTo(BigDecimal.ZERO)!=0) {
						   BigDecimal sdoSinAjustar=b.getDebe().subtract(b.getHaber());
						   sdo=sdo.subtract(sdoSinAjustar);
						
						   if(sdo.compareTo(BigDecimal.ZERO)>0 ) {
								d.setDebe(sdo);
								d.setHaber(BigDecimal.ZERO);	
						   }else {
								d.setDebe(BigDecimal.ZERO);
								d.setHaber(sdo.abs() );
						   }
						   if(sdo.compareTo(BigDecimal.ZERO)!=0) {
							   asiento.getDetalle().add(d);
						   }	   
						}     
						
					   }
					   if(asiento.getDetalle()==null || asiento.getDetalle().size()==0) {
						   
						   errores.add("Verifique que esten cargados los coeficientes para el ajuste y las cuentas a ajustar esten marcadas para que sean incluídas en el proceso");
						   
						   renderRequest.setAttribute("errores",errores);
					   }
				}
				 
				session.setAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION,asiento);
				return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.editar_asientos_apertura_cierre_search_result"));
			} else if("agregar".equals(cmd)) {
				Integer cuenta = ParamUtil.getInteger(renderRequest,"cuenta");
				String debeHaber = ParamUtil.getString(renderRequest,"debeHaber");
				Double importe = ParamUtil.getDouble(renderRequest,"importe");
				
				asiento=(Asiento) session.getAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION);
				Detalle d = new Detalle();
				List<PlanCuentas>pcs=(List<PlanCuentas>) renderRequest.getAttribute("planCuentas");
				
				for(PlanCuentas c:pcs) {
					if(cuenta==c.getId()) {
						d.setCuenta(c);
						break;
					}
				}
				if("D".equals(debeHaber)) {
					d.setDebe(new BigDecimal(importe));
					d.setHaber(BigDecimal.ZERO);
				}else {
					d.setDebe(BigDecimal.ZERO);
					d.setHaber(new BigDecimal(importe));
				}
				
				if(d.getCuenta()!=null) {
					Integer maximo=mayorPase(asiento.getDetalle());
					d.setId(maximo+1);
					d.setPase(maximo+1);
				}
				
				asiento.getDetalle().add(d);
				session.setAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION,asiento);
				return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.editar_asientos_apertura_cierre_search_result"));
			}else if("save".equals(cmd)) {
				String tipo = ParamUtil.getString(renderRequest,"tipo");
				SessionMessages.clear(renderRequest);
				SessionErrors.clear(renderRequest);
				asiento = (Asiento) session.getAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION);
				asiento.setFechaString(fechaString);
				asiento.setDescripcion(descripcion);
				if (DateUtils.compararFechasTruncarEnDia(
						ContabilidadServiceUtil.getFechaCierreAsientos(entidad),
						asiento.getFecha()) > 0) {
					throw new FechaMenorACierreContableException();
				}

				User user = PortalUtil.getUser(renderRequest);
				if (asiento.getId() == 0) {
					if(asiento.getDetalle().size()>0) {
						
						List<Detalle> lista = asiento.getDetalle();
						Integer pase=1;
						for(Detalle d:lista){
							d.setPase(pase++);
						}
						asiento.setDetalle(lista);
						try {
						
					       AsientoServiceUtil.save(asiento, user, entidad);
					    
					       if("C".equals(tipo)) {
					           AsientoServiceUtil.updateCierre(asiento, entidad);
					       }else if("A".equals(tipo)) {
						       AsientoServiceUtil.updateApertura(asiento, entidad);
						   }
						   String successMessage = ParamUtil.getString(renderRequest,
								"successMessage");
						   SessionMessages.add(renderRequest, "request_processed",	successMessage);
					  	   session.setAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION,new Asiento());
						}catch(PSQLException e) {
							asiento.setId(0);
							SessionErrors.add(renderRequest, e.getMessage());
						}
					}else {
						SessionErrors.add(renderRequest, "No tiene registros preparados para  generar el asiento");
					}
					
				} 
				
			    return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.asientos_apertura_cierre"));
			}else if("delete".equals(cmd)) {
				Integer id = ParamUtil.getInteger(renderRequest,"id");
				List<Detalle> lista = new ArrayList<Detalle>();
				asiento=(Asiento) session.getAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION);
				for (Detalle d : asiento.getDetalle()) {
					if(d.getId()!=id) {
						lista.add(d);
					}
				}
				asiento.setDetalle(lista);
  			    session.setAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION,asiento); 
				return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.editar_asientos_apertura_cierre_search_result")); 
			}else if("uploadxls".equals(cmd)) {
				
				 asiento = (Asiento) session.getAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION);
				 if(fechaString!=null && !"".equals(fechaString)) {
					asiento.setFechaString(fechaString);
				 }
				 if(descripcion!=null) {
				    asiento.setDescripcion(descripcion);
				 }
				 asiento.setEjercicioDesdeString(ddString);
				 asiento.setEjercicioHastaString(htaString);
				 renderRequest.setAttribute("ejercicio_desde", ddString);
				 renderRequest.setAttribute("ejercicio_hasta", htaString);
				 session.setAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION,asiento);
				 
				 Boolean er=false;
				 for(Detalle d:asiento.getDetalle()) {
						if(d.getCuenta().getId()==0) {
							er=true;
						}
				 }
				 if(er) {
					errores.add("Existen cuentas que no fueron  encontradas en el plan de cuentas");
					renderRequest.setAttribute("errores", errores);
				 }
				 
				 
				 return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.asientos_apertura_cierre"));
				 
			}	
		}

		
		
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.contabilidad.editar_asientos"));
	}
	
	private static void mergearCuentas(
			List<BalanceSumasYSaldos> balanceSumasYSaldos,
			List<BalanceSumasYSaldos> saldosIniciales) {
		// agrego todas las cuentas para las que exista un saldo
		// inicial/anterior pero que no existan asientos para el periodo dado
		if (saldosIniciales != null) {
			for (BalanceSumasYSaldos saldos : saldosIniciales) {
				BalanceSumasYSaldos balanceSaldoInicial = new BalanceSumasYSaldos(
						new PlanCuentas(saldos.getNumeroCuenta(),
								saldos.getDescripcionCuenta()));
				int indexOf = balanceSumasYSaldos.indexOf(balanceSaldoInicial);
				if (indexOf == -1) {
					balanceSumasYSaldos.add(saldos);
				} else {
					BalanceSumasYSaldos balanceAActualizar = balanceSumasYSaldos
							.get(indexOf);
					balanceAActualizar.setDebe(balanceAActualizar.getDebe()
							.add(saldos.getDebe()));
					balanceAActualizar.setHaber(balanceAActualizar.getHaber()
							.add(saldos.getHaber()));
				}
			}
		}
	}
	
	private Integer mayorPase(List<Detalle> d) {
		Integer maximo=0;
		for(Detalle det : d) {
			if(maximo<det.getId()) {
		       maximo=det.getId();		
			}
		}
		return maximo;
	}
}
