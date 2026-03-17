package kuzminov.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
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
    private final Map<String, List<TypeElement>> mroCache = new HashMap<>();

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

                TypeMirror interfaceType = interfaceElement.asType();

                TypeSpec.Builder classBuilder =
                        TypeSpec.classBuilder(rootName)
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .addSuperinterface(TypeName.get(interfaceType));

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
                 * chainInitialized
                 */
                FieldSpec chainInit =
                        FieldSpec.builder(
                                boolean.class,
                                "chainInitialized",
                                Modifier.PRIVATE,
                                Modifier.VOLATILE
                        ).build();

                classBuilder.addField(chainInit);

                /*
                 * методы
                 */
                for (Element e : interfaceElement.getEnclosedElements()) {

                    if (e.getKind() != ElementKind.METHOD) continue;

                    ExecutableElement method = (ExecutableElement) e;

                    String methodName = method.getSimpleName().toString();

                    String nextMethod =
                            "next" +
                                    Character.toUpperCase(methodName.charAt(0)) +
                                    methodName.substring(1);

                    /*
                     * override
                     */
                    MethodSpec overrideMethod =
                            MethodSpec.methodBuilder(methodName)
                                    .addAnnotation(Override.class)
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(TypeName.get(method.getReturnType()))
                                    .addStatement("$L()", nextMethod)
                                    .build();

                    classBuilder.addMethod(overrideMethod);

                    /*
                     * nextMethod
                     */
                    MethodSpec next =
                            MethodSpec.methodBuilder(nextMethod)
                                    .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                                    .returns(TypeName.get(method.getReturnType()))
                                    .addStatement("ensureChainInitialized()")
                                    .beginControlFlow("if (next != null)")
                                    .addStatement("next.$L()", methodName)
                                    .endControlFlow()
                                    .build();

                    classBuilder.addMethod(next);
                }

                /*
                 * ensureChainInitialized
                 */
                MethodSpec ensure =
                        MethodSpec.methodBuilder("ensureChainInitialized")
                                .addModifiers(Modifier.PRIVATE)
                                .beginControlFlow("if (!chainInitialized)")
                                .beginControlFlow("synchronized (this)")
                                .beginControlFlow("if (!chainInitialized)")
                                .addStatement("initChain()")
                                .addStatement("chainInitialized = true")
                                .endControlFlow()
                                .endControlFlow()
                                .endControlFlow()
                                .build();

                classBuilder.addMethod(ensure);

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
                                .addStatement("$T cons = cls.getConstructor()", java.lang.reflect.Constructor.class)
                                .addStatement("$L instance = ($L) cons.newInstance()", rootName, rootName)
                                .addStatement("instance.next = current")
                                .addStatement("instance.chainInitialized = true")
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
            TypeMirror interfaceType = interfaceElement.asType();
            String interfaceName = interfaceElement.getSimpleName().toString();
            String hierarchyName = interfaceName + "Hierarchy";

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

                Set<TypeElement> allClasses = new HashSet<>(supersGraph.keySet());
                for (List<TypeElement> parents : supersGraph.values()) {
                    allClasses.addAll(parents);
                }

                mroCache.clear();

                for (TypeElement cls : allClasses) {

                    List<TypeElement> mro;
                    try {
                        mro = buildMRO(cls, supersGraph);
                    } catch (IllegalStateException ex) {
                        processingEnv.getMessager().printMessage(
                                Diagnostic.Kind.ERROR,
                                "Failed to build C3 MRO for " + cls.getQualifiedName() + ": " + ex.getMessage()
                        );
                        mro = List.of(cls);
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
                                .addStatement("return MROS.getOrDefault(cls, $T.of())", List.class)
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

    /*
     * C3 MRO
     */

    private List<TypeElement> buildMRO(
            TypeElement cls,
            Map<TypeElement, List<TypeElement>> graph) {

        return buildMRO(cls, graph, new HashSet<>());
    }

    private List<TypeElement> buildMRO(
            TypeElement cls,
            Map<TypeElement, List<TypeElement>> graph,
            Set<String> visiting) {

        String qname = cls.getQualifiedName().toString();

        if (mroCache.containsKey(qname)) {
            return mroCache.get(qname);
        }

        if (visiting.contains(qname)) {
            throw new IllegalStateException("Cycle detected in inheritance graph at " + qname);
        }

        visiting.add(qname);

        List<TypeElement> result = new ArrayList<>();
        result.add(cls);

        List<List<TypeElement>> sequences = new ArrayList<>();

        List<TypeElement> parents = graph.getOrDefault(cls, List.of());

        for (TypeElement parent : parents) {
            sequences.add(new ArrayList<>(buildMRO(parent, graph, visiting)));
        }

        sequences.add(new ArrayList<>(parents));

        result.addAll(merge(sequences));

        List<TypeElement> cached = new ArrayList<>(result);
        mroCache.put(qname, cached);

        visiting.remove(qname);

        return result;
    }

    private List<TypeElement> merge(List<List<TypeElement>> sequences) {

        List<List<TypeElement>> seqs = new ArrayList<>();
        for (List<TypeElement> s : sequences) {
            seqs.add(new ArrayList<>(s));
        }

        List<TypeElement> result = new ArrayList<>();

        while (true) {

            Iterator<List<TypeElement>> it = seqs.iterator();
            while (it.hasNext()) {
                if (it.next().isEmpty()) it.remove();
            }

            if (seqs.isEmpty()) {
                return result;
            }

            TypeElement candidate = null;

            outer:
            for (List<TypeElement> seq : seqs) {
                if (seq.isEmpty()) continue;
                TypeElement head = seq.get(0);

                if (isGoodCandidate(head, seqs)) {
                    candidate = head;
                    break outer;
                }
            }

            if (candidate == null) {
                throw new IllegalStateException("C3 linearization failed: inconsistent hierarchy");
            }

            result.add(candidate);

            for (List<TypeElement> seq : seqs) {
                Iterator<TypeElement> it2 = seq.iterator();
                while (it2.hasNext()) {
                    TypeElement t = it2.next();
                    if (sameQualified(t, candidate)) {
                        it2.remove();
                    }
                }
            }
        }
    }

    private boolean isGoodCandidate(
            TypeElement candidate,
            List<List<TypeElement>> sequences) {

        for (List<TypeElement> seq : sequences) {
            for (int i = 1; i < seq.size(); i++) {
                TypeElement t = seq.get(i);
                if (sameQualified(t, candidate)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean sameQualified(TypeElement a, TypeElement b) {
        if (a == null || b == null) return false;
        return a.getQualifiedName().toString().equals(b.getQualifiedName().toString());
    }
}