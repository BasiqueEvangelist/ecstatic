package me.basiqueevangelist.ecstatic.impl;

import com.google.common.hash.Hashing;
import me.basiqueevangelist.ecstatic.api.EcstaticExtension;
import net.fabricmc.loom.configuration.processors.JarProcessor;
import org.zeroturnaround.zip.ZipUtil;
import org.zeroturnaround.zip.transform.ZipEntryTransformerEntry;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class EcstaticProcessor implements JarProcessor {
    private final EcstaticExtension extension;
    private String[] classesToMakeStatic;
    private String[] allContainingClasses;
    private byte[] classesHash;

    public EcstaticProcessor(EcstaticExtension extension) {
        this.extension = extension;
    }

    @Override
    public void setup() {
        classesToMakeStatic = extension.getTargetedClasses().get().toArray(new String[0]);
        classesHash = Hashing.sha256().hashString(String.join(";", classesToMakeStatic), Charset.defaultCharset()).asBytes();
        allContainingClasses = Arrays.stream(classesToMakeStatic).map(x -> x.substring(0, x.lastIndexOf('$'))).toArray(String[]::new);
    }

    @Override
    public void process(File file) {
        var fixFieldsTransformer = new MakeFieldNonsyntheticTransformer();
        var staticifyTransformer = new MakeInnerClassesStaticTransformer(classesToMakeStatic);
        ZipEntryTransformerEntry[] transformers = Stream.concat(
            Arrays.stream(classesToMakeStatic)
                .map(x -> new ZipEntryTransformerEntry(x + ".class", fixFieldsTransformer)),
            Arrays.stream(allContainingClasses)
                .map(x -> new ZipEntryTransformerEntry(x + ".class", staticifyTransformer))
        ).toArray(ZipEntryTransformerEntry[]::new);

        ZipUtil.transformEntries(file, transformers);

        ZipUtil.addEntry(file, "ecstatic.txt", classesHash);
    }

    @Override
    public boolean isInvalid(File file) {
        byte[] data = ZipUtil.unpackEntry(file, "ecstatic.txt");

        if (data == null) return true;

        return !Arrays.equals(data, classesHash);
    }
}
