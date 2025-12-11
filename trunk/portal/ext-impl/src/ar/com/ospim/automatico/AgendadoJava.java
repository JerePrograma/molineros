package ar.com.ospim.automatico;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.automatico.beans.ReporteAutomatico;

public abstract class AgendadoJava {

	public abstract void correrAgendado(ReporteAutomatico ra);
	
	public abstract HSSFWorkbook getResultados();
}
