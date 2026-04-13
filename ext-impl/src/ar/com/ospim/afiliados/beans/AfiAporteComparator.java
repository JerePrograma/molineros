package ar.com.ospim.afiliados.beans;

import java.util.Comparator;

public class AfiAporteComparator implements Comparator<AfiAporte> {
	
	public int compare(AfiAporte o1, AfiAporte o2) {
		if(null==o1.getFecha_egre()){
			return 1;
		}if(null==o2.getFecha_egre()){
			return 1;
		}else{
			return o1.getFecha_egre().compareTo(o2.getFecha_egre());
		}
	}

}
