package com.zjonline.xsb_processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.zjonline.lib_annotation.LayoutAnn;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public class XsbProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements elementUtils;
    private Trees trees;
    private static final String layoutAnn = "@LayoutAnn";//this special ann
    private static final String layout_FLAG = "layout", title_FLAG = "title";
    private static final String titleStringRes_FLAG = "titleStringRes", leftImgRes_FLAG = "leftImgRes";
    private static final String rightImgRes_FLAG = "rightImgRes", rightText_FLAG = "rightText";
    private static final String isSwipeBack_FLAG = "isSwipeBack";
    private String rPackage = null;//R’packageName
    private final TypeName VIEW_TYPE = ClassName.get("android.view", "View");
    private final TypeName VIEWGROUP_TYPE = ClassName.get("android.view", "ViewGroup");
    private final TypeName STRING_TYPE = ClassName.get("java.lang", "String");

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        elementUtils = env.getElementUtils();
        filer = env.getFiler();
        try {
            trees = Trees.instance(processingEnv);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(LayoutAnn.class);
        return annotations;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (trees == null) return false;
        scanForRClasses(roundEnv);
        createAutoClass(roundEnv);
        return false;
    }

    private void scanForRClasses(RoundEnvironment env) {
        if (trees == null) return;
        RClassScanner scanner = new RClassScanner();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            for (Element element : env.getElementsAnnotatedWith(annotation)) {
                JCTree tree = (JCTree) trees.getTree(element, getMirror(element, annotation));
                if (tree != null) tree.accept(scanner);
            }
        }

    }

    private class RClassScanner extends TreeScanner {

        @Override
        public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
            Symbol symbol = jcFieldAccess.sym;
            if (symbol != null && symbol.getEnclosingElement() != null && symbol.getEnclosingElement().getEnclosingElement() != null
                    && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {
                rPackage = symbol.getEnclosingElement().getEnclosingElement().enclClass().className();
                int lastIndex = rPackage.lastIndexOf(".");
                rPackage = rPackage.substring(0, lastIndex);
            }
        }
    }

    private void createAutoClass(RoundEnvironment roundEnv) {
        if (rPackage == null) return;
        for (Element element : roundEnv.getElementsAnnotatedWith(LayoutAnn.class)) {
            JCTree tree = (JCTree) trees.getTree(element, getMirror(element, LayoutAnn.class));
            if (tree != null) { // tree can be null if the references are compiled types and not source
                String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
                String className = element.getSimpleName().toString();
                String layoutAnnString = tree.toString();
                if (layoutAnnString == null || "".equals(layoutAnnString) || !layoutAnnString.startsWith(layoutAnn)) return;
                layoutAnnString = layoutAnnString.replaceAll(" ", "");
                int firstIndex = layoutAnnString.indexOf("(");
                layoutAnnString = layoutAnnString.substring(firstIndex + 1, layoutAnnString.length() - 1);

                TypeName targetType = ClassName.get(packageName, className);
                String layout = getRresString(layoutAnnString, layout_FLAG);
                String title = getRresString(layoutAnnString, title_FLAG);
                String titleStringRes = getRresString(layoutAnnString, titleStringRes_FLAG);
                String leftImgRes = getRresString(layoutAnnString, leftImgRes_FLAG);
                String rightImgRes = getRresString(layoutAnnString, rightImgRes_FLAG);
                String rightText = getRresString(layoutAnnString, rightText_FLAG);
                String isSwipeBack = getRresString(layoutAnnString, isSwipeBack_FLAG);

                if (layout == null) layout = "0";
                if (titleStringRes == null) titleStringRes = "0";
                if (leftImgRes == null) leftImgRes = "0";
                if (rightImgRes != null) rightImgRes = "new int []{" + rightImgRes + "}";
                if (rightText != null) rightText = "new int []{" + rightText + "}";
                if (isSwipeBack == null) isSwipeBack = "true";

                //
                MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(PUBLIC)
                        .addParameter(targetType, "target");
                constructorBuilder.addStatement("this(target,0)");

                //
                MethodSpec.Builder viewConstructorBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(PUBLIC)
                        .addParameter(targetType, "target")
                        .addParameter(ClassName.INT, "titleViewId");

                viewConstructorBuilder.addStatement("int layoutId = " + layout);
                viewConstructorBuilder.beginControlFlow("if (layoutId == 0)")
                        .addStatement("layoutId = target.layoutId()")
                        .endControlFlow();

                viewConstructorBuilder.addStatement("if(!" + isSwipeBack + ")target.setContentView(layoutId)");//!swipeBack
                viewConstructorBuilder.beginControlFlow("else ")
                        .addStatement("View v = target.createSwipeBackView(layoutId)")
                        .addStatement("if (v!=null)target.setContentView(v)")
                        .addStatement("else target.setContentView(layoutId)")
                        .endControlFlow();
                viewConstructorBuilder.addStatement("android.view.View titleView = titleViewId == 0? null : target.findViewById(titleViewId)");

                viewConstructorBuilder.addStatement("initTitleView(target,titleView," + title + "," + titleStringRes + ","
                        + leftImgRes + "," + rightImgRes + "," + rightText + ")");

                FieldSpec.Builder fieldSpec = FieldSpec.builder(VIEW_TYPE, "view", PUBLIC);
                MethodSpec.Builder viewGroupBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(PUBLIC)
                        .addParameter(targetType, "target")
                        .addParameter(ClassName.INT, "titleViewId")
                        .addParameter(VIEWGROUP_TYPE, "viewGroup");
                viewGroupBuilder.addStatement("if(" + isSwipeBack + ")view = target.createSwipeBackView(" + layout + ")");
                viewGroupBuilder.addStatement(" else view =  target.getLayoutInflater().inflate(" + layout + ",viewGroup,false);");
                viewGroupBuilder.addStatement("  android.view.View titleView = titleViewId == 0 ? null : view.findViewById(titleViewId)");
                viewGroupBuilder.addStatement("initTitleView(target,titleView," + title + "," + titleStringRes + ","
                        + leftImgRes + "," + rightImgRes + "," + rightText + ")");

                MethodSpec.Builder getViewMethod = MethodSpec.methodBuilder("getView").addModifiers(PUBLIC)
                        .returns(VIEW_TYPE)
                        .addStatement("return view");

                TypeSpec.Builder result = TypeSpec.classBuilder(className + "_LayoutAnn").addModifiers(PUBLIC);
                result.addMethod(constructorBuilder.build());
                result.addMethod(viewConstructorBuilder.build());
                result.addMethod(viewGroupBuilder.build());
                result.addField(fieldSpec.build());
                result.addMethod(initTitleView(targetType));
                result.addMethod(getViewMethod.build());
//
                JavaFile javaFile = JavaFile.builder(packageName, result.build())
                        .addFileComment("Generated code from Layout. Do not modify!")
                        .build();
                try {
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private MethodSpec initTitleView(TypeName typeName) {
        return MethodSpec.methodBuilder("initTitleView")
                .addModifiers(PRIVATE)
                .addParameter(typeName, "activity")
                .addParameter(VIEW_TYPE, "titleView")
                .addParameter(STRING_TYPE, "title")
                .addParameter(TypeName.INT, "titleStringRes")
                .addParameter(TypeName.INT, "leftImgRes")
                .addParameter(int[].class, "rightImgRes")
                .addParameter(int[].class, "rightText")
                .addStatement("  if (titleView ==null|| !titleView.getClass().getName().endsWith(\"TitleView\")) return")
                .addStatement("activity.setTitleView(titleView)")
                .beginControlFlow("try ")
                .beginControlFlow("if (titleStringRes != 0)")
                .addStatement(callMethod("setTitle", "int", "titleStringRes")).endControlFlow()
                .beginControlFlow("else ").addStatement(callMethod("setTitle", "String", "title")).endControlFlow()

                .beginControlFlow("if (leftImgRes == 0) ")
                .addStatement(callMethod("setLeftOneImge", "int", rPackage + ".R.mipmap.xsb_view_return_btn")).endControlFlow()
                .beginControlFlow("else if (leftImgRes > 0)").addStatement(callMethod("setLeftOneImge", "int", "leftImgRes")).endControlFlow()
                .beginControlFlow("else if (leftImgRes < 0) ").addStatement(callMethod("setLeftOneImge", "int", "0")).endControlFlow()

                .beginControlFlow(" if (rightImgRes != null) ")
                .beginControlFlow("if (rightImgRes.length == 1) ")
                .addStatement(callMethod("setRightOneImge", "int", "rightImgRes[0]"))
                .endControlFlow()

                .beginControlFlow(" else if (rightImgRes.length == 2) ")
                .addStatement(callMethod("setRightOneImge", "int", "rightImgRes[0]"))
                .addStatement(callMethod("setRightTwoImge", "int", "rightImgRes[1]"))
                .endControlFlow().endControlFlow()

                .beginControlFlow(" if (rightText != null) ")
                .beginControlFlow(" if (rightText.length == 1)")
                .addStatement(callMethod("setRightOneText", "String", "rightText[0] == 0 ? null : activity.getString(rightText[0])"))
                .endControlFlow()

                .beginControlFlow("else if (rightText.length == 2)")
                .addStatement(callMethod("setRightOneText", "String", "rightText[0] == 0 ? null : activity.getString(rightText[0])"))
                .addStatement(callMethod("setRightTwoText", "String", "rightText[1] == 0 ? null : activity.getString(rightText[1])"))
                .endControlFlow()
                .endControlFlow()
                .endControlFlow().beginControlFlow("catch (java.lang.Exception e) ").addStatement(" e.printStackTrace()").endControlFlow()
                .build();
    }

    public String callMethod(String methodName, String classType, String param) {
        return "titleView.getClass().getDeclaredMethod(\"" + methodName + "\"," +
                classType + ".class).invoke(titleView," + param + ")";
    }

    private String getRresString(String annString, String flag) {
        flag = flag + "=";
        String arrFlag = flag + "{";
        String layout = null;
        int arrIndex = annString.indexOf(arrFlag);
        if (arrIndex >= 0) {
            int endIndex = annString.indexOf("}", arrIndex);
            if (endIndex < 0) endIndex = annString.length();
            layout = annString.substring(arrIndex + arrFlag.length(), endIndex);
            String arr[] = layout.split(",");
            StringBuilder tem = new StringBuilder();
            int length = arr.length;
            for (int i = 0; i < length; i++) {
                String ss = arr[i];
                String aaArr[] = ss.split("\\.");
                if (aaArr.length >= 2) ss = rPackage + "." + "R." + aaArr[1] + "." + aaArr[2];
                arr[i] = ss;
                tem.append(ss);
                if (i < length - 1) tem.append(",");
            }
            return tem.toString();
        } else {
            int flagIndex = annString.indexOf(flag);
            if (flagIndex >= 0) {
                int endIndex = annString.indexOf(",", flagIndex);
                if (endIndex < 0) endIndex = annString.length();
                layout = annString.substring(flagIndex + flag.length(), endIndex);
                String arr[] = layout.split("\\.");
                if (arr.length >= 2) layout = rPackage + "." + "R." + arr[1] + "." + arr[2];
            }
            return layout;
        }
    }

    private static AnnotationMirror getMirror(Element element, Class<? extends Annotation> annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotation.getCanonicalName())) {
                return annotationMirror;
            }
        }
        return null;
    }

}
