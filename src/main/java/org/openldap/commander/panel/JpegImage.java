/*
 * This work is part of OpenLDAP Software <http://www.openldap.org/>.
 *
 * Copyright 1998-2014 The OpenLDAP Foundation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted only as authorized by the OpenLDAP
 * Public License.
 *
 * A copy of this license is available in the file LICENSE in the
 * top-level directory of the distribution or, alternatively, at
 * <http://www.OpenLDAP.org/license.html>.
 */

package org.openldap.commander.panel;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;

/**
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public abstract class JpegImage extends Image
{
    public JpegImage(String id)
    {
        super(id);
        setImageResource(new DynamicImageResource()
        {
            protected byte[] getImageData(IResource.Attributes attributes)
            {
                return getPhoto();
            }
        });
    }
    protected abstract byte[] getPhoto();
}
