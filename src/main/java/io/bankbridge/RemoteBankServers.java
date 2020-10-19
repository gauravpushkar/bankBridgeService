package io.bankbridge;

import static spark.Spark.*;

public class RemoteBankServers {

    public static void startServer(int port) {

		port(port);

		get("/rbb", (request, response) -> "{\n" +
				"\"bic\":\"1234\",\n" +
				"\"name\":\"Royal Bank of Boredom\",\n" +
				"\"countryCode\":\"GB\",\n" +
				"\"auth\":\"OAUTH\"\n" +
				"}");
		get("/cs", (request, response) -> "{\n" +
				"\"bic\":\"5678\",\n" +
				"\"name\":\"Credit Sweets\",\n" +
				"\"countryCode\":\"CH\",\n" +
				"\"auth\":\"OpenID\"\n" +
				"}");
		get("/bes", (request, response) -> "{\n" +
				"\"bic\":\"9870\",\n" +
				"\"name\":\"Banco de espiritu santo\",\n" +
				"\"countryCode\":\"PT\",\n" +
				"\"auth\":\"SSL\"\n" +
				"}");
	}

	public static void stopServer()
	{
		stop();
	}

}