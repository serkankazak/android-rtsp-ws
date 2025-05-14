package com.example.try1.ui.cam1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.try1.BuildConfig;
import com.example.try1.R;
import com.example.try1.databinding.FragmentCam1Binding;

import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class Cam1Fragment extends Fragment {

    void log(String s) {
        if (BuildConfig.DEBUG) {
            Log.e("myTag", s);
        }
    }

    void toast(String s) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }

    public byte hexToByte(String hexString) { return (byte)((toDigit(hexString.charAt(0)) << 4) + toDigit(hexString.charAt(1))); }
    private int toDigit(char hexChar) {
        return Character.digit(hexChar, 16);
    }
    public byte[] decodeHexString(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    ImageView imageView = null;

    WebSocket webSocket;
    private OkHttpClient mClient;

    boolean welcomed = false;

    boolean sent = false;

    long millis = System.currentTimeMillis();

    Timer timer = null;

    private final class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            log("send password");
            webSocket.send("<secret>");
        }
        @Override
        public void onMessage(WebSocket webSocket, String message) {
            if (message.equals("welcome")) {
                welcomed = true;
                log("welcomed");
            } else if (message.substring(0, 5).equals("info\t")) {
                String m = message.split("\t", 2)[1];
                log(m);
                toast(m);
            } else if (message.substring(0, 5).equals("cam1\t")) {
                sent = false;
                log("cam1 received");
                Bitmap decodedByte = null;
                try {
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decodeHexString("<key>"), "AES"), new IvParameterSpec(decodeHexString("<iv>")));
                    byte[] decodedString = Base64.getDecoder().decode(new String(cipher.doFinal(Base64.getDecoder().decode(message.split("\t", 2)[1]))));
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                } catch (Exception ex) {}
                runrun(decodedByte);
            }
        }
        public void runrun(final Bitmap data) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    imageView.setImageBitmap(data);
                }
            });
        }
    }

    private boolean DrCa = false;

    private FragmentCam1Binding binding;

    //Timer myTimer;

    ExecutorService executor = Executors.newSingleThreadExecutor();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Cam1ViewModel cam1ViewModel =
                new ViewModelProvider(this).get(Cam1ViewModel.class);

        binding = FragmentCam1Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageView = binding.imageViewCam1;

        if (((WifiManager)getContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID().equals("\"DrCa\"") || ((WifiManager)getContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID().equals("\"DrCa2\"")) {
            DrCa = true;
            Toast.makeText(getContext(), "DrCa", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Render", Toast.LENGTH_SHORT).show();
        }

        if (DrCa) {

            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Bitmap image = BitmapFactory.decodeStream(new java.net.URL("http://192.168.1.14:88/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=<user>&pwd=<password>").openStream());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(image);
                                }
                            });
                        } catch (Exception e) {}
                    }
                }
            });

        } else {

            mClient = new OkHttpClient();
            webSocket = mClient.newWebSocket(new Request.Builder().url("wss://<server>").build(), new EchoWebSocketListener());
            mClient.dispatcher().executorService().shutdown();
            final Handler handler = new Handler();
            final int delay = 100;
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (welcomed) {
                        timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                boolean timeout = System.currentTimeMillis() - millis > Integer.parseInt(getString(R.string.timeout));
                                if (timeout) {
                                    log("timeout, try again");
                                    //toast("timeout, try again");
                                }
                                if (!sent || timeout) {
                                    sent = true;
                                    log("send cam1");
                                    webSocket.send("cam1");
                                    millis = System.currentTimeMillis();
                                }
                            }
                        }, 0, 1000);
                    } else {
                        handler.postDelayed(this, delay);
                    }
                }
            }, delay);

        }

        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.nav_grid);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {

        log("bye cam1");

        if (DrCa) {
            //myTimer.cancel();
            executor.shutdownNow();
        } else {
            webSocket.cancel();
            if (timer != null) {
                timer.cancel();
            }
        }

        super.onDestroyView();
        binding = null;

        if (DrCa) {
            executor = null;
            timer = null;
            //myTimer = null;
        }

    }

}
