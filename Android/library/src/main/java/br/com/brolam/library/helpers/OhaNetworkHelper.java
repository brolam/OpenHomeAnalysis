package br.com.brolam.library.helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brenomar on 28/12/15.
 */
public class OhaNetworkHelper {
    private static final String DEBUG_TAG = "OhaNetworkHelper: ";

    /**
     * Realizar uma requisição HTTP
     * @param method Informar GET, POST ou outros HTTP methods válidos.
     * @param strUrl Informar uma URL válida
     * @return Uma lista de textos.
     * @throws IOException
     */
    public static List<String> requestHttp(String method, String strUrl) throws IOException {
        ArrayList<String> strings = new ArrayList<>();
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(strUrl);
            httpURLConnection  = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(40000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(40000 /* milliseconds */);
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoInput(true);
            Log.d(DEBUG_TAG, String.format("Method / URL : %s / %s ", method ,  strUrl));
            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + responseCode);
            if ( (responseCode >= 200) && ( (responseCode < 300))  ) {
                inputStream = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    strings.add(line);
                    Log.d(DEBUG_TAG, "Line: " + line);
                }
                reader.close();
            } else {
                strings.add(String.format("HTTP_CODE_ERROR:%s",responseCode));
            }

        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if ( httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
            } catch (Exception e){
                Log.e (DEBUG_TAG, e.getMessage());
            }
        }
        return strings;
    }

    /**
     * Analisar e validar um URL.
     * @param hostName informar o IP ou nome do Host.
     * @param webMethod Informar o Nome do WebMethod.
     * @param params informar os valores dos paramentos.
     * @return texto com a URL.
     */
    public static String parseUrl(String hostName, String webMethod, String... params){
        StringBuilder url = new StringBuilder(String.format("http://%s/%s", hostName, webMethod));
        for(String param: params){
            url.append(String.format("%s/", param));
        }
        return url.toString();
    }

}
