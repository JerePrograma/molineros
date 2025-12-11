package ar.com.uoma.unidad_operativa.services;

import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.uoma.beans.Incidente;
import ar.com.uoma.beans.IncidenteComparator;
import ar.com.uoma.beans.IncidenteTotal;
import ar.com.uoma.unidad_operativa.BusquedaIncidentesUnidadOpeFiltro;

public class UnidadOperativaServiceUtil {
	private static Log logger = LogFactoryUtil
			.getLog(UnidadOperativaServiceUtil.class);

	private static UnidadOperativaServiceImpl instance = null;

	public static UnidadOperativaServiceImpl getInstance() {
		if (null == instance) {
			instance = new UnidadOperativaServiceImpl();
		}
		return instance;
	}

	
	public static Incidente grabarIncidente(Incidente incidente, User user) throws SystemException {
		int id_incidente=getInstance().grabarIncidente(incidente, user);
		return getInstance().buscarIncidente(id_incidente);
	}
	public static Incidente buscarIncidente(int id_incidente) throws SystemException {
		return getInstance().buscarIncidente(id_incidente);
	}
	
	public static Incidente editarIncidente(Incidente incidente, User user) throws SystemException {
		IncidenteComparator comparator=new IncidenteComparator();
		Incidente incidenteViejo=buscarIncidente(incidente.getIdIncidente());
		//Si son diferentes tengo que actualizar el incidente
		int compara=comparator.compare(incidenteViejo, incidente);
		if(compara!=0){
			logger.debug("editando");
			getInstance().editarIncidente(incidente, user, compara);
		}
		return incidente;
		
	}
	public static List<IncidenteTotal> buscarIncidentes(BusquedaIncidentesUnidadOpeFiltro filtro) throws SystemException{
		logger.debug("buscando incidentes Unidad Operativa");
		return getInstance().buscarIncidentes(filtro);
	}

}
