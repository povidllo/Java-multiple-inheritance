package kuzminov.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "kuzminov.annotations.RootInterface",
        "kuzminov.annotations.Supers"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class AnnotationProcessor extends AbstractProcessor {

    private final Map<TypeElement, List<TypeElement>> supersGraph = new LinkedHashMap<>();
    private final MroResolver mroResolver = new MroResolver();

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        if (!roundEnv.processingOver()) {
            return processAnnotations(annotations, roundEnv, false);
        }

        return processAnnotations(annotations, roundEnv, true);
    }

    private boolean processAnnotations(Set<? extends TypeElement> annotations,
                                       RoundEnvironment roundEnv,
                                       boolean generateFinal) {

        /*
         * @Supers
         */
        for (Element elem : roundEnv.getElementsAnnotatedWith(
                processingEnv.getElementUtils()
                        .getTypeElement("kuzminov.annotations.Supers")
        )) {

            if (elem.getKind() != ElementKind.CLASS) continue;

            TypeElement cls = (TypeElement) elem;

            List<TypeElement> parents = new ArrayList<>();

            for (AnnotationMirror mirror : cls.getAnnotationMirrors()) {

                if (!mirror.getAnnotationType().toString()
                        .equals("kuzminov.annotations.Supers")) continue;

                for (var entry : mirror.getElementValues().entrySet()) {

                    List<?> values = (List<?>) entry.getValue().getValue();

                    for (Object v : values) {

                        TypeMirror tm =
                                (TypeMirror) ((AnnotationValue) v).getValue();

                        TypeElement parent =
                                (TypeElement) processingEnv
                                        .getTypeUtils()
                                        .asElement(tm);

                        parents.add(parent);
                    }
                }
            }

            supersGraph.put(cls, parents);
        }

        /*
         * RootClass
         */
        for (Element element : roundEnv.getElementsAnnotatedWith(
                processingEnv.getElementUtils()
                        .getTypeElement("kuzminov.annotations.RootInterface")
        )) {

            if (element.getKind() != ElementKind.INTERFACE) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "@RootInterface only for interfaces",
                        element
                );
                continue;
            }

            TypeElement interfaceElement = (TypeElement) element;

            String interfaceName = interfaceElement.getSimpleName().toString();
            String rootName = interfaceName + "RootClass";

            try {

                TypeSpec.Builder classBuilder =
                        TypeSpec.classBuilder(rootName)
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                /*.addSuperinterface(TypeName.get(interfaceType))*/;

                /*
                 * next field
                 */
                FieldSpec nextField =
                        FieldSpec.builder(
                                ClassName.bestGuess(rootName),
                                "next",
                                Modifier.PROTECTED, Modifier.VOLATILE
                        ).build();

                classBuilder.addField(nextField);

                /*
                 * Конструктор для инициализации цепочки
                 */
                MethodSpec constructor =
                        MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PROTECTED)
                                .addStatement("initChain()")
                                .build();

                classBuilder.addMethod(constructor);

                /*
                 * методы
                 */
                for (Element e : interfaceElement.getEnclosedElements()) {
                    if (e.getKind() != ElementKind.METHOD) continue;

                    ExecutableElement method = (ExecutableElement) e;
                    String methodName = method.getSimpleName().toString();
                    String nextMethod = "next" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);

                    List<? extends VariableElement> params = method.getParameters();
                    String paramsList = params.stream()
                            .map(p -> p.getSimpleName().toString())
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");

                    MethodSpec.Builder overrideBuilder = MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.get(method.getReturnType()));

                    MethodSpec.Builder nextBuilder = MethodSpec.methodBuilder(nextMethod)
                            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                            .returns(TypeName.get(method.getReturnType()));

                    for (VariableElement param : params) {
                        TypeName typeName = TypeName.get(param.asType());
                        String paramName = param.getSimpleName().toString();
                        overrideBuilder.addParameter(typeName, paramName);
                        nextBuilder.addParameter(typeName, paramName);
                    }

                    for (TypeMirror thrown : method.getThrownTypes()) {
                        overrideBuilder.addException(TypeName.get(thrown));
                        nextBuilder.addException(TypeName.get(thrown));
                    }

                    if (method.getReturnType().getKind() == TypeKind.VOID) {
                        overrideBuilder.addStatement("$L($L)", nextMethod, paramsList);
                    } else {
                        overrideBuilder.addStatement("return $L($L)", nextMethod, paramsList);
                    }

                    classBuilder.addMethod(overrideBuilder.build());

                    nextBuilder.beginControlFlow("if (next != null)");
                    if (method.getReturnType().getKind() == TypeKind.VOID) {
                        nextBuilder.addStatement("next.$L($L)", methodName, paramsList);
                    } else {
                        nextBuilder.addStatement("return next.$L($L)", methodName, paramsList);
                    }
                    nextBuilder.endControlFlow();

                    if (method.getReturnType().getKind() != TypeKind.VOID) {
                        String defaultValue = getDefaultValue(method.getReturnType().getKind());
                        nextBuilder.addStatement("return " + defaultValue);
                    }

                    classBuilder.addMethod(nextBuilder.build());
                }

                /*
                 * initChain
                 */
                MethodSpec initChain =
                        MethodSpec.methodBuilder("initChain")
                                .addModifiers(Modifier.PROTECTED)
                                .addStatement("$T mro = $LHierarchy.getMRO(getClass())", List.class, interfaceName)
                                .beginControlFlow("if (mro == null || mro.size() <= 1)")
                                .addStatement("return")
                                .endControlFlow()
                                .addStatement("$L current = null", rootName)
                                .beginControlFlow("for (int i = mro.size() - 1; i >= 1; i--)")
                                .addStatement("Class<?> cls = (Class<?>) mro.get(i)")
                                .beginControlFlow("try")
                                .addStatement("$T cons = cls.getDeclaredConstructor()", java.lang.reflect.Constructor.class)
                                .addStatement("cons.setAccessible(true)")
                                .addStatement("$L instance = ($L) cons.newInstance()", rootName, rootName).addStatement("instance.next = current")
                                .addStatement("current = instance")
                                .nextControlFlow("catch ($T e)", Exception.class)
                                .addStatement("throw new RuntimeException(e)")
                                .endControlFlow()
                                .endControlFlow()
                                .addStatement("this.next = current")
                                .build();

                classBuilder.addMethod(initChain);

                String packageName =
                        processingEnv.getElementUtils()
                                .getPackageOf(interfaceElement)
                                .getQualifiedName()
                                .toString();

                JavaFile.builder(packageName, classBuilder.build())
                        .build()
                        .writeTo(processingEnv.getFiler());

            } catch (IOException e) {

                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        e.toString()
                );
            }
        }

        /*
         * Hierarchy для @RootInterface
         */
        for (Element element : roundEnv.getElementsAnnotatedWith(
                processingEnv.getElementUtils()
                        .getTypeElement("kuzminov.annotations.RootInterface")
        )) {

            if (element.getKind() != ElementKind.INTERFACE) continue;


            TypeElement interfaceElement = (TypeElement) element;
            String interfaceName = interfaceElement.getSimpleName().toString();
            String hierarchyName = interfaceName + "Hierarchy";
            String rootClassName = interfaceName + "RootClass";

            try {

                TypeSpec.Builder hierarchy =
                        TypeSpec.classBuilder(hierarchyName)
                                .addModifiers(Modifier.PUBLIC);

                FieldSpec mapField =
                        FieldSpec.builder(
                                        ParameterizedTypeName.get(
                                                ClassName.get(Map.class),
                                                ClassName.get(Class.class),
                                                ParameterizedTypeName.get(
                                                        ClassName.get(List.class),
                                                        ClassName.get(Class.class)
                                                )
                                        ),
                                        "MROS",
                                        Modifier.PRIVATE,
                                        Modifier.STATIC,
                                        Modifier.FINAL
                                )
                                .initializer("new $T<>()", HashMap.class)
                                .build();

                hierarchy.addField(mapField);

                CodeBlock.Builder staticBlock = CodeBlock.builder();

                Set<TypeElement> relevantClasses = new HashSet<>();
                for (TypeElement cls : supersGraph.keySet()) {
                    if (HierarchyUtils.inheritsFrom(cls, rootClassName, processingEnv)) {
                        relevantClasses.add(cls);
                        HierarchyUtils.addAllParents(cls, supersGraph, relevantClasses);
                    }
                }

                for (TypeElement cls : relevantClasses) {

                    List<TypeElement> mro;
                    try {
                        mro = mroResolver.buildMRO(cls, supersGraph);
                    } catch (IllegalStateException ex) {

                        staticBlock.addStatement(
                                "MROS.put($T.class, null)",
                                TypeName.get(cls.asType())
                        );

                        continue;
                    }

                    CodeBlock.Builder list =
                            CodeBlock.builder().add("$T.of(", List.class);

                    for (int i = 0; i < mro.size(); i++) {
                        list.add("$T.class", TypeName.get(mro.get(i).asType()));

                        if (i != mro.size() - 1) list.add(", ");
                    }

                    list.add(")");

                    staticBlock.addStatement(
                            "MROS.put($T.class, $L)",
                            TypeName.get(cls.asType()),
                            list.build()
                    );
                }

                hierarchy.addStaticBlock(staticBlock.build());

                MethodSpec getMRO =
                        MethodSpec.methodBuilder("getMRO")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .returns(
                                        ParameterizedTypeName.get(
                                                ClassName.get(List.class),
                                                ClassName.get(Class.class)
                                        )
                                )
                                .addParameter(Class.class, "cls")
                                .beginControlFlow("if (!MROS.containsKey(cls))")
                                .addStatement("return $T.of()", List.class)
                                .endControlFlow()
                                .beginControlFlow("if (MROS.get(cls) == null)")
                                .addStatement("throw new $T($S + cls)",
                                        IllegalStateException.class,
                                        "C3 linearization failed for ")
                                .endControlFlow()
                                .addStatement("return MROS.get(cls)")
                                .build();

                hierarchy.addMethod(getMRO);

                String packageName =
                        processingEnv.getElementUtils()
                                .getPackageOf(interfaceElement)
                                .getQualifiedName()
                                .toString();

                JavaFile.builder(packageName, hierarchy.build())
                        .build()
                        .writeTo(processingEnv.getFiler());

            } catch (IOException e) {

                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        e.toString()
                );
            }
        }

        return false;
    }

        private String getDefaultValue(TypeKind kind) {
                return switch (kind) {
                        case BOOLEAN -> "false";
                        case BYTE, SHORT, INT, LONG, CHAR -> "0";
                        case FLOAT -> "0.0f";
                        case DOUBLE -> "0.0d";
                        default -> "null";
                };
        }
}