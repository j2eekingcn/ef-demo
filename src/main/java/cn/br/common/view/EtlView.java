package cn.br.common.view;

import static java.util.Objects.requireNonNull;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.br.template.Engine;
import cn.br.template.Template;
import io.jooby.Context;
import io.jooby.MediaType;
import io.jooby.ModelAndView;
import io.jooby.TemplateEngine;

public class EtlView implements TemplateEngine {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private Engine engne;

	private String suffix;

	public EtlView(Engine freemarker, String suffix) {
		this.engne = requireNonNull(freemarker, "Etl config is required.");
		this.suffix = suffix;
	}

	private Template template(final String name, final Charset charset) throws Exception {
		engne.setEncoding(charset.name());
		return engne.getTemplate(name);
	}

	@Override
	public String render(Context ctx, ModelAndView modelAndView) throws Exception {
//		String name = modelAndView.getView() + suffix;
		String name = modelAndView.getView();
		Template template = template(name, StandardCharsets.UTF_8);
		Map<String, Object> hash = new HashMap<>();
		hash.put("_vname", modelAndView.getView());

//		// Locale:
//		Locale locale = (Locale) hash.getOrDefault("locale", ctx.getre);
//		hash.putIfAbsent("locale", locale);
//
//		// locals
//		Map<String, Object> locals = ctx.
//		hash.putAll(locals);

		// model
		hash.putAll(modelAndView.getModel());
		StringWriter writer = new StringWriter();

		// Locale:
		// template.setLocale(locale);

		// output
		// template.process(model, writer);

		// if (JFinalViewResolver.sessionInView) {
		//
		// HttpSession hs = ctx. .getSession(JFinalViewResolver.createSession);
		// if (hs != null) {
		// model.put("session", new InnerSession(hs));
		// }
		// }
		
//		ctx.setResponseType(MediaType.html).send(writer.toString());
		template.render(hash, writer);
		return writer.toString();
	}

}