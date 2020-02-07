package com.provys.wikiloader.earepository.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EaObjectRefBaseTest {

    @Nonnull
    static Stream<Object[]> getPlainNameTest() {
        return Stream.of(
                new Object[]{"Simple name", "Simple name"}
                , new Object[]{"Simple [comment] name", "Simple name"}
                , new Object[]{"Simple name [comment]", "Simple name"}
        );
    }

    @ParameterizedTest
    @MethodSource
    void getPlainNameTest(String name, String result) {
        var eaRepository = mock(EaRepositoryImpl.class);
        var objectRef = new TestEaObjectRef(eaRepository, name, null);
        assertThat(objectRef.getPlainName()).isEqualTo(result);
    }
}