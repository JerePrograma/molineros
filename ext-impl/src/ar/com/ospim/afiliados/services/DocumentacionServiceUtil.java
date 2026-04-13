package ar.com.ospim.afiliados.services;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiDocumentacion;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * <a href="DocumentacionServiceUtil .java.html"><b><i>View Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.afiliados.services.DocumentacionServiceUtil </code> bean.
 * The static methods of this class calls the same methods of the bean instance.
 * It's convenient to be able to just write one line to call a method on a bean
 * instead of writing a lookup call and a method call.
 * </p>
 * 
 * @author Federico Brachi
 * 
 * @see ar.com.ospim.liquidaciones.services.DocumentacionServiceImpl
 * 
 */
public class DocumentacionServiceUtil {

	private static Log _log = LogFactoryUtil
			.getLog(DocumentacionServiceUtil.class);
	private static DocumentacionServiceImpl instance = null;

	public static DocumentacionServiceImpl getInstance() {
		if (null == instance) {
			instance = new DocumentacionServiceImpl();
		}
		return instance;
	}

	public static String grabaDocumentacion(
			String cuil, int inte, int id_doc, Date fechaIngreso,
			Date fechaEgreso, User user, int id_motivo_baja,String certificado) throws Exception {
		return grabaDocumentacion(cuil, inte, id_doc, fechaIngreso,
				fechaEgreso, user, id_motivo_baja,certificado, null);
	}

	public static String grabaDocumentacion(
			String cuil, int inte, int id_doc, Date fechaIngreso,
			Date fechaEgreso, User user, int id_motivo_baja,String certificado,
			Connection connectionParameter) throws Exception {
		_log.debug("Grabando documentacion");
		Connection con = null;
		String result =  null;
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
		} else {
			con = connectionParameter;
		}
		try {
			Afiliado afiliado = EditarAfiliadoServiceUtil
					.getAfiliadoEntryInclusoDadoBaja(cuil, inte, con);
			Date fechaMayoriaEdad = DateUtils.addYears(
					afiliado.getNaci_fecha(), WebKeysGlobal.ANIOS_MAYOR_EDAD);
			boolean compararFecha = false;
			if (fechaEgreso != null && fechaMayoriaEdad != null) {
				compararFecha = ar.com.ospim.util.DateUtils.esMayor(
						fechaEgreso, fechaMayoriaEdad);
			}
			int age = ar.com.ospim.util.DateUtils.getEdad(afiliado.getNaci_fecha());
			
			if (id_motivo_baja == -1) {
				if ((id_doc == 4) && (compararFecha) && (age < 25)) {
					id_motivo_baja = WebKeysAfiliados.HIJO_MAYOR;
				}
				if (id_doc == 5 || id_doc == 15 || id_doc == 19) {
					id_motivo_baja = WebKeysAfiliados.CERTIFICADO_POR_INCAPACIDAD;
				}
			}
			result = getInstance()
					.grabaDocumentacion(cuil, inte, id_doc,
							fechaIngreso, fechaEgreso, user, id_motivo_baja,certificado,
							con);
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			} else {
				throw e;
			}
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(con);
			}
		}
		return result;
	}

	public static List<AfiDocumentacion> buscaDocumentacion(String cuil,
			int inte) throws Exception {
		return getInstance().buscaDocumentos(cuil, inte);
	}
			
	public static List<AfiDocumentacion> buscaDocumentacionDiscapacidad(String cuil, int inte) throws Exception {

		List<AfiDocumentacion> documentacionList = DocumentacionServiceUtil
				.buscaDocumentacion(cuil, inte);
		
		if (documentacionList == null) {
			documentacionList = new ArrayList<AfiDocumentacion>();
		}
		
		Iterator<AfiDocumentacion> iterator = documentacionList.iterator();
		
		while (iterator.hasNext()) {
			AfiDocumentacion afiDoc = iterator.next();
			if (afiDoc.getAfiliado().getInte() != inte || 
					(afiDoc.getDocumento().getId_documento() != 5 && 
					 afiDoc.getDocumento().getId_documento() != 15 &&
					 afiDoc.getDocumento().getId_documento() != 19
					)
			){
				iterator.remove();
			}
		}
		return documentacionList;
	}

	public static String editaDocumentacion(
			String cuil, int inte, int id_doc, Date fechaIngreso,
			Date fechaEgreso, User user, int id,String certificado) throws Exception {
		return getInstance().editaDocumentacion(cuil, inte, id_doc,
				fechaIngreso, fechaEgreso, user, id,certificado);
	}

	public static int  borraDocumentacion(
			String cuil, int inte, int id_tercerizadora, Date fechaIngreso,
			User user, Date fechaMayoriaEdad, int id) throws Exception {
		return getInstance().borraDocumentacion(cuil, inte,
				id_tercerizadora, fechaIngreso, user, fechaMayoriaEdad, id);
	}
}