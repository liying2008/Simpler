package cc.duduhuo.simpler;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import cc.duduhuo.simpler.net.HttpGetTask;
import cc.duduhuo.simpler.net.HttpListener;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("cc.duduhuo.simpler", appContext.getPackageName());
    }

    @Test
    public void testOptNull() throws JSONException {
        String json = "{\n" +
                "  \"followGroup\": 0,\n" +
                "  \"maxPage\": 1,\n" +
                "  \"previous_cursor\": null,\n" +
                "  \"next_cursor\": 0,\n" +
                "  \"ok\": 0,\n" +
                "  \"msg\": \"系统提示：为了避免骚扰，微博智能反垃圾系统已过滤掉部分广告用户。\",\n" +
                "  \"userInfo\": null,\n" +
                "  \"st\": \"78667e\"\n" +
                "}";
        JSONObject jsonObject = new JSONObject(json);
        String userInfo = jsonObject.optString("userInfo");
        assertEquals("null", userInfo);     // 通过
    }
}
