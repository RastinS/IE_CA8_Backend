package Extras;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class MyServletRequestWrapper extends HttpServletRequestWrapper {

	private Map<String, String> headerMap;

	MyServletRequestWrapper (HttpServletRequest request) {
		super(request);
		headerMap = new HashMap<>();
	}

	void addHeader (String name, String value) {
		headerMap.put(name, value);
	}

	public Enumeration getHeaderNames () {
		HttpServletRequest request = (HttpServletRequest) getRequest();
		List               list    = new ArrayList();
		for (Enumeration<? extends String> e = request.getHeaderNames(); e.hasMoreElements(); )
			list.add(e.nextElement());
		list.addAll(headerMap.keySet());
		return Collections.enumeration(list);
	}

	public String getHeader (String name) {
		String value;
		if ((value = headerMap.get("" + name)) != null)
			return value;
		else
			return ((HttpServletRequest) getRequest()).getHeader(name);
	}
}