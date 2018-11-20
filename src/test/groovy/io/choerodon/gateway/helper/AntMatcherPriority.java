package io.choerodon.gateway.helper;

import org.junit.Test;
import org.springframework.util.AntPathMatcher;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AntMatcherPriority {

    private final AntPathMatcher matcher = new AntPathMatcher();

    @Test
    public void path() {
        System.out.println(compareTo("/v1/menus/2345/tree", "/v1/menus/{id}/tree", "/v1/menus/{id}/{name}"));
        System.out.println(compareTo("/v1/menus/2345/tree", "/v1/menus/{id}/{name}", "/v1/menus/{id}/tree"));


        System.out.println(compareTo("/v1/version/drag", "/v1/version/drag", "/v1/version/{id}"));
        System.out.println(compareTo("/v1/version/drag", "/v1/version/drag/", "/v1/version/{id}"));
        System.out.println(compareTo("/v1/version/drag/", "/v1/version/drag/", "/v1/version/{id}"));
        System.out.println(compareTo("/v1/version/drag/", "/v1/version/drag/", "/v1/version/{id}/"));

    }

    public int compareTo(String lookupPath, String pathA, String pathB) {
        Comparator<String> patternComparator = matcher.getPatternComparator(lookupPath);
        return patternComparator.compare(pathA, pathB);
    }

    @Test
    public void list() {
        List<String> list = Collections.emptyList();
        System.out.println(list.get(0));
    }
}
