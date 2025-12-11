package ar.com.uoma.facturacion.afip_ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.liferay.portal.SystemException;

import ar.com.ospim.util.StringUtils;
import ar.com.uoma.facturacion.LoginCmsResponse;
import ar.com.uoma.facturacion.services.FacturacionServiceUtil;

public class AfipLoginCmsClient {//extends AgendadoJava {
		
	private static Logger logger = Logger.getLogger(AfipLoginCmsClient.class);

	private FacturacionServiceUtil serviceUtil = new FacturacionServiceUtil();
	
	private String urlServicio;
	private String service = "wsfe";
	private String dstDN ;
	private String p12file;
	private String signer;
	private String p12pass;
	private Long ticketTime;
	private String usuario;
	
	public AfipLoginCmsClient(String usuario){
		super();
				
		logger.info("Instanciando AfipLoginCmsClient");
		
		this.usuario = usuario;
	}
	
	public LoginCmsResponse getTokenValido(){
		
		LoginCmsResponse respo = null;
		
		logger.info("Buscando Token existente en la base");
	
		try {
			
			respo = serviceUtil.buscarLoginCmsResponseVigente();
			
		} catch (SystemException e) {
			logger.error(e);
		}

		
		
		
		try {
			if(respo==null) {
				
				logger.info("Buscando Token via WS AFIP");
				
				respo = invocarWSAA();
			}
			
		} catch (AxisFault e) {
			logger.error(e);
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		
		logger.debug("Utilizando el token: " + respo.getToken());
		logger.debug("Utilizando el sign: " + respo.getSign());
		
		return respo;
	}
	
	private LoginCmsResponse invocarWSAA() 
			throws FileNotFoundException, IOException, AxisFault, Exception {
		
		LoginCmsResponse respo = null;
		
		
		String loginTicketResponse = null;
		
		/*Seteamos si corre o no con un archivo de propiedades */
		File configDir = new File(System.getProperty("catalina.base"), "conf");
		File configFile = new File(configDir, "liferay_schedulers.properties");
		
		InputStream stream = new FileInputStream(configFile);
		
		Properties props = new Properties();
		props.load(stream);
		urlServicio = props.getProperty("afip_login_cms_url_service");
			
		logger.info("AfipLoginCmsClient url: " + urlServicio);

		dstDN = props.getProperty("dstdn","C=AR,CN=UOMAtest,O=UOMA,serialNumber=CUIT 30531143856");
		
		logger.debug("dstDN " + dstDN);
				
		p12file = props.getProperty("keystore","test-keystore.p12");
		signer  = props.getProperty("keystore-signer","coqui");
		p12pass = props.getProperty("keystore-password","miclaveprivada");
		
		// Set the keystore used by SSL
		System.setProperty("javax.net.ssl.trustStore", props.getProperty("trustStore",""));
		System.setProperty("javax.net.ssl.trustStorePassword",props.getProperty("trustStore_password","")); 
		
		ticketTime = new Long(props.getProperty("TicketTime","36000"));
	
		// Create LoginTicketRequest_xml_cms
		byte [] loginTicketRequest_xml_cms = afip_wsaa_client.create_cms(p12file, p12pass, 
					signer, dstDN, service, ticketTime);
			
		// Invoke AFIP wsaa and get LoginTicketResponse
		loginTicketResponse = afip_wsaa_client.invoke_wsaa( loginTicketRequest_xml_cms, urlServicio );
			
		logger.debug(loginTicketResponse);
		
		// Get token & sign from LoginTicketResponse

		Reader tokenReader = new StringReader(loginTicketResponse);
		Document tokenDoc = new SAXReader(false).read(tokenReader);
		
		String token = tokenDoc.valueOf("/loginTicketResponse/credentials/token");
		String sign  = tokenDoc.valueOf("/loginTicketResponse/credentials/sign");
		String source = tokenDoc.valueOf("/loginTicketResponse/header/source");
		String destination = tokenDoc.valueOf("/loginTicketResponse/header/destination");
		String uniqueId = tokenDoc.valueOf("/loginTicketResponse/header/uniqueId");
		String generationTime = tokenDoc.valueOf("/loginTicketResponse/header/generationTime");
		String expirationTime = tokenDoc.valueOf("/loginTicketResponse/header/expirationTime");
		
		logger.debug(token);
		logger.debug(sign);
		logger.debug(source);
		logger.debug(destination);
		logger.debug(uniqueId);
		logger.debug(generationTime);
		logger.debug(expirationTime);
		
		TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); 
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //2018-11-26T17:46:33.258-03:00
		sdf.setTimeZone(tz);
		
		if(StringUtils.checkNotEmpty(token)) {
		
			respo = new LoginCmsResponse(source, destination, uniqueId, sdf.parse(generationTime),
					sdf.parse(expirationTime), token, sign);
					
			
			if(respo != null) {
				logger.info("Grabando el Token de Afip: ");

				serviceUtil.insertarLoginCmsResponse(respo, usuario);
			}
			
		}
		
		logger.debug("TOKEN: " + token);
		logger.debug("SIGN: " + sign);
		logger.debug("source: " + source);
		logger.debug("destination: " + destination);
		logger.debug("uniqueId: " + uniqueId);
		logger.debug("generationTime: " + generationTime);
		logger.debug("expirationTime: " + expirationTime);	
		
		
		return respo;
	}
} 