package io.iamcyw.tower.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CommonKitTest {

    @Test
    void arrayFormat() {

        Assertions.assertThat(CommonKit.arrayFormat("a{}b{}", "a", "b")).isEqualTo("aabb");
    }

}
