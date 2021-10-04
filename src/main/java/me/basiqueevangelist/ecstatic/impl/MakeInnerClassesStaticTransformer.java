package me.basiqueevangelist.ecstatic.impl;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;

import java.io.IOException;

public class MakeInnerClassesStaticTransformer extends ClassNodeZipEntryTransformer {
    private final String[] classesToMakeStatic;

    public MakeInnerClassesStaticTransformer(String[] classesToMakeStatic) {
        this.classesToMakeStatic = classesToMakeStatic;
    }

    @Override
    protected ClassNode transform(ClassNode node) throws IOException {
        for (InnerClassNode innerClass : node.innerClasses) {
            for (String name : classesToMakeStatic) {
                if (innerClass.name.equals(name)) {
                    innerClass.access |= Opcodes.ACC_STATIC;
                    System.out.println("Made " + innerClass.name + " static!");
                }
            }
        }

        return node;
    }
}
