package com.zjonline.xsb_ann_plugin;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class CreateR {
    private static final String SUPPORT_ANNOTATION_PACKAGE = "android.support.annotation";
    private static final String[] SUPPORTED_TYPES = {
            "anim", "array", "attr", "bool", "color", "dimen", "drawable", "id", "integer", "layout", "menu", "plurals",
            "string", "style", "styleable", "mipmap"
    };

    private CreateR() {
    }

    public static void brewJava(File rFile, File outputDir, String packageName, String className) throws Exception {
        CompilationUnit compilationUnit = JavaParser.parse(rFile);
        TypeDeclaration resourceClass = compilationUnit.getTypes().get(0);

        TypeSpec.Builder result =
                TypeSpec.classBuilder(className).addModifiers(PUBLIC).addModifiers(FINAL);

        for (Node node : resourceClass.getChildNodes()) {
            if (node instanceof ClassOrInterfaceDeclaration) {
                addResourceType(Arrays.asList(SUPPORTED_TYPES), result, (ClassOrInterfaceDeclaration) node);
            }
        }

        JavaFile finalR = JavaFile.builder(packageName, result.build())
                .addFileComment("Generated code from My gradle plugin. Do not modify!")
                .build();

        finalR.writeTo(outputDir);
    }

    private static void addResourceType(List<String> supportedTypes, TypeSpec.Builder result, ClassOrInterfaceDeclaration node) {
        if (!supportedTypes.contains(node.getNameAsString())) {
            return;
        }

        String type = node.getNameAsString();
        TypeSpec.Builder resourceType = TypeSpec.classBuilder(type).addModifiers(PUBLIC, STATIC, FINAL);

        for (BodyDeclaration field : node.getMembers()) {
            if (field instanceof FieldDeclaration) {
                FieldDeclaration declaration = (FieldDeclaration) field;
                // Check that the field is an Int because styleable also contains Int arrays which can't be
                // used in annotations.
                if (isInt(declaration)) {
                    addResourceField(resourceType, declaration.getVariables().get(0),
                            getSupportAnnotationClass(type));
                }
            }
        }

        result.addType(resourceType.build());
    }

    private static boolean isInt(FieldDeclaration field) {
        Type type = field.getCommonType();
        return type instanceof PrimitiveType
                && ((PrimitiveType) type).getType() == PrimitiveType.Primitive.INT;
    }

    private static void addResourceField(TypeSpec.Builder resourceType, VariableDeclarator variable,
                                         ClassName annotation) {
        String fieldName = variable.getNameAsString();
        String fieldValue = variable.getInitializer().map(Node::toString).orElse(null);
        FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(int.class, fieldName)
                .addModifiers(PUBLIC, STATIC, FINAL)
                .initializer(fieldValue);

        if (annotation != null) {
            fieldSpecBuilder.addAnnotation(annotation);
        }

        resourceType.addField(fieldSpecBuilder.build());
    }

    private static ClassName getSupportAnnotationClass(String type) {
        return ClassName.get(SUPPORT_ANNOTATION_PACKAGE, capitalize(type) + "Res");
    }

    private static String capitalize(String word) {
        if (word.startsWith("mi")) word = "drawable";
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    @SuppressWarnings("unchecked")
    public static void unZipFiles(String zipFilePath, String fileSavePath) {
//        FileOperateUtil fileOperateUtil = new FileOperateUtil();
        boolean isUnZipSuccess = true;
        try {
            (new File(fileSavePath)).mkdirs();
            File f = new File(zipFilePath);
            if ((!f.exists()) && (f.length() <= 0)) {
                throw new RuntimeException("not find " + zipFilePath + "!");
            }
            //
            System.out.println("------------>: " + f.exists());
            JarFile zipFile = new JarFile(zipFilePath);
            String gbkPath, strtemp;
            Enumeration<JarEntry> e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry zipEnt = e.nextElement();
                gbkPath = zipEnt.getName();
                strtemp = fileSavePath + File.separator + gbkPath;
                if (zipEnt.isDirectory()) { //
                    File dir = new File(strtemp);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    continue;
                } else {
                    //
                    InputStream is = zipFile.getInputStream(zipEnt);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    //
                    String strsubdir = gbkPath;
                    for (int i = 0; i < strsubdir.length(); i++) {
                        if (strsubdir.substring(i, i + 1).equalsIgnoreCase("/")) {
                            String temp = fileSavePath + File.separator
                                    + strsubdir.substring(0, i);
                            File subdir = new File(temp);
                            if (!subdir.exists())
                                subdir.mkdir();
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(strtemp);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    int len;
                    byte[] buff = new byte[5120];
                    while ((len = bis.read(buff)) != -1) {
                        bos.write(buff, 0, len);
                    }
                    bos.close();
                    fos.close();
                }
            }
            zipFile.close();
        } catch (Exception e) {
            isUnZipSuccess = false;
            System.out.println("extract file error: " + zipFilePath);
        }
    }

}