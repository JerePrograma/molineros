package ar.com.global.action;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.struts.ActionConstants;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileShortcut;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileShortcutServiceUtil;
import com.liferay.portlet.documentlibrary.service.permission.DLFileEntryPermission;
import com.liferay.portlet.documentlibrary.util.DocumentConversionUtil;
import com.liferay.util.servlet.ServletResponseUtil;

import java.io.InputStream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <a href="GetFileAction.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Charles May
 * @author Bruno Farache
 *
 */
public class GetFileActionExt extends PortletAction {
	private Logger _log = Logger.getLogger(this.getClass());
	public ActionForward strutsExecute(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response)
		throws Exception {

		try {
			long folderId = ParamUtil.getLong(request, "folderId");
			String name = ParamUtil.getString(request, "name");
			double version = ParamUtil.getDouble(request, "version");

			long fileShortcutId = ParamUtil.getLong(request, "fileShortcutId");

			String uuid = ParamUtil.getString(request, "uuid");
			long groupId = ParamUtil.getLong(request, "groupId");

			String targetExtension = ParamUtil.getString(
				request, "targetExtension");

			ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
				WebKeys.THEME_DISPLAY);

			getFile(
				folderId, name, version, fileShortcutId, uuid, groupId,
				targetExtension, themeDisplay, request, response);

			return null;
		}
		catch (Exception e) {
			_log.debug("Error View File: " + e);
			PortalUtil.sendError(e, request, response);
			return null;
		}
	}

	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			long folderId = ParamUtil.getLong(actionRequest, "folderId");
			String name = ParamUtil.getString(actionRequest, "name");
			double version = ParamUtil.getDouble(actionRequest, "version");

			long fileShortcutId = ParamUtil.getLong(
				actionRequest, "fileShortcutId");

			String uuid = ParamUtil.getString(actionRequest, "uuid");
			long groupId = ParamUtil.getLong(actionRequest, "groupId");

			String targetExtension = ParamUtil.getString(
				actionRequest, "targetExtension");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			HttpServletRequest request = PortalUtil.getHttpServletRequest(
				actionRequest);
			HttpServletResponse response = PortalUtil.getHttpServletResponse(
				actionResponse);

			getFile(
				folderId, name, version, fileShortcutId, uuid, groupId,
				targetExtension, themeDisplay, request, response);

			setForward(actionRequest, ActionConstants.COMMON_NULL);
		}
		catch (Exception e) {
			_log.debug("Error View File: " + e ); 
			PortalUtil.sendError(e, actionRequest, actionResponse);
		}
	}

	protected void getFile(
			long folderId, String name, double version, long fileShortcutId,
			String uuid, long groupId, String targetExtension,
			ThemeDisplay themeDisplay, HttpServletRequest request,
			HttpServletResponse response)
		throws Exception {

		InputStream is = null;

		try {
			long companyId = themeDisplay.getCompanyId();
			long userId = themeDisplay.getUserId();

			DLFileEntry fileEntry = null;

			if (Validator.isNotNull(uuid) && (groupId > 0)) {
				try {
					fileEntry = DLFileEntryLocalServiceUtil.
						getFileEntryByUuidAndGroupId(
							uuid, groupId);

					folderId = fileEntry.getFolderId();
					name = fileEntry.getName();
				}
				catch (Exception e) {
				}
			}

			if (fileShortcutId <= 0) {
/* 				
				DLFileEntryPermission.check(
					themeDisplay.getPermissionChecker(), folderId, name,
					ActionKeys.VIEW);
*/					
			}
			else {
				DLFileShortcut fileShortcut =
					DLFileShortcutServiceUtil.getFileShortcut(fileShortcutId);

				folderId = fileShortcut.getToFolderId();
				name = fileShortcut.getToName();
			}

			if (fileEntry == null) {
				fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(
					folderId, name);
			}

			if (version == 0) {
				version = fileEntry.getVersion();
			}

			is = DLFileEntryLocalServiceUtil.getFileAsStream(
				companyId, userId, folderId, name, version);

			String fileName = fileEntry.getTitleWithExtension();

			if (Validator.isNotNull(targetExtension)) {
				String id = DocumentConversionUtil.getTempFileId(
					fileEntry.getFileEntryId(), version);

				String sourceExtension = FileUtil.getExtension(name);

				InputStream convertedIS = DocumentConversionUtil.convert(
					id, is, sourceExtension, targetExtension);

				if ((convertedIS != null) && (convertedIS != is)) {
					StringBuilder sb = new StringBuilder();

					sb.append(fileEntry.getTitle());
					sb.append(StringPool.PERIOD);
					sb.append(targetExtension);

					fileName = sb.toString();

					is = convertedIS;
				}
			}

			int contentLength = fileEntry.getSize();
			String contentType = MimeTypesUtil.getContentType(fileName);

			ServletResponseUtil.sendFile(
				response, fileName, is, contentLength, contentType);
		}
		finally {
			ServletResponseUtil.cleanUp(is);
		}
	}

	protected boolean isCheckMethodOnProcessAction() {
		return _CHECK_METHOD_ON_PROCESS_ACTION;
	}

	private static final boolean _CHECK_METHOD_ON_PROCESS_ACTION = false;

}