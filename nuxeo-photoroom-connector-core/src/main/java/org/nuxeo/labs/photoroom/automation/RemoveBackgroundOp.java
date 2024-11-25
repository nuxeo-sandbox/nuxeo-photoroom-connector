package org.nuxeo.labs.photoroom.automation;

import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.labs.photoroom.service.PhotoroomService;

@Operation(
        id=RemoveBackgroundOp.ID, category="Photoroom.com",
        label="Remove Picture Background")
public class RemoveBackgroundOp {

    public static final String ID = "Photoroom.RemoveBackgroundOp";

    @Context
    protected CoreSession session;

    @Context
    protected PhotoroomService photoroomService;

    @OperationMethod
    public Blob run(Blob input) {
        return photoroomService.removeImageBackground(input);
    }
}
