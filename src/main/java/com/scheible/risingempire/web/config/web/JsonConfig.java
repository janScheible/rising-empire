package com.scheible.risingempire.web.config.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

/**
 *
 * @author sj
 */
@Component
public class JsonConfig {
	
	@Autowired
	ObjectMapper objectMapper;
	
	public MessageConverter configure() {
		DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		
		// NOTE Includes Java type informations (only for the deserialization process, objectMapper.enableDefaultTyping() would do it for both ways).
		// https://github.com/FasterXML/jackson-databind/issues/352
        TypeResolverBuilder<?> typer = new ObjectMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
        // we'll always use full class name, when using defaulting
        typer = typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = typer.inclusion(JsonTypeInfo.As.WRAPPER_ARRAY);		
		objectMapper.setConfig(objectMapper.getDeserializationConfig().with(typer));
		
		// NOTE Operate on a field basis.
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		
        converter.setObjectMapper(objectMapper);
        converter.setContentTypeResolver(resolver);
		
		return converter;
	}
}
