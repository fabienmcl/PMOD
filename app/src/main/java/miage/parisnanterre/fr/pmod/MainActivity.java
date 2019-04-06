package miage.parisnanterre.fr.pmod;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    CinemaAdapter adapter;
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    RecyclerView recycler;
    List<Film> films;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        recycler = (RecyclerView) findViewById(R.id.recycler_view);
        recycler.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);

        Button save = (Button) findViewById(R.id.button);
        Button load = (Button) findViewById(R.id.button2);




        Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(),
                R.drawable.ic_launcher_background);

       films = new ArrayList<Film>();
        //films.add(new Film("Shutter Island", "20/09/2010", "Jean"));
        //films.add(new Film("Avatar", "20/09/2009", "Cameron"));
        //films.add(new Film("La cité de la peur", "20/09/1994", "Marc"));
        for (int i = 0; i<50; i++) {
            Film f = new Film("La cité de la peur", "20/09/1994", "Marc", null);
            films.add(f);
        }

        adapter = new CinemaAdapter(films);
        recycler.setAdapter(adapter);




        //h.doStuff();
        /*
        //async
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Film f : films) {
                    new DownloadImagesTask(f, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://lorempixel.com/100/100");
                    //new DownloadImagesTask(f, adapter).execute("http://lorempixel.com/100/100");
                }
            }
        });

*/



    }

    private int REQUEST_CODE = 1;
    public void save(View v){
        //checking wether the permission is already granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            write(v);
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},REQUEST_CODE);
        }

    }
    public void load(View v){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            try {
                read();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE){
            //checking if the permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission is granted,do your operation
            }else{
                finish();
            }
        }else if(requestCode == 2){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


            }else{
                Toast.makeText(this,"You denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void write(View v){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation( findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
        Button gogo = (Button) popupView.findViewById(R.id.button3);
        /*final List<Film> films = new ArrayList<Film>();
        //films.add(new Film("Shutter Island", "20/09/2010", "Jean"));
        //films.add(new Film("Avatar", "20/09/2009", "Cameron"));
        //films.add(new Film("La cité de la peur", "20/09/1994", "Marc"));
        Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(),
                R.mipmap.ic_launcher);
        for (int i = 0; i<50; i++) {
            Film f = new Film("josé", "20/09/1994", "Marc");
            films.add(f);
        }*/
        gogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();

                OutputStream os = null;
                File path = getBaseContext().getFilesDir();
                File file = new File(path, "sample.txt");

                try {
                    os = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    SaveFilms.encrypt((Serializable) films, os, "key");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                /*
                //sans clé

                FileOutputStream fileout = null;
                ObjectOutputStream out = null;
                try {
                    File file = new File(getFilesDir() , "sample.txt");
                    fileout = new FileOutputStream(file);
                    out = new ObjectOutputStream(fileout);
                    out.writeObject(films);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
            }
        });



    }
    public void read() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {


        /*
        ObjectInputStream input;
        String filename = "sample.txt";

        try {
            input = new ObjectInputStream(new FileInputStream(new File(new File( getFilesDir(),"")+File.separator+filename)));



            //List<Film> filmList = (List<Film>) input.readObject();
            films.clear();
            films.addAll((List<Film>) input.readObject());
            //adapter = new CinemaAdapter(filmList);
            adapter.notifyDataSetChanged();
            recycler.setAdapter(adapter);
            input.close();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        */
        InputStream is = null;
        File path = getBaseContext().getFilesDir();
        File file = new File(path, "sample.txt");

        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            List<Film> filmsSerial = (List<Film>) SaveFilms.decrypt(is, "key");
            films.clear();
            films.addAll(filmsSerial);
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }






    private static final byte[] key = "MyDifficultPassw".getBytes();
    private static final String transformation = "AES/ECB/PKCS5Padding";

    public static void encrypt(Serializable object, OutputStream ostream) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        try {
            // Length is 16 byte
            SecretKeySpec sks = new SecretKeySpec(key, transformation);

            // Create cipher
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            SealedObject sealedObject = new SealedObject(object, cipher);

            // Wrap the output stream
            CipherOutputStream cos = new CipherOutputStream(ostream, cipher);
            ObjectOutputStream outputStream = new ObjectOutputStream(cos);
            outputStream.writeObject(sealedObject);
            outputStream.close();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }
    public static Object decrypt(InputStream istream) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec sks = new SecretKeySpec(key, transformation);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, sks);

        CipherInputStream cipherInputStream = new CipherInputStream(istream, cipher);
        ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
        SealedObject sealedObject;
        try {
            sealedObject = (SealedObject) inputStream.readObject();
            return sealedObject.getObject(cipher);
        } catch (ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void downloadIMG(View v){
        ThreadFactoryIMG sThreadFactory = new ThreadFactoryIMG();
        //handler
        //HandlerDownload h = new HandlerDownload(films,adapter,this);
        HandlerThread handlerThread = new HandlerThread("handler");
        handlerThread.start();

        Looper looper = handlerThread.getLooper();


        Handler handler = new Handler(looper);
        handler.post(new HandlerDownload(films,adapter,this));
        Executor THREAD_POOL_EXECUTOR
                = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

        /*for (Film f : films) {
            THREAD_POOL_EXECUTOR.execute(new HandlerDownload(films,adapter,this));
        }*/

    }

    public void intentCerticate(View v){
        Intent intent = new Intent(this, CertificateActivity.class);
        startActivity(intent);

    }

    public void loadJSON(View v){


    }


}
