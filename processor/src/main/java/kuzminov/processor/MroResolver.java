package kuzminov.processor;

import javax.lang.model.element.TypeElement;
import java.util.*;

class MroResolver {

    private final Map<String, List<TypeElement>> mroCache = new HashMap<>();

    List<TypeElement> buildMRO(TypeElement cls, Map<TypeElement, List<TypeElement>> graph) {
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

            for (List<TypeElement> seq : seqs) {
                if (seq.isEmpty()) continue;
                TypeElement head = seq.get(0);

                if (isGoodCandidate(head, seqs)) {
                    candidate = head;
                    break;
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
