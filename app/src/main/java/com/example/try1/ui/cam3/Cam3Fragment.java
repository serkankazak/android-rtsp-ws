package com.example.try1.ui.cam3;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.try1.BuildConfig;
import com.example.try1.R;
import com.example.try1.databinding.FragmentCam3Binding;

import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class Cam3Fragment extends Fragment {

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
            } else if (message.substring(0, 5).equals("cam3\t")) {
                sent = false;
                log("cam3 received");
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

    private FragmentCam3Binding binding;
    VideoView videoView3;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Cam3ViewModel cam3ViewModel =
                new ViewModelProvider(this).get(Cam3ViewModel.class);

        binding = FragmentCam3Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageView = binding.imageViewCam3;
        videoView3 = binding.videoViewCam3;

        if (((WifiManager)getContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID().equals("\"DrCa\"") || ((WifiManager)getContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID().equals("\"DrCa2\"")) {
            DrCa = true;
            Toast.makeText(getContext(), "DrCa", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Render", Toast.LENGTH_SHORT).show();
        }

        if (DrCa) {

            imageView.setVisibility(View.INVISIBLE);
            videoView3.setVisibility(View.VISIBLE);

            ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setTitle("Please wait");
            pd.setMessage("Buffering...");
            pd.setCancelable(true);
            pd.show();
            videoView3.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    pd.dismiss();
                }
            });
            videoView3.setVideoPath("rtsp://<user>:<password>@192.168.1.154:554/22");
            videoView3.start();

            videoView3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_grid);
                }
            });

        } else {

            imageView.setVisibility(View.VISIBLE);
            videoView3.setVisibility(View.INVISIBLE);

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
                                    log("send cam3");
                                    webSocket.send("cam3");
                                    millis = System.currentTimeMillis();
                                }
                            }
                        }, 0, 1000);
                    } else {
                        handler.postDelayed(this, delay);
                    }
                }
            }, delay);

            imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_grid);
                }
            });

        }

        return root;
    }

    @Override
    public void onDestroyView() {

        log("bye cam3");

        if (DrCa) {
            videoView3.stopPlayback();
        } else {
            webSocket.cancel();
            if (timer != null) {
                timer.cancel();
            }
        }

        super.onDestroyView();
        binding = null;

        if (DrCa) {
            timer = null;
        }

    }

}
