package ar.com.ospim.afiliados.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

import ar.com.ospim.afiliados.AfliadoYaTieneConyugeException;
import ar.com.ospim.afiliados.ConyugeNoPuedeSerSolteroException;
import ar.com.ospim.afiliados.HijoNoPuedeSerCasadoException;
import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.SituacionLaboral;
import ar.com.ospim.afiliados.exceptions.FaltanDatosAfiliadoException;
import ar.com.ospim.afiliados.exceptions.FaltanSituacionesLaboralesException;
import ar.com.ospim.afiliados.exceptions.FaltanTercerizadorasException;
import ar.com.ospim.afiliados.exceptions.TercNoCorrespPlanException;
import ar.com.ospim.global.beans.AportesMonotributo;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.webservice.service.AfiliadoServiceImpl;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;

public class AfiliadoServiceUtil {
	private static Log _log = LogFactoryUtil.getLog(AfiliadoServiceUtil.class);

	private static PlanServiceUtil planService = new PlanServiceUtil();
	

	public static void guardarOtrosDatos(ActionRequest actionRequest, HttpSession session,
			PortletSession portletSession, Afiliado afiInSession, List<SituacionLaboral> situLaborales, String accion,
			User user, Boolean bajaCascada, List<SituacionLaboral> situLaboralesAdd,
			List<SituacionLaboral> situLaboralesUp, List<AfiTercerizadoraServicio> tercerizadoras,
			Date fechaIngresoOriginal, String opciones, String preCarga, String idPreAfi, AfiPlan planActual,
			AfiPlan planNuevo,String updateAfiBorrado) throws NoSuchAfiliadoEntryException, SystemException, AfliadoYaTieneConyugeException,
			ConyugeNoPuedeSerSolteroException, HijoNoPuedeSerCasadoException, Exception, SQLException {

		Connection connection = null;
		ArrayList<String> mensajes = new ArrayList<String>();
		ArrayList<String> mensajesAux = new ArrayList<String>();
		
		try {
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);

			String cuilTitular = afiInSession.getCuil_titular();
			int inte = afiInSession.getInte();
			int id_ospim = 0;
			int id_amtima = 0;
			int id_uoma = 0;
			Afiliado afi = afiInSession;
			boolean exitoPlan = false;
			int idPlan = 0;

			if (bajaCascada == true) {
				// necesitaba recuperar la fecha de baja y el motivo de la baja
				// cascada, xq en el Afiliado en session no esta.
				for (SituacionLaboral sl : situLaborales) {
					if (sl.getEstado() != null && (sl.getEstado().equals("update") || sl.getEstado().equals("add")) && sl.isBaja_cascada()) {
						afi.setId_motivo_baja(sl.getMotivoBaja().getId_motivo_baja());
						afi.setBaja_fecha(sl.getFecha_baja());
						break;
					}
				}
			}

			// Add afiliado entry
			if (afiInSession != null && situLaborales.size() > 0 && tercerizadoras != null && tercerizadoras.size() > 0
					&& (accion.equals(Constants.ADD) || null != opciones && opciones.trim().equals("true"))) {

				try {
					EditarAfiliadoServiceUtil.cargaAfiliadoEntry(afiInSession, opciones, preCarga, idPreAfi,
							user.getScreenName(), afiInSession.getBaja_fecha(), afiInSession.getId_motivo_baja(),
							connection);
					
					if (bajaCascada == true) {
						integranteAporta(actionRequest, situLaborales);
					}

					guardarSitusLaborales(situLaborales, user.getScreenName(), connection);

					if (inte == 0) {
						// Guardamos plan nuevo y aportes nuevos
						exitoPlan = planService.insertaPlanyAportes(connection, planNuevo, user.getScreenName());

						idPlan = planNuevo.getPlan().getId();

						mensajesAux = guardarTercerizadora(afiInSession, user, tercerizadoras, /* idPlan, */ planActual,
								planNuevo, connection);
						
						mensajes.addAll(mensajesAux);

					}
					
					try {
						
						String codAreaTel = afiInSession.getDomicilioDefault() != null ? afiInSession.getDomicilioDefault().getCod_area_telefono() : null;
						String numeroTel  = afiInSession.getDomicilioDefault() != null ? afiInSession.getDomicilioDefault().getTelefono() : null;
						String codAreaCel = afiInSession.getDomicilioDefault() != null ? afiInSession.getDomicilioDefault().getCod_area_celular() : null;
						String numeroCel  = afiInSession.getDomicilioDefault() != null ? afiInSession.getDomicilioDefault().getCelular() : null;

						// traemos los teléfonos actuales
						List<Telefono> actuales = TelefonoServiceUtil.getTelefonos(
						    afiInSession.getCuil_titular(),
						    afiInSession.getInte()
						);

						final String TIPO_FIJO = "F";
						final String TIPO_CEL  = "C";

						// FIJO
						if ((codAreaTel != null && !codAreaTel.isEmpty()) ||
						    (numeroTel != null && !numeroTel.isEmpty())) {

						    Telefono telFijo = null;
						    for (Telefono t : actuales) {
						        if (TIPO_FIJO.equalsIgnoreCase(t.getTipo())) {
						            telFijo = t;
						            break;
						        }
						    }
						    if (telFijo == null) {
						        telFijo = new Telefono();
						    }

						    telFijo.setTipo(TIPO_FIJO);
						    telFijo.setCodigoArea(codAreaTel);
						    telFijo.setNumero(numeroTel);

						    if (telFijo.getId() > 0) {
						        TelefonoServiceUtil.actualizaTelefono(connection, afiInSession.getCuil_titular(), afiInSession.getInte(), telFijo, user.getScreenName());
						    } else {
						        TelefonoServiceUtil.insertaTelefono(connection, afiInSession.getCuil_titular(), afiInSession.getInte(), telFijo, user.getScreenName());
						    }
						}

						// CELULAR
						if ((codAreaCel != null && !codAreaCel.isEmpty()) ||
						    (numeroCel != null && !numeroCel.isEmpty())) {

						    Telefono telCel = null;
						    for (Telefono t : actuales) {
						        if (TIPO_CEL.equalsIgnoreCase(t.getTipo())) {
						            telCel = t;
						            break;
						        }
						    }
						    if (telCel == null) {
						        telCel = new Telefono();
						    }

						    telCel.setTipo(TIPO_CEL);
						    telCel.setCodigoArea(codAreaCel);
						    telCel.setNumero(numeroCel);

						    if (telCel.getId() > 0) {
						        TelefonoServiceUtil.actualizaTelefono(connection, afiInSession.getCuil_titular(), afiInSession.getInte(), telCel, user.getScreenName());
						    } else {
						        TelefonoServiceUtil.insertaTelefono(connection, afiInSession.getCuil_titular(), afiInSession.getInte(), telCel, user.getScreenName());
						    }
						}
					    
					} catch (Exception e) {
						 _log.error("Error guardando teléfonos del afiliado");
					}
					
					session.setAttribute(Constants.CMD, Constants.UPDATE);
				} catch (FaltanTercerizadorasException e) {
					throw e;
				} catch (TercNoCorrespPlanException e) {
					throw e;
				} catch (Exception e) {
					throw new FaltanDatosAfiliadoException(e);
				}
				// Update afiliado entry
			} else if (afiInSession != null && accion.equals(Constants.UPDATE)) {
				
				_log.debug("Por grabar situ laborales!");
				String editar = ParamUtil.getString(actionRequest, "editar");

				if (editar.equals("true") || situLaboralesUp.size() > 0 || situLaboralesAdd.size() > 0) {

					guardarSitusLaborales(situLaborales, user.getScreenName(), connection);

					if (bajaCascada == true) {
						integranteAporta(actionRequest, situLaborales);
					}
				}
				
				
				EditarAfiliadoServiceUtil.actualizaAfiliadoEntry(afi, preCarga, idPreAfi, user.getScreenName(),
						updateAfiBorrado, bajaCascada, connection); 
				
				try {
				    //teléfonos desde el request
				    String codAreaTel = ParamUtil.getString(
				        actionRequest, 
				        "cod_area_telefono", 
				        afiInSession.getDomicilioDefault() != null ? afiInSession.getDomicilioDefault().getCod_area_telefono() : null
				    );

				    String numeroTel = ParamUtil.getString(
				        actionRequest, 
				        "telefono", 
				        afiInSession.getDomicilioDefault() != null ? afiInSession.getDomicilioDefault().getTelefono() : null
				    );

				    String codAreaCel = ParamUtil.getString(
				        actionRequest, 
				        "cod_area_celular", 
				        afiInSession.getDomicilioDefault() != null ? afiInSession.getDomicilioDefault().getCod_area_celular() : null
				    );

				    String numeroCel = ParamUtil.getString(
				        actionRequest, 
				        "celular", 
				        afiInSession.getDomicilioDefault() != null ? afiInSession.getDomicilioDefault().getCelular() : null
				    );
				    
				    _log.debug("Tel fijo - codArea=" + codAreaTel + ", numero=" + numeroTel);
				    _log.debug("Celular - codArea=" + codAreaCel + ", numero=" + numeroCel);

				    // traemos los teléfonos actuales (solo activos)
				    List<Telefono> actuales = TelefonoServiceUtil.getTelefonos(
				        afiInSession.getCuil_titular(),
				        afiInSession.getInte()
				    );

				    final String TIPO_FIJO = "F";
				    final String TIPO_CEL  = "C";

				    // FIJO
				    if ((codAreaTel != null && !codAreaTel.isEmpty()) ||
				        (numeroTel != null && !numeroTel.isEmpty())) {

				        Telefono telFijo = null;
				        for (Telefono t : actuales) {
				            if (TIPO_FIJO.equalsIgnoreCase(t.getTipo())) {
				                telFijo = t;
				                break;
				            }
				        }
				        if (telFijo == null) {
				            telFijo = new Telefono();
				        }

				        telFijo.setTipo(TIPO_FIJO);
				        telFijo.setCodigoArea(codAreaTel);
				        telFijo.setNumero(numeroTel);

				        if (telFijo.getId() > 0) {
				            _log.info("actualiza telefono fijo id=" + telFijo.getId());
				            TelefonoServiceUtil.actualizaTelefono(connection, afiInSession.getCuil_titular(), afiInSession.getInte(), telFijo, user.getScreenName());
				        } else {
				            _log.info("inserta telefono fijo nuevo");
				            TelefonoServiceUtil.insertaTelefono(connection, afiInSession.getCuil_titular(), afiInSession.getInte(), telFijo, user.getScreenName());
				        }

				    } else {
				        // si los campos están vacios, dar de baja el telefono fijo existente
				        for (Telefono t : actuales) {
				            if (TIPO_FIJO.equalsIgnoreCase(t.getTipo()) && t.getBajaFecha() == null) {
				                _log.info("baja telefono fijo id=" + t.getId());
				                TelefonoServiceUtil.bajaTelefono(t.getId(), user.getScreenName());
				                break;
				            }
				        }
				    }

				    // CELULAR
				    if ((codAreaCel != null && !codAreaCel.isEmpty()) ||
				        (numeroCel != null && !numeroCel.isEmpty())) {

				        Telefono telCel = null;
				        for (Telefono t : actuales) {
				            if (TIPO_CEL.equalsIgnoreCase(t.getTipo())) {
				                telCel = t;
				                break;
				            }
				        }
				        if (telCel == null) {
				            telCel = new Telefono();
				        }

				        telCel.setTipo(TIPO_CEL);
				        telCel.setCodigoArea(codAreaCel);
				        telCel.setNumero(numeroCel);

				        if (telCel.getId() > 0) {
				            _log.info("atualiza telefono celular id=" + telCel.getId());
				            TelefonoServiceUtil.actualizaTelefono(connection, afiInSession.getCuil_titular(), afiInSession.getInte(), telCel, user.getScreenName());
				        } else {
				            _log.info("inserta telefono CELULAR nuevo");
				            TelefonoServiceUtil.insertaTelefono(connection, afiInSession.getCuil_titular(), afiInSession.getInte(), telCel, user.getScreenName());
				        }

				    } else {
				        // si los campos estan vacios, dar de baja el telefono celular existente
				        for (Telefono t : actuales) {
				            if (TIPO_CEL.equalsIgnoreCase(t.getTipo()) && t.getBajaFecha() == null) {
				                _log.info("baja telefono celular id=" + t.getId());
				                TelefonoServiceUtil.bajaTelefono(t.getId(), user.getScreenName());
				                break;
				            }
				        }
				    }

				} catch (Exception e) {
				    _log.error("Error guardando teléfonos del afiliado", e);
				}

				
				if (bajaCascada == true) {
					EditarAfiliadoServiceUtil.updateBajaFecha(connection, afi.getCuil_titular(), afi.getInte(),
							afi.getBaja_fecha(), afi.getId_motivo_baja(), user.getScreenName());
				}
				// grabar situ laboral
			
				// actualiza baja plan y aportes e insertanuevo plan y aportes
				if (planActual != null && planActual.getEstado() != null
						&& planActual.getEstado().equals(AfiPlan.ESTADOS.MODIFICADO)) {
					exitoPlan = planService.cambioDePlanyAportes(connection, planActual, planNuevo, bajaCascada,
							user.getScreenName());
				} else {
					exitoPlan = true;
				}
				// boolean flagCascada = false;
				// if (situLaboralesUp != null && situLaboralesUp.size() > 0) {
				// flagCascada = situLaboralesUp.get(situLaboralesUp.size() -
				// 1).isBaja_cascada();
				// }
				// if (situLaboralesAdd != null && situLaboralesAdd.size() > 0
				// && flagCascada == false) {
				// flagCascada = situLaboralesAdd.get(situLaboralesAdd.size() -
				// 1).isBaja_cascada();
				// }
				// if (!flagCascada) {
				// updatePlanYAportes(afiInSession, afiAporteList,
				// user.getScreenName(), idPlan, id_plan_omint,
				// listaTiposAporte, esCambioPlan, aportesNuevos,
				// connection, ParamUtil.getInteger(actionRequest,
				// "id_motivo_baja", 0));
				// }
				if (planNuevo != null) {
					idPlan = planNuevo.getPlan().getId();
				} else {
					idPlan = planActual.getPlan().getId();
				}
				if (afiInSession.getInte() == 0) {
					mensajesAux = guardarTercerizadora(afiInSession, user, tercerizadoras, planActual, planNuevo, connection);
					
					mensajes.addAll(mensajesAux);
				}

			} else {
				throw new FaltanDatosAfiliadoException();
			}

			// cargar los id ospim, uoma y amtima
			try {
				afi = EditarAfiliadoServiceUtil.getAfiliadoEntry(cuilTitular, inte, connection);
			} catch (Exception e) {
				afi = EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuilTitular, inte, connection);
			}
			Date baja_fecha_ospim = afi.getId_ospim_baja_fecha();
			if (baja_fecha_ospim == null || (baja_fecha_ospim != null
					&& baja_fecha_ospim.compareTo(new Date(System.currentTimeMillis())) > 0)) {
				id_ospim = afi.getId_ospim();
			}
			Date baja_fecha_uoma = afi.getId_uoma_baja_fecha();
			if (baja_fecha_uoma == null || (baja_fecha_uoma != null
					&& baja_fecha_uoma.compareTo(new Date(System.currentTimeMillis())) > 0)) {
				id_uoma = afi.getId_uoma();
			}
			Date baja_fecha_amtima = afi.getId_amtima_baja_fecha();
			if (baja_fecha_amtima == null || (baja_fecha_amtima != null
					&& baja_fecha_amtima.compareTo(new Date(System.currentTimeMillis())) > 0)) {
				id_amtima = afi.getId_amtima();
			}
			boolean exitoSL = false;
			// boolean exitoPlan = false;
			boolean exitoTerc = false;
			// chequear exito al cargar situ laboral
			exitoSL = validarSituLaboral(exitoSL, situLaborales, afi.getVigen_fecha());
			// chequear exito al cargar plan y aportes
			// exitoPlan = validarPlanAporte(aportesNuevos, exitoPlan,
			// afi.getVigen_fecha(),
			// aportesValidosParaFechaVigenciaOriginal);
			// chequear exito al cargar tercerizadora
			List<AfiTercerizadoraServicio> tercer = TercerizadoraServiceUtil.buscaTercerizadoras(cuilTitular, inte,
					connection);
//			List<AfiTercerizadoraServicio> tercer = TercerizadoraServiceUtil.historicoTercerizadoraDelAfiliado(cuil_titular);
			exitoTerc = validarTerc(exitoTerc, tercer, afi.getVigen_fecha());
			// valida situs laborales, planes-aportes y tercerizadoras

