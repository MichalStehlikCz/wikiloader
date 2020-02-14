package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EaGroupRefTest {

    @Nonnull
    static Stream<Object[]> appendLinkNoCheckTest() {
        return Stream.of(
                new Object[]{"alias", ":test:namespace:alias:"}
                , new Object[]{"alias:second", ":test:namespace:alias:second:"}
                , new Object[]{".:alias", ":test:namespace:alias:"}
                , new Object[]{"..:alias", ":test:alias:"}
                , new Object[]{"..:..:alias", ":alias:"}
        );
    }

    @ParameterizedTest
    @MethodSource
    void appendLinkNoCheckTest(String alias, String result) {
        var eaRepository = mock(EaRepositoryImpl.class);
        var parent = mock(EaObjectRef.class);
        doAnswer(invocation -> {
            StringBuilder arg0 = invocation.getArgument(0);
            arg0.append("test:namespace:");
            return null;
        }).when(parent).appendNamespace(any(StringBuilder.class), eq(true));
        var testGroupRef = new EaGroupRef(eaRepository, parent, "Name", alias, "Boundary", 1, 1);
        var builder = new StringBuilder();
        testGroupRef.appendLinkNoCheck(builder);
        assertThat(builder.toString()).isEqualTo(result);
    }
}