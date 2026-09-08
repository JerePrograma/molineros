package ar.com.global.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.axis.AxisFault;
import org.jfree.util.Log;

import ar.com.global.beans.Boletin;
import ar.com.global.beans.Contenido;
import ar.com.global.beans.Destinatario;
import ar.com.global.webservices.agnitas_webservice.EmmWebService_PortProxy;
import ar.com.global.webservices.agnitas_webservice.EmmWebService_PortService;
import ar.com.global.webservices.agnitas_webservice.EmmWebService_PortServiceLocator;
import ar.com.global.webservices.agnitas_webservice.StringArrayType;

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
	private static final String WS_USER = "portalmolineros";
	private static final String WS_PASS = "portalmolineros";
	private static final boolean OVERWRITE = true;
	private static final boolean DOUBLE_CHECK = true;
	public static final int MOROSIDAD_TEMPLATE_TEXTO = 60;
	public static final int DDHH_TEMPLATE = 35;
	public static final int DDHH_TEMPLATE_TEXTO = 40;

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

	public static void main(String[] args) {
		try {
			EmmWebService_PortProxy emm = new EmmWebService_PortProxy();

			StringArrayType addSParameters = new StringArrayType(6);
			addSParameters.setX(0, new String("email"));
			addSParameters.setX(1, new String("mailtype"));
			addSParameters.setX(2, new String("gender"));
			addSParameters.setX(3, new String("firstname"));
			addSParameters.setX(4, new String("lastname"));
			addSParameters.setX(5, new String("title"));

			StringArrayType addSValues = new StringArrayType(6);
			addSValues.setX(0, new String("fbrachi@ospim.org.ar"));
			addSValues.setX(1, new String("1"));
			addSValues.setX(2, new String("1"));
			addSValues.setX(3, new String("EMPRESA PRUEBA"));
			addSValues.setX(4, new String("Doe"));
			addSValues.setX(5, new String("Mr."));

			int idResult = emm.addSubscriber("portalmolineros",
					"portalmolineros", true, "email", false, addSParameters,
					addSValues);
			System.out.println("RESULT: " + idResult);

			int binding = emm.setSubscriberBinding("portalmolineros",
					"portalmolineros", idResult, 9, 0, 1, "A",
					"prueba WebService", 0);

			int content = emm.insertContent("portalmolineros",
					"portalmolineros", 9, "Description", "EMPRESA PRUEBA!", 0,
					0);
			/*
			 * int content2=emm.insertContent("portalmolineros",
			 * "portalmolineros", 6, "ResumenHeader",
			 * "Este es el boletín de la organización.", 0, 0); int
			 * content3=emm.insertContent("portalmolineros", "portalmolineros",
			 * 6, "Title", "DD.HH. CGT", 0, 0); int
			 * content4=emm.insertContent("portalmolineros", "portalmolineros",
			 * 6, "TituloLista1", "SECCIONES:", 0, 0); int
			 * content5=emm.insertContent("portalmolineros", "portalmolineros",
			 * 6, "Lista1Item1", "Cárceles", 0, 0); int
			 * content6=emm.insertContent("portalmolineros", "portalmolineros",
			 * 6, "Lista1Item2", "Mujer", 0, 0); int
			 * content7=emm.insertContent("portalmolineros", "portalmolineros",
			 * 6, "Lista1Item3", "Minorías", 0, 0); int
			 * content8=emm.insertContent("portalmolineros", "portalmolineros",
			 * 6, "Lista1Item4", "Legales", 0, 0); int
			 * content9=emm.insertContent("portalmolineros", "portalmolineros",
			 * 6, "Lista1Item5", "Minoridad", 0, 0); int
			 * content10=emm.insertContent("portalmolineros", "portalmolineros",
			 * 6, "TituloArticuloPrincipal", "NUEVO PRIMER BOLETÍN DE LA ORG.",
			 * 0, 0); StringBuffer sb= new
			 * StringBuffer("Este es el NUEVO texto del artículo principal");
			 * int content11=emm.insertContent("portalmolineros",
			 * "portalmolineros", 6, "TextoArticuloPrincipal", sb.toString(), 0,
			 * 0); int content12=emm.insertContent("portalmolineros",
			 * "portalmolineros", 6, "TituloArticuloSecundario",
			 * "ARTICULO SECUNDARIO", 0, 0); StringBuffer sb2= new StringBuffer(
			 * "Este es el NUeVO texto del artículo secundario. Puede ser cualquier cosa que se considere secundaria \n\r"
			 * ); int content13=emm.insertContent("portalmolineros",
			 * "portalmolineros", 6, "TextoArticuloSecundario", sb2.toString(),
			 * 0, 0); int content16=emm.insertContent("portalmolineros",
			 * "portalmolineros", 6,
			 * "TituloNotaInferior1","Título de nota Inferior", 0, 0);
			 * StringBuffer sb4= new StringBuffer(
			 * "Este es el NUEVO texto de la nota inferior. Puede ser cualquier cosa que se considere posicionar aquí\n\r"
			 * ); sb4.append(
			 * "Puede tener muchas líneas y etc. Y porque no, más lineas. Y más, y más, y más, y más y más.\n\r"
			 * ); sb4.append(
			 * "Puede tener muchas líneas y etc. Y porque no, más lineas. Y más, y más, y más, y más y más.\n\r"
			 * ); int content17=emm.insertContent("portalmolineros",
			 * "portalmolineros", 6, "TextoNotaInferior1",sb4.toString(), 0, 0);
			 * int content18=emm.insertContent("portalmolineros",
			 * "portalmolineros", 6, "Organizacion","Sec. DD.HH. CGT", 0, 0);
			 * int content19=emm.insertContent("portalmolineros",
			 * "portalmolineros", 6,
			 * "WebOrganizacion","secretariadeddhhcgt.blogspot.com", 0, 0); int
			 * content20=emm.insertContent("portalmolineros", "portalmolineros",
			 * 6, "NroBoletin","1", 0, 0);
			 */

			String prueba = emm.getSubscriberBinding("portalmolineros",
					"portalmolineros", idResult, 9, 0);
			int sendMail = emm.sendMailing("portalmolineros",
					"portalmolineros", 9, "A",
					(int) (System.currentTimeMillis() / 100L), 0, 0);
			// int result=emm.addMailinglist("openemm", "openemm",
			// "NUEVA LISTA", "Nueva Lista Main");
			// System.out.println("Binding: " + binding);
			// System.out.println("Mailing: " + result);
			System.out.println("Send Mail: " + sendMail);
			// System.out.println("ID recipient: " + sendMail);

		} catch (AxisFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	} // end main

}
