package ar.com.ospim.procesaArchivos;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.procesaArchivos.services.EnviaMailsServiceImpl;

/**
 * Read and write a file using an explicit encoding. Removing the encoding from
 * this code will simply cause the system's default encoding to be used instead.
 */
public final class EnviaMails {

	/**
	 * Requires two arguments - the file name, and the encoding to use.
	 * 
	 * @throws SQLException
	 */
	public static void main(String... aArgs) throws IOException, SQLException,
			Exception {
		// A quién va dirigido
		EnviaMailsServiceImpl serv = new EnviaMailsServiceImpl();
		List<String> emails = serv.getListaEmails();
		// String mail=leerArchivo();
		// ITERAR SOBRE ESTO, SINO NO VA A ANDAR
		//for (int i = 0; i < emails.size(); i++) {
		MailUtils
		.enviarMailGmailconPdf(
				"info@ospim.org.ar",
				"ospim12345",
				emails,
				"Vademecum O.S.P.I.M. - A.M.T.I.M.A.",
				"Buenas Tardes,\n\r"
						+ "Adjuntamos instructivo para descargar el Vademecum OSPIM - AMTIMA vigente para este período.\n\rAtentamente,\n\rOSPIM",
				"/home/fbrachi/INSTRUCTIVO_VADEMECUM.pdf",
				"INSTRUCTIVO_VADEMECUM.pdf");
		//}
		/*
		 * "MODIFICACION EN NORMAS DE FACTURACION",
		 * "Estimadas Farmacias,\n \r\n" +
		 * "Adjuntamos la nueva norma de aplicación para el Vademecum OSPIM - AMTIMA, vigente a partir del 1° de Mayo de 2014.\n\rAtentamente,\n\rOSPIM"
		 * , "/home/sistemas-01/NUEVA_NORMA.zip", "NUEVA_NORMA.zip", true);
		 */
	}
}
