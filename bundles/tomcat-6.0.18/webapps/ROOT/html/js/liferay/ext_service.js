Liferay.Service.register("Liferay.Service.ActualizaAfiliacionPrevencion", "ar.com.ospim.webservice.actualizaCredencialPrevencion.service");

Liferay.Service.registerClass(
	Liferay.Service.ActualizaAfiliacionPrevencion, "ActuCredenPrevencion",
	{
		actualizarCredencialBeneficiario: true
	}
);

Liferay.Service.register("Liferay.Service.TestConexion", "ar.com.ospim.webservice.test.service");

Liferay.Service.registerClass(
	Liferay.Service.TestConexion, "TestHola",
	{
		getSaludo: true
	}
);