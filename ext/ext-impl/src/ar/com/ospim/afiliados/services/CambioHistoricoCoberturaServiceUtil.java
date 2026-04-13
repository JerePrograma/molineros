package ar.com.ospim.afiliados.services;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.util.ConnectionHelper;

public class CambioHistoricoCoberturaServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(CambioHistoricoCoberturaServiceUtil.class);
	
	public boolean aplicarCambios(Afiliado afi, List<AfiPlan> planes, List<AfiTercerizadoraServicio> tercerizadoras, 
			boolean cambioVigenDesde, Date vigenDesdeNueva, boolean cambioBajaFecha, Date bajaFechaNueva, 
			Integer idMotivoBaja, Integer idMotivoBajaNuevo, User user) throws Exception{
		
		boolean result = false;
		
		Connection con = ConnectionHelper.getConnectionForTransaction();
		
		try{
			
			if(cambioVigenDesde){
				EditarAfiliadoServiceUtil.updateVigenDesde(con, afi.getCuil_titular(), afi.getInte(), vigenDesdeNueva, user.getScreenName()); 
			}
			
			for (AfiPlan ap : planes){
				
				if(ap.getEstado()!=null){
					if(ap.getEstado().equals(AfiPlan.ESTADOS.ALTA)){
						PlanServiceUtil.getInstance().insertaAfiPlan(con, ap, user.getScreenName());
					}
					if(ap.getEstado().equals(AfiPlan.ESTADOS.MODIFICADO)){
						PlanServiceUtil.getInstance().actualizaAfiPlan(con, ap, user.getScreenName());
					}
					if(ap.getEstado().equals(AfiPlan.ESTADOS.BAJA)){
						PlanServiceUtil.getInstance().borrarAfiPlan(con, ap.getId().intValue(), user.getScreenName());
					}
				}
			}
			
			for (AfiTercerizadoraServicio ats : tercerizadoras) {
				
				if(ats.getEstado()!=null && ats.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.ALTA)){ //ats.isNuevo()
					TercerizadoraServiceUtil.grabaTercerizadora(afi.getCuil_titular(), afi.getInte(), ats.getTercerizadora().getId_tercerizadora(),
							ats.getFechaInicioPres(), ats.getFechaFinPres(), user, con);
				}else if(ats.getEstado()!=null && ats.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)){ //ats.isBorradoLogico()
					TercerizadoraServiceUtil.borraTercerizadora(afi.getCuil_titular(), afi.getInte(), ats.getTercerizadora().getId_tercerizadora(),
							ats.getFechaInicioPres(), user, con);
				}else if(ats.getEstado()!=null && ats.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.MODIFICADO)){
					TercerizadoraServiceUtil.actualizaTercerizadora(ats, user, con);
				}
			}
			
			if(cambioBajaFecha){
				EditarAfiliadoServiceUtil.updateBajaFecha(con, afi.getCuil_titular(), afi.getInte(), bajaFechaNueva, idMotivoBajaNuevo, user.getScreenName()); 
			}
			
			PlanServiceUtil.getInstance().ajustaIDsAfiPlan(con, afi.getCuil_titular());
			
			con.commit();
			result = true;
		}catch (Exception e) {
			_log.error(e);
			ConnectionHelper.rollback(con);
			throw new SystemException();
		}finally{
			ConnectionHelper.cerrar(con);
		}
		
		return result;
	}
}
