package com.rede.indicanetapp.Toolbox;

/**
 * Variáveis Globais de uso geral do App.
 *
 * @author      Igor Maximo
 * @date        10/08/2021
 */
public abstract class VariaveisGlobais {

    //////////////////////
    // Controle de IP
    public static String IP = "http://indica.net.br/";
    public static String IP_FIREBASE_INDICANET = "http://187.95.0.22/producao/indicanet/indexFirebase.php?psw=inDNt*";

    //////////////////////
    // Controle de versionamento
    public static int[] VERSAO_APP_LOCAL = {2, 0, 1}; // Para exibição

    //////////////////////
    // SQLite
    public static final int VERSAO_DB = 3;
    public static final String NOME_BANCO = "indicanet.db";

    //////////////////////
    // Conexões por http // PADRÃO 7K
    public static int HTTP_CONNECTION_TIME = 100;
    public static int HTTP_URL_CONNECTION = 35000; // Usado para json

    ////////////////////
    // Para interface gráfica
    public static int VIBRAR_TOQUE_MILI = 45;

}