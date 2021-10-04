package me.basiqueevangelist.ecstatic.impl;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.zeroturnaround.zip.transform.StreamZipEntryTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;

public abstract class ClassNodeZipEntryTransformer extends StreamZipEntryTransformer {
    protected abstract ClassNode transform(ClassNode node) throws IOException;

    @Override
    protected void transform(ZipEntry zipEntry, InputStream in, OutputStream out) throws IOException {
        var node = new ClassNode();
        new ClassReader(in).accept(node, 0);
        var transformedNode = transform(node);
        var cw = new ClassWriter(0);
        transformedNode.accept(cw);
        out.write(cw.toByteArray());
    }
}
