package com.scheible.risingempire.webapp;

import java.util.Arrays;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author sj
 */
@Configuration(proxyBeanMethods = false)
class WebConfiguration implements WebMvcConfigurer {

	@Override
	public void addFormatters(final FormatterRegistry registry) {
		registry.addConverter(new Converter<String, Player>() {
			@Override
			public Player convert(final String source) {
				return Arrays.stream(Player.values())
					.filter(e -> e.name().equalsIgnoreCase(source))
					.findAny()
					.orElse(null);
			}
		});

		registry.addConverter(new Converter<String, SystemId>() {
			@Override
			public SystemId convert(final String source) {
				return new SystemId(source);
			}
		});

		registry.addConverter(new Converter<String, FleetId>() {
			@Override
			public FleetId convert(final String source) {
				return new FleetId(source);
			}
		});
	}

}
