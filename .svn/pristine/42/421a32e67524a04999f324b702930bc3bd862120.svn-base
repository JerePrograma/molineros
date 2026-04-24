package ar.com.global.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.global.beans.DetalleEscalaSalarial;
import ar.com.global.beans.TablaEscalaSalarial;
import ar.com.global.beans.TablaEscalaSalarial.Camara;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class EscalaSalarialServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(EscalaSalarialServiceImpl.class);
	
	public static Map<Camara, List<DetalleEscalaSalarial>> getEscalasSalariales(Date fecha){
		
		Connection con = null;
		CallableStatement stmt = null;		
		ArrayList<TablaEscalaSalarial> tablasEscalaSalarial= new ArrayList<TablaEscalaSalarial>();
		try {

			String sql = "{call trae_escalas_salariales(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());

			if (null != fecha) {
				stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}			
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {				
				TablaEscalaSalarial escala = TablaEscalaSalarial.getMapping(rs);
				tablasEscalaSalarial.add(escala);
			}			
			
			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return getMapTablaFromList(tablasEscalaSalarial);
		
	}
	
	private static Map<Camara, List<DetalleEscalaSalarial>> getMapTablaFromList(List<TablaEscalaSalarial> lista){
		Map<Camara, List<DetalleEscalaSalarial>> mapResult=new HashMap<Camara, List<DetalleEscalaSalarial>>();
		
		ArrayList<TablaEscalaSalarial> tablasEscalaSalarialCEPA= new ArrayList<TablaEscalaSalarial>();
		ArrayList<TablaEscalaSalarial> tablasEscalaSalarialCAENA= new ArrayList<TablaEscalaSalarial>();
		ArrayList<TablaEscalaSalarial> tablasEscalaSalarialFAIM= new ArrayList<TablaEscalaSalarial>();
		
		for(TablaEscalaSalarial tabla: lista){
			if(tabla.getCamara().equals(Camara.CEPA)){
				tablasEscalaSalarialCEPA.add(tabla);
			}else if(tabla.getCamara().equals(Camara.CAENA)){
				tablasEscalaSalarialCAENA.add(tabla);
			}else if(tabla.getCamara().equals(Camara.FAIM)){
				tablasEscalaSalarialFAIM.add(tabla);
			}
		}
		List<DetalleEscalaSalarial> detalleListCEPA=new ArrayList<DetalleEscalaSalarial>(); 
		if(tablasEscalaSalarialCEPA.size()>0){
			DetalleEscalaSalarial detalle= new DetalleEscalaSalarial();
			detalle.setFechaDesde(tablasEscalaSalarialCEPA.get(0).getFechaDesde());
			detalle.setEscalaSalarial(tablasEscalaSalarialCEPA);
			detalleListCEPA.add(detalle);
		}
		List<DetalleEscalaSalarial> detalleListCAENA=new ArrayList<DetalleEscalaSalarial>();
		if(tablasEscalaSalarialCAENA.size()>0){
			DetalleEscalaSalarial detalle= new DetalleEscalaSalarial();
			detalle.setFechaDesde(tablasEscalaSalarialCAENA.get(0).getFechaDesde());
			detalle.setEscalaSalarial(tablasEscalaSalarialCAENA);
			detalleListCAENA.add(detalle);
		}
		List<DetalleEscalaSalarial> detalleListFAIM=new ArrayList<DetalleEscalaSalarial>();
		if(tablasEscalaSalarialFAIM.size()>0){
			DetalleEscalaSalarial detalle= new DetalleEscalaSalarial();
			detalle.setFechaDesde(tablasEscalaSalarialFAIM.get(0).getFechaDesde());
			detalle.setEscalaSalarial(tablasEscalaSalarialFAIM);
			detalleListFAIM.add(detalle);
		}
		
		mapResult.put(Camara.CEPA, detalleListCEPA);
		mapResult.put(Camara.CAENA, detalleListCAENA);
		mapResult.put(Camara.FAIM, detalleListFAIM);
		
		return mapResult;
		
	}
	
}
