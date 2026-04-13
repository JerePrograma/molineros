package ar.com.ospim.login.exception;

import com.liferay.portal.PortalException;


public class UsuarioConCoordenadasException extends PortalException {

        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		public UsuarioConCoordenadasException() {
                super();
        }

        public UsuarioConCoordenadasException(String msg) {
                super(msg);
        }

        public UsuarioConCoordenadasException(String msg, Throwable cause) {
                super(msg, cause);
        }

        public UsuarioConCoordenadasException(Throwable cause) {
                super(cause);
        }

}
