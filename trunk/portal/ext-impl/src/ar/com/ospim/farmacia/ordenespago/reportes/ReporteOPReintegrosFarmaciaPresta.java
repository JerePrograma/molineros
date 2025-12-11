package ar.com.ospim.farmacia.ordenespago.reportes;

import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.farmacia.beans.ReporteOrdenPagoReintegrosFarmacia;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.liquidaciones.beans.ReporteOrdenPagoReintegros;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteOPReintegros;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteOPReintegrosFarmaciaPresta extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteOPReintegrosFarmaciaPresta.class);

	public static HSSFWorkbook generaReporteOPReintegrosFarmaciaPresta(
			HttpServletRequest req, HttpServletResponse res) {
		String idListaString = ParamUtil.getString(req, "idLista");
		
		HSSFWorkbook wb = new HSSFWorkbook();

		List<ReporteOrdenPagoReintegrosFarmacia> listFarmacia = null;
		List<ReporteOrdenPagoReintegros> list= null;
		try {
			if (null != idListaString && !idListaString.equals("")) {
				idListaString=idListaString.replace("[", "");
				idListaString=idListaString.replace("]", "");
				StringTokenizer listas = new StringTokenizer(idListaString,",");
				while (listas.hasMoreTokens()) {
					String listaPrestacion = listas.nextToken();
					StringTokenizer listaPrestacionTkn = new StringTokenizer(listaPrestacion,"|");
					while (listaPrestacionTkn.hasMoreTokens()) {
						int idLista = Integer.parseInt(listaPrestacionTkn
								.nextToken().trim());
						String tipo = listaPrestacionTkn.nextToken();
						if (tipo.equals("FARMACIA")) {
							listFarmacia = OrdenPagoServiceUtil
									.getReintegrosFarmaciaFromListaId(idLista);
							ReporteOPReintegrosFarmacia.addFarmaciaSheet(listFarmacia,idLista,wb);
							
						}else{
							list = OrdenPagoServiceUtil
									.getReintegrosFromListaId(idLista);
							ReporteOPReintegros.addReintegroSheet(list,idLista,wb);
						}
					}

				}
			}
		} catch (NoSuchReintegroEntryException nsree) {
			list = null;
		} catch (SystemException e) {
			_log.debug(e);
		}
		return wb;
	}

	

	public static HSSFWorkbook generaReporteOPReintegrosFarmaciaPresta(String idListaString) {
		
		HSSFWorkbook wb = new HSSFWorkbook();

		List<ReporteOrdenPagoReintegrosFarmacia> listFarmacia = null;
		List<ReporteOrdenPagoReintegros> list= null;
		try {
			if (null != idListaString && !idListaString.equals("")) {
				idListaString=idListaString.replace("[", "");
				idListaString=idListaString.replace("]", "");
				StringTokenizer listas = new StringTokenizer(idListaString,",");
				while (listas.hasMoreTokens()) {
					String listaPrestacion = listas.nextToken();
					StringTokenizer listaPrestacionTkn = new StringTokenizer(listaPrestacion,"|");
					while (listaPrestacionTkn.hasMoreTokens()) {
						int idLista = Integer.parseInt(listaPrestacionTkn
								.nextToken().trim());
						String tipo = listaPrestacionTkn.nextToken();
						if (tipo.equals("FARMACIA")) {
							listFarmacia = OrdenPagoServiceUtil
									.getReintegrosFarmaciaFromListaId(idLista);
							ReporteOPReintegrosFarmacia.addFarmaciaSheet(listFarmacia,idLista,wb);
							
						}else{
							list = OrdenPagoServiceUtil
									.getReintegrosFromListaId(idLista);
							ReporteOPReintegros.addReintegroSheet(list,idLista,wb);
						}
					}

				}
			}
		} catch (NoSuchReintegroEntryException nsree) {
			list = null;
		} catch (SystemException e) {
			_log.debug(e);
		}
		return wb;
	}
	
		
	}
