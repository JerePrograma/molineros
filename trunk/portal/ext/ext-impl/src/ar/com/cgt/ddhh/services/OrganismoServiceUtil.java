package ar.com.cgt.ddhh.services;

import java.sql.Connection;
import java.util.List;

import ar.com.cgt.ddhh.beans.Area;
import ar.com.cgt.ddhh.beans.Comentario;
import ar.com.cgt.ddhh.beans.Contacto;
import ar.com.cgt.ddhh.beans.LineaTrabajo;
import ar.com.cgt.ddhh.beans.Organismo;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.model.User;

public class OrganismoServiceUtil {
	
	private static final int AREA=2;
	private static final int ORGANISMO=1;
	
	public static void borrarArea(int id_area) throws Exception{
		OrganismoServiceImpl.getInstance().borrarArea(id_area);		
	}
	
	public static void borrarOrganismo(int id_organismo) throws Exception{
		OrganismoServiceImpl.getInstance().borrarOrganismo(id_organismo);		
	}

	public static void save(Organismo organismo, User user)
			throws Exception {
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnectionCGT();
			connection.setAutoCommit(false);

			int id_organismo = OrganismoServiceImpl.getInstance().save(organismo,
					user.getScreenName(), connection);
			
			List<Contacto> contactos = organismo.getContactos();
			if (contactos != null) {
				for (Contacto p : contactos) {
						OrganismoServiceImpl.getInstance().saveContacto(p, user.getScreenName(),id_organismo,
								connection,ORGANISMO);
				}
				
			}
			List<LineaTrabajo> lineas = organismo.getLineasTrabajo();
			if (lineas != null) {
				for (LineaTrabajo p : lineas) {
						OrganismoServiceImpl.getInstance().saveLineas(p, user.getScreenName(),id_organismo,
								connection, ORGANISMO);
				}
				
			}
			
			List<Comentario> comentarios = organismo.getComentario();
			if (comentarios != null) {
				for (Comentario p : comentarios) {
						OrganismoServiceImpl.getInstance().saveComentarios(p, user.getScreenName(),id_organismo,
								connection, ORGANISMO);
				}
				
			}
			/*

			if (cerrarActa) {
				ActaServiceImpl.getInstance().cerrarActa(acta,
						user.getScreenName(), connection);
			}*/
			connection.commit();
		} catch (Exception e) {
			if(null!=connection){
				connection.rollback();
				throw e;
			}			
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	public static void saveArea(Area area, User user)
			throws Exception {
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnectionCGT();
			connection.setAutoCommit(false);

			int id_organismo = OrganismoServiceImpl.getInstance().saveArea(area,
					user.getScreenName(), connection);
			
			List<Contacto> contactos = area.getContactos();
			if (contactos != null) {
				for (Contacto p : contactos) {
						OrganismoServiceImpl.getInstance().saveContacto(p, user.getScreenName(),id_organismo,
								connection, AREA);
				}
				
			}
			List<LineaTrabajo> lineas = area.getLineasTrabajo();
			if (lineas != null) {
				for (LineaTrabajo p : lineas) {
						OrganismoServiceImpl.getInstance().saveLineas(p, user.getScreenName(),id_organismo,
								connection, AREA);
				}
				
			}
			
			List<Comentario> comentarios = area.getComentario();
			if (comentarios != null) {
				for (Comentario p : comentarios) {
						OrganismoServiceImpl.getInstance().saveComentarios(p, user.getScreenName(),id_organismo,
								connection, AREA);
				}
				
			}
			/*

			if (cerrarActa) {
				ActaServiceImpl.getInstance().cerrarActa(acta,
						user.getScreenName(), connection);
			}*/
			connection.commit();
		} catch (Exception e) {
			if(null!=connection){
				connection.rollback();
				throw e;
			}			
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	public static List<Organismo> getOrganismos(String nombre, String ambito, String linea, String sigla, String orbita) throws Exception{
		return OrganismoServiceImpl.getInstance().getOrganismos(nombre,ambito,linea, sigla, orbita);
		
	}
	
	public static Organismo getOrganismo(int id_organismo) throws Exception{
		return OrganismoServiceImpl.getInstance().getOrganismo(id_organismo);
		
	}
	
	public static Area getArea(int id_area) throws Exception{
		return OrganismoServiceImpl.getInstance().getArea(id_area);
		
	}

	public static void update(Organismo organismo, User user)
			throws Exception {
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnectionCGT();
			connection.setAutoCommit(false);

			int id_organismo = OrganismoServiceImpl.getInstance().update(organismo,
					user.getScreenName(), connection);
			
			//PRIMERO BORRO TODOOO.
			OrganismoServiceImpl.getInstance().deleteContactos(id_organismo,user.getScreenName(),connection, ORGANISMO);
			OrganismoServiceImpl.getInstance().deleteComentarios(id_organismo,user.getScreenName(),connection, ORGANISMO);
			OrganismoServiceImpl.getInstance().deleteLineasTrabajo(id_organismo,user.getScreenName(),connection, ORGANISMO);
			
			//DESPUES LO VUELVO A AGREGAR
			List<Contacto> contactos = organismo.getContactos();
			if (contactos != null) {
				for (Contacto p : contactos) {
						OrganismoServiceImpl.getInstance().saveContacto(p, user.getScreenName(),id_organismo,
								connection, ORGANISMO);
				}
				
			}
			List<LineaTrabajo> lineas = organismo.getLineasTrabajo();
			if (lineas != null) {
				for (LineaTrabajo p : lineas) {
						OrganismoServiceImpl.getInstance().saveLineas(p, user.getScreenName(),id_organismo,
								connection, ORGANISMO);
				}
				
			}
			
			List<Comentario> comentarios = organismo.getComentario();
			if (comentarios != null) {
				for (Comentario p : comentarios) {
						OrganismoServiceImpl.getInstance().saveComentarios(p, user.getScreenName(),id_organismo,
								connection, ORGANISMO);
				}
				
			}
			connection.commit();
		} catch (Exception e) {
			if(null!=connection){
				connection.rollback();
				throw e;
			}			
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	public static void updateArea(Area area, User user)
			throws Exception {
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnectionCGT();
			connection.setAutoCommit(false);

			int id_area = OrganismoServiceImpl.getInstance().updateArea(area,
					user.getScreenName(), connection);
			
			//PRIMERO BORRO TODOOO.
			OrganismoServiceImpl.getInstance().deleteContactos(id_area,user.getScreenName(),connection, AREA);
			OrganismoServiceImpl.getInstance().deleteComentarios(id_area,user.getScreenName(),connection, AREA);
			OrganismoServiceImpl.getInstance().deleteLineasTrabajo(id_area,user.getScreenName(),connection, AREA);
			
			//DESPUES LO VUELVO A AGREGAR
			List<Contacto> contactos = area.getContactos();
			if (contactos != null) {
				for (Contacto p : contactos) {
						OrganismoServiceImpl.getInstance().saveContacto(p, user.getScreenName(),id_area,
								connection, AREA);
				}
				
			}
			List<LineaTrabajo> lineas = area.getLineasTrabajo();
			if (lineas != null) {
				for (LineaTrabajo p : lineas) {
						OrganismoServiceImpl.getInstance().saveLineas(p, user.getScreenName(),id_area,
								connection, AREA);
				}
				
			}
			
			List<Comentario> comentarios = area.getComentario();
			if (comentarios != null) {
				for (Comentario p : comentarios) {
						OrganismoServiceImpl.getInstance().saveComentarios(p, user.getScreenName(),id_area,
								connection, AREA);
				}
				
			}
			connection.commit();
		} catch (Exception e) {
			if(null!=connection){
				connection.rollback();
				throw e;
			}			
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	

}
