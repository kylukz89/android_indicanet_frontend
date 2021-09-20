package com.rede.indicanetapp.JSON;

import android.os.StrictMode;


import com.rede.indicanetapp.Toolbox.VariaveisGlobais;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Envia um JSON da aplicação para o servidor
 * RETORNO:
 * 0                      - null
 * 1                      - JSON carregado
 *
 * @author Igor Maximo
 * @criado 19/02/2019
 * @editado 02/03/2019
 */
public class EnviaJson {



    public boolean enviaJSON(String arquivoPHP, JSONObject json) {


        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                URL url = new URL(VariaveisGlobais.IP + arquivoPHP);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(VariaveisGlobais.HTTP_URL_CONNECTION);
                connection.setReadTimeout(VariaveisGlobais.HTTP_URL_CONNECTION);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(String.valueOf(json));
                out.close();

                //System.out.println("TAG salva! (json):");

                int http_status = connection.getResponseCode();
                if (http_status / VariaveisGlobais.HTTP_CONNECTION_TIME != 2) {
                    //LOG.log(Level.SEVERE, "Ocorreu algum erro. Codigo de resposta: {0}", http_status);
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
               // LOG.log(Level.SEVERE, null, e);
            }
        } catch (Exception e) {
          //  LOG.log(Level.SEVERE, null, e);
        }

        return false;
    }

}
