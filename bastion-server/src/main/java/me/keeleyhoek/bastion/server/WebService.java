package me.keeleyhoek.bastion.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.keeleyhoek.bastion.Response;

/**
 *
 * @author khoek
 */
public abstract class WebService extends HttpServlet {
    
    public static final String SERVICESUFFIX = "Service";

    public static String toSlug(Class<? extends WebService> clazz) {
        String name = clazz.getSimpleName();
        return (name.endsWith(SERVICESUFFIX) ? name.substring(0, name.length() - SERVICESUFFIX.length()) : name).toLowerCase();
    }

    public abstract Response respond(RequestContext request) throws ParameterException;

    private void handle(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Response response;
        
        RequestContext request = RequestContext.begin(new MapParameterProvider(httpRequest.getParameterMap()));

        try {
            response = respond(request);
        } catch (ParameterException ex) {
            response = Responses.error(ex.getStatusCode(), ex.getParameter());
        } catch (Exception ex) {
            request.abort();

            throw ex;
        }

        try (PrintWriter out = httpResponse.getWriter()) {
            Response.send(out, response);
        } catch (Exception ex) {
            request.abort();

            throw new RuntimeException("exception while servicing: " + getServletInfo(), ex);
        }

        request.end();
    }

    @Override
    public final String getServletName() {
        return toSlug(getClass());
    }

    @Override
    public final String getServletInfo() {
        return toSlug(getClass());
    }

    @Override
    protected final void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws ServletException, IOException {
        handle(httpRequest, httpResponse);
    }

    @Override
    protected final void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws ServletException, IOException {
        handle(httpRequest, httpResponse);
    }

    private static class MapParameterProvider implements ParameterProvider {

        private final Map<String, String[]> params;

        public MapParameterProvider(Map<String, String[]> params) {
            this.params = params;
        }

        @Override
        public String getRawParameter(String key) {
            String[] raw = params.get(key);
            if (raw != null && raw.length == 1) {
                return raw[0];
            }
            return null;
        }
    }
}
