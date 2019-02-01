package miage.parisnanterre.fr.pmod;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class HandlerDownload  implements Runnable {
    List<Film> films;
    WeakReference<CinemaAdapter> adapter;
    WeakReference<Activity> a;

    public HandlerDownload(List<Film> films, CinemaAdapter adapter, Activity a) {
        this.films = films;
        this.adapter = new WeakReference<CinemaAdapter>(adapter);
        this.a=new WeakReference<Activity>(a);
       // doStuff();
    }
    /*
    public void doStuff(){

        HandlerThread handlerThread = new HandlerThread("handler");
        handlerThread.start();

        Looper looper = handlerThread.getLooper();

        Handler handler = new Handler(looper);

        handler.post(new Runnable () {

            @Override
            public void run() {
                for (Film f : films) {

                    Bitmap bm = null;
                    try {
                        URL aURL = new URL("http://lorempixel.com/100/100");
                        URLConnection conn = aURL.openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is);
                        bm = BitmapFactory.decodeStream(bis);
                        bis.close();
                        is.close();
                    } catch (IOException e) {
                        Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
                    }
                    f.setImage(bm);


                    a.runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
            }
        });
    }
*/
    @Override
    public void run() {
        for (Film f : films) {

            Bitmap bm = null;
            try {
                URL aURL = new URL("http://lorempixel.com/100/100");
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
            }
            f.setImage(bm);

            Activity activity = null;
            if(a.get() != null){
                activity = a.get();
            }
            CinemaAdapter cinemaAdapter = null;
            if(adapter.get()!=null){
                cinemaAdapter=adapter.get();
            }

            final CinemaAdapter finalCinemaAdapter = cinemaAdapter;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    finalCinemaAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
