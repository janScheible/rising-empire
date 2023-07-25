package com.scheible.risingempire.webapp.hypermedia;

import com.scheible.pocketsaw.api.SubModule;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Jackson;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireUtil;
import com.scheible.risingempire.webapp.ExternalFunctionalities.SpringFramework;

/**
 *
 * @author sj
 */
@SubModule(includeSubPackages = false, uses = { Jackson.class, RisingEmpireUtil.class, SpringFramework.class })
public final class HypermediaSubModule {

	private HypermediaSubModule() {
	}
}
