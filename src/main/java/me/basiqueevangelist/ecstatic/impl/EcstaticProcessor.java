package me.basiqueevangelist.ecstatic.impl;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import me.basiqueevangelist.ecstatic.api.EcstaticExtension;
import net.fabricmc.loom.configuration.processors.JarProcessor;
import net.fabricmc.loom.util.Pair;
import net.fabricmc.loom.util.ZipUtils;

import java.io.File;
import java.io.IOException;
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
    public String getId() {
        return "ecstatic:ecstatic_" + BaseEncoding.base16().lowerCase().encode(classesHash);
    }

    @Override
    public void setup() {
        classesToMakeStatic = extension.getTargetedClasses().get().toArray(new String[0]);
        classesHash = Hashing.sha256().hashString(String.join(";", classesToMakeStatic), Charset.defaultCharset()).asBytes();
        allContainingClasses = Arrays.stream(classesToMakeStatic).map(x -> x.substring(0, x.lastIndexOf('$'))).toArray(String[]::new);
    }

    @Override
    public void process(File file) {
        var innerTransformer = new InnerClassTransformer();
        var outerTransformer = new OuterClassTransformer(classesToMakeStatic);
//        ZipEntryTransformerEntry[] transformers = Stream.concat(
//            Arrays.stream(classesToMakeStatic)
//                .map(x -> new ZipEntryTransformerEntry(x + ".class", innerTransformer)),
//            Arrays.stream(allContainingClasses)
//                .map(x -> new ZipEntryTransformerEntry(x + ".class", outerTransformer))
//        ).toArray(ZipEntryTransformerEntry[]::new);

        try {
            ZipUtils.transform(file.toPath(), Stream.concat(
                Arrays.stream(classesToMakeStatic)
                    .map(x -> new Pair<>(x, innerTransformer)),
                Arrays.stream(allContainingClasses)
                    .map(x -> new Pair<>(x, outerTransformer))
            ));
        } catch (IOException e) {
            throw new RuntimeException("Failed to ecstaticify " + file, e);
        }

//        ZipUtil.transformEntries(file, transformers);
    }
}
