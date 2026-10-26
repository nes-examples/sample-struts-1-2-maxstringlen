package example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

/** A DispatchAction whose "save" method stands in for the database write. */
public class EditAction extends DispatchAction {

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        EditForm f = (EditForm) form;
        log.info("save: title=" + f.getTitle()
                + " descriptionLength=" + (f.getDescription() == null ? "null" : String.valueOf(f.getDescription().length()))
                + " attachment=" + (f.getAttachment() == null ? "null" : f.getAttachment().getFileName()));
        request.setAttribute("saved", f);
        return mapping.findForward("success");
    }
}
