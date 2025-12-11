package ar.com.ospim.afiliados.beans;

import java.util.Comparator;

public class AfiliadoInteComparator implements Comparator<Afiliado> {
	
	public int compare(Afiliado o1, Afiliado o2) {
		if(o1.getInte()>o2.getInte()){
			return 1;		
		}else if(o1.getInte()<o2.getInte()){
			return -1;
		}else{
			return 0;
		}
	}

}
