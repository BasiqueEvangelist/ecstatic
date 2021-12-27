package me.basiqueevangelist.ecstatic.impl;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class InnerClassTransformer extends ClassNodeZipEntryTransformer {
    @Override
    protected ClassNode transform(ClassNode node) {
        String containingName = node.name.substring(0, node.name.lastIndexOf('$'));

        for (MethodNode method : node.methods) {
            if (method.name.equals("<init>")) {
                for (AbstractInsnNode instr : method.instructions) {
                    AbstractInsnNode prev = instr.getPrevious();

                    if (!(prev instanceof VarInsnNode varInsn))
                        continue;

                    if (varInsn.var != 1)
                        continue;

                    if (!(instr instanceof FieldInsnNode fieldInsn))
                        continue;

                    if (!fieldInsn.owner.equals(node.name))
                        continue;

                    if (fieldInsn.getOpcode() != Opcodes.PUTFIELD)
                        continue;

                    if (!fieldInsn.desc.equals("L" + containingName + ";"))
                        continue;

                    for (FieldNode field : node.fields) {
                        if (field.name.equals(fieldInsn.name) && field.desc.equals(fieldInsn.desc)) {
                            field.access &= ~Opcodes.ACC_SYNTHETIC;
                            System.out.printf("Making %s.%s %s not synthetic\n", node.name, field.name, field.desc);
                        }
                    }
                }
            }
        }

//        if (node.outerMethod != null) {
//            node.outerMethod = null;
//            node.outerMethodDesc = null;
//            node.outerClass = containingName;
//            System.out.printf("Making %s not in a method\n", node.name);
//        }


        return node;
    }
}
