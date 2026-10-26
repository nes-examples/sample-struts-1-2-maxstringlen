package example;

import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorForm;

/** A form with a title, a long description and an attachment. */
public class EditForm extends ValidatorForm {

    private String method;
    private String title;
    private String description;
    private FormFile attachment;

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public FormFile getAttachment() { return attachment; }
    public void setAttachment(FormFile attachment) { this.attachment = attachment; }
}
