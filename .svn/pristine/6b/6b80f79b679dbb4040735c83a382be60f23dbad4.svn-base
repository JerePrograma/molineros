package ar.com.uoma.beans;

import java.util.Comparator;

public class IncidenteComparator implements Comparator<Incidente> {

	public int compare(Incidente o1, Incidente o2) {
		int result = 0;
		int resultDom = 0;
		int resultSeg = 0;
		if (o1.getFecha().compareTo(o2.getFecha()) != 0) {
			result = -1;
		}
		if (o1.getIdSeccional() != o2.getIdSeccional()) {
			result = -1;
		}
		if (!o1.getAfiliado().getCuil_titular()
				.equals(o2.getAfiliado().getCuil_titular())) {
			result = -1;
		}
		if (o1.getAfiliado().getInte() != o2.getAfiliado().getInte()) {
			result = -1;
		}
		if (!o1.getDetalleIncidente().trim()
				.equals(o2.getDetalleIncidente().trim())) {
			result = -1;
		}
		if ((null != o1.getLugarIncidente() && null == o2.getLugarIncidente())
				|| (null == o1.getLugarIncidente() && null != o2
						.getLugarIncidente())) {
			resultDom = -2;
		} else {

			if (o1.getLugarIncidente().getProvinciaId() != o2
					.getLugarIncidente().getProvinciaId()) {
				resultDom = -2;
			}
			if (o1.getLugarIncidente().getLocalidadId() != o2
					.getLugarIncidente().getLocalidadId()) {
				resultDom = -2;
			}

			if (!o1.getLugarIncidente().getCalle().trim()
					.equals(o2.getLugarIncidente().getCalle().trim())) {
				resultDom = -2;
			}
			if (!o1.getLugarIncidente().getNumero().trim()
					.equals(o2.getLugarIncidente().getNumero().trim())) {
				resultDom = -2;
			}
			if (null != o1.getLugarIncidente().getPiso()
					&& !o1.getLugarIncidente().getPiso().trim()
							.equals(o2.getLugarIncidente().getPiso().trim())) {
				resultDom = -2;
			}
			if (null != o1.getLugarIncidente().getDepto()
					&& !o1.getLugarIncidente().getDepto().trim()
							.equals(o2.getLugarIncidente().getDepto().trim())) {
				resultDom = -2;
			}
			if (null != o1.getLugarIncidente().getPostal_codi()
					&& !o1.getLugarIncidente()
							.getPostal_codi()
							.trim()
							.equals(o2.getLugarIncidente().getPostal_codi()
									.trim())) {
				resultDom = -2;
			}
			if (null != o1.getLugarIncidente().getObservaciones()
					&& !o1.getLugarIncidente()
							.getObservaciones()
							.trim()
							.equals(o2.getLugarIncidente().getObservaciones()
									.trim())) {
				resultDom = -2;
			}
		}
		if (null != o2.getSeguimientoIncidenteNuevo()
				&& !o2.getSeguimientoIncidenteNuevo().equals("")) {
			resultSeg = -4;
		}

		return result + resultDom + resultSeg;
	}
}
