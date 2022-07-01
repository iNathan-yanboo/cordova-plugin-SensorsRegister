package cordova.plugin.sensors;

import static com.sensorsdata.analytics.android.sdk.util.AppInfoUtils.getAppName;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

/**
 * This class echoes a string called from JavaScript.
 */
public class SensorsRegister extends CordovaPlugin {

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
        Log.i("SensorRegister", "initialize ");

        String SA_SERVER_URL = preferences.getString("SaServiceUrl", "");
        Log.i("SensorRegister", "onCreate -----" + SA_SERVER_URL);

        // 初始化配置
        SAConfigOptions saConfigOptions = new SAConfigOptions(SA_SERVER_URL);
        // 开启全埋点
        saConfigOptions.setAutoTrackEventType(SensorsAnalyticsAutoTrackEventType.APP_CLICK |
                SensorsAnalyticsAutoTrackEventType.APP_START |
                SensorsAnalyticsAutoTrackEventType.APP_END |
                SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN)
                //开启 Log
                .enableLog(true);
        /**
         * 其他配置，如开启可视化全埋点
         */
        // 需要在主线程初始化神策 SDK
        SensorsDataAPI.startWithConfigOptions((Context) cordova.getContext(), saConfigOptions);
        saConfigOptions.enableJavaScriptBridge(true);

        // 将应用名称作为事件公共属性，后续所有 track() 追踪的事件都会自动带上 "AppName" 属性
        try {
            JSONObject properties = new JSONObject();
            // 应用名称
            properties.put("appName", getAppName((Context) cordova.getContext()));
            // 渠道
            properties.put("channel", preferences.getString("SaParamsChannel", ""));
            // 来源
            properties.put("source", "Android");
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
