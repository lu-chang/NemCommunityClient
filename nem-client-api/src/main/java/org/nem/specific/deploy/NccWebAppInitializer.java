package org.nem.specific.deploy;

import org.nem.deploy.*;
import org.nem.core.serialization.SimpleAccountLookup;
import org.nem.ncc.controller.interceptors.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.http.*;
import java.util.*;
import java.util.concurrent.CompletionException;

/**
 * Class supplying Spring MVC configuration.
 */
@Configuration
@ComponentScan(basePackages = { "org.nem.ncc.controller" })
//@EnableWebMvc // this cannot be present, when using WebMvcConfigurationSupport
public class NccWebAppInitializer extends WebMvcConfigurationSupport {
	@Autowired
	private SimpleAccountLookup accountLookup;

	@Autowired
	private org.nem.ncc.model.Configuration configuration;

	@Autowired
	private CommonConfiguration nccConfiguration;

	private ApplicationContext applicationContext;

	private static void addConvertersForPolicy(
			final List<HttpMessageConverter<?>> converters,
			final SerializationPolicy policy) {
		converters.add(new DeserializerHttpMessageConverter(policy));
		converters.add(new SerializableEntityHttpMessageConverter(policy));
		converters.add(new DeserializableEntityMessageConverter(policy));
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		super.setApplicationContext(applicationContext);
	}

	@Override
	protected void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
		converters.add(new OctetStreamMessageConverter());

		// NCC should only support JSON APIs
		addConvertersForPolicy(converters, new JsonSerializationPolicy(this.accountLookup));
		// addConvertersForPolicy(converters, new BinarySerializationPolicy(this.accountLookup));
		this.addDefaultHttpMessageConverters(converters);
	}

	@Override
	protected void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(new AuditInterceptor(Arrays.asList(this.nccConfiguration.getNonAuditedApiPaths())));
		registry.addInterceptor(new LocalNisInterceptor(this.configuration));
		super.addInterceptors(registry);
	}

	@Override
	protected void configureHandlerExceptionResolvers(final List<HandlerExceptionResolver> exceptionResolvers) {
		final CompletionExceptionResolver resolver = new CompletionExceptionResolver();
		resolver.setApplicationContext(this.applicationContext);
		resolver.setContentNegotiationManager(this.mvcContentNegotiationManager());
		resolver.setMessageConverters(this.getMessageConverters());
		resolver.afterPropertiesSet();
		exceptionResolvers.add(resolver);
	}

	/**
	 * A custom HandlerExceptionResolver that automatically unwraps CompletionException exceptions.
	 * Spring is then able to dispatch the unwrapped exception to @ExceptionHandler methods.
	 */
	private static class CompletionExceptionResolver extends ExceptionHandlerExceptionResolver {

		@Override
		public ModelAndView resolveException(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) {
			return super.resolveException(request, response, handler, unwrap(ex));
		}

		@Override
		public ModelAndView doResolveHandlerMethodException(final HttpServletRequest request, final HttpServletResponse response, final HandlerMethod handlerMethod, final Exception ex) {
			return super.doResolveHandlerMethodException(request, response, handlerMethod, unwrap(ex));
		}

		private static Exception unwrap(final Exception ex) {
			return ex instanceof CompletionException && ex.getCause() instanceof Exception
					? (Exception)ex.getCause()
					: ex;
		}
	}
}
