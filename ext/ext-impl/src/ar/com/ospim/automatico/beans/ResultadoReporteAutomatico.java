package ar.com.ospim.automatico.beans;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultadoReporteAutomatico {
	private List<String> nombres = new ArrayList<String>();
	private List<ItemResultadoReporteAutomatico> items = new ArrayList<ItemResultadoReporteAutomatico>();

	public void setInitialInfo(ResultSetMetaData resultSetMetaData)
			throws SQLException {
		int nroColumnas = resultSetMetaData.getColumnCount();
		for (int i = 1; i <= nroColumnas; i++) {
			String name = resultSetMetaData.getColumnName(i);
			getNombres().add(name);
		}
	}

	public void addItem(ItemResultadoReporteAutomatico item) {
		items.add(item);
	}

	public void setItems(List<ItemResultadoReporteAutomatico> items) {
		this.items = items;
	}

	public List<ItemResultadoReporteAutomatico> getItems() {
		return items;
	}

	public void setNombres(List<String> nombres) {
		this.nombres = nombres;
	}

	public List<String> getNombres() {
		return nombres;
	}

	public static class ItemResultadoReporteAutomatico {
		private List<Object> objects = new ArrayList<Object>();

		public static ItemResultadoReporteAutomatico getMapping(ResultSet rs)
				throws SQLException {
			ItemResultadoReporteAutomatico r = new ItemResultadoReporteAutomatico();
			ResultSetMetaData metaData = rs.getMetaData();
			int nroColumnas = metaData.getColumnCount();
			for (int i = 1; i <= nroColumnas; i++) {
				Object obj = rs.getObject(i);
				r.addObject(obj);
			}
			return r;
		}

		public void addObject(Object object) {
			objects.add(object);
		}

		public void setObjects(List<Object> objects) {
			this.objects = objects;
		}

		public List<Object> getObjects() {
			return objects;
		}
	}
}
