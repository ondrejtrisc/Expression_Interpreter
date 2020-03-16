package trisc.ondrej.oraculum;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

class MyServer extends AbstractHandler {

    private CommandHandler cmdHandler;
    private Server server;

    MyServer() {

        cmdHandler = new CommandHandler();
        server = null;
    }

    void startServer(int port) {

        server = new Server(port);
        server.setHandler(this);

        try {

            server.start();
            server.join();

        }
        catch (Exception e) {

            throw new Error(e);
        }
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {

        String queryString = request.getQueryString();

        if (queryString != null) {

            String[] queryParts = queryString.split("&");
            for (String queryPart : queryParts) {

                String[] eqParts = queryPart.split("=");
                String key = eqParts[0];
                String val = URLDecoder.decode(eqParts[1], "UTF-8");

                if (key.equals("cmd")) {

                    System.out.println("cmd: " + val);
                    cmdHandler.handleCommand(val);
                }
            }
        }

        // Declare response encoding and types
        response.setContentType("application/javascript;charset=utf-8");
        // Declare response status code
        response.setStatus(HttpServletResponse.SC_OK);

        ArrayList<String> result = cmdHandler.getResult();
        System.out.println("result:");
        for (String line : result) { System.out.println(line); }

        result.add("");
        Collections.reverse(result);

        for (String line : result) {

            response.getWriter().println("serverUpdate('" + line + "');");
        }

        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }
}
