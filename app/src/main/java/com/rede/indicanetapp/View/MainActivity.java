package com.rede.indicanetapp.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rede.indicanetapp.JSON.RecebeJson;
import com.rede.indicanetapp.SQLite.GeraTabelasSQLite;
import com.rede.indicanetapp.SQLite.SQLiteGeraTabelaGerenciamento;
import com.rede.indicanetapp.Toolbox.Ferramentas;
import com.rede.indicanetapp.Toolbox.VariaveisGlobais;
import com.rede.indicanet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocationListener {

    SQLiteGeraTabelaGerenciamento sqliteGerenciamento = new SQLiteGeraTabelaGerenciamento(MainActivity.this);
    RecebeJson recebeJson = new RecebeJson();
    Ferramentas ferramenta = new Ferramentas();

    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private WebView webView;
    private WebSettings webSettings;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private String tokenFirebase;

    // Botão HOME
    Button botaoVoltarHome;
    private String cred = "";
    private String vers = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        return;
    }


    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide(); // remove barra de título

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);



//        getWindow().getDecorView();
/*
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
*/
        //////////////////////////////// FIREBASE TOKEN ////////////////////////////////
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                // Get new Instance ID token@=
                String token = task.getResult();
                System.err.println("Token Firebase ==>" + token);
                tokenFirebase = token;

                // Log and toast
                    /*    String msg = getString(R.string.msg_token_fmt, token);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();*/
            }
        });
        //////////////////////////////////////////////////////////////////////////////
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //bundle must contain all info sent in "data" field of the notification
        }
        /*if (getIntent().getExtras() != null) {
            // Call your NotificationActivity here..
            Intent intent = new Intent(MainActivity.this, FcmTokenRegistrationService.class);
            startActivity(intent);
        }*/


////////////// SQLITE GERA DB //////////////////////////////////////////////////////////////////////
        GeraTabelasSQLite dbh = new GeraTabelasSQLite(MainActivity.this);
        dbh.getWritableDatabase();
////////////////////////////////////////////////////////////////////////////////////////////////////


        webView = (WebView) findViewById(R.id.webview);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(this.getCacheDir().getAbsolutePath());
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // Para limpar o cache sempre que app abrir
        String DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";
        webView.getSettings().setUserAgentString(DESKTOP_USER_AGENT);
        webView.setWebViewClient(new Client());
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setHorizontalScrollBarEnabled(false);
        WebScrollListener scrollListener = new WebScrollListener(); // save this in an instance variable
        webView.addJavascriptInterface(scrollListener, "WebScrollListener");
        // iFrame
        webView.setInitialScale(1);
        // Coleta do console log do lado do javascript na parte web
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", "CONSOLE ===> " + consoleMessage.message());
                return true;
            }
        });

        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus(View.FOCUS_DOWN);

        // redirect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        // Para limpar cache após app ser fechado
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    Log.d(TAG, "Cookie removed: " + aBoolean);
                }
            });
        } else {
            cookieManager.removeAllCookie();
        }


        webView.setWebChromeClient(new ChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                System.err.println("WebView ==========> " + consoleMessage.message());
                return true;
            }


            // Need to accept permissions to use the camera
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        ///////////////////////////////////////////////
        // JS
        webView.addJavascriptInterface(this, "trunca");
        webView.addJavascriptInterface(this, "auth");
        webView.addJavascriptInterface(this, "gps");
        webView.addJavascriptInterface(this, "notificacaoSisRede");

        ///////////////////////////////////////////////
        // SQLite
        String[] credenciaisSalvas = sqliteGerenciamento.selectUltimoLogin("select _id, usuario, senha from gerenciamento", 3);

        cred = "{\"usuario\":\"" + credenciaisSalvas[1] + "\", \"senha\":\"" + credenciaisSalvas[2] + "\"}";
        vers = "{\"versao_maior\":\"" + VariaveisGlobais.VERSAO_APP_LOCAL[0] + "\", \"versao_menor\":\"" + VariaveisGlobais.VERSAO_APP_LOCAL[1] + "\", \"correcao\":\"" + VariaveisGlobais.VERSAO_APP_LOCAL[2] + "\"}";

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:javaGetCredenciaisSalvasSQLiteJAVA('" + cred + "', '" + vers + "')");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                ////// AVISO //////
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final AlertDialog show = builder.show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                // set title
                alertDialogBuilder.setTitle("AVISO!");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Ops, verifique sua conexão com a internet!")
                        .setCancelable(false)
                        .setPositiveButton("Tentar novamente...", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idD) {
                                String html = "<!DOCTYPE html>" +
                                        "<html>" +
                                        "<body onload='document.frm1.submit()'>" +
                                        "<form action='" + VariaveisGlobais.IP + "' method='post' name='frm1'>" +
                                        "<input type='hidden' name='mobile' value='1'><br>" +
                                        "</form>" +
                                        "</body>" +
                                        "</html>";
                                webView.loadData(html, "text/html; video/mpeg", "UTF-8");
                                show.dismiss();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                //////////////
            }
        });


