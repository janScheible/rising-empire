package com.scheible.risingempire.mootheme.lbx;

import static com.scheible.risingempire.mootheme.lbx.LbxEntry.Type.GFX;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.scheible.risingempire.mootheme.lbx.LbxEntry.Type;

/**
 *
 * @author sj
 */
class LbxEntryTest {

	@Test
	void testTypeValueOf() {
		assertThat(Type.valueOf(GFX.getId())).isEqualTo(GFX);
	}
}
