package com.provys.wikiloader.earepository.impl;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class EaNamespaceElementRefTest {

    private final EaRepositoryImpl eaRepository = mock(EaRepositoryImpl.class);
    private final EaObjectRefBase parent = new TestEaObjectRef(eaRepository,"Mock parent", "parent");
    private final EaObjectRefBase parent2 = new TestEaObjectRef(eaRepository,"Mock parent", null);
    private final EaNamespaceElementRef elementRef1 = new EaNamespaceElementRef(eaRepository, parent, "Element",
            "alelement","Type", "", 1,1);
    private final EaNamespaceElementRef elementRef2 = new EaNamespaceElementRef(eaRepository, parent2, "Element",
            "alelement","Type","", 1,1);

    @Test
    void getTopicIdTest() {
        assertThat(elementRef1.getTopicId()).isEqualTo(Optional.of("parent:alelement:start"));
        assertThat(elementRef2.getTopicId()).isEmpty();
    }

    @Test
    void getNamespaceTest() {
        assertThat(elementRef1.getNamespace()).isEqualTo(Optional.of("parent:alelement"));
        assertThat(elementRef2.getNamespace()).isEmpty();
    }

    @Test
    void hasLinkTest() {
        assertThat(elementRef1.hasLink()).isTrue();
        assertThat(elementRef2.hasLink()).isFalse();
    }

    @Test
    void appendNamespaceTest() {
        var builder = new StringBuilder();
        elementRef1.appendNamespace(builder, true);
        assertThat(builder.toString()).isEqualTo("parent:alelement:");
        assertThatThrownBy(() -> elementRef2.appendNamespace(builder, true));
    }

    @Test
    void appendLinkTest() {
        var builder = new StringBuilder();
        elementRef1.appendLink(builder);
        assertThat(builder.toString()).isEqualTo(":parent:alelement:");
        assertThatThrownBy(() -> elementRef2.appendLink(new StringBuilder()));
    }

    @Test
    void appendParentLinkTest() {
        var builder = new StringBuilder();
        elementRef1.appendParentLink(builder);
        assertThat(builder.toString()).isEqualTo(".alelement:");
        assertThatThrownBy(() -> elementRef2.appendParentLink(new StringBuilder()));
    }
}
