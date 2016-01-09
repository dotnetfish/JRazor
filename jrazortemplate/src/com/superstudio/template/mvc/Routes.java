package com.superstudio.template.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Routes {

	private static List<Route> routes;
	private static Object routeLocker = new Object();

	public static void mapRoute(Route route) throws Exception {

		if (route == null)
			throw new Exception("Route name could not be null.");

		synchronized (routeLocker) {
			if (routes == null) {
				routes = new ArrayList<Route>();
			}

			if (routes.stream().anyMatch((item) -> {
				return item.getName().equals(route.getName()) && item.getArea().equals(route.getArea());
			})) {
				// if(ListExtension.any(routes,(item)->{
				// return item.getName().equals(route.getName()) &&
				// item.getArea().equals(route.getArea());
				// })){
				throw new Exception("duplicate route config.Route item already in list,route name:" + route.getName()
						+ ",area :" + route.getArea());
			}

			routes.add(route);

		}
	}

	public static List<Route> match(String url) {

		List<Route> result=routes.stream().filter((item) -> {
			String p = "\\{\\w+\\}";

			String urlPattern = item.getUri().replaceAll(p, "\\\\w+");
			urlPattern = urlPattern.replace(".", "\\.");
			Pattern pattern = Pattern.compile("^" + urlPattern);

			Matcher matcher = pattern.matcher(url);
			return matcher.find();
		}).collect(Collectors.toList());
		
		/*List<Route> result = ListExtension.filter(routes, (item) -> {
			String p = "\\{\\w+\\}";

			String urlPattern = item.getUri().replaceAll(p, "\\\\w+");
			urlPattern = urlPattern.replace(".", "\\.");
			Pattern pattern = Pattern.compile("^" + urlPattern);

			Matcher matcher = pattern.matcher(url);
			return matcher.find();
		});*/
		return result;
	}
}
