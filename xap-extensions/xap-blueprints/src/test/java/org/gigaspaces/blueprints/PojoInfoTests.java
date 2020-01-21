package org.gigaspaces.blueprints;

import com.gigaspaces.internal.io.BootIOUtils;
import org.gigaspaces.blueprints.java.PojoInfo;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class PojoInfoTests {
    @Test
    public void testFoo() throws IOException {
        String expected = BootIOUtils.readAsString(BootIOUtils.getResourcePath("samples/Person.java"));
        PojoInfo personPojoInfo = new PojoInfo("Person", "com.gigaspaces.demo");
        personPojoInfo.addProperty("id", int.class);
        personPojoInfo.addProperty("name", String.class);
        personPojoInfo.addPropertyWithAutoGenerate("auto-generate", long.class);

        String actual = personPojoInfo.generate();
        System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }

}
