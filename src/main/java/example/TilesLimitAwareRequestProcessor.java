package example;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.InvalidCancelException;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.tiles.TilesRequestProcessor;
import org.apache.struts.upload.MultipartRequestHandler;

/**
 * App-wide handling for a multipart text field over maxStringLen. Struts leaves
 * the whole form unpopulated in that case, so stop before validation and the
 * action run, and send the user to a "textTooLong" forward (action-local or
 * global) or, failing that, back to the mapping's input page with a message.
 * Runs for every mapping, including validate="false" ones.
 *
 * Tiles variant: with the TilesPlugin, a processorClass that doesn't extend
 * TilesRequestProcessor makes Struts fail to start.
 */
public class TilesLimitAwareRequestProcessor extends TilesRequestProcessor {

    protected boolean processValidate(HttpServletRequest request, HttpServletResponse response,
            ActionForm form, ActionMapping mapping)
            throws IOException, ServletException, InvalidCancelException {

        if (!Boolean.TRUE.equals(request.getAttribute(
                MultipartRequestHandler.ATTRIBUTE_MAX_STRING_LENGTH_EXCEEDED))) {
            return super.processValidate(request, response, form, mapping);
        }

        if (form != null && form.getMultipartRequestHandler() != null) {
            form.getMultipartRequestHandler().rollback();
        }

        ActionMessages errors = new ActionMessages();
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.text.tooLong"));
        request.setAttribute(Globals.ERROR_KEY, errors);

        ForwardConfig target = mapping.findForward("textTooLong");
        if (target == null && mapping.getInput() != null) {
            target = mapping.getInputForward();
        }
        if (target == null) {
            response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
            return false;
        }
        processForwardConfig(request, response, target);
        return false;
    }
}
