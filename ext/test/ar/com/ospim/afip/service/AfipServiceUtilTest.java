package ar.com.ospim.afip.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class AfipServiceUtilTest extends TestCase{
	public void testAFIPUnMesCompletoMismoMesMismoAnio() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("11/02/2010");
		assertEquals(30, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPCincoMesesCompletosMismoMesMismoAnio() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("11/06/2010");
		assertEquals(150, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPDiferentesAnios() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2009");
		Date dateFin = format.parse("11/06/2010");
		assertEquals(510, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}

	public void testAFIPMenosDeUnMes() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("05/02/2010");
		assertEquals(25, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPMasDeUnMesIncompleto() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("07/04/2010");
		assertEquals(87, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPMasDeUnMesIncompletoExtremoFebrero() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("01/03/2010");
		assertEquals(48, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPMasDeUnMesIncompletoExtremoFebrero2() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("28/02/2010");
		assertEquals(47, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPMasDeUnMesIncompletoExtremoFebrero3() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("01/01/2010");
		Date dateFin = format.parse("01/03/2010");
		assertEquals(61, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPMasDeUnMesIncompletoExtremoFebrero4() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("01/01/2010");
		Date dateFin = format.parse("28/02/2010");
		assertEquals(58, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPMasDeUnMesIncompletoExtremoJunio() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("02/06/2010");
		Date dateFin = format.parse("01/08/2010");
		assertEquals(60, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPMasDeUnMesIncompletoExtremoJunio2() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("03/06/2010");
		Date dateFin = format.parse("01/08/2010");
		assertEquals(60, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPMasDeUnMesIncompletoExtremoOctubre() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("10/08/2010");
		Date dateFin = format.parse("01/10/2010");
		assertEquals(52, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPMasDeUnMesIncompletoExtremoOctubre2() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("01/08/2010");
		Date dateFin = format.parse("01/10/2010");
		assertEquals(61, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPMasDeUnMesIncompletoExtremoOctubre3() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("31/07/2010");
		Date dateFin = format.parse("01/10/2010");
		assertEquals(62, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}
	
	public void testAFIPMasDeUnMesIncompletoExtremoOctubre4() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("02/08/2010");
		Date dateFin = format.parse("01/10/2010");
		assertEquals(60, AfipServiceUtil.obtenerDiasAFIPParaInteres(dateIni, dateFin));
	}

	
	//Testeo los dias NO AFIP
	public void testUnMesCompletoMismoMesMismoAnio() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("11/02/2010");
		assertEquals(31, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testCincoMesesCompletosMismoMesMismoAnio() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("11/06/2010");
		assertEquals(151, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testDiferentesAnios() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2009");
		Date dateFin = format.parse("11/06/2010");
		assertEquals(516, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}

	public void testMenosDeUnMes() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("05/02/2010");
		assertEquals(25, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testMasDeUnMesIncompleto() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("07/04/2010");
		assertEquals(86, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testMasDeUnMesIncompletoExtremoFebrero() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("01/03/2010");
		assertEquals(49, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testMasDeUnMesIncompletoExtremoFebrero2() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("12/01/2010");
		Date dateFin = format.parse("28/02/2010");
		assertEquals(48, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testMasDeUnMesIncompletoExtremoFebrero3() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("01/01/2010");
		Date dateFin = format.parse("01/03/2010");
		assertEquals(60, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testMasDeUnMesIncompletoExtremoFebrero4() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("01/01/2010");
		Date dateFin = format.parse("28/02/2010");
		assertEquals(59, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testMasDeUnMesIncompletoExtremoJunio() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("02/06/2010");
		Date dateFin = format.parse("01/08/2010");
		assertEquals(61, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testMasDeUnMesIncompletoExtremoJunio2() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("03/06/2010");
		Date dateFin = format.parse("01/08/2010");
		assertEquals(60, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testMasDeUnMesIncompletoExtremoOctubre() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("10/08/2010");
		Date dateFin = format.parse("01/10/2010");
		assertEquals(53, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testMasDeUnMesIncompletoExtremoOctubre2() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("01/08/2010");
		Date dateFin = format.parse("01/10/2010");
		assertEquals(62, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testMasDeUnMesIncompletoExtremoOctubre3() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("31/07/2010");
		Date dateFin = format.parse("01/10/2010");
		assertEquals(63, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
	
	public void testMasDeUnMesIncompletoExtremoOctubre4() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date dateIni = format.parse("02/08/2010");
		Date dateFin = format.parse("01/10/2010");
		assertEquals(61, AfipServiceUtil.obtenerDiasParaInteres(dateIni, dateFin));
	}
}
