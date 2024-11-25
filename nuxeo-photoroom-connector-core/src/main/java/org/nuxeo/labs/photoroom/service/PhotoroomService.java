package org.nuxeo.labs.photoroom.service;

import org.nuxeo.ecm.core.api.Blob;

public interface PhotoroomService {

    /**
     * Remove the background of the picture and keep only the main object
     * @param source the image with a background
     * @return a picture with a white background
     */
    Blob removeImageBackground(Blob source);

}
