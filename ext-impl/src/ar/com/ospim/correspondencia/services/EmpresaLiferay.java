package ar.com.ospim.correspondencia.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.liferay.portal.model.Organization;

public class EmpresaLiferay {

	Organization empresa ;
	List<SectorLiferay> sectores ;
	
	public Organization getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Organization empresa) {
		this.empresa = empresa;
	}
	public List<SectorLiferay> getSectores() {
		
		Collections.sort(sectores, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				SectorLiferay o11 = (SectorLiferay) o1;
				SectorLiferay o22 = (SectorLiferay) o2;
				
				return o11.getSector().getName().compareTo(o22.getSector().getName()) ;
			}
		});
		
		return sectores;
	}
	public void setSectores(List<SectorLiferay> sectores) {
		this.sectores = sectores;
	}
	
	
	
	
}
