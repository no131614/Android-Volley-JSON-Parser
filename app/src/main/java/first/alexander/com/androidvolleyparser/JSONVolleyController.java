package first.alexander.com.androidvolleyparser;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class JSONVolleyController extends Application {

    public static final String TAG = JSONVolleyController.class.getSimpleName();

    private RequestQueue RequestQ;

    private static JSONVolleyController JSONVolleyInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        JSONVolleyInstance = this;
    }

    public static synchronized JSONVolleyController getInstance() {
        return JSONVolleyInstance;
    }

    public RequestQueue getRequestQueue() {
        if (RequestQ == null) {
            RequestQ = Volley.newRequestQueue(getApplicationContext());
        }

        return RequestQ;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (RequestQ != null) {
            RequestQ.cancelAll(tag);
        }
    }
}
