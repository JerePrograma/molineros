package ar.com.ospim.prestadores.services;

import java.io.File;
import java.util.List;

public class ImportarCartillaSOPServiceUtil {

    private static final ImportarCartillaSOPServiceImpl service = new ImportarCartillaSOPServiceImpl();

    private ImportarCartillaSOPServiceUtil() {
    }

    public static int importarCartillaSOP(File archivo) throws Exception {

        return service.importarCartillaSOP(archivo);
    }

    public static List<Object[]> getImportacionesCartillaSOP() throws Exception {

        return service.getImportacionesCartillaSOP();
    }
}
