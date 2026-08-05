package ar.com.global.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.util.Log;

import ar.com.global.beans.Boletin;
import ar.com.global.beans.Contenido;
import ar.com.global.beans.Destinatario;
import ar.com.global.webservices.agnitas_webservice.EmmWebService_PortProxy;
import ar.com.global.webservices.agnitas_webservice.EmmWebService_PortService;
import ar.com.global.webservices.agnitas_webservice.EmmWebService_PortServiceLocator;
import ar.com.global.webservices.agnitas_webservice.StringArrayType;
import ar.com.ospim.global.services.TraeListasServiceUtil;

public class OpenemmClient {

	private static final int MAIL_STATUS_ACTIVE = 1;
	private static final String MAIL_TYPE_EMAIL = "1";
	private static final String GENDER_UNKNOWN = "2";
	private static final int MEDIA_TYPE_EMAIL = 0;
	private static final int MAILING_TYPE_NORMAL = 0;
	private static final String BINDING_TYPE_NORMAL = "W";
	private static final String BINDING_TYPE_ADMIN = "A";
	private static final String BINDING_TYPE_TEST = "T";
	private static final String BINDING_REMARK = "Lista generada desde Liferay";
	private static final String DOUBLE_CHECK_FIELD = "email";
	private static final String CONFIG_WS_USER = "OPENEMM_WS_USER";
	private static final String CONFIG_WS_PASSWORD = "OPENEMM_WS_PASSWORD";
	private static final String WS_USER = getRequiredSystemConfig(CONFIG_WS_USER);
	private static final String WS_PASS = getRequiredSystemConfig(CONFIG_WS_PASSWORD);
	private static final boolean OVERWRITE = true;
	private static final boolean DOUBLE_CHECK = true;
	public static final int MOROSIDAD_TEMPLATE_TEXTO = 60;
	public static final int DDHH_TEMPLATE = 35;
	public static final int DDHH_TEMPLATE_TEXTO = 40;

	private static String getRequiredSystemConfig(String key) {
		String value = TraeListasServiceUtil.getSystemConfig(key);

		if (value == null || value.trim().length() == 0) {
			throw new IllegalStateException("Falta configuracion requerida: " + key);
		}

		return value;
	}

	public static List<Destinatario> insertSubscriberList(
			List<Destinatario> destinatarios, int id_lista) throws Exception {
		EmmWebService_PortProxy emm = new EmmWebService_PortProxy();

		StringArrayType addSParameters = new StringArrayType(6);
		addSParameters.setX(0, new String("email"));
		addSParameters.setX(1, new String("mailtype"));
		addSParameters.setX(2, new String("gender"));
		addSParameters.setX(3, new String("firstname"));
		addSParameters.setX(4, new String("lastname"));
		addSParameters.setX(5, new String("title"));

		List<Destinatario> errores = new ArrayList<Destinatario>();

		for (Destinatario d : destinatarios) {
			StringArrayType addSValues = new StringArrayType(6);
			addSValues.setX(0, new String(d.getEmail()));
			addSValues.setX(1, new String(MAIL_TYPE_EMAIL));
			addSValues.setX(2, new String(GENDER_UNKNOWN));
			addSValues.setX(3, new String(d.getFirstname()));
			addSValues.setX(4, new String(d.getLastname()));
			addSValues.setX(5, new String(d.getTitle()));
			int idResult = emm.addSubscriber(WS_USER, WS_PASS, DOUBLE_CHECK,
					DOUBLE_CHECK_FIELD, OVERWRITE, addSParameters, addSValues);
			d.setIdDestinatario(idResult);
			if (idResult != 0) {
				String binding_type = d.isCasillaPrueba() ? BINDING_TYPE_ADMIN
						: BINDING_TYPE_NORMAL;
				int binding = emm.setSubscriberBinding(WS_USER, WS_PASS,
						idResult, id_lista, MEDIA_TYPE_EMAIL,
						MAIL_STATUS_ACTIVE, binding_type, BINDING_REMARK, 0);
				if (binding == 0) {
					errores.add(d);
				}
			} else {
				errores.add(d);
			}

		}

		return errores;
	}

	public static int insertarContenido(Boletin boletin) throws Exception {
		EmmWebService_PortProxy emm = new EmmWebService_PortProxy();
		int cont_noticias = 0;
		int cont_reportajes = 0;
		int cont_novedades = 0;
		int cont_documentos = 0;
		StringBuffer sb = new StringBuffer();
		List<Contenido> contenidos=boletin.getListaContenidos();
		int id_mailing=boletin.getIdBoletin();
		for (Contenido c : contenidos) {
			if (boletin.isSoloTexto()) {
				sb.append(c.getContenido()).append(" ");
			} else {

				if (c.getSeccion().equals("TITULO")) {
					emm.insertContent(WS_USER, WS_PASS, id_mailing, "Title",
							c.getContenido(), 0, 0);
				}
				if (c.getSeccion().equals("NOTICIAS")) {
					cont_noticias++;
					emm.insertContent(WS_USER, WS_PASS, id_mailing,
							"TituloNoticias" + cont_noticias, c.getTitulo(), 0,
							0);
					emm.insertContent(WS_USER, WS_PASS, id_mailing,
							"TextoNoticias" + cont_noticias, c.getContenido(),
							0, 0);
				}
				if (c.getSeccion().equals("REPORTAJES")) {
					cont_reportajes++;
					emm.insertContent(WS_USER, WS_PASS, id_mailing,
							"TituloReportaje" + cont_reportajes, c.getTitulo(),
							0, 0);
					emm.insertContent(WS_USER, WS_PASS, id_mailing,
							"TextoReportaje" + cont_reportajes,
							c.getContenido(), 0, 0);
				}
				if (c.getSeccion().equals("NOVEDADES")) {
					cont_novedades++;
					emm.insertContent(WS_USER, WS_PASS, id_mailing,
							"TituloNovedades" + cont_novedades, c.getTitulo(),
							0, 0);
					emm.insertContent(WS_USER, WS_PASS, id_mailing,
							"TextoNovedades" + cont_novedades,
							c.getContenido(), 0, 0);
				}
				if (c.getSeccion().equals("DOCUMENTOS")) {
					cont_documentos++;
					emm.insertContent(WS_USER, WS_PASS, id_mailing,
							"TituloDocumentos" + cont_documentos,
							c.getTitulo(), 0, 0);
					emm.insertContent(WS_USER, WS_PASS, id_mailing,
							"TextoDocumentos" + cont_documentos,
							c.getContenido(), 0, 0);
				}
			}
		}
		if (boletin.isSoloTexto()) {
			emm.insertContent(WS_USER, WS_PASS, id_mailing, "emailText",
					sb.toString(), 0, 0);
		}
		return 0;

	}