			validator(actionRequest, session, id_ospim, id_uoma, id_amtima, exitoSL, exitoPlan,
					exitoTerc, situLaborales, tercer, afi, bajaCascada, tercerizadoras, mensajes);

			connection.commit();

			// Recuperamos el AfiPlan (Plan y Aportes) para el afiliado titular
			AfiPlan afiPlan = PlanServiceUtil.getInstance().buscarUltimoPlanAportes(cuilTitular);
			afiInSession.setAfiPlan(afiPlan);

		} catch (Exception e) {
			ConnectionHelper.rollback(connection);
			_log.error(e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(connection);
		}
	}

	@Deprecated
	private static void validator(ActionRequest actionRequest, HttpSession session,
			int idOspim, int idUoma, int idAmtima, boolean exitoSL,
			boolean exitoPlan, boolean exitoTerc, List<SituacionLaboral> situLab, List<AfiTercerizadoraServicio> tercer,
			Afiliado afi, boolean bajaCascada, List<AfiTercerizadoraServicio> afiTercerizadorasOriginal, ArrayList<String> mensajes)
			throws Exception {
		if (exitoSL && exitoPlan && exitoTerc && !bajaCascada) {
			actionRequest.setAttribute("Exito",
					String.valueOf(idOspim) + "|" + String.valueOf(idUoma) + "|" + String.valueOf(idAmtima));
			
			if(mensajes.size()>0) {
				
				for (int i = 0; i < mensajes.size(); i++) {
					String msg = mensajes.get(i);
					SessionMessages.add(actionRequest, "tercerizadorasOK"+i);
					actionRequest.setAttribute("msgTercerizadoraOk"+i, msg);
				} 
			}
			
		} else if (bajaCascada) {
			int i = 0;
			boolean exito = false;
			while (i < situLab.size() && !exito) {
				if (situLab.get(i).getEstado() != null
						&& (situLab.get(i).getEstado().equals("add") || situLab.get(i).getEstado().equals("update"))) {
					if (situLab.get(i).getFecha_baja() != null) {
						Date fechaEgreso = situLab.get(i).getFecha_baja();
						int comp = fechaEgreso.compareTo(new Date(System.currentTimeMillis()));
						if (comp < 1) {
							actionRequest.setAttribute("ExitoBaja", "Los cambios se guardaron exitosamente!");
							exito = true;
						}
					}
				}
				i++;
			}
		} else if (!exitoSL && situLab.size() > 0 && !bajaCascada) {
			actionRequest.setAttribute("NoValido", "a Situación Laboral");
			throw new Exception();
			// } else if (!exitoPlan && afiAporteList.getPlan() != null) {
			// actionRequest.setAttribute("NoValido", " Plan");
			// throw new Exception();
		} else if (!exitoTerc && tercer.size() > 0) {
			actionRequest.setAttribute(WebKeysAfiliados.TERCERIZADORAS_EN_SESSION, afiTercerizadorasOriginal);
			session.setAttribute(WebKeysAfiliados.TERCERIZADORAS_EN_SESSION, afiTercerizadorasOriginal);
			actionRequest.setAttribute("NoValido", "a Tercerizadora");
			throw new Exception();
		}
	}

	private static boolean validarSituLaboral(boolean exitoSL, List<SituacionLaboral> situLab, Date vigenciaAfiliado) {
		if (situLab != null && situLab.size() > 0) {
			int i = 0;
			while (i < situLab.size() && !exitoSL) {
				Date fIngre = situLab.get(i).getFecha_ingre();
				Date fEgre = situLab.get(i).getFecha_baja();
				exitoSL = isVigente(fIngre, fEgre, vigenciaAfiliado);
				i++;
			}
		}
		return exitoSL;
	}

	private static boolean validarTerc(boolean exitoTerc, List<AfiTercerizadoraServicio> tercer,
			Date vigenciaAfiliado) {
		if (tercer != null && tercer.size() > 0) {
			int h = 0;
			while (h < tercer.size() && !exitoTerc) {
				Date fIngre = tercer.get(h).getFechaInicioPres();
				Date fEgre = tercer.get(h).getFechaFinPres();
				exitoTerc = isVigente(fIngre, fEgre, vigenciaAfiliado);
				h++;
			}
		}
		return exitoTerc;
	}

	private static boolean isVigente(Date fIngre, Date fEgre, Date vigenciaAfiliado) {
		boolean isValid = false;
		if (fIngre != null) {
			if (vigenciaAfiliado.compareTo(fIngre) >= 0 && (fEgre == null || vigenciaAfiliado.compareTo(fEgre) <= 0)) {
				isValid = true;
			}
		}
		return isValid;
	}

	private static void integranteAporta(ActionRequest actionRequest, List<SituacionLaboral> situLaborales) {
		int inte = 0;
		int aportante_titular = 0;
		Date baja_fecha = null;
		String cuil = null;
		String nombre = null;
		String apellido = null;
		int i = 0;
		boolean flag = false;
		while (i < situLaborales.size() && !flag) {
			inte = situLaborales.get(i).getAfiliado().getInte();
			aportante_titular = situLaborales.get(i).getAfiliado().getAportante_titular();
			baja_fecha = situLaborales.get(i).getAfiliado().getBaja_fecha();
			cuil = situLaborales.get(i).getAfiliado().getCuil();
			nombre = situLaborales.get(i).getAfiliado().getNombre();
			apellido = situLaborales.get(i).getAfiliado().getApellido();
			if (inte != 0 && aportante_titular == 1
					&& (baja_fecha == null || baja_fecha.compareTo(new Date(System.currentTimeMillis())) > 0)) {
				String msj = "Unificación de aportes, verifique los datos del inte: " + nombre + " " + apellido;
				String cuilS = "Cuil: " + cuil;
				actionRequest.setAttribute("integrante_aporta", msj);
				actionRequest.setAttribute("cuilS", cuilS);
				flag = true;
			}
			i++;
		}
	}

	public static ArrayList<String> guardarTercerizadora(Afiliado afi, User user,
			List<AfiTercerizadoraServicio> tercerizadoras, /* int id_plan, */ AfiPlan planActual, AfiPlan planNuevo,
			Connection connection) throws Exception {
		
		ArrayList<String> mensajes = new ArrayList<String>();
				
		if (tercerizadoras == null || tercerizadoras.size() == 0) {
			throw new FaltanTercerizadorasException(
					"La Tercerizadora del Afiliado no puede ser vacía: Por favor, ingrese una Tercerizadora de Servicios");
		} else {
			mensajes = TercerizadoraServiceUtil.editarTercerizadora(afi, user, tercerizadoras, /* id_plan, */ planActual,
					planNuevo, connection);
		}
		
		return mensajes;
	}

	public static void guardarSitusLaborales(List<SituacionLaboral> situLaborales, String user, Connection connection)
			throws Exception {
		if (situLaborales == null || situLaborales.size() == 0 || situLaboralTodoBajas(situLaborales)) {
			situLaborales = new ArrayList<SituacionLaboral>();
			throw new FaltanSituacionesLaboralesException();
		} else {
			if (situLaborales.size() > 0) {
				// ordeno la lista poniendo primero los que no tienen fecha de
				// baja
				Collections.sort(situLaborales, new Comparator<SituacionLaboral>() {
					public int compare(SituacionLaboral o1, SituacionLaboral o2) {
						if (o1.getFecha_baja() == null && o2.getFecha_baja() == null) {
							return 0;
						}
						if (o1.getFecha_baja() == null && o2.getFecha_baja() != null) {
							return -1;
						} else if (o1.getFecha_baja() != null && o2.getFecha_baja() == null) {
							return 1;
						} else {
							return o1.getFecha_baja().compareTo(o2.getFecha_baja());
						}
					}
				});
			}
			SituLaboralServiceUtil.editarSituLaboral(situLaborales, user, connection);
		}
	}

	private static boolean situLaboralTodoBajas(List<SituacionLaboral> situLaborales) {
		for (SituacionLaboral situ : situLaborales) {
			if (situ.getFecha_baja_logica() == null) {
				return false;
			}
		}
		return true;
	}
	
	
	public static Integer permanenciaDesdeUltimoLaboral(String cuil, Integer inte,String categoriasAEvaluar,Date fechaOspim) throws Exception {
		Integer res=0;
		Date hoy= new Date();
		List<SituacionLaboral>laborales=SituLaboralServiceUtil.buscaSituLaboral(cuil,inte);
		SituacionLaboral ultimoLaboral = new SituacionLaboral();
		for(SituacionLaboral l:laborales) {
		   if((l.getFecha_baja()==null || hoy.before(l.getFecha_baja())) && (ultimoLaboral.getFecha_ingre()==null || ultimoLaboral.getFecha_ingre().before(l.getFecha_ingre())) ) {
			  ultimoLaboral=l; 
		   }
		}
		   
		if(ultimoLaboral!=null && ultimoLaboral.getFecha_ingre()!=null &&
				categoriasAEvaluar.contains(String.valueOf(ultimoLaboral.getId_categoria()))) {// Monotributistas
			   res= DateUtils.diferenciaDias(DateUtils.toCalendar(ultimoLaboral.getFecha_ingre()), DateUtils.toCalendar(fechaOspim));
		}
		
		if(ultimoLaboral.getFecha_ingre()==null) res=-1;
		
		return res;
	}

}
