package ar.com.global.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import ar.com.global.beans.Boletin;
import ar.com.global.beans.Destinatario;
import ar.com.global.beans.ListaDestinatarios;
import ar.com.global.webservices.agnitas_webservice.EmmWebService_PortProxy;
import ar.com.global.webservices.agnitas_webservice.StringArrayType;

public class MailingServiceUtil {
	private static final String USER = "portalmolineros";
	private static final String PASS = "portalmolineros";
	private static final String MAIL_TYPE_EMAIL = "1";
	private static final int MAIL_STATUS_ACTIVE=1;
	private static final int MEDIA_TYPE_EMAIL=0;
	private static final String BINDING_TYPE_NORMAL="W";
	private static final String BINDING_TYPE_ADMIN="A";
	private static final String BINDING_TYPE_TEST="T";

	private static MailingServiceImpl instance = null;

	public static MailingServiceImpl getInstance() {
		if (null == instance) {
			instance = new MailingServiceImpl();
		}
		return instance;
	}

	public static List<ListaDestinatarios> getListasMailing(String nombre)
			throws Exception {
		// Primero creo el mailing
		List<ListaDestinatarios> destinatarios = new ArrayList<ListaDestinatarios>();

		destinatarios = getInstance().getListasMailing(nombre);

		return destinatarios;
	}
	
	public static ListaDestinatarios getListaMailing(int id_lista)
			throws Exception {
		// Primero creo el mailing
		ListaDestinatarios destinatarios = new ListaDestinatarios();

		destinatarios = getInstance().getListaMailing(id_lista);

		return destinatarios;
	}
	
	public static ListaDestinatarios getSubscribers(Destinatario destinatario)
			throws Exception {
		// Primero creo el mailing
		ListaDestinatarios destinatarios = new ListaDestinatarios();

		destinatarios.setListaDestinatarios(getInstance().getSubscribers(destinatario));

		return destinatarios;
	}
	
	public static Destinatario getSubscriber(int id_destinatario)
			throws Exception {
		// Primero creo el mailing
		return getInstance().getSubscriber(id_destinatario);
	}
	
		
	public static void nuevoListaMailing(ListaDestinatarios destinatarios, String screenname) throws Exception{
		 /*nuevoMailingList(destinatarios.getNombre(),
				destinatarios.getObservaciones());*/
		EmmWebService_PortProxy emm = new EmmWebService_PortProxy();
		 addSubscribersFromLista(destinatarios);
		 //int binding = emm.setSubscriberBinding(USER, PASS, d.getIdDestinatario(),destinatarios.getIdListaDestinatarios(), MEDIA_TYPE_EMAIL,MAIL_STATUS_ACTIVE, BINDING_TYPE_NORMAL , "AGREGADO DESDE LIFERAY", 0);
	}

	public static ListaDestinatarios nuevaListaMail(
			ListaDestinatarios destinatarios, String screenname) throws Exception {
		// Primero creo el mailing
		//int mailingListId = nuevoMailingList(destinatarios.getNombre(),
		//		destinatarios.getObservaciones());
		//destinatarios.setIdListaDestinatarios(mailingListId);
		getInstance().saveListaEnBase(destinatarios, screenname);
		return destinatarios;
	}
	
	public static Boletin editarBoletin(
			Boletin boletin, String screenname) throws Exception {	
		getInstance().borrarContenidosBoletin(boletin.getIdBoletin(), screenname);
		getInstance().borrarListasBoletin(boletin.getIdBoletin(), screenname);
		getInstance().grabarContenidos(boletin.getListaContenidos(), boletin.getIdBoletin(), screenname, null);
		getInstance().insertarListasBoletin(boletin.getListas(), boletin.getIdBoletin(), screenname, null);
		getInstance().editarBoletin(boletin, screenname,null);
		return boletin;
	}
	
	public static Boletin nuevoBoletin(
			Boletin boletin, String screenname) throws Exception {
		// Primero creo el mailing
		//int mailingListId = nuevoMailingList(destinatarios.getNombre(),
		//		destinatarios.getObservaciones());
		//destinatarios.setIdListaDestinatarios(mailingListId);
		getInstance().saveBoletin(boletin, screenname);
		return boletin;
	}

	public static ListaDestinatarios editarMailingConDestinatarios(
			ListaDestinatarios destinatarios, String screenname) throws Exception {
		// Primero borro
		getInstance().borrarListaEnBase(destinatarios.getIdListaDestinatarios(), screenname);
		getInstance().grabarSubscribers(destinatarios, screenname, null);
		getInstance().insertarSubscribersMailing(destinatarios, screenname, null);
		getInstance().actualizarListaEnBase(destinatarios, screenname);		
		
		return destinatarios;
	}

	public static int nuevoMailingList(String nombre, String observaciones)
			throws RemoteException {		
		EmmWebService_PortProxy emm = new EmmWebService_PortProxy();
		return emm.addMailinglist(USER, PASS, nombre, observaciones);
	}

	public static int addSubscribersFromLista(
			ListaDestinatarios listaDestinatario) throws RemoteException {		
		EmmWebService_PortProxy emm = new EmmWebService_PortProxy();
		List<Destinatario> lista = listaDestinatario.getListaDestinatarios();
		StringArrayType addSParameters = new StringArrayType(6);
		addSParameters.setX(0, new String("email"));
		addSParameters.setX(1, new String("mailtype"));
		addSParameters.setX(2, new String("gender"));
		addSParameters.setX(3, new String("firstname"));
		addSParameters.setX(4, new String("lastname"));
		addSParameters.setX(5, new String("title"));

		StringArrayType addSValues = new StringArrayType(6);
		for (Destinatario d : lista) {
			addSValues.setX(0, d.getEmail());
			addSValues.setX(1, MAIL_TYPE_EMAIL);
			addSValues.setX(2, "1");
			addSValues.setX(3, d.getFirstname());
			addSValues.setX(4, d.getLastname());
			addSValues.setX(5, d.getTitle());
			d.setIdDestinatario(emm.addSubscriber(USER, PASS, true, "email",
					false, addSParameters, addSValues));			
		}

		return 0;

	}
	
	public static Destinatario addSubscriber(Destinatario destinatario, String screenname) throws Exception {		
		return getInstance().grabarSubscriber(destinatario, screenname,null);

	}
	
	public static Destinatario editarDestinatario(Destinatario destinatario, String screenname) throws Exception {		
		getInstance().actualizarSubscriber(destinatario, screenname);
		return destinatario;	
	}
	
	public static void borrarDestinatario(int id_destinatario, String screenname) throws Exception {		
		getInstance().borrarDestinatario(id_destinatario, screenname);			
	}
	
	public static List<Boletin> getBoletines(Boletin boletin)
			throws Exception {
		return getInstance().getBoletines(boletin);
	}
	
	public static Boletin getBoletin(int id_boletin)
			throws Exception {
		return getInstance().getBoletin(id_boletin);
	}
	
	public static List<Destinatario> getDestinatariosFromListas(String[] listas)
			throws Exception {
		return getInstance().getDestinatariosFromListas(listas);
	}

}
