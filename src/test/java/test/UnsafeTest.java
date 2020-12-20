package test;

import java.lang.annotation.RetentionPolicy;
import java.lang.invoke.MethodHandle;
import net.gudenau.lib.unsafe.Unsafe;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import user11681.reflect.Classes;
import user11681.reflect.Fields;
import user11681.reflect.Invoker;

@Testable
public class UnsafeTest {
    @Test
    public void enumInstance() throws Throwable {
        final ClassNode node = new ClassNode();
        node.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER + Opcodes.ACC_ENUM + Opcodes.ACC_FINAL, "enum", null, "java/lang/Enum", null);

        final MethodNode constructor = (MethodNode) node.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/lang/String;I)V", null, null);
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitVarInsn(Opcodes.ALOAD, 1);
        constructor.visitVarInsn(Opcodes.ILOAD, 2);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V", false);
        constructor.visitInsn(Opcodes.RETURN);

        final MethodNode instantiate = (MethodNode) node.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "instantiate", "(Ljava/lang/String;I)Lenum;", null, null);
        instantiate.visitTypeInsn(Opcodes.NEW, "enum");
        instantiate.visitInsn(Opcodes.DUP);
        instantiate.visitVarInsn(Opcodes.ALOAD, 0);
        instantiate.visitVarInsn(Opcodes.ILOAD, 1);
        instantiate.visitMethodInsn(Opcodes.INVOKESPECIAL, "enum", "<init>", "(Ljava/lang/String;I)V", false);
        instantiate.visitInsn(Opcodes.ARETURN);

        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);

        final Class<Enum<?>> klass = Classes.defineClass(UnsafeTest.class.getClassLoader(), "enum", writer.toByteArray());
        final Enum<?> instance = (Enum<?>) klass.getMethod("instantiate", String.class, int.class).invoke(null, "ENUM", 0);

        System.out.printf("class: %s\n", instance.getClass());
        System.out.printf("superclass: %s\n", instance.getClass().getSuperclass());
        System.out.println(instance);
    }

    @Test
    public void enumSubclassSubclass() throws Throwable {
        final ClassNode node = new ClassNode();
        node.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, "enum", null, "java/lang/annotation/RetentionPolicy", null);

        final ClassWriter writer = new ClassWriter(0);
        node.accept(writer);

        final Class<Enum<?>> klass = Classes.defineClass(UnsafeTest.class.getClassLoader(), "enum", writer.toByteArray());

        System.out.printf("class: %s\n", klass);
        System.out.printf("superclass: %s\n", klass.getSuperclass());

        RetentionPolicy.valueOf("bruh");
    }

    @Test
    public void enumSubclass() throws Throwable {
        final ClassNode node = new ClassNode();
        node.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, "enum", null, "java/lang/Enum", null);

        final MethodNode constructor = (MethodNode) node.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitLdcInsn("TEST");
        constructor.visitInsn(Opcodes.ICONST_0);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V", false);
        constructor.visitInsn(Opcodes.RETURN);

        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);

        final Class<Enum<?>> klass = Classes.defineClass(UnsafeTest.class.getClassLoader(), "enum", writer.toByteArray());
        final Enum<?> instance = klass.newInstance();

        System.out.printf("class: %s\n", instance.getClass());
        System.out.printf("superclass: %s\n", instance.getClass().getSuperclass());
        System.out.println(instance);
    }

    @Test
    public void newClassInstance() throws Throwable {
        final MethodHandle constructor = Invoker.findConstructor(Class.class, ClassLoader.class);
        Unsafe.putBoolean(constructor, Fields.overrideOffset, true);
        final Class<?> klass = (Class<?>) constructor.invokeExact(UnsafeTest.class.getClassLoader());

        Unsafe.allocateInstance(klass);
    }
}