	public static int crearLista(Boletin boletin) throws Exception {
		EmmWebService_PortProxy emm = new EmmWebService_PortProxy();

		int result = emm.addMailinglist(WS_USER, WS_PASS, boletin.getNombre(),
				boletin.getObservaciones());
		boletin.setIdListaBoletin(result);

		return result;
	}

	public static int crearMail(Boletin boletin) throws Exception {
		EmmWebService_PortProxy emm = new EmmWebService_PortProxy();

		StringArrayType target = new StringArrayType(1);
		target.setX(0, new String("0"));
		int result = 0;
		if (boletin.isSoloTexto() && !boletin.isDifusion()) {
			result = emm
					.newEmailMailing(WS_USER, WS_PASS, boletin.getNombre(),
							boletin.getObservaciones(),
							boletin.getIdListaBoletin(), target,
							MAILING_TYPE_NORMAL, DDHH_TEMPLATE_TEXTO ,
							boletin.getAsunto(), "info@uoma.org.ar",
							"ISO-8859-1", 0, 0);

		} else if(boletin.isDifusion()){
			result = emm
					.newEmailMailing(WS_USER, WS_PASS, boletin.getNombre(),
							boletin.getObservaciones(),
							boletin.getIdListaBoletin(), target,
							MAILING_TYPE_NORMAL, MOROSIDAD_TEMPLATE_TEXTO ,
							boletin.getAsunto(), "info@uoma.org.ar",
							"ISO-8859-1", 0, 0);
			
		}else {
			result = emm
					.newEmailMailing(WS_USER, WS_PASS, boletin.getNombre(),
							boletin.getObservaciones(),
							boletin.getIdListaBoletin(), target,
							MAILING_TYPE_NORMAL, DDHH_TEMPLATE,
							boletin.getAsunto(), "info@uoma.org.ar",
							"ISO-8859-1", 0, 2);

		}

		return result;
	}
	
	public static boolean actualizarMail(Boletin boletin) throws Exception {
		EmmWebService_PortProxy emm = new EmmWebService_PortProxy();

		StringArrayType target = new StringArrayType(1);
		target.setX(0, new String("0"));		
		if (boletin.isSoloTexto()) {			
			 return emm.updateEmailMailing(WS_USER, 
							WS_PASS, 
							boletin.getIdBoletin(),
							boletin.getNombre(),
							boletin.getObservaciones(),
							boletin.getIdListaBoletin(), 
							target ,
							MAILING_TYPE_NORMAL,
							boletin.getAsunto(),
							"info@uoma.org.ar",
							"info@uoma.org.ar",
							"ISO-8859-1",
							0, 
							0);

		} else {
			return emm.updateEmailMailing(WS_USER, WS_PASS,
							boletin.getIdBoletin(),
							boletin.getNombre(),
							boletin.getObservaciones(),
							boletin.getIdListaBoletin(), 
							target,
							MAILING_TYPE_NORMAL, 
							boletin.getAsunto(), "info@uoma.org.ar","info@uoma.org.ar",
							"ISO-8859-1", 0, 2);

		}

		
	}

	public static int enviarMailAdmin(Boletin boletin)
			throws Exception {
		EmmWebService_PortProxy emm = new EmmWebService_PortProxy();		
		int sendMail = emm.sendMailing(WS_USER, WS_PASS, boletin.getIdBoletin(), "A",
				(int) (System.currentTimeMillis() / 1000L), 0, 0);
		return sendMail;
	}

	public static int enviarMail(Boletin boletin,
			String tipoDestinatarios) throws Exception {
		int result = 0;
		if (tipoDestinatarios.trim().equals("TODOS")) {
			result = enviarMailTodos(boletin);
		} else {
			result = enviarMailAdmin(boletin);
		}
		return result;
	}

	public static int enviarMailTodos(Boletin boletin)
			throws Exception {
		EmmWebService_PortProxy emm = new EmmWebService_PortProxy();
		Log.debug("HORA DE ENVIO: "
				+ new Date(System.currentTimeMillis() / 1000L));
		int sendMail = emm.sendMailing(WS_USER, WS_PASS, boletin.getIdBoletin(), "W",
				(int) (System.currentTimeMillis() / 1000L), 0, 0);
		return sendMail;
	}


}
