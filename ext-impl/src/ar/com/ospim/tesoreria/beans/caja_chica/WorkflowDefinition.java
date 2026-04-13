package ar.com.ospim.tesoreria.beans.caja_chica;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;

import edu.emory.mathcs.backport.java.util.Arrays;

public class WorkflowDefinition implements Serializable {
	private static final long serialVersionUID = -2548638930198948748L;
	
	private int id;
	private String descripcion;
	private Date fecha;
		
	
	public WorkflowDefinition() {
	}

	public WorkflowDefinition(int id, String descripcion) {
		super();
		this.id = id;
		this.descripcion = descripcion;
	}

	public WorkflowDefinition(int id, String descripcion, Date fecha) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.fecha = fecha;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

		
}
