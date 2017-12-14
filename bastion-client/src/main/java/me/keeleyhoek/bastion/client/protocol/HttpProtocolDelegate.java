package me.keeleyhoek.bastion.client.protocol;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import me.keeleyhoek.bastion.ObjectTranscoder;
import me.keeleyhoek.bastion.Response;
import me.keeleyhoek.bastion.client.objects.Token;
import me.keeleyhoek.bastion.client.ProtocolDelegate;

/**
 *
 * @author escortkeel
 */
public class HttpProtocolDelegate extends ProtocolDelegate {

    public static final String CHARSET_NAME = "UTF-8";
    public static final Charset CHARSET = Charset.forName(CHARSET_NAME);
    private final String url;

    public HttpProtocolDelegate(String url) {
        this.url = url.endsWith("/") ? url : url + "/";
    }

    private static void appendEncodedParameter(StringBuilder sb, Parameter p) throws UnsupportedEncodingException {
        sb.append(URLEncoder.encode(p.getKey(), CHARSET_NAME));
        sb.append("=");
        sb.append(URLEncoder.encode(ObjectTranscoder.encodeObject(p.getValue()), CHARSET_NAME));
        sb.append("&");
    }

    @Override
    protected <T> Response<T> doRequest(Token token, String path, Type t, Parameter... params) throws IOException {
        StringBuilder sb = new StringBuilder();
        appendEncodedParameter(sb, new Parameter("token", token == null ? "" : token.getRaw()));
        for (Parameter p : params) {
            appendEncodedParameter(sb, p);
        }
        
        String data = sb.toString().substring(0, sb.toString().length() - 1);
        HttpURLConnection conn = (HttpURLConnection) new URL(url + path).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
        try (OutputStream os = conn.getOutputStream()) {
            os.write(data.getBytes());
        }
        
        return Response.read(new InputStreamReader(conn.getInputStream(), CHARSET), t);
    }
}
