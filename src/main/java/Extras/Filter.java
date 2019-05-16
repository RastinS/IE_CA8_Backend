package Extras;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebFilter ("/*")
public class Filter implements javax.servlet.Filter {
	private HashMap<String, String> services = new HashMap<>();

	public void destroy () {
	}

	public void doFilter (ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

		HttpServletRequest  httpReq  = (HttpServletRequest) req;
		HttpServletResponse httpResp = (HttpServletResponse) resp;

		if (httpReq.getMethod().equals("OPTIONS")) {
			String serviceMethod = getServiceMethod(httpReq.getRequestURI());

			switch (serviceMethod) {
				case "POST":
					httpResp.addHeader("Access-Control-Allow-Methods", "POST");
					break;
				case "GET":
					httpResp.addHeader("Access-Control-Allow-Methods", "GET");
					break;
				case "NOT FOUND":
					httpResp.addHeader("Access-Control-Allow-Methods", "POST, GET");
					break;
			}
		}

		httpResp.addHeader("Access-Control-Allow-Origin", "*");
		httpResp.addHeader("Access-Control-Allow-Headers", "*");
		httpResp.addHeader("Access-Control-Allow-Credentials", "true");

		chain.doFilter(httpReq, httpResp);

	}

	public void init (FilterConfig config) {
		services.put("/removeSkill", "POST");
		services.put("/users", "GET");
		services.put("/projects", "GET");
		services.put("/endorse", "POST");
		services.put("/addSkill", "POST");
		services.put("/addBid", "POST");
	}

	private String getServiceMethod (String reqURI) {
		String service = reqURI.substring(reqURI.indexOf('/', 2));
		if (services.containsKey(service))
			return services.get(service);
		else
			return "NOT FOUND";
	}

}
