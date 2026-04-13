package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Seccional;

public class TratamientoOdo extends Reintegro{
	
	public TratamientoOdo() {

	}

	public TratamientoOdo(Reintegro reintegro){
		
	}
	
	/**
	 * @return the id_reintegro
	 */	
	public TratamientoOdo(Date fecha, Date periodo, int id_seccional, String cuil_titular, int inte, String descripcion,
			int id_reintegro, String tipo_reintegro, Date fecha_baja, String usr_baja, String entidad, int id_plan, 
			String nombre_plan, Date fecha_baja_afil, int id_orden_pago, BigInteger chequeOp, Date fechaOp) {
		super.fecha = fecha;
		super.periodo = periodo;		
		super.id_reintegro = id_reintegro;
		super.tipo_reintegro = tipo_reintegro;		
		super.afiliado = new Afiliado();
		super.afiliado.setCuil_titular(cuil_titular);
		super.afiliado.setInte(inte);
		super.afiliado.setUltimo_plan(new Plan(id_plan, nombre_plan));
		super.afiliado.setBaja_fecha(fecha_baja_afil);
		Seccional seccional = new Seccional();
		seccional.setId_seccional(id_seccional);		
		seccional.setDescripcion(descripcion);
		super.setId_seccional(id_seccional);
		super.afiliado.setSeccional(seccional);
		super.baja_fecha = fecha_baja;
		super.baja_usr = usr_baja;
		super.entidad = entidad;
		super.estado = id_orden_pago != 0 ? 2 : 0; //estado liquidado 2, cargado 0		
		super.idOP = id_orden_pago;
		super.chequeOP = chequeOp;
		super.fechaOP = fechaOp;
	}
	
	public TratamientoOdo(Date fecha, Date periodo, int id_seccional, String cuil_titular, int inte, String descripcion,
			int id_reintegro, String tipo_reintegro, Date fecha_baja, String usr_baja, String entidad, int id_plan, 
			String nombre_plan, Date fecha_baja_afil, int id_orden_pago, BigInteger chequeOp, Date fechaOp, int estado) {
		super.fecha = fecha;
		super.periodo = periodo;		
		super.id_reintegro = id_reintegro;
		super.tipo_reintegro = tipo_reintegro;		
		super.afiliado = new Afiliado();
		super.afiliado.setCuil_titular(cuil_titular);
		super.afiliado.setInte(inte);
		super.afiliado.setUltimo_plan(new Plan(id_plan, nombre_plan));
		super.afiliado.setBaja_fecha(fecha_baja_afil);
		Seccional seccional = new Seccional();
		seccional.setId_seccional(id_seccional);		
		seccional.setDescripcion(descripcion);
		super.setId_seccional(id_seccional);
		super.afiliado.setSeccional(seccional);
		super.baja_fecha = fecha_baja;
		super.baja_usr = usr_baja;
		super.entidad = entidad;
		super.estado = id_orden_pago != 0 ? 2 : 0; //estado liquidado 2, cargado 0		
		super.idOP = id_orden_pago;
		super.chequeOP = chequeOp;
		super.fechaOP = fechaOp;
		super.estado = estado;
	}

	public TratamientoOdo(int id_reintegro, BigDecimal importe) {
		super.id_reintegro = id_reintegro;
		super.importeTotal = importe;
	}	
}