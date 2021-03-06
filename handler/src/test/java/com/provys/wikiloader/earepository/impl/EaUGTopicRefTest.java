package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EaUGTopicRefTest {

    private static class TestUGTopicRef extends EaUGTopicRefBase<TestUGTopicRef, TestUGTopic> {

        TestUGTopicRef(EaRepositoryImpl repository, EaObjectRefBase parent, String name, @Nullable String alias,
                       String type, @Nullable String stereotype, int treePos, int elementId) {
            super(repository, parent, name, alias, type, stereotype, treePos, elementId);
        }

        @Override
        protected void loadObject() {
            throw new InternalException("Load object not implemented in test class");
        }
    }

    private static class TestUGTopic extends EaUGTopicBase<TestUGTopicRef, TestUGTopic> {

        TestUGTopic(TestUGTopicRef objectRef, @Nullable String notes, List<EaDiagramRef> diagrams) {
            super(objectRef, notes, diagrams, Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList());
        }

        @Override
        @Nonnull
        Exporter getExporter(ProvysWikiClient wikiClient) {
            throw new InternalException("Get exporter not implemented in test class");
        }
    }

    @Test
    void getUserGuideTopicIdTest() {
        var repository = mock(EaRepositoryImpl.class);
        var parent = mock(EaObjectRefBase.class);
        var testTopicRef = new TestUGTopicRef(repository, parent, "name", "alias", "type",
                null, 0, 0);
        when(parent.getNamespace()).thenReturn(Optional.of("ns"));
        assertThat(testTopicRef.getUserGuideTopicId()).isEqualTo(Optional.of("ns:alias.user_guide"));
        when(parent.getNamespace()).thenReturn(Optional.empty());
        assertThat(testTopicRef.getUserGuideTopicId()).isEmpty();
    }
}