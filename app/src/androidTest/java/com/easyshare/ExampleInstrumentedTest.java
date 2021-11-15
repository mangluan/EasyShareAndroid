package com.easyshare;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.easyshare.utils.AESUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.easyshare", appContext.getPackageName());
    }

    @Test
    public void testAES(){
        String code = "123456";
        Log.e( "testAES: ", "code: " + code);
        String encrypt = AESUtils.encrypt(code);
        Log.e( "testAES: ","encrypt: " + encrypt);
        String decrypt = AESUtils.decrypt(encrypt);
        Log.e( "testAES: ","decrypt: " + decrypt );
        assertEquals(code,decrypt);
    }

}