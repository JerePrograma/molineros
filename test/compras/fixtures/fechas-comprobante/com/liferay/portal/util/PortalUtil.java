package com.liferay.portal.util;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
public class PortalUtil {
 public static HttpServletRequest request;
 public static HttpServletRequest getHttpServletRequest(PortletRequest ignored) { return request; }
}
