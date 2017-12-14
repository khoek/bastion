package me.keeleyhoek.bastion.server;

import me.keeleyhoek.bastion.Response;
import me.keeleyhoek.bastion.server.objects.Ident;

/**
 *
 * @author escortkeel
 */
public abstract class AuthorisedWebService extends WebService {

    public abstract Response authorisedRespond(RequestContext request) throws ParameterException;

    @Override
    public final Response respond(RequestContext request) throws ParameterException {
        request.get(Ident.class);
        return authorisedRespond(request);
    }
}
