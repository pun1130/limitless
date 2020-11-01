package test;

import java.lang.invoke.MethodHandle;
import net.gudenau.lib.unsafe.Unsafe;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import user11681.reflect.Fields;
import user11681.reflect.Invoker;

@Testable
public class UnsafeTest {
    @Test
    public void newClassInstance() throws Throwable {
        final MethodHandle constructor = Invoker.findConstructor(Class.class, ClassLoader.class);
        Unsafe.putBoolean(constructor, Fields.overrideOffset, true);
        final Class<?> klass = (Class<?>) constructor.invokeExact(UnsafeTest.class.getClassLoader());

        Unsafe.allocateInstance(klass);
    }
}
