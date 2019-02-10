package com.superstudio.web.razor;

import com.superstudio.commons.CollectionHelper;

import com.superstudio.commons.HostingEnvironment;
import com.superstudio.commons.WebConfigurationManager;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class WebRazorHostFactory {
	private static java.util.concurrent.ConcurrentHashMap<String, Supplier<WebRazorHostFactory>> _factories = new java.util.concurrent.ConcurrentHashMap<String, Supplier<WebRazorHostFactory>>();
	public static Function<String, java.lang.Class> TypeFactory = (type) -> {
		try {
			return defaultTypeFactory(type);
		} catch (Exception ex) {
			return null;
		}

	};

	public static WebPageRazorHost createDefaultHost(String virtualPath) {
		return WebRazorHostFactory.createDefaultHost(virtualPath, null);
	}

	public static WebPageRazorHost createDefaultHost(String virtualPath, String physicalPath) {
		return WebRazorHostFactory.createHostFromConfigCore(null, virtualPath, physicalPath);
	}

	public static WebPageRazorHost createHostFromConfig(String virtualPath) {
		return WebRazorHostFactory.createHostFromConfig(virtualPath, null);
	}

	public static WebPageRazorHost createHostFromConfig(String virtualPath, String physicalPath) {
		if (StringUtils.isBlank(virtualPath)) {
			// throw new
			// IllegalArgumentException(String.format(Locale.CurrentCulture,
			// CommonResources.Argument_Cannot_Be_Null_Or_Empty, new Object[] {
			// "virtualPath" }), "virtualPath");
		}
		return WebRazorHostFactory.createHostFromConfigCore(WebRazorHostFactory.getRazorSection(virtualPath),
				virtualPath, physicalPath);
	}

	public static WebPageRazorHost createHostFromConfig(RazorWebSectionGroup config, String virtualPath) {
		return WebRazorHostFactory.createHostFromConfig(config, virtualPath, null);
	}

	public static WebPageRazorHost createHostFromConfig(RazorWebSectionGroup config, String virtualPath,
														String physicalPath) {
		if (config == null) {
			throw new IllegalArgumentException("config");
		}
		if (StringUtils.isBlank(virtualPath)) {
			// throw new
			// IllegalArgumentException(String.format(Locale.CurrentCulture,
			// CommonResources.Argument_Cannot_Be_Null_Or_Empty, new Object[] {
			// "virtualPath" }), "virtualPath");
		}
		return WebRazorHostFactory.createHostFromConfigCore(config, virtualPath, physicalPath);
	}

	public static WebPageRazorHost createHostFromConfigCore(RazorWebSectionGroup config, String virtualPath,
															String physicalPath) {
		virtualPath = WebRazorHostFactory.ensureAppRelative(virtualPath);
		WebPageRazorHost webPageRazorHost;

		if (StringUtils.startsWithIgnoreCase(virtualPath, "~/App_Code")) {
			webPageRazorHost = new WebCodeRazorHost(virtualPath, physicalPath);
		} else {
			WebRazorHostFactory webRazorHostFactory = null;
			if (config != null && config.getHost() != null
					&& !StringUtils.isBlank(config.getHost().getFactoryType())) {
				Supplier<WebRazorHostFactory> orAdd = WebRazorHostFactory._factories
						.putIfAbsent(config.getHost().getFactoryType(), () -> {
							try {
								return (WebRazorHostFactory) createFactory(config.getHost().getFactoryType());
							} catch (Exception e) {
								return null;
							}
						});

				webRazorHostFactory = orAdd.get();
			}
			webPageRazorHost = ((webRazorHostFactory != null) ? webRazorHostFactory : new WebRazorHostFactory())
					.createHost(virtualPath, physicalPath);
			if (config != null && config.getPages() != null) {
				WebRazorHostFactory.applyConfigurationToHost(config.getPages(), webPageRazorHost);
			}
		}
		return webPageRazorHost;

	}

	private static Supplier<WebRazorHostFactory> createFactory(String typeName)
			throws InstantiationException, IllegalAccessException {
		java.lang.Class type = TypeFactory.apply(typeName);
		if (type == null) {
			throw new IllegalStateException(String.format(
					RazorWebResources.Could_Not_Locate_FactoryType, new Object[] { typeName }));
		}
		// return Expression.<Supplier<WebRazorHostFactory>>
		// Lambda(Expression.New(type), new ParameterExpression[0])
		// .compile();
		return () -> {
			try {
				return (WebRazorHostFactory) type.newInstance();
			} catch (Exception ex) {
				return null;
			}
		};
	}

	public static void applyConfigurationToHost(RazorPagesSection config, WebPageRazorHost host) {
		host.setDefaultPageBaseClass(config.getPageBaseType());
		
		List<String> namespaces = CollectionHelper.select(config.getNamespaces(), (p) -> p);
		// for (String current : from ns in
		// config.Namespaces.<NamespaceInfo>OfType() select ns.Namespace)
		for (String current : namespaces) {
			host.getNamespaceImports().add(current);
		}
	}

	public WebPageRazorHost createHost(String virtualPath, String physicalPath) {
		return new WebPageRazorHost(virtualPath, physicalPath);
	}

	public static RazorWebSectionGroup getRazorSection(String virtualPath) {
		RazorWebSectionGroup tempVar = new RazorWebSectionGroup();
		// tempVar.setHost((HostSection)
		// WebConfigurationManager.GetSection(HostSection.SectionName,
		// virtualPath));
		// tempVar.setHost("");
		RazorPagesSection section=(RazorPagesSection) WebConfigurationManager.GetSection(RazorPagesSection.SectionName, virtualPath);
		tempVar.setPages(section
				);
		return tempVar;
	}

	private static String ensureAppRelative(String virtualPath) {
		if (HostingEnvironment.getIsHosted()) {
			// virtualPath = VirtualPathUtility.ToAppRelative(virtualPath);
		} else {
			if (virtualPath.startsWith("/")) {
				virtualPath = "~" + virtualPath;
			} else {
				if (!virtualPath.startsWith("~/")) {
					virtualPath = "~/" + virtualPath;
				}
			}
		}
		return virtualPath;
	}

	private static java.lang.Class defaultTypeFactory(String typeName) throws ClassNotFoundException {
		// return BuildManager.GetType(typeName, false, false);
		return Class.forName(typeName);
	}
}