package com.provys.wikiloader.earepository.impl;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class EaRefDefaultPackageTest {

    private final EaRepositoryImpl eaRepository = mock(EaRepositoryImpl.class);
    private final EaObjectRefBase parent = new TestEaObjectRef(eaRepository, "parent");
    private final EaObjectRefBase parent2 = new TestEaObjectRef(eaRepository, null);
    private final EaDefaultPackageRef packageRef1 = new EaDefaultPackageRef(eaRepository, parent, "Package",
            "alpkg","", 1,1);
    private final EaDefaultPackageRef packageRef2 = new EaDefaultPackageRef(eaRepository, parent, "Package",
            "","", 1,1);
    private final EaDefaultPackageRef packageRef3 = new EaDefaultPackageRef(eaRepository, null, "Package",
            "alpkg","", 1,1);
    private final EaDefaultPackageRef packageRef4 = new EaDefaultPackageRef(eaRepository, parent2, "Package",
            "alpkg","", 1,1);

    @Test
    void getTopicIdTest() {
        assertThat(packageRef1.getTopicId()).isEqualTo(Optional.of("parent:alpkg:start"));
        assertThat(packageRef2.getTopicId()).isEmpty();
        assertThat(packageRef3.getTopicId()).isEqualTo(Optional.of("alpkg:start"));
        assertThat(packageRef4.getTopicId()).isEmpty();
    }

    @Test
    void getNamespaceTest() {
        assertThat(packageRef1.getNamespace()).isEqualTo(Optional.of("parent:alpkg"));
        assertThat(packageRef2.getNamespace()).isEmpty();
        assertThat(packageRef3.getNamespace()).isEqualTo(Optional.of("alpkg"));
        assertThat(packageRef4.getNamespace()).isEmpty();
    }

    @Test
    void hasLinkTest() {
        assertThat(packageRef1.hasLink()).isTrue();
        assertThat(packageRef2.hasLink()).isFalse();
        assertThat(packageRef3.hasLink()).isTrue();
        assertThat(packageRef4.hasLink()).isFalse();
    }

    @Test
    void appendNamespaceTest() {
        var builder = new StringBuilder();
        packageRef1.appendNamespace(builder, true);
        assertThat(builder.toString()).isEqualTo("parent:alpkg:");
        assertThatThrownBy(() -> packageRef2.appendNamespace(new StringBuilder(), true));
        builder = new StringBuilder();
        packageRef3.appendNamespace(builder, true);
        assertThat(builder.toString()).isEqualTo("alpkg:");
        assertThatThrownBy(() -> packageRef4.appendNamespace(new StringBuilder(), true));
    }

    @Test
    void appendLinkTest() {
        var builder = new StringBuilder();
        packageRef1.appendLink(builder);
        assertThat(builder.toString()).isEqualTo(":parent:alpkg:");
        assertThatThrownBy(() -> packageRef2.appendLink(new StringBuilder()));
        builder = new StringBuilder();
        packageRef3.appendLink(builder);
        assertThat(builder.toString()).isEqualTo(":alpkg:");
        assertThatThrownBy(() -> packageRef4.appendLink(new StringBuilder()));
    }

    @Test
    void appendParentLinkTest() {
        var builder = new StringBuilder();
        packageRef1.appendParentLink(builder);
        assertThat(builder.toString()).isEqualTo(".alpkg:");
        assertThatThrownBy(() -> packageRef2.appendParentLink(new StringBuilder()));
        builder = new StringBuilder();
        packageRef3.appendParentLink(builder);
        assertThat(builder.toString()).isEqualTo(".alpkg:");
        assertThatThrownBy(() -> packageRef4.appendParentLink(new StringBuilder()));
    }
}