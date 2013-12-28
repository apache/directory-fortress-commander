/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

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