////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

        webView.addJavascriptInterface(this, "androidWebViewClient");
        String html = "<!DOCTYPE html>" +
                "<html>" +
                "<body onload='document.frm1.submit()'>" +
                "<form action='" + VariaveisGlobais.IP + "' method='post' name='frm1'>" +
                "<input type='hidden' name='mobile' value='1'><br>" +
                "</form>" +
                "</body>" +
                "</html>";
        webView.loadData(html, "text/html; video/mpeg", "UTF-8");


////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

        if (savedInstanceState != null) {
            ((WebView) findViewById(R.id.webview)).restoreState(savedInstanceState);
        }


        //  gps
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions((Activity) MainActivity.this, PERMISSIONS, 2);
            }
            mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
            dumpProviders();
            Criteria criteria = new Criteria();
            best = mgr.getBestProvider(criteria, true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            Location location = mgr.getLastKnownLocation(best);
            dumpLocation(location);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private LocationManager mgr;
    private String best;
    public static double myLocationLatitude;
    public static double myLocationLongitude;

    /**
     * Ponte de escuta para as credenciais do SisRede para login automático
     *
     * @return
     */
    @JavascriptInterface
    public String escutaJsAutenticacao(String json) {

        final String[] usuarioSisRede = new String[2];
        try {
            JSONArray jsonArray = null;
            jsonArray = new JSONArray(json);
            int length = jsonArray.length();
            JSONObject jsonObject = null;


            for (int i = 0; i < length; i++) {
                jsonObject = jsonArray.getJSONObject(i);
                usuarioSisRede[0] = jsonObject.getString("usuario");
                usuarioSisRede[1] = jsonObject.getString("senha");
            }
            // Grava no SQLite
            gravaCredenciaisSQLite(usuarioSisRede);
            // Grava no MySQL

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        setAtualizaTokenDBFirebase(usuarioSisRede, tokenFirebase);
                    } catch (Exception e) {

                    }
                }
            }, 1200);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Insere/Atualiza o token do firebase por usuário.
     *
     * @author Igor Maximo
     * @date 25/10/2019
     */
    private void setAtualizaTokenDBFirebase(String[] usuarioSisRede, String tokenFirebase) {
        try {
            JSONArray jsonArray = new JSONArray(recebeJson.retornaJSON(VariaveisGlobais.IP_FIREBASE_INDICANET, new Uri.Builder()
                    .appendQueryParameter("user", usuarioSisRede[0])
                    .appendQueryParameter("token", tokenFirebase)
                    .appendQueryParameter("dispositivo", getMarcaModeloDispositivo(this))));
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            System.err.println("tok ==> " + jsonObject.getString("status"));

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @JavascriptInterface
    public void onAjaxRequest(JSONObject jsonObject, final CompletionHandler handler) {
        System.err.println(jsonObject);
    }

    // Retorna Marca e Modelo do smartphone
    public static String getMarcaModeloDispositivo(Context ctx) {
        String manufacturer = "";
        String model = "";
        try {
            manufacturer = Build.MANUFACTURER;
            model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                return setCapitalizarTexto(model);
            }
        } catch (Exception e) {

        }
        return "Marca: " + setCapitalizarTexto(manufacturer) + " / Modelo: " + model;
    }


    /**
     * Capitaliza um texto passado como parâmetro
     *
     * @author Igor Maximo
     * @date 21/01/2021
     */
    public static String setCapitalizarTexto(String str) {
        str = str.toLowerCase();
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    /**
     * Ponte de escuta para as notificações do SisRede
     *
     * @author Igor Maximo
     * @date 23/10/2019
     * @updated 23/10/2019
     */
    @JavascriptInterface
    public String getNotificacao(String json) {

        String[] notificacao = new String[2];
        try {
            JSONArray jsonArray = null;
            jsonArray = new JSONArray(json);
            int length = jsonArray.length();
            JSONObject jsonObject = null;

            for (int i = 0; i < length; i++) {
                jsonObject = jsonArray.getJSONObject(i);
                notificacao[0] = jsonObject.getString("titulo");
                notificacao[1] = jsonObject.getString("mensagem");
            }

            ferramenta.geraNotificacaoPushPadrao(MainActivity.this, notificacao[0], notificacao[1], 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Ponte de escuta para as consultas de geolocalização
     *
     * @author Igor Maximo
     * @date 17/10/2019
     * @updated 17/10/2019
     */
    @JavascriptInterface
    public String solicitaGeoLocalizacao() {
        Location l = null;
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                l = mLocationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        System.out.println("=========> {\"lon\":\"" + bestLocation.getLongitude() + "\", \"lat\":\"" + bestLocation.getLatitude() + "\"}");
        return "{\"lon\":\"" + bestLocation.getLongitude() + "\", \"lat\":\"" + bestLocation.getLatitude() + "\"}";
    }


    /**
     * Grava as credênciais do SisRede para login automático
     */
    public void gravaCredenciaisSQLite(String[] credenciais) {
        try {
            sqliteGerenciamento.atualizaLogin("update gerenciamento set usuario = '" + credenciais[0] + "', senha = '" + credenciais[1] + "'");

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Truncate gerenciamento
     *
     * @return
     */
    @JavascriptInterface
    public String truncateGerenciamento() {
        cred = "{\"usuario\":\"null\", \"senha\":\"null\"}";
        System.err.println("=======> TRUNCATED");
        try {
            sqliteGerenciamento.truncate();
            sqliteGerenciamento.truncate2();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "truncada";
    }

    /**
     * Método que escuta as functions JS do front-end da aplicação web, para realizar comunicação
     * entre linguagem nativa JAVA com linguagem web JAVASCSRIPT
     *
     * @return JSON
     * @author Igor Maximo
     * @date 31/08/2019
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @JavascriptInterface
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.evaluateJavascript("javascript:androidButtonBackEvent()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if (value.equals("1")) {
                        webView.goBack();
                    }
                }
            });
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * Cria arquivo
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }


    /**
     * Interface de nagevador para uso dentro do app via webview
     */
    public class ChromeClient extends WebChromeClient {
        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;
            Intent takePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");
            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }
            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            return true;
        }

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }
            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);
            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[]{captureIntent});
            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }


    }


    @JavascriptInterface
    public void tellAndroidPid(String pid) {
        //you can save your pid in here,
        Log.i("check pid", " get pid: " + pid);
    }

    public class Client extends WebViewClient {

        private String element;
        private int margin;


        public void onScrollPositionChange(String topElementCssSelector, int topElementTopMargin) {
            Log.d("WebScrollListener", "Scroll position changed: " + topElementCssSelector + " " + topElementTopMargin);
            element = topElementCssSelector;
            margin = topElementTopMargin;
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
        }

    }

    public class WebScrollListener {

        private String element;
        private int margin;

        @JavascriptInterface
        public void onScrollPositionChange(String topElementCssSelector, int topElementTopMargin) {
            Log.d("WebScrollListener", "Scroll position changed: " + topElementCssSelector + " " + topElementTopMargin);
            element = topElementCssSelector;
            margin = topElementTopMargin;
        }

    }

    /* MÉTODOS ACIONADOS AUTOMATICAMENTE PELA ACTIVITY */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    // REFRESH TELA SE ALGO FOI ALTERADO
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        dumpLocation(location);
        System.out.println("============>" + myLocationLatitude);
        System.out.println("============>" + myLocationLongitude);
    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onPause() {

        super.onPause();
        mgr.removeUpdates(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {

        super.onResume();
        try {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }

            mgr.requestLocationUpdates(best, 15000, 1, this);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void dumpLocation(Location l) {

        if (l == null) {
            myLocationLatitude = 0.0;
            myLocationLongitude = 0.0;
        } else {
            myLocationLatitude = l.getLatitude();
            myLocationLongitude = l.getLongitude();
        }

        System.out.println("============>" + myLocationLatitude);
        System.out.println("============>" + myLocationLongitude);
    }

    private void dumpProviders() {

        List<String> providers = mgr.getAllProviders();
        for (String p : providers) {

            dumpProviders(p);
        }
    }

    private void dumpProviders(String s) {

        LocationProvider info = mgr.getProvider(s);
        StringBuilder builder = new StringBuilder();
        builder.append("name: ").append(info.getName());
    }
}
