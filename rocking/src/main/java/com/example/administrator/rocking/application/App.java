package com.example.administrator.rocking.application;

import android.app.Application;

import com.litesuits.http.HttpConfig;
import com.litesuits.http.LiteHttp;

/**
 * Created by Administrator on 2015/10/27.
 */
public class App extends Application {
    private LiteHttp liteHttp = null;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public LiteHttp getLiteHttp() {
        if (liteHttp == null) {
            HttpConfig config = new HttpConfig(getApplicationContext());

            // set app context
            config.setContext(getApplicationContext());

            // custom User-Agent
           // config.setUserAgent("Mozilla/5.0 (...)");

            // connect timeout: 5s,  socket timeout: 5s
            config.setTimeOut(5000, 5000);

            // new with config
            liteHttp = LiteHttp.newApacheHttpClient(config);
        }

        return liteHttp;

    }
}
