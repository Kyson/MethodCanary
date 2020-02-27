package cn.hikyson.methodcanary.lib;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.hikyson.methodcanary.lib.helper.Log4Test;
import cn.hikyson.methodcanary.lib.helper.MethodEventHelper;

import static org.junit.Assert.*;

public class MethodEventTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToString() {
        Log4Test.d(MethodEventHelper.createRandomMethodEvent().toString());
    }

    @Test
    public void toFormatString() {
        Log4Test.d(MethodEventHelper.createRandomMethodEvent().toFormatString());
    }
}