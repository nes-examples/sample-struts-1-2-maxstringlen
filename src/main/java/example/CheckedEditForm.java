package example;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.MultipartRequestHandler;

/**
 * The recommended per-form fix: detect the limit before normal validation runs
 * and return a message, so Struts forwards to the mapping's input page.
 */
public class CheckedEditForm extends EditForm {

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (Boolean.TRUE.equals(request.getAttribute(
                MultipartRequestHandler.ATTRIBUTE_MAX_STRING_LENGTH_EXCEEDED))) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.text.tooLong"));
            return errors;
        }
        return super.validate(mapping, request);
    }
}
