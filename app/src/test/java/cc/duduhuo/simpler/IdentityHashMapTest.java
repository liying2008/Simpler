package cc.duduhuo.simpler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.junit.Test;

import java.util.Map;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/7 15:01
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class IdentityHashMapTest {
    private Multimap<Class, String> map = ArrayListMultimap.create();
    /**
     * 注册任务
     * @param clazz Activity Class
     * @param task AsyncTask对象
     */
    public void registerAsyncTask(Class clazz, String task) {
        map.put(clazz, task);
    }

    @Test
    public void test1() {
        registerAsyncTask(ExampleUnitTest.class, "1");
        registerAsyncTask(ExampleUnitTest.class, "2");
        registerAsyncTask(ExampleUnitTest.class, "3");

        for (Map.Entry<Class, String> entry : map.entries()) {
            System.out.print(entry.getKey() +"    ");
            System.out.println(entry.getValue());
        }
    }
}
