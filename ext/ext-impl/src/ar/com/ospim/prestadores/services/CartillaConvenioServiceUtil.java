package ar.com.ospim.prestadores.services;

import java.util.List;

import ar.com.ospim.prestadores.beans.BusquedaCartillaConvenioFiltro;
import ar.com.ospim.prestadores.beans.CartillaConvenioRow;

import com.liferay.portal.SystemException;

public class CartillaConvenioServiceUtil {

    private static CartillaConvenioServiceImpl instance = null;

    public static CartillaConvenioServiceImpl getInstance() {
        if (instance == null) {
            instance = new CartillaConvenioServiceImpl();
        }
        return instance;
    }

    public static List<CartillaConvenioRow> buscarCartillaConvenioPorPlan(
            BusquedaCartillaConvenioFiltro filtro) throws SystemException {
        return getInstance().buscarCartillaConvenioPorPlan(filtro);
    }
}