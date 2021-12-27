package me.basiqueevangelist.ecstatic.impl;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;

import java.io.IOException;

public class OuterClassTransformer extends ClassNodeZipEntryTransformer {
    private final String[] classesToMakeStatic;

    public OuterClassTransformer(String[] classesToMakeStatic) {
        this.classesToMakeStatic = classesToMakeStatic;
    }

    @Override
    protected ClassNode transform(ClassNode node) {
        for (InnerClassNode innerClass : node.innerClasses) {
            for (String name : classesToMakeStatic) {
                if (innerClass.name.equals(name)) {
                    if (innerClass.outerName == null) {
                        innerClass.outerName = node.name;
                        System.out.println("Made " + innerClass.name + " non-local!");
                    }

                    if ((innerClass.access & Opcodes.ACC_STATIC) == 0) {
                        innerClass.access |= Opcodes.ACC_STATIC;
                        System.out.println("Made " + innerClass.name + " static!");
                    }
                }
            }
        }

        return node;
    }
}
