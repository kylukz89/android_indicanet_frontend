package com.rede.indicanetapp.JSON;


import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Recebe um JSON enviada do servidor para a aplicação
 * RETORNO:
 * 0                      - null
 * 1                      - JSON carregado
 *
 * @author Igor Maximo
 * @criado 19/02/2019
 * @editado 02/03/2019
 */
public class RecebeJson {
    URL url = null;
    HttpURLConnection urlConnection = null;
    InputStream in = null;

    public String retornaJSON(String stringUrl, Uri.Builder builderAppends) { // teste via post

        try {
            url = new URL(stringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.setReadTimeout(VariaveisGlobais.HTTP_TIME_OUT);
            //urlConnection.setConnectTimeout(VariaveisGlobais.HTTP_URL_CONNECTION);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


            Uri.Builder builder = builderAppends;//new Uri.Builder().appendQueryParameter(nomeParametro, valorParametro);

            String query = "";
            if (builderAppends != null) {
                query = builder.build().getEncodedQuery();
            }


            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();


            /*Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("firstParam", paramValue1)
                    .appendQueryParameter("secondParam", paramValue2)
                    .appendQueryParameter("thirdParam", paramValue3);
            String query = builder.build().getEncodedQuery();*/

            // Send post request

         /*   urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(urlParametros.getBytes("UTF-8"));*/

            /*urlConnection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(urlParametros);
            wr.flush();
            wr.close();*/

            in = new BufferedInputStream(urlConnection.getInputStream());

            return readStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return "";
    }

    public String retornaJSON(String stringUrl) {

        try {
            url = new URL(stringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());

            return readStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return "";
    }

    /*public String enviaEretornaJSON(String stringUrl, JSONObject json) {

        Logger LOG = Logger.getLogger(com.rede.ncarede.BuildConfig.class.getName());

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                URL url = new URL(VariaveisGlobais.IP);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(VariaveisGlobais.TEMPO_LIMITE);
                connection.setReadTimeout(VariaveisGlobais.TEMPO_LIMITE);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(String.valueOf(json));
                out.close();

                //System.out.println("TAG salva! (json):");

                int http_status = connection.getResponseCode();
                if (http_status / VariaveisGlobais.HTTP_CONNECTION_TIME != 2) {
                    LOG.log(Level.SEVERE, "Ocorreu algum erro. Codigo de resposta: {0}", http_status);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } finally {
                    reader.close();
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }

        try {
            url = new URL(stringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());

            return readStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return "";
    }*/

    public String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();

        return sb.toString();
    }
}
