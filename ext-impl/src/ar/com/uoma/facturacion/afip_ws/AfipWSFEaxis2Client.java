package ar.com.uoma.facturacion.afip_ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import ar.com.ospim.util.DateUtils;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.LoginCmsResponse;
import fev1.dif.afip.gov.ar.ServiceStub;
import fev1.dif.afip.gov.ar.ServiceStub.AlicIva;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfAlicIva;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfCbteTipo;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfConceptoTipo;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfDocTipo;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfErr;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfEvt;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfFECAEDetRequest;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfFECAEDetResponse;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfIvaTipo;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfMoneda;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfObs;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfPtoVenta;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfTributoTipo;
import fev1.dif.afip.gov.ar.ServiceStub.CbteTipo;
import fev1.dif.afip.gov.ar.ServiceStub.CbteTipoResponse;
import fev1.dif.afip.gov.ar.ServiceStub.ConceptoTipo;
import fev1.dif.afip.gov.ar.ServiceStub.ConceptoTipoResponse;
import fev1.dif.afip.gov.ar.ServiceStub.DocTipo;
import fev1.dif.afip.gov.ar.ServiceStub.DocTipoResponse;
import fev1.dif.afip.gov.ar.ServiceStub.Err;
import fev1.dif.afip.gov.ar.ServiceStub.Evt;
import fev1.dif.afip.gov.ar.ServiceStub.FEAuthRequest;
import fev1.dif.afip.gov.ar.ServiceStub.FECAECabRequest;
import fev1.dif.afip.gov.ar.ServiceStub.FECAECabResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FECAEDetRequest;
import fev1.dif.afip.gov.ar.ServiceStub.FECAEDetResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FECAERequest;
import fev1.dif.afip.gov.ar.ServiceStub.FECAEResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FECAESolicitar;
import fev1.dif.afip.gov.ar.ServiceStub.FECAESolicitarResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FECompUltimoAutorizado;
import fev1.dif.afip.gov.ar.ServiceStub.FECompUltimoAutorizadoResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FEDummy;
import fev1.dif.afip.gov.ar.ServiceStub.FEDummyResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetPtosVenta;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetPtosVentaResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposCbte;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposCbteResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposConcepto;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposConceptoResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposDoc;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposDocResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposIva;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposIvaResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposMonedas;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposMonedasResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposTributos;
import fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposTributosResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FEPtoVentaResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FERecuperaLastCbteResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FETributoResponse;
import fev1.dif.afip.gov.ar.ServiceStub.IvaTipo;
import fev1.dif.afip.gov.ar.ServiceStub.IvaTipoResponse;
import fev1.dif.afip.gov.ar.ServiceStub.Moneda;
import fev1.dif.afip.gov.ar.ServiceStub.MonedaResponse;
import fev1.dif.afip.gov.ar.ServiceStub.Obs;
import fev1.dif.afip.gov.ar.ServiceStub.PtoVenta;
import fev1.dif.afip.gov.ar.ServiceStub.TributoTipo;

public class AfipWSFEaxis2Client implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 106167398475084969L;

	private Logger _log = Logger.getLogger(this.getClass());

	private static final String cuit = "20181512831"; // MARCE p/ QA
