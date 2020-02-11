package com.provys.wikiloader.earepository.impl;

import com.provys.common.datatype.DtDate;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EaObjectRegularExporterTest {

    private static class TestEaObjectRegularExporter extends EaObjectRegularExporter<EaObject> {

        TestEaObjectRegularExporter(EaObject eaObject, ProvysWikiClient wikiClient) {
            super(eaObject, wikiClient);
        }

        String getPageText() {
            return startBuilder.toString();
        }
    }

    @Nonnull
    static Stream<Object[]> appendListEmptyTest() {
        return Stream.of(
                new Object[]{null, ""}
                , new Object[]{"Empty", "Empty\\\\\n"}
        );
    }

    @ParameterizedTest
    @MethodSource
    void appendListEmptyTest(@Nullable String noEntry, String result) {
        var eaObject = mock(EaObject.class);
        var wikiClient = mock(ProvysWikiClient.class);
        var testExporter = new TestEaObjectRegularExporter(eaObject, wikiClient);
        testExporter.appendList(noEntry, "Single", "Multiple", Collections.emptyList());
        assertThat(testExporter.getPageText()).isEqualTo(result);
    }

    @Test
    void appendListSingleTest() {
        var eaObject = mock(EaObject.class);
        var wikiClient = mock(ProvysWikiClient.class);
        var testExporter = new TestEaObjectRegularExporter(eaObject, wikiClient);
        var entry = mock(EaObjectRef.class);
        doAnswer(invocation -> {((StringBuilder) invocation.getArgument(0)).append("Short Title"); return null;})
                .when(entry)
                .appendWikiLink(any(StringBuilder.class));
        testExporter.appendList("Empty", "Single", "Multiple",
                Collections.singletonList(entry));
        assertThat(testExporter.getPageText()).isEqualTo("Single Short Title.\\\\\n");
    }

    @Test
    void appendListMultiTest() {
        var eaObject = mock(EaObject.class);
        var wikiClient = mock(ProvysWikiClient.class);
        var testExporter = new TestEaObjectRegularExporter(eaObject, wikiClient);
        var entry1 = mock(EaObjectRef.class);
        doAnswer(invocation -> {((StringBuilder) invocation.getArgument(0)).append("[[entry_1:Entry 1]]"); return null;})
                .when(entry1)
                .appendWikiLink(any(StringBuilder.class));
        var entry2 = mock(EaObjectRef.class);
        doAnswer(invocation -> {((StringBuilder) invocation.getArgument(0)).append("Entry 2"); return null;})
                .when(entry2)
                .appendWikiLink(any(StringBuilder.class));
        testExporter.appendList("Empty", "Single", "Multiple",
                List.of(entry1, entry2));
        assertThat(testExporter.getPageText())
                .isEqualTo("Multiple:\\\\\n  * [[entry_1:Entry 1]]\\\\\n  * Entry 2\\\\\n");
    }
}