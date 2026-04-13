package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

public class RespuestaPreAutorizPSDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2288133928373338033L;
	private static Log _log = LogFactoryUtil.getLog(AfiliacionPrevencionDTO.class);
	
	protected Integer authorizationProposalNumber; 
	protected Integer transactionId; 
	protected String authorizationStatus;
	protected String tributaryCodeNumber;
	protected String finishDate;
	protected String medicalPractice;
	protected String medicalPracticeDescription;
	protected String medicalPracticeStatus;
	protected String afiliadoApeyNom;
	protected Seccional seccional;
	protected String deleteReason;
	protected String externalObservations;
	
	public RespuestaPreAutorizPSDTO(){
		super();
	}
	
	public RespuestaPreAutorizPSDTO(String line) {
		super();
		
		_log.debug(line);
		String[] linea = line.split("\\|");
//		AuthorizationProposalNumber | TransactionId | AuthorizationStatus | TributaryCodeNumber | FinishDate | MedicalPractice | MedicalPracticeDescription | MedicalPracticeStatus | DeleteReason
//		576719 |  | P | 27137348522 |  | 070208 | Tratamiento quirurgico de los aneurismas de la aorta ascendente o descendente. | P | motivo rechazo | obs externas
		
		this.authorizationProposalNumber = linea[0] != null && linea[0].trim().length() > 0 ? Integer.parseInt(linea[0].trim()) : null;
		this.transactionId = linea[1] != null && linea[1].trim().length() > 0 ? Integer.parseInt(linea[1].trim()) : null;
		this.authorizationStatus = linea[2].trim();
		this.tributaryCodeNumber = linea[3].trim();
		this.finishDate = linea[4].trim();
		this.medicalPractice = linea[5].trim();
		this.medicalPracticeDescription = linea[6].trim();
		this.medicalPracticeStatus = linea[7].trim();
		this.deleteReason = linea[8].trim();
		this.externalObservations = linea[9].trim();
		
	}

	public Integer getAuthorizationProposalNumber() {
		return authorizationProposalNumber;
	}

	public void setAuthorizationProposalNumber(Integer authorizationProposalNumber) {
		this.authorizationProposalNumber = authorizationProposalNumber;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public String getAuthorizationStatus() {
		return authorizationStatus;
	}

	public void setAuthorizationStatus(String authorizationStatus) {
		this.authorizationStatus = authorizationStatus;
	}

	public String getTributaryCodeNumber() {
		return tributaryCodeNumber;
	}

	public void setTributaryCodeNumber(String tributaryCodeNumber) {
		this.tributaryCodeNumber = tributaryCodeNumber;
	}

	public String getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}

	public String getMedicalPractice() {
		return medicalPractice;
	}

	public void setMedicalPractice(String medicalPractice) {
		this.medicalPractice = medicalPractice;
	}

	public String getMedicalPracticeDescription() {
		return medicalPracticeDescription;
	}

	public void setMedicalPracticeDescription(String medicalPracticeDescription) {
		this.medicalPracticeDescription = medicalPracticeDescription;
	}

	public String getMedicalPracticeStatus() {
		return medicalPracticeStatus;
	}

	public void setMedicalPracticeStatus(String medicalPracticeStatus) {
		this.medicalPracticeStatus = medicalPracticeStatus;
	}

	public String getAfiliadoApeyNom() {
		return afiliadoApeyNom;
	}

	public void setAfiliadoApeyNom(String afiliadoApeyNom) {
		this.afiliadoApeyNom = afiliadoApeyNom;
	}

	public Seccional getSeccional() {
		return seccional;
	}

	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}

	public String getExternalObservations() {
		return externalObservations;
	}

	public void setExternalObservations(String externalObservations) {
		this.externalObservations = externalObservations;
	}

	
	
}
