package com.zjonline.xsb_ann_plugin;

import com.android.build.gradle.api.ApplicationVariant;
import com.android.build.gradle.tasks.ProcessAndroidResources;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateApplicationR {
    private static final String SUPPORT_ANNOTATION_PACKAGE = "androidx.annotation";
    private static final String[] SUPPORTED_TYPES = {
            "anim", "array", "attr", "bool", "color", "dimen", "drawable", "id", "integer", "layout", "menu", "plurals",
            "string", "style", "styleable", "mipmap"
    };

    private CreateApplicationR() {
    }

    public static void brewJava(Project project, DomainObjectSet<ApplicationVariant> set) throws Exception {
        File buildFile = project.getBuildDir();
        File packagePath = new File(buildFile, "generated/source/r/debug/com");
        List<String> rPaths = getRPath(packagePath);
        int size = rPaths == null ? 0 : rPaths.size();
        for (int i = 0; i < size; i++) {
            String rPath = rPaths.get(i);
            System.out.println("---------rPath--------->" + rPath);
            File rFile = new File(rPath);
            set.all(applicationVariant -> {
                Task task =  project.task("generate------>R3"+Math.random());
                task.getOutputs().dir(packagePath);
                applicationVariant.registerJavaGeneratingTask(task, packagePath);
                applicationVariant.getOutputs().all(baseVariantOutput -> {
                    ProcessAndroidResources processResources = baseVariantOutput.getProcessResources();
                    task.dependsOn(processResources);
                    task.getInputs().file(rFile);
                    task.doLast(task1 -> {
                        createR3(packagePath,rPath,buildFile,rFile);
                    });
                });
            });


        }
    }

    public static void createR3(File packagePath, String rPath, File buildFile, File rFile) {
        try {
            int length = packagePath.getAbsolutePath().length();
            String rPackagePath = rPath.substring(length + 1, rPath.length());
            System.out.println("------------------>" + rPackagePath);
            File outFile = new File(buildFile, "generated/source/r2/debug/com");
            if (!outFile.exists()) outFile.mkdir();
            System.out.println("----------outFile-------->" + outFile);
            CreateR.brewJava(rFile, outFile, rPackagePath, "R2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> getRPath(File packagePath) {
        List<String> rPath = new ArrayList<>();
        File[] files = packagePath.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                File[] childFiles = file.listFiles();
                assert childFiles != null;
                for (File childFile : childFiles) {
                    if (childFile.isDirectory()) {
                        File[] childChildFiles = childFile.listFiles();
                        assert childChildFiles != null;
                        for (File childChildFile : childChildFiles) {
                            if (childChildFile.isDirectory()) {
                                File[] childChildChildFiles = childChildFile.listFiles();
                                assert childChildChildFiles != null;
                                for (File childChildChildFile : childChildChildFiles) {
                                    if (childChildChildFile.isDirectory()) {
                                        File[] childChildChildChildFiles = childChildChildFile.listFiles();
                                        assert childChildChildChildFiles != null;
                                        for (File childChildChildChildFile : childChildChildChildFiles) {
                                            if (!childChildChildChildFile.isDirectory()) rPath.add(childChildChildChildFile.getAbsolutePath());
                                        }
                                    } else rPath.add(childChildChildFile.getAbsolutePath());
                                }
                            } else rPath.add(childChildFile.getAbsolutePath());
                        }
                    } else rPath.add(childFile.getAbsolutePath());
                }
            } else rPath.add(file.getAbsolutePath());
        }
        return rPath;
    }

}
