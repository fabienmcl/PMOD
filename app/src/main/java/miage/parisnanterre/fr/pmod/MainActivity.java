package miage.parisnanterre.fr.pmod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view);
        recycler.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);

        Button b = (Button) findViewById(R.id.button);

        Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(),
                R.drawable.ic_launcher_background);

        final List<Film> films = new ArrayList<Film>();
        //films.add(new Film("Shutter Island", "20/09/2010", "Jean"));
        //films.add(new Film("Avatar", "20/09/2009", "Cameron"));
        //films.add(new Film("La cité de la peur", "20/09/1994", "Marc"));
        for (int i = 0; i<50; i++) {
            Film f = new Film("La cité de la peur", "20/09/1994", "Marc", icon);
            films.add(f);
        }

        final CinemaAdapter adapter = new CinemaAdapter(films);
        recycler.setAdapter(adapter);


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

        for (Film f : films) {
           THREAD_POOL_EXECUTOR.execute(new HandlerDownload(films,adapter,this));
        }


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



    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);


}
