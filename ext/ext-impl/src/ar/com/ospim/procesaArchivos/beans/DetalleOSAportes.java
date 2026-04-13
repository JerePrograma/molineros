package ar.com.ospim.procesaArchivos.beans;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetalleOSAportes {
	private String organismo_destino;		//4
	private String concepto_transf;			//3
	private BigDecimal importe;				//15
	private String debito_credito;			//1
	private Date fecha_transf;				//10
	private Date fecha_recauda;				//10
	private String cuit_contrib;			//11
	private Date periodo;					//4
	private String num_obligacion;				//12
	private String sec_obligacion;				//3
	private String cuit_aportante;			//11
	private String banco;					//3
	private String cod_sucur;				//3
	private String zona;					//2
	private int porc_reduccion;				//2
	private int porc_reduccion2;			//1
	private int porc_reduccion3;			//1
	private String grupo_fliar;				//2
	private String tipo_pago;				//1
	private String marca_aprop;				//1 P parcial C cumplida
	
	public DetalleOSAportes(String line) throws ParseException{
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfPeriodo= new SimpleDateFormat("yyMM");
		this.organismo_destino=line.substring(0,4).trim();
		this.concepto_transf=line.substring(4,7).trim();
		this.importe= new BigDecimal(line.substring(7,20)+"."+line.substring(20,22));
		this.debito_credito=line.substring(22,23).trim();
		this.fecha_transf=sdf.parse(line.substring(23,33));
		this.fecha_recauda=sdf.parse(line.substring(33,43));
		this.cuit_contrib=line.substring(43,54).trim();
		this.periodo=sdfPeriodo.parse(line.substring(54,58));
		this.num_obligacion=line.substring(58,70).trim();
		this.sec_obligacion=line.substring(70,73).trim();
		this.cuit_aportante=line.substring(73,84).trim();
		this.banco=line.substring(84,87).trim();
		this.cod_sucur=line.substring(87,90).trim();
		this.zona=line.substring(90,92).trim();
		String porc_red1 = line.substring(92,94).trim();
		if (porc_red1.equals("")){
			porc_red1 = "0";
		}
		try{
		this.porc_reduccion=Integer.parseInt(porc_red1);
		}catch(NumberFormatException ne){
			this.porc_reduccion=0;
		}
		String porc_red2 = line.substring(94,95).trim();
		if (porc_red2.equals("")){
			porc_red2 = "0";
		}
		try{
			this.porc_reduccion2=Integer.parseInt(porc_red2);
		}catch(NumberFormatException ne){
			this.porc_reduccion2=0;
		}
		String por_red3 = line.substring(95,96).trim();		
		if (por_red3.equals("")){
			por_red3 = "0";
		}
		try{
			this.porc_reduccion3=Integer.parseInt(por_red3);
		}catch(NumberFormatException ne){
			this.porc_reduccion3=0;
		}
		if (line.length() > 96){
			this.grupo_fliar=line.substring(96,97).trim();
		}
		if (line.length() > 98){
			this.tipo_pago=line.substring(98,99).trim();
		}
		if (line.length() > 99){
			this.marca_aprop=line.substring(99,100).trim();
		}
		
		if (debito_credito!= null && debito_credito.trim().toUpperCase().equals("D")){
			importe = importe.negate();
		}
	}
	
	public String getOrganismo_destino() {
		return organismo_destino;
	}
	public void setOrganismo_destino(String organismoDestino) {
		organismo_destino = organismoDestino;
	}
	public String getConcepto_transf() {
		return concepto_transf;
	}
	public void setConcepto_transf(String conceptoTransf) {
		concepto_transf = conceptoTransf;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	public String getDebito_credito() {
		return debito_credito;
	}
	public void setDebito_credito(String debitoCredito) {
		debito_credito = debitoCredito;
	}
	public Date getFecha_transf() {
		return fecha_transf;
	}
	public void setFecha_transf(Date fechaTransf) {
		fecha_transf = fechaTransf;
	}
	public Date getFecha_recauda() {
		return fecha_recauda;
	}
	public void setFecha_recauda(Date fechaRecauda) {
		fecha_recauda = fechaRecauda;
	}
	public String getCuit_contrib() {
		return cuit_contrib;
	}
	public void setCuit_contrib(String cuitContrib) {
		cuit_contrib = cuitContrib;
	}
	public Date getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}
	public String getNum_obligacion() {
		return num_obligacion;
	}
	public void setNum_obligacion(String numObligacion) {
		num_obligacion = numObligacion;
	}
	public String getSec_obligacion() {
		return sec_obligacion;
	}
	public void setSec_obligacion(String secObligacion) {
		sec_obligacion = secObligacion;
	}
	public String getCuit_aportante() {
		return cuit_aportante;
	}
	public void setCuit_aportante(String cuitAportante) {
		cuit_aportante = cuitAportante;
	}
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
	public String getCod_sucur() {
		return cod_sucur;
	}
	public void setCod_sucur(String codSucur) {
		cod_sucur = codSucur;
	}
	public String getZona() {
		return zona;
	}
	public void setZona(String zona) {
		this.zona = zona;
	}
	public int getPorc_reduccion() {
		return porc_reduccion;
	}
	public void setPorc_reduccion(int porcReduccion) {
		porc_reduccion = porcReduccion;
	}
	public int getPorc_reduccion2() {
		return porc_reduccion2;
	}
	public void setPorc_reduccion2(int porcReduccion2) {
		porc_reduccion2 = porcReduccion2;
	}
	public int getPorc_reduccion3() {
		return porc_reduccion3;
	}
	public void setPorc_reduccion3(int porcReduccion3) {
		porc_reduccion3 = porcReduccion3;
	}
	public String getGrupo_fliar() {
		return grupo_fliar;
	}
	public void setGrupo_fliar(String grupoFliar) {
		grupo_fliar = grupoFliar;
	}
	public String getTipo_pago() {
		return tipo_pago;
	}
	public void setTipo_pago(String tipoPago) {
		tipo_pago = tipoPago;
	}
	public String getMarca_aprop() {
		return marca_aprop;
	}
	public void setMarca_aprop(String marcaAprop) {
		marca_aprop = marcaAprop;
	}
	
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("ORG DEST: "+this.organismo_destino);
		sb.append("\nCONCEPTO TRANSF: "+this.concepto_transf);
		sb.append("\nIMPORTE: "+this.importe);
		sb.append("\nDeb/Cred: "+this.debito_credito);
		sb.append("\nFecha_transf: "+this.fecha_transf);
		sb.append("\nFecha_recauda: "+this.fecha_recauda);
		sb.append("\nCuit Contrib: "+this.cuit_contrib);
		sb.append("\nPeriodo: "+this.periodo);
		sb.append("\nNum. Obliga: "+this.num_obligacion);
		sb.append("\nSec. Obliga: "+this.sec_obligacion);
		sb.append("\nCuit Aportante: "+this.cuit_aportante);
		sb.append("\nBanco: "+this.banco);
		sb.append("\nSucursal: "+this.cod_sucur);
		sb.append("\nZona: "+this.zona);
		sb.append("\nPorc. Reducc: "+this.porc_reduccion);
		sb.append("\nPorc. Reducc2: "+this.porc_reduccion2);
		sb.append("\nPorc. Reducc3: "+this.porc_reduccion3);
		sb.append("\nGrupo Fliar.: "+this.grupo_fliar);
		sb.append("\nTipo Pago: "+this.tipo_pago);
		sb.append("\nMarca Aprop: "+this.marca_aprop);		
		return sb.toString();
	}
	
}
