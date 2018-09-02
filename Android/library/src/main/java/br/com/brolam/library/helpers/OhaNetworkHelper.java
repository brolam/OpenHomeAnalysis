package br.com.brolam.library.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brenomar on 28/12/15.
 */
public class OhaNetworkHelper {
    private static final String DEBUG_TAG = "OhaNetworkHelper: ";
    private static Network ohaNetworkConnected = null;

    /**
     * Realizar uma requisição HTTP
     *
     * @param urlConnection Informar a conexão conforme a rede que o registrador está conectado;
     * @param method        Informar GET, POST ou outros HTTP methods válidos;
     * @return Uma lista de textos.
     * @throws IOException
     */
    public static List<String> requestHttp(URLConnection urlConnection, String method) throws IOException {
        ArrayList<String> strings = new ArrayList<>();
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setReadTimeout(40000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(40000 /* milliseconds */);
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoInput(true);
            Log.d(DEBUG_TAG, String.format("Method / URL : %s / %s ", method, urlConnection.getURL()));
            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + responseCode);
            if ((responseCode >= 200) && ((responseCode < 300))) {
                inputStream = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    strings.add(line);
                    Log.d(DEBUG_TAG, "Line: " + line);
                }
                reader.close();
            } else {
                strings.add(String.format("HTTP_CODE_ERROR:%s", responseCode));
            }

        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e(DEBUG_TAG, e.getMessage());
            }
        }
        return strings;
    }

    /**
     * Realizar uma requisição HTTP na rede que o registrador de energia está conectado.
     * Também define a rede das próximas requisições;
     *
     * @param context Informar um contexto válido;
     * @param method  Informar GET, POST ou outros HTTP methods válidos;
     * @return Uma lista de textos.
     * @throws IOException
     */
    public static List<String> requestHttp(Context context, String method, String strUrl) throws IOException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return requestHttp(new URL(strUrl).openConnection(), method);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (ohaNetworkConnected != null) {
                try {
                    Log.d(DEBUG_TAG, "requestHttp on ohaNetworkConnected: " + ohaNetworkConnected.toString());
                    return requestHttp(ohaNetworkConnected.openConnection(new URL(strUrl)), method);
                } catch (Exception e) {
                    Log.d(DEBUG_TAG, "requestHttp fail on network : " + ohaNetworkConnected.toString());
                    ohaNetworkConnected = null;
                }
            }
            for (Network network : connMgr.getAllNetworks()) {
                try {
                    List<String> result = requestHttp(network.openConnection(new URL(strUrl)), method);
                    ohaNetworkConnected = network;
                    Log.d(DEBUG_TAG, "Set ohaNetworkConnected : " + ohaNetworkConnected.toString());
                    return result;
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, e.getMessage());
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Analisar e validar um URL.
     *
     * @param hostName  informar o IP ou nome do Host.
     * @param webMethod Informar o Nome do WebMethod.
     * @param params    informar os valores dos paramentos.
     * @return texto com a URL.
     */
    public static String parseUrl(String hostName, String webMethod, String... params) {
        StringBuilder url = new StringBuilder(String.format("http://%s/%s", hostName, webMethod));
        for (String param : params) {
            url.append(String.format("%s/", param));
        }
        return url.toString();
    }
}
