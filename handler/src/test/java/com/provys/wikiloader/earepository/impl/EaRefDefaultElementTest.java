package com.provys.wikiloader.earepository.impl;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class EaRefDefaultElementTest {

    private final EaRepositoryImpl eaRepository = mock(EaRepositoryImpl.class);
    private final EaObjectRefBase parent = new TestEaObjectRef(eaRepository,"parent");
    private final EaObjectRefBase parent2 = new TestEaObjectRef(eaRepository,null);
    private final EaDefaultElementRef elementRef1 = new EaDefaultElementRef(eaRepository, parent, "Element",
            "alelement","", 1,1, true);
    private final EaDefaultElementRef elementRef2 = new EaDefaultElementRef(eaRepository, parent, "Element",
            "","", 1,1, true);
    private final EaDefaultElementRef elementRef3 = new EaDefaultElementRef(eaRepository, parent, "Element",
            "alelement","", 1,1, false);
    private final EaDefaultElementRef elementRef4 = new EaDefaultElementRef(eaRepository, parent2, "Element",
            "alelement","", 1,1, false);

    @Test
    void getTopicIdTest() {
        assertThat(elementRef1.getTopicId()).isEqualTo(Optional.of("parent:alelement"));
        assertThat(elementRef2.getTopicId()).isEmpty();
        assertThat(elementRef3.getTopicId()).isEqualTo(Optional.of("parent:alelement:start"));
        assertThat(elementRef4.getTopicId()).isEmpty();
    }

    @Test
    void getNamespaceTest() {
        assertThat(elementRef1.getNamespace()).isEmpty();
        assertThat(elementRef2.getNamespace()).isEmpty();
        assertThat(elementRef3.getNamespace()).isEqualTo(Optional.of("parent:alelement:"));
        assertThat(elementRef4.getNamespace()).isEmpty();
    }

    @Test
    void hasLinkTest() {
        assertThat(elementRef1.hasLink()).isTrue();
        assertThat(elementRef2.hasLink()).isFalse();
        assertThat(elementRef3.hasLink()).isTrue();
        assertThat(elementRef4.hasLink()).isFalse();
    }

    @Test
    void appendNamespaceTest() {
        var builder = new StringBuilder();
        assertThatThrownBy(() -> elementRef1.appendNamespace(builder, true));
        assertThatThrownBy(() -> elementRef2.appendNamespace(builder, true));
        elementRef3.appendNamespace(builder, true);
        assertThat(builder.toString()).isEqualTo("parent:alelement:");
        assertThatThrownBy(() -> elementRef4.appendNamespace(builder, true));
    }

    @Test
    void appendLinkTest() {
        var builder = new StringBuilder();
        elementRef1.appendLink(builder);
        assertThat(builder.toString()).isEqualTo(":parent:alelement");
        assertThatThrownBy(() -> elementRef2.appendLink(new StringBuilder()));
        builder = new StringBuilder();
        elementRef3.appendLink(builder);
        assertThat(builder.toString()).isEqualTo(":parent:alelement:");
        assertThatThrownBy(() -> elementRef4.appendLink(new StringBuilder()));
    }

    @Test
    void appendParentLinkTest() {
        var builder = new StringBuilder();
        elementRef1.appendParentLink(builder);
        assertThat(builder.toString()).isEqualTo("alelement");
        assertThatThrownBy(() -> elementRef2.appendParentLink(new StringBuilder()));
        builder = new StringBuilder();
        elementRef3.appendParentLink(builder);
        assertThat(builder.toString()).isEqualTo(".alelement:");
        assertThatThrownBy(() -> elementRef4.appendParentLink(new StringBuilder()));
    }
}