//	private static final String cuit = "30531143856"; // UOMA p/ PRODUCCION
	private Factura factura;
	private LoginCmsResponse token;
	private ServiceStub stub ;
	
	public AfipWSFEaxis2Client() {
		super();
	}
	
	public AfipWSFEaxis2Client(Factura fac, LoginCmsResponse resp) {
		super();
		
		this.factura = fac;
		this.token = resp;
		
		/*Seteamos si corre o no con un archivo de propiedades */
		File configDir = new File(System.getProperty("catalina.base"), "conf");
		File configFile = new File(configDir, "liferay_schedulers.properties");
		String urlServicio="";
		try {
			InputStream stream = new FileInputStream(configFile);
			
			Properties props = new Properties();
			
			props.load(stream);
			
			urlServicio = props.getProperty("afip_wsfev1_url_service");
			
			stub = new ServiceStub(urlServicio);
			_log.info("AFIP " + urlServicio + " inicializado correctamente");
			
		} catch (FileNotFoundException e) {
			_log.error(e);
		} catch (IOException e) {
			_log.error(e);
		} catch (Exception e) {
			_log.error(e);
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServiceStub stub;
		try {
			stub = new ServiceStub("https://wswhomo.afip.gov.ar/wsfev1/service.asmx");

//			FEDummy(stub); // Paso OK
		
			FEAuthRequest autorization = new FEAuthRequest();
			autorization.setCuit(Long.parseLong(cuit));
//			autorization.setToken("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/Pgo8c3NvIHZlcnNpb249IjIuMCI+CiAgICA8aWQgc3JjPSJDTj13c2FhaG9tbywgTz1BRklQLCBDPUFSLCBTRVJJQUxOVU1CRVI9Q1VJVCAzMzY5MzQ1MDIzOSIgZHN0PSJDTj13c2ZlLCBPPUFGSVAsIEM9QVIiIHVuaXF1ZV9pZD0iMzkwMzEwMzI2NiIgZ2VuX3RpbWU9IjE1NDIzNzYxMzQiIGV4cF90aW1lPSIxNTQyNDE5Mzk0Ii8+CiAgICA8b3BlcmF0aW9uIHR5cGU9ImxvZ2luIiB2YWx1ZT0iZ3JhbnRlZCI+CiAgICAgICAgPGxvZ2luIGVudGl0eT0iMzM2OTM0NTAyMzkiIHNlcnZpY2U9IndzZmUiIHVpZD0iU0VSSUFMTlVNQkVSPUNVSVQgMjAxODE1MTI4MzEsIENOPXVvbWFxYSIgYXV0aG1ldGhvZD0iY21zIiByZWdtZXRob2Q9IjIyIj4KICAgICAgICAgICAgPHJlbGF0aW9ucz4KICAgICAgICAgICAgICAgIDxyZWxhdGlvbiBrZXk9IjIwMTgxNTEyODMxIiByZWx0eXBlPSI0Ii8+CiAgICAgICAgICAgIDwvcmVsYXRpb25zPgogICAgICAgIDwvbG9naW4+CiAgICA8L29wZXJhdGlvbj4KPC9zc28+Cg==");
//			autorization.setSign("flUGFv38PUuuX8JzJvc18p2VtViI/H6O7lWMWeFGWTsvkqG40rFJFwNLnoJhfI6MLt17pv01VUkomVzzOzHeyHZkITNphjPgbFGKTU2MZiBz9ZfKjtSUA9Cn9qaZu3uZoU72Jk+Obhw+9F2m6TAcXDqvyB3z9/ZqbDKNv5MdVHg=");

//			FEMonedas(stub, autorization); // Fail 
			
//			FEIva(stub, autorization); // Paso OK
			
//			FEDocsTipo(stub, autorization); // Paso OK
			
//			FEComprobante(stub, autorization); // PAso OK
			
//			FEPtosVenta(stub, autorization); // Paso OK
			
//			FETributos(stub, autorization); // Paso OK
				
//			FEConceptos(stub, autorization); // Fail 
			
//			FEUltimaAutoriz(stub, autorization);
			
//			FESolicitar(stub, autorization);

		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	private static void FESolicitar(ServiceStub stub, FEAuthRequest autorization) throws RemoteException {
		FECAESolicitar fECAESolicitar10 = new FECAESolicitar();
		fECAESolicitar10.setAuth(autorization);
		
		FECAERequest requestCAE = new FECAERequest();
		FECAECabRequest cabecera = new FECAECabRequest();
		cabecera.setCantReg(1);
		cabecera.setCbteTipo(6);  //1 Factura A, 2 Nota de Débito A, 3 Nota de Crédito A, 6 Factura B,
		cabecera.setPtoVta(2);    //2 CAE - Monotributo
		
		requestCAE.setFeCabReq(cabecera);
		
		ArrayOfFECAEDetRequest detalle = new ArrayOfFECAEDetRequest();
//			FECAEDetRequest[] detalleCAE = new  FECAEDetRequest[1];
//			detalle.setFECAEDetRequest(detalleCAE);
//			detalle.addFECAEDetRequest(param);
//			FECAEDetRequest[] detalleCAE = detalle.getFECAEDetRequest();
		
		FECAEDetRequest d = new FECAEDetRequest();
		d.setCbteDesde(2);
		d.setCbteFch("20181115"); //yyyymmdd
		d.setCbteHasta(2);
		d.setCbtesAsoc(null);
		d.setCompradores(null);
		d.setConcepto(2);
		d.setDocNro(Long.valueOf("27249579020"));
		d.setDocTipo(96);  // 96 DNI
		d.setFchServDesde("20181101");
		d.setFchServHasta("20181114");
		d.setFchVtoPago("20181115");
		/**
		 * Suma de los importes del array de IVA.
		 * Para comprobantes tipo C debe ser igual a cero (0).
		 * Para comprobantes tipo Bienes Usados ? Emisor Monotributista no debe informarse o debe ser igual a cero (0).
		 */
		d.setImpIVA(new BigDecimal(21).doubleValue());
		/**
		 * Importe neto gravado. Debe ser menor o igual a Importe total y no puede ser menor a cero. Para comprobantes tipo C este campo corresponde al Importe del Sub Total.
		 * Para comprobantes tipo Bienes Usados ? Emisor Monotributista no debe informarse o debe ser igual a cero (0).
		 */
		d.setImpNeto(new BigDecimal(100).doubleValue()); 
		/**
		 * Importe exento. Debe ser menor o igual a Importe total y no puede ser menor a cero.
		 * Para comprobantes tipo C debe ser igual a cero (0).
		 * Para comprobantes tipo Bienes Usados ? Emisor Monotributista no debe informarse o debe ser igual a cero (0).
		 */
		d.setImpOpEx(new BigDecimal(0).doubleValue());
		/**
		 * Importe total del comprobante, Debe ser igual a Importe neto no gravado + Importe exento + Importe neto gravado + todos los campos de IVA al XX% + Importe de tributos.
		 */
		d.setImpTotal(new BigDecimal(121).doubleValue());
		/**Importe neto no gravado.
		 * Debe ser menor o igual a Importe total y no puede ser menor a cero.
		 * No puede ser mayor al Importe total de la operación ni menor a cero (0).
		 * Para comprobantes tipo C debe ser igual a cero (0). Para comprobantes tipo Bienes Usados ? Emisor Monotributista este campo corresponde al importe subtotal.
		 */
		d.setImpTotConc(0);
		/**
		 * Suma de los importes del array de tributos
		 */
		d.setImpTrib(0);
		ArrayOfAlicIva arrayIva = new ArrayOfAlicIva();
		AlicIva alicIva = new AlicIva();
		alicIva.setBaseImp(new BigDecimal(100).doubleValue());
		alicIva.setId(5);  //3 - 0%, 4 - 10.5%, 5 - 21%, 6 - 27%, 8 - 5%, 9 - 2.5%
		alicIva.setImporte(new BigDecimal(21).doubleValue());
		arrayIva.addAlicIva(alicIva );
		d.setIva(arrayIva );
		d.setMonCotiz(1); // Cotización de la moneda informada. Para PES, pesos argentinos la misma debe ser 1
		d.setMonId("PES"); // PES Pesos Argentinos 20090403 NULL, DOL Dólar Estadounidense 20090403 NULL
		d.setOpcionales(null);
		d.setTributos(null);
//			ArrayOfTributo at = new ArrayOfTributo();
//			Tributo[] tri = new Tributo[1];
//			tri[1].setAlic(tri);
//			tri[2].setBaseImp(tri);
//			tri[3].setDesc("la descripcion");
//			tri[4].setId( (short) 99); //99 Otro
//			tri[5].setImporte(new BigDecimal("10").doubleValue());
//			at.setTributo(tri);
//			d.setTributos(at); //6 Percepción de IVA 20170719 NULL, 99 Otro 20100917 NULL, 

		detalle.addFECAEDetRequest(d);
		
		requestCAE.setFeDetReq(detalle);
		
		fECAESolicitar10.setFeCAEReq(requestCAE);
		
		FECAESolicitarResponse resp = stub.fECAESolicitar(fECAESolicitar10);
		FECAEResponse caeResp = resp.getFECAESolicitarResult();
		FECAECabResponse cabResp = caeResp.getFeCabResp();
		ArrayOfFECAEDetResponse detResp = caeResp.getFeDetResp();
		FECAEDetResponse[] arrayDet = detResp.getFECAEDetResponse();

		System.out.println("CABECERA RESULTADO: " + cabResp.getResultado());	
		System.out.println("DETALLE");
		
		for (int i = 0; i < arrayDet.length; i++) {
			System.out.println(arrayDet[i].getCAE());
			System.out.println(arrayDet[i].getCAEFchVto());
			System.out.println(arrayDet[i].getResultado());
			
			ArrayOfObs arrayObs = arrayDet[i].getObservaciones();
			if(arrayObs!=null){
				Obs[] obs = arrayObs.getObs();
				if(obs!=null){
					for (int j = 0; j < obs.length; j++) {
						System.out.println(obs[i].getCode());
						System.out.println(obs[i].getMsg());
					}
				}
			}
		}
		if(caeResp.getEvents() != null){
			System.err.println("EVENTOS");
			ArrayOfEvt arrayEv = caeResp.getEvents();
			Evt[] evs = arrayEv.getEvt();
			for (int i = 0; i < evs.length; i++) {
				System.out.println(evs[i].getCode());
				System.out.println(evs[i].getMsg());
			}
		}
		
		if(caeResp.getErrors() != null){
			System.err.println("ERRORES");
			ArrayOfErr arrayErr = caeResp.getErrors();
			Err[] err = arrayErr.getErr();
			for (int i = 0; i < err.length; i++) {
				System.out.println(err[i].getCode());
				System.out.println(err[i].getMsg());
			}
		}
		
	}

	private static void FEConceptos(ServiceStub stub, FEAuthRequest autorization) throws RemoteException {
		FEParamGetTiposConcepto fEParamGetTiposConcepto16 = new FEParamGetTiposConcepto();
		fEParamGetTiposConcepto16.setAuth(autorization);
		
		FEParamGetTiposConceptoResponse resp = stub.fEParamGetTiposConcepto(fEParamGetTiposConcepto16);
		
		ConceptoTipoResponse respConc = resp.getFEParamGetTiposConceptoResult();
		
		ArrayOfConceptoTipo array = respConc.getResultGet();
		
		ConceptoTipo[] detalleArray = array.getConceptoTipo();
		
		for (int i = 0; i < detalleArray.length; i++) {
			System.out.println( detalleArray[i].getId());
			System.out.println( detalleArray[i].getDesc());
			System.out.println( detalleArray[i].getFchDesde());
			System.out.println( detalleArray[i].getFchHasta());

		}
	}

	private static void FETributos(ServiceStub stub, FEAuthRequest autorization) throws RemoteException {
		FEParamGetTiposTributos fEParamGetTiposTributos32 = new FEParamGetTiposTributos();
		fEParamGetTiposTributos32.setAuth(autorization);
		
		FEParamGetTiposTributosResponse resp = stub.fEParamGetTiposTributos(fEParamGetTiposTributos32);
		
		FETributoResponse respPtoVta = resp.getFEParamGetTiposTributosResult();
		
		ArrayOfTributoTipo array = respPtoVta.getResultGet();
		
		TributoTipo[] detalleArray = array.getTributoTipo();
		
		for (int i = 0; i < detalleArray.length; i++) {
			System.out.println( detalleArray[i].getId());
			System.out.println( detalleArray[i].getDesc());
			System.out.println( detalleArray[i].getFchDesde());
			System.out.println( detalleArray[i].getFchHasta());

		}
	}

	private static void FEPtosVenta(ServiceStub stub, FEAuthRequest autorization) throws RemoteException {
		FEParamGetPtosVenta fEParamGetPtosVenta8 = new FEParamGetPtosVenta();
		fEParamGetPtosVenta8.setAuth(autorization);
		
		FEParamGetPtosVentaResponse resp = stub.fEParamGetPtosVenta(fEParamGetPtosVenta8);
		
		FEPtoVentaResponse respPtoVta = resp.getFEParamGetPtosVentaResult();
		
		ArrayOfPtoVenta arrayPtos = respPtoVta.getResultGet();
		
		PtoVenta[] ptoVenta = arrayPtos.getPtoVenta();
		
		for (int i = 0; i < ptoVenta.length; i++) {
			System.out.println( ptoVenta[i].getNro());
			System.out.println( ptoVenta[i].getEmisionTipo() );
			System.out.println( ptoVenta[i].getBloqueado() );
			System.out.println( ptoVenta[i].getFchBaja() );
		}
	}

	private static void FEComprobante(ServiceStub stub, FEAuthRequest autorization) throws RemoteException {
		FEParamGetTiposCbte fEParamGetTiposCbte36 = new FEParamGetTiposCbte();
		fEParamGetTiposCbte36.setAuth(autorization);
		
		FEParamGetTiposCbteResponse resp = stub.fEParamGetTiposCbte(fEParamGetTiposCbte36);
		
		CbteTipoResponse respCbte = resp.getFEParamGetTiposCbteResult();
		
		ArrayOfCbteTipo arrayCbte = respCbte.getResultGet();
		
		CbteTipo[] cbteTipo = arrayCbte.getCbteTipo();
		
		for (int i = 0; i < cbteTipo.length; i++) {
			System.out.println( cbteTipo[i].getId() );
			System.out.println( cbteTipo[i].getDesc() );
		}
	}

	private static void FEDocsTipo(ServiceStub stub, FEAuthRequest autorization) throws RemoteException {
		FEParamGetTiposDoc fEParamGetTiposDoc38 = new FEParamGetTiposDoc();
		fEParamGetTiposDoc38.setAuth(autorization);
		
		FEParamGetTiposDocResponse response = stub.fEParamGetTiposDoc(fEParamGetTiposDoc38);
		
		DocTipoResponse docResp = response.getFEParamGetTiposDocResult();
		
		ArrayOfDocTipo arrayDocTipo = docResp.getResultGet(); 
		
		DocTipo[] arrayDocs = arrayDocTipo.getDocTipo();
		
		for (int i = 0; i < arrayDocs.length; i++) {
			System.out.println(arrayDocs[i].getId());
			System.out.println(arrayDocs[i].getDesc());
		}
	}

	private static void FEIva(ServiceStub stub, FEAuthRequest autorization) throws RemoteException {
		FEParamGetTiposIva fEParamGetTiposIva2 = new FEParamGetTiposIva();
		fEParamGetTiposIva2.setAuth(autorization);
		
		FEParamGetTiposIvaResponse resp = stub.fEParamGetTiposIva(fEParamGetTiposIva2 );
		
		IvaTipoResponse respIva = resp.getFEParamGetTiposIvaResult();
		
		ArrayOfIvaTipo arrayIva = respIva.getResultGet();
		
		IvaTipo[] ivaTipo = arrayIva.getIvaTipo();
		
		for (int i = 0; i < ivaTipo.length; i++) {
			System.out.println( ivaTipo[i].getId() + " - " + ivaTipo[i].getDesc() );
			
		}
	}

	private static void FEMonedas(ServiceStub stub, FEAuthRequest autorization) throws RemoteException {
		FEParamGetTiposMonedas fEParamGetTiposMonedas18 = new FEParamGetTiposMonedas();
		
		fEParamGetTiposMonedas18.setAuth(autorization);
		
		FEParamGetTiposMonedasResponse resp = stub.fEParamGetTiposMonedas(fEParamGetTiposMonedas18);
		
		MonedaResponse monResp = resp.getFEParamGetTiposMonedasResult();
		
		if(monResp.getErrors()!=null){
			ArrayOfErr arrayErrores = monResp.getErrors(); 
			Err[] errores =  arrayErrores.getErr();
			
			for (int i = 0; i < errores.length; i++) {
				System.err.println(errores[i].getCode());
				System.err.println(errores[i].getMsg());
			}
			
			System.err.println();
		}else{
		
			ArrayOfMoneda arrayMoneda = monResp.getResultGet();
			Moneda[] listaMoneda = arrayMoneda.getMoneda();
			
			if(listaMoneda!=null){
				
				for (int i = 0; i < listaMoneda.length; i++) {
					Moneda m = listaMoneda[i];
					System.out.println(m.getId());
					System.out.println(m.getDesc());
					System.out.println(m.getFchDesde());
					System.out.println(m.getFchHasta());
				}
			}
		}
	}

	private static void FEDummy(ServiceStub stub) throws RemoteException {
		FEDummy fEDummy14 =  new FEDummy();
		
		FEDummyResponse response = stub.fEDummy(fEDummy14);
		
		System.out.println(response.getFEDummyResult().getAppServer());
		System.out.println(response.getFEDummyResult().getAuthServer());
		System.out.println(response.getFEDummyResult().getDbServer());
	}

//	private static void FEUltimaAutoriz(ServiceStub stub, FEAuthRequest autorization) throws RemoteException {
//		FECompUltimoAutorizado fECompUltimoAutorizado6 = new FECompUltimoAutorizado();
//		fECompUltimoAutorizado6.setAuth(autorization);
//		fECompUltimoAutorizado6.setCbteTipo(6);
//		fECompUltimoAutorizado6.setPtoVta(2);
//		
//		FECompUltimoAutorizadoResponse resp = stub.fECompUltimoAutorizado(fECompUltimoAutorizado6 );
//		FERecuperaLastCbteResponse respUlt = resp.getFECompUltimoAutorizadoResult();
//		System.out.println("Punto Vta:" + respUlt.getPtoVta());
//		System.out.println("Tipo Comp:" + respUlt.getCbteTipo());
//		System.out.println("Nro. Comp:" + respUlt.getCbteNro());
//		
//		if(respUlt.getEvents() != null && respUlt.getEvents().isEvtSpecified()){
//			System.err.println("EVENTOS");
//			ArrayOfEvt arrayEv = respUlt.getEvents();
//			Evt[] evs = arrayEv.getEvt();
//			for (int i = 0; i < evs.length; i++) {
//				System.out.println(evs[i].getCode());
//				System.out.println(evs[i].getMsg());
//			}
//		}
//		
//		if(respUlt.getErrors() != null && respUlt.getErrors().isErrSpecified()){
//			System.err.println("ERRORES");
//			ArrayOfErr arrayErr = respUlt.getErrors();
//			Err[] err = arrayErr.getErr();
//			for (int i = 0; i < err.length; i++) {
//				System.out.println(err[i].getCode());
//				System.out.println(err[i].getMsg());
//			}
//		}
//	}
	
	public FECAEResponse FESolicitarCAE(FEAuthRequest autorization,
			Factura fact) throws RemoteException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar hoy = DateUtils.getCalendarGMTMenos3();
		
		FECAESolicitar fECAESolicitar10 = new FECAESolicitar();
		fECAESolicitar10.setAuth(autorization);
		
		FECAERequest requestCAE = new FECAERequest();
		FECAECabRequest cabecera = new FECAECabRequest();
		cabecera.setCantReg(1); //fact.getDetalles().size()
		cabecera.setCbteTipo(fact.getComprobanteAFIP());
		cabecera.setPtoVta(Integer.parseInt(fact.getSucursal()));    //2 CAE - Monotributo --
		
		requestCAE.setFeCabReq(cabecera);
		
		ArrayOfFECAEDetRequest detalle = new ArrayOfFECAEDetRequest();
		long nroProxFact = Long.parseLong(fact.getNumero());

		FECAEDetRequest d = new FECAEDetRequest();
		d.setCbteDesde(nroProxFact);
		d.setCbteFch(sdf.format(hoy.getTime())); //yyyymmdd
		d.setCbteHasta(nroProxFact);
		d.setCbtesAsoc(null);
		d.setCompradores(null);
		d.setConcepto(2);
		//Empresa
		if(fact.getCliente().getCuit()!=null) {
			d.setDocNro(Long.valueOf(fact.getCliente().getCuit()));
			d.setDocTipo(80);  // 96 DNI // 80 (CUIT)
		} else { //Personas
			d.setDocNro(Long.valueOf(fact.getCliente().getDocumentoNro()));
			d.setDocTipo(96);  // 96 DNI // 80 (CUIT)
		}
		d.setFchServDesde(sdf.format(hoy.getTime()));
		d.setFchServHasta(sdf.format(hoy.getTime()));
		d.setFchVtoPago(sdf.format(hoy.getTime()));
		/**
		 * Suma de los importes del array de IVA.
		 * Para comprobantes tipo C debe ser igual a cero (0).
		 * Para comprobantes tipo Bienes Usados ? Emisor Monotributista no debe informarse o debe ser igual a cero (0).
		 */
		
		if(fact.getLetra().equalsIgnoreCase("A") 
			|| (fact.getLetra().equalsIgnoreCase("B") && fact.getCliente().getTipo().equals(Cliente.TIPOS_CLIENTE.VISITA)  )
		) {
			d.setImpIVA(fact.getIva().doubleValue());
		}else {
			d.setImpIVA(new BigDecimal(0).doubleValue());
		}
		/**
		 * Importe neto gravado. Debe ser menor o igual a Importe total y no puede ser menor a cero. Para comprobantes tipo C este campo corresponde al Importe del Sub Total.
		 * Para comprobantes tipo Bienes Usados ? Emisor Monotributista no debe informarse o debe ser igual a cero (0).
		 */
		if(fact.getLetra().equalsIgnoreCase("A")
			|| (fact.getLetra().equalsIgnoreCase("B") && fact.getCliente().getTipo().equals(Cliente.TIPOS_CLIENTE.VISITA)  )
				) {
			d.setImpNeto(fact.getImporteNeto().doubleValue());
		}else {
			d.setImpNeto(new BigDecimal(0).doubleValue());
		}	
		/**
		 * Importe exento. Debe ser menor o igual a Importe total y no puede ser menor a cero.
		 * Para comprobantes tipo C debe ser igual a cero (0).
		 * Para comprobantes tipo Bienes Usados ? Emisor Monotributista no debe informarse o debe ser igual a cero (0).
		 */
		if(fact.getLetra().equalsIgnoreCase("A")
			|| (fact.getLetra().equalsIgnoreCase("B") && fact.getCliente().getTipo().equals(Cliente.TIPOS_CLIENTE.VISITA)  )	
				) {
			d.setImpOpEx(new BigDecimal(0).doubleValue());
		}else {
			d.setImpOpEx(fact.getImporteTotal().doubleValue()); // ver si va el fact.getImporteExento()
		}		
		/**
		 * Importe total del comprobante, Debe ser igual a Importe neto no gravado + Importe exento + Importe neto gravado + todos los campos de IVA al XX% + Importe de tributos.
		 */
		d.setImpTotal(fact.getImporteTotal().doubleValue());
		/**Importe neto no gravado.
		 * Debe ser menor o igual a Importe total y no puede ser menor a cero.
		 * No puede ser mayor al Importe total de la operación ni menor a cero (0).
		 * Para comprobantes tipo C debe ser igual a cero (0). Para comprobantes tipo Bienes Usados ? Emisor Monotributista este campo corresponde al importe subtotal.
		 */
		d.setImpTotConc(0);
		/**
		 * Suma de los importes del array de tributos
		 */
		d.setImpTrib(0);
		if(fact.getLetra().equalsIgnoreCase("A")
				|| (fact.getLetra().equalsIgnoreCase("B") && fact.getCliente().getTipo().equals(Cliente.TIPOS_CLIENTE.VISITA)  )	
				) {
			ArrayOfAlicIva arrayIva = new ArrayOfAlicIva();
			AlicIva alicIva = new AlicIva();
			alicIva.setBaseImp(fact.getImporteNeto().doubleValue());
			alicIva.setId(5);  //3 - 0%, 4 - 10.5%, 5 - 21%, 6 - 27%, 8 - 5%, 9 - 2.5%
			alicIva.setImporte(fact.getIva().doubleValue());
			arrayIva.addAlicIva(alicIva );
			d.setIva(arrayIva );
		}else {
			d.setIva(null);
		}
	
		d.setMonCotiz(1); // Cotización de la moneda informada. Para PES, pesos argentinos la misma debe ser 1
		d.setMonId("PES"); // PES Pesos Argentinos 20090403 NULL, DOL Dólar Estadounidense 20090403 NULL
		d.setOpcionales(null);
		d.setTributos(null);

		detalle.addFECAEDetRequest(d);
			
		requestCAE.setFeDetReq(detalle);
		
		fECAESolicitar10.setFeCAEReq(requestCAE);
		
		FECAESolicitarResponse resp = stub.fECAESolicitar(fECAESolicitar10);
		FECAEResponse caeResp = resp.getFECAESolicitarResult();
		FECAECabResponse cabResp = caeResp.getFeCabResp();
		ArrayOfFECAEDetResponse detResp = caeResp.getFeDetResp();
		FECAEDetResponse[] arrayDet = detResp.getFECAEDetResponse();

		_log.debug("CABECERA RESULTADO: " + cabResp.getResultado());	
		_log.debug("DETALLE");
		
		if(detResp.getFECAEDetResponse()[0] != null) {
			ArrayOfObs arrayObs = detResp.getFECAEDetResponse()[0].getObservaciones();
			if(arrayObs!=null){
				Obs[] obs = arrayObs.getObs();
				if(obs!=null){
					for (int j = 0; j < obs.length; j++) {
						_log.debug(obs[j].getCode());
						_log.debug(obs[j].getMsg());
					}
				}
			}
		}
		
		for (int i = 0; i < arrayDet.length; i++) {
			
			_log.debug(arrayDet[i].getCAE());
			_log.debug(arrayDet[i].getCAEFchVto());
			_log.debug(arrayDet[i].getResultado());
			
			ArrayOfObs arrayObs = arrayDet[i].getObservaciones();
			if(arrayObs!=null){
				Obs[] obs = arrayObs.getObs();
				if(obs!=null){
					for (int j = 0; j < obs.length; j++) {
						_log.debug(obs[j].getCode());
						_log.debug(obs[j].getMsg());
					}
				}
			}
		}
		if(caeResp.getEvents() != null){
			_log.error("EVENTOS");
			ArrayOfEvt arrayEv = caeResp.getEvents();
			Evt[] evs = arrayEv.getEvt();
			for (int i = 0; i < evs.length; i++) {
				_log.error(evs[i].getCode());
				_log.error(evs[i].getMsg());
			}
		}
		
		if(caeResp.getErrors() != null){
			_log.error("ERRORES");
			ArrayOfErr arrayErr = caeResp.getErrors();
			Err[] err = arrayErr.getErr();
			for (int i = 0; i < err.length; i++) {
				_log.error(err[i].getCode());
				_log.error(err[i].getMsg());
			}
		}
		
		return caeResp;
	}
	
	public String FEUltimaAutoriz(FEAuthRequest autorization, Factura fact) throws RemoteException {
		
		int nroComprobante = 0;
		
		FECompUltimoAutorizado fECompUltimoAutorizado6 = new FECompUltimoAutorizado();
		fECompUltimoAutorizado6.setAuth(autorization);
		fECompUltimoAutorizado6.setCbteTipo(fact.getComprobanteAFIP()); 
		fECompUltimoAutorizado6.setPtoVta(Integer.parseInt(fact.getSucursal()));
		
		FECompUltimoAutorizadoResponse resp = stub.fECompUltimoAutorizado(fECompUltimoAutorizado6 );
		FERecuperaLastCbteResponse respUlt = resp.getFECompUltimoAutorizadoResult();
		_log.debug("Punto Vta:" + respUlt.getPtoVta());
		_log.debug("Tipo Comp:" + respUlt.getCbteTipo());
		_log.debug("Nro. Comp:" + respUlt.getCbteNro());
		
		if(respUlt.getEvents() == null && respUlt.getErrors() == null ) {
			
			nroComprobante  = respUlt.getCbteNro() + 1;
		}
		
		if(respUlt.getEvents() != null && respUlt.getEvents().isEvtSpecified()){
			System.err.println("EVENTOS");
			ArrayOfEvt arrayEv = respUlt.getEvents();
			Evt[] evs = arrayEv.getEvt();
			for (int i = 0; i < evs.length; i++) {
				System.out.println(evs[i].getCode());
				System.out.println(evs[i].getMsg());
			}
		}
		
		if(respUlt.getErrors() != null && respUlt.getErrors().isErrSpecified()){
			System.err.println("ERRORES");
			ArrayOfErr arrayErr = respUlt.getErrors();
			Err[] err = arrayErr.getErr();
			for (int i = 0; i < err.length; i++) {
				System.out.println(err[i].getCode());
				System.out.println(err[i].getMsg());
			}
		}
		
		return Integer.toString(nroComprobante);
	}
}
