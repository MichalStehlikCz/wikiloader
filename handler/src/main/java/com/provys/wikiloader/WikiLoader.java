package com.provys.wikiloader;

import javax.annotation.Nullable;

public interface WikiLoader {
    void run(@Nullable String path, boolean recursive);
}
