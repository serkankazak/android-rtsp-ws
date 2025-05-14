package com.example.try1.ui.grid;

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
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.try1.BuildConfig;
import com.example.try1.R;
import com.example.try1.databinding.FragmentGridBinding;

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

public class GridFragment extends Fragment {

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

    WebSocket webSocket;
    private OkHttpClient mClient;

    boolean welcomed = false;

    boolean sent1 = false;
    boolean sent2 = false;
    boolean sent3 = false;
    boolean sent4 = false;
    boolean sent5 = false;

    long millis1 = System.currentTimeMillis();
    long millis2 = System.currentTimeMillis();
    long millis3 = System.currentTimeMillis();
    long millis4 = System.currentTimeMillis();
    long millis5 = System.currentTimeMillis();

    Timer timer = null;

    private FragmentGridBinding binding;

    //Timer myTimer;

    ExecutorService executor1 = Executors.newSingleThreadExecutor();
    ExecutorService executor2 = Executors.newSingleThreadExecutor();

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;

    VideoView videoView3;
    VideoView videoView4;
    VideoView videoView6;

    private boolean DrCa = false;

    void decrypt(String s, ImageView i) {
        Bitmap decodedByte = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decodeHexString("<key>"), "AES"), new IvParameterSpec(decodeHexString("<iv>")));
            byte[] decodedString = Base64.getDecoder().decode(new String(cipher.doFinal(Base64.getDecoder().decode(s))));
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception ex) {}
        runrun(decodedByte, i);
    }

    public void runrun(final Bitmap data, ImageView i) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                i.setImageBitmap(data);
            }
        });
    }

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
                sent1 = false;
                log("cam1 received");
                decrypt(message.split("\t", 2)[1], imageView1);
            } else if (message.substring(0, 5).equals("cam2\t")) {
                sent2 = false;
                log("cam2 received");
                decrypt(message.split("\t", 2)[1], imageView2);
            } else if (message.substring(0, 5).equals("cam3\t")) {
                sent3 = false;
                log("cam3 received");
                decrypt(message.split("\t", 2)[1], imageView3);
            } else if (message.substring(0, 5).equals("cam4\t")) {
                sent4 = false;
                log("cam4 received");
                decrypt(message.split("\t", 2)[1], imageView4);
            } else if (message.substring(0, 5).equals("cam5\t")) {
                sent5 = false;
                log("cam5 received");
                decrypt(message.split("\t", 2)[1], imageView6);
            }

        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GridViewModel homeViewModel = new ViewModelProvider(this).get(GridViewModel.class);

        binding = FragmentGridBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageView1 = binding.imageView1;
        imageView2 = binding.imageView2;
        imageView3 = binding.imageView3;
        imageView4 = binding.imageView4;
        imageView5 = binding.imageView5;
        imageView6 = binding.imageView6;

        videoView3 = binding.videoView3;
        videoView4 = binding.videoView4;
        videoView6 = binding.videoView6;

        if (((WifiManager)getContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID().equals("\"DrCa\"") || ((WifiManager)getContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID().equals("\"DrCa2\"")) {
            DrCa = true;
            Toast.makeText(getContext(), "DrCa", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Render", Toast.LENGTH_SHORT).show();
        }

        if (DrCa) {

            imageView3.setVisibility(View.INVISIBLE);
            imageView4.setVisibility(View.INVISIBLE);
            imageView6.setVisibility(View.INVISIBLE);

            videoView3.setVisibility(View.VISIBLE);
            videoView4.setVisibility(View.VISIBLE);
            videoView6.setVisibility(View.VISIBLE);

            videoView3.setVideoPath("rtsp://<user>:<password>@192.168.1.154/22");
            videoView3.start();

            videoView4.setVideoPath("rtsp://<user>:<password>@192.168.1.49/22");
            videoView4.start();

            videoView6.setVideoPath("rtsp://<user>:<password>@192.168.1.26/22");
            videoView6.start();

            Handler handler = new Handler(Looper.getMainLooper());
            executor1.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Bitmap image = BitmapFactory.decodeStream(new java.net.URL("http://192.168.1.14:88/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=<user>&pwd=<password>").openStream());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageView1.setImageBitmap(image);
                                }
                            });
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                        }
                    }
                }
            });

            Handler handler2 = new Handler(Looper.getMainLooper());
            executor2.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Bitmap image = BitmapFactory.decodeStream(new java.net.URL("http://192.168.1.23:88/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=<user>&pwd=<password>").openStream());
                            handler2.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageView2.setImageBitmap(image);
                                }
                            });
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                        }
                    }
                }
            });

            imageView1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_cam1);
                }
            });
            imageView2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_cam2);
                }
            });
            videoView3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_cam3);
                }
            });
            videoView4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_cam4);
                }
            });
            videoView6.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_cam5);
                }
            });

        } else {

            imageView3.setVisibility(View.VISIBLE);
            imageView4.setVisibility(View.VISIBLE);
            imageView6.setVisibility(View.VISIBLE);

            videoView3.setVisibility(View.INVISIBLE);
            videoView4.setVisibility(View.INVISIBLE);
            videoView6.setVisibility(View.INVISIBLE);

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

                                boolean timeout1 = System.currentTimeMillis() - millis1 > Integer.parseInt(getString(R.string.timeout));
                                if (timeout1) {
                                    log("cam1 timeout, try again");
                                    //toast("cam1 timeout, try again");
                                }
                                if (!sent1 || timeout1) {
                                    sent1 = true;
                                    log("send cam1");
                                    webSocket.send("cam1");
                                    millis1 = System.currentTimeMillis();
                                }

                                boolean timeout2 = System.currentTimeMillis() - millis2 > Integer.parseInt(getString(R.string.timeout));
                                if (timeout2) {
                                    log("cam2 timeout, try again");
                                    //toast("cam2 timeout, try again");
                                }
                                if (!sent2 || timeout2) {
                                    sent2 = true;
                                    log("send cam2");
                                    webSocket.send("cam2");
                                    millis2 = System.currentTimeMillis();
                                }

                                boolean timeout3 = System.currentTimeMillis() - millis3 > Integer.parseInt(getString(R.string.timeout));
                                if (timeout3) {
                                    log("cam3 timeout, try again");
                                    //toast("cam3 timeout, try again");
                                }
                                if (!sent3 || timeout3) {
                                    sent3 = true;
                                    log("send cam3");
                                    webSocket.send("cam3");
                                    millis3 = System.currentTimeMillis();
                                }

                                boolean timeout4 = System.currentTimeMillis() - millis4 > Integer.parseInt(getString(R.string.timeout));
                                if (timeout4) {
                                    log("cam4 timeout, try again");
                                    //toast("cam4 timeout, try again");
                                }
                                if (!sent4 || timeout4) {
                                    sent4 = true;
                                    log("send cam4");
                                    webSocket.send("cam4");
                                    millis4 = System.currentTimeMillis();
                                }

                                boolean timeout5 = System.currentTimeMillis() - millis5 > Integer.parseInt(getString(R.string.timeout));
                                if (timeout5) {
                                    log("cam5 timeout, try again");
                                    //toast("cam5 timeout, try again");
                                }
                                if (!sent5 || timeout5) {
                                    sent5 = true;
                                    log("send cam5");
                                    webSocket.send("cam5");
                                    millis5 = System.currentTimeMillis();
                                }

                            }
                        }, 0, 1000);
                    } else {
                        handler.postDelayed(this, delay);
                    }
                }
            }, delay);

            imageView1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_cam1);
                }
            });
            imageView2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_cam2);
                }
            });
            imageView3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_cam3);
                }
            });
            imageView4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_cam4);
                }
            });
            imageView6.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.nav_cam5);
                }
            });

        }

        return root;

    }

    @Override
    public void onDestroyView() {

        log("bye grid");

        if (DrCa) {

            //myTimer.cancel();

            executor1.shutdownNow();
            executor2.shutdownNow();

            videoView3.stopPlayback();
            videoView4.stopPlayback();
            videoView6.stopPlayback();

        } else {

            webSocket.cancel();

            if (timer != null) {
                timer.cancel();
            }

        }

        super.onDestroyView();
        binding = null;

        if (DrCa) {

            executor1 = null;
            executor2 = null;

            timer = null;

            //myTimer = null;

        }

    }

}
