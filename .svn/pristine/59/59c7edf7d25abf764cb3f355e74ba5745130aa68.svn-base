package ar.com.ospim.tesoreria.beans.interes;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.portlet.PortletRequest;

import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.afip.service.FeriadosServiceUtil;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysCajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceImpl;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import edu.emory.mathcs.backport.java.util.Arrays;

public class Interes implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6514721875746277069L;
	private static Log _log = LogFactoryUtil.getLog(Interes.class);
	
	private String fechaInicio;
	private String fechaFin;
	private Double interesDia;
	
	public String getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public String getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Double getInteresDia() {
		return interesDia;
	}

	public void setInteresDia(Double interesDia) {
		this.interesDia = interesDia;
	}
	
	public Interes() {
		interesDia=0D;
	}
			
	public static Interes getMapping(ResultSet rs) throws SQLException {
		Interes _interes = new Interes();
		_interes.setFechaInicio(rs.getDate("fechainicio").toString());
		_interes.setFechaFin(rs.getDate("fechafin").toString());
		_interes.setInteresDia(rs.getDouble("interesdia")); 		
		return _interes;
	}	
}
