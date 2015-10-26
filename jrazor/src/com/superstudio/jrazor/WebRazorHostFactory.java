package com.superstudio.jrazor;

import java.util.List;
import java.util.function.Function;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.HostingEnvironment;
import com.superstudio.commons.WebConfigurationManager;
import com.superstudio.commons.csharpbridge.StringComparison;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.csharpbridge.action.Func;

public class WebRazorHostFactory {
	private static java.util.concurrent.ConcurrentHashMap<String, Func<WebRazorHostFactory>> _factories = new java.util.concurrent.ConcurrentHashMap<String, Func<WebRazorHostFactory>>();
	public static Function<String, java.lang.Class> TypeFactory = (type) -> {
		try {
			return WebRazorHostFactory.defaultTypeFactory(type);
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
		if (StringHelper.isNullOrEmpty(virtualPath)) {
			// throw new
			// IllegalArgumentException(String.format(CultureInfo.CurrentCulture,
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
		if (StringHelper.isNullOrEmpty(virtualPath)) {
			// throw new
			// IllegalArgumentException(String.format(CultureInfo.CurrentCulture,
			// CommonResources.Argument_Cannot_Be_Null_Or_Empty, new Object[] {
			// "virtualPath" }), "virtualPath");
		}
		return WebRazorHostFactory.createHostFromConfigCore(config, virtualPath, physicalPath);
	}

	public static WebPageRazorHost createHostFromConfigCore(RazorWebSectionGroup config, String virtualPath,
			String physicalPath) {
		virtualPath = WebRazorHostFactory.ensureAppRelative(virtualPath);
		WebPageRazorHost webPageRazorHost;
		if (StringHelper.startWith(virtualPath, "~/App_Code", StringComparison.OrdinalIgnoreCase)) {
			webPageRazorHost = new WebCodeRazorHost(virtualPath, physicalPath);
		} else {
			WebRazorHostFactory webRazorHostFactory = null;
			if (config != null && config.getHost() != null
					&& !StringHelper.isNullOrEmpty(config.getHost().getFactoryType())) {
				Func<WebRazorHostFactory> orAdd = WebRazorHostFactory._factories
						.putIfAbsent(config.getHost().getFactoryType(), () -> {
							try {
								return (WebRazorHostFactory) createFactory(config.getHost().getFactoryType());
							} catch (Exception e) {
								return null;
							}
						});

				webRazorHostFactory = orAdd.execute();
			}
			webPageRazorHost = ((webRazorHostFactory != null) ? webRazorHostFactory : new WebRazorHostFactory())
					.createHost(virtualPath, physicalPath);
			if (config != null && config.getPages() != null) {
				WebRazorHostFactory.applyConfigurationToHost(config.getPages(), webPageRazorHost);
			}
		}
		return webPageRazorHost;

	}

	private static Func<WebRazorHostFactory> createFactory(String typeName)
			throws InstantiationException, IllegalAccessException {
		java.lang.Class type = TypeFactory.apply(typeName);
		if (type == null) {
			throw new IllegalStateException(String.format(CultureInfo.CurrentCulture,
					RazorWebResources.Could_Not_Locate_FactoryType, new Object[] { typeName }));
		}
		// return Expression.<Func<WebRazorHostFactory>>
		// Lambda(Expression.New(type), new ParameterExpression[0])
		// .Compile();
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