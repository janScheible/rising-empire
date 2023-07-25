package com.scheible.risingempire.webapp.adapter.frontend.annotation;

import com.scheible.pocketsaw.api.SubModule;
import com.scheible.risingempire.webapp.ExternalFunctionalities.SpringFramework;

/**
 *
 * @author sj
 */
@SubModule(uses = { SpringFramework.class })
public final class AnnotationFrontendAdapterSubModule {

	private AnnotationFrontendAdapterSubModule() {
	}
}
