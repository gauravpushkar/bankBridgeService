package io.bankbridge;

import io.bankbridge.utils.BanksPropertyHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Objects;

/****
 * @author gauravk
 * Server implementation for Banks application using embedded jetty.
 */
public class BankBridgeJettyServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankBridgeJettyServer.class);
    private static final BanksPropertyHandler BANKS_PROPERTY_HANDLER = BanksPropertyHandler.getInstance();
    private static Server jettyServer;

    public static void main(String args[]) throws Exception {
        int port = parseServerPort(args);
        int derivedPort = port == -1 ? BANKS_PROPERTY_HANDLER.getApplicationProperties().getJettyServerPort() : port;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(derivedPort);
        jettyServer = new Server(inetSocketAddress);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ServletHolder jerseyServletHolder = context.addServlet(ServletContainer.class, "/*");
        jettyServer.setHandler(context);
        jerseyServletHolder.setInitOrder(1);
        jerseyServletHolder.setInitParameter("jersey.config.server.provider.packages", "io.bankbridge");
        try {
            jettyServer.start();
            //This operation happens in a separate thread to avoid any overhead on Application Server
            initRemoteBankServers();
            jettyServer.join();
        } catch (Exception ex) {
            LOGGER.error("Error occurred while starting the server", ex);
            //Can't handle this so let it go to caller
            throw ex;
        } finally {
            jettyServer.destroy();
            stopRemoteBankServers();
        }
    }

    public static void stopJettyServer() {
        //Stop remote bank servers first
        stopRemoteBankServers();
        //stop Jetty
        if (jettyServer == null || !jettyServer.isStarted()) {
            LOGGER.warn("Server handle is null or server is not started");
            return;
        }
        try {
            LOGGER.info("Stopping Jetty server : {}", jettyServer);
            jettyServer.stop();
        } catch (Exception e) {
            LOGGER.warn("Failed to stop an embedded Jetty: {}", jettyServer, e);
        }
    }

    /******
     * This method is to start remote bank servers so that application can work.
     * We all are aware that such kind of binding is IMPOSSIBLE in real world scenario
     * Starting remote end points along with Jetty (Actual app server) will help in executing the flow.
     * In case we want to execute this code with some real endpoints on different servers this method invocation can be
     * flipped with a property
     */
    private static void initRemoteBankServers() {
        //Starting this in a separate thread
        Thread thread = new Thread(() -> {
            //Set setDefaultUncaughtExceptionHandler
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                LOGGER.error("Error occurred while starting remote server", e);
            });
            RemoteBankServers.startServer(BANKS_PROPERTY_HANDLER.getApplicationProperties().getRemoteBankServerPort());
        });
        thread.start();
    }

    /****
     * This method will shutdown spark server when Jetty (Application Server is being shutdown)
     */
    private static void stopRemoteBankServers() {
        Thread thread = new Thread(() -> {
            //Set setDefaultUncaughtExceptionHandler
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                LOGGER.error("Error occurred while stopping remote server", e);
            });
            RemoteBankServers.stopServer();
        });
        thread.start();
    }


    //This capability is introduced specifically to create server instances on different ports for E2E Tests
    private static int parseServerPort(String[] args) {
        int port = -1;
        if (!Objects.isNull(args) && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                //No need to log stack trace as we are catching specific exception
                // Ignore this exception and fallback to default value in the property file
                LOGGER.warn("Error occurred while parsing Jetty Server Port");
            }
        }
        return port;
    }
}

