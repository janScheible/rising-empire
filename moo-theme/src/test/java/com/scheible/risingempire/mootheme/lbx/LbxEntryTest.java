package com.scheible.risingempire.mootheme.lbx;

import com.scheible.risingempire.mootheme.lbx.LbxEntry.Type;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class LbxEntryTest {

	@Test
	void testTypeValueOf() {
		assertThat(Type.valueOf(Type.GFX.getId())).isEqualTo(Type.GFX);
	}

}
