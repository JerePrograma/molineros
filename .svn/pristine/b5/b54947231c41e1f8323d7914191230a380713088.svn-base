package ar.com.ospim.procesaArchivos;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.procesaArchivos.services.EnviaMailsServiceImpl;



/**
 * Read and write a file using an explicit encoding. Removing the encoding from
 * this code will simply cause the system's default encoding to be used instead.
 */
public final class EnviaMailsEmpleadores {

	/** Requires two arguments - the file name, and the encoding to use. 
	 * @throws SQLException */
	public static void main(String... aArgs) throws IOException, SQLException {
		// A quién va dirigido
		EnviaMailsServiceImpl serv = new EnviaMailsServiceImpl();
		List<String> emails = serv.getListaEmailsEmpleadores();
		MailUtils.enviarMailGmailconPdf(
						"empleadores@uoma.org.ar",
						"2015UOMA",
						emails,
						"Portal Empleadores - Actualización de Datos de su Empresa",
						"Buenas tardes,\nTenemos el agrado de dirigirnos a usted, para ponerlo al tanto de una modificación en el "
						+ "aplicativo (www.uomaempleadores.org.ar), que entrará en vigencia el próximo día 6 de Julio de 2015. "
						+ "A partir de esa fecha, se le solicitará que actualice/verifique los datos de su empresa (tarea que se le solicitará cada seis meses, "
						+ "para asegurarnos de contar con sus datos correctos).\n\n Adjunto a este correo, encontrará un instructivo para facilitarle esta tarea.\n\n"
						+ "Ante cualquier inconveniente, no dude en comunicarse, a través de nuestro correo electrónico empleadores@uoma.org.ar.\n\n"
						+ "Muchas gracias.\n\n"
						+ "Lo saluda cordialmente,\n\n"
						+"Unión Obrera Molinera Argentina",						
						"/home/fbrachi/InstructivoActualizaDomicilio.pdf","PortalEmpleadores.pdf");
	}
}
