package com.tsiro.dogvip.app;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.ResponseBody;

/**
 * Created by giannis on 23/5/2017.
 */

public class MOck implements Interceptor {

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {

        addDelay();

        return new okhttp3.Response.Builder()
                .code(500)
                .message("OK")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("application/json"), "{}"))
                .build();
    }

    private void addDelay() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
