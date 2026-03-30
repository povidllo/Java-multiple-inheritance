package kuzminov.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;
import java.util.Set;

class HierarchyUtils {

    static void addAllParents(TypeElement cls,
                              Map<TypeElement, List<TypeElement>> supersGraph,
                              Set<TypeElement> set) {
        for (TypeElement parent : supersGraph.getOrDefault(cls, List.of())) {
            if (!set.contains(parent)) {
                set.add(parent);
                addAllParents(parent, supersGraph, set);
            }
        }
    }

    static boolean inheritsFrom(TypeElement cls,
                                String rootClassName,
                                ProcessingEnvironment processingEnv) {

        TypeMirror superClass = cls.getSuperclass();
        if (superClass.getKind() == TypeKind.NONE) return false;

        String superName = superClass.toString();
        if (superName.equals(rootClassName)) return true;

        if (superClass.getKind() == TypeKind.DECLARED) {
            TypeElement superElement = (TypeElement) processingEnv.getTypeUtils().asElement(superClass);
            return inheritsFrom(superElement, rootClassName, processingEnv);
        }

        return false;
    }
}
