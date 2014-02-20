/*
 * Copyright (c) 2009-2014, JoshuaTree. All Rights Reserved.
 */

package us.jts.commander.integration;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.jts.fortress.GlobalIds;
import us.jts.fortress.util.LogUtil;
import us.jts.fortress.util.attr.VUtil;


/**
 * Description of the Class
 *
 * @author Shawn McKinney
 */
public class TUtils extends TestCase
{
    private static final String CLS_NM = TUtils.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger( CLS_NM );

    /**
     * Fortress stores complex attribute types within a single attribute in ldap.  Usually a delimiter of ',' is used for string tokenization.
     * format: {@code name:value}
     */
    public static final String DELIMITER_TEST_DATA = ",";

    public static byte[] readJpegFile( String fileName )
    {
        URL fUrl = TUtils.class.getClassLoader().getResource( fileName );
        byte[] image = null;
        try
        {
            if ( fUrl != null )
            {
                image = FileUtils.readFileToByteArray( new File( fUrl.toURI() ) );
            }
        }
        catch ( URISyntaxException se )
        {
            String error = "readJpegFile caught URISyntaxException=" + se;
            LOG.error( error );
        }
        catch ( IOException ioe )
        {
            String error = "readJpegFile caught IOException=" + ioe;
            LOG.error( error );
        }
        return image;
    }


    /**
     *
     * @param len
     */
    public static void sleep( String len )
    {
        try
        {
            Integer iSleep = ( Integer.parseInt( len ) * 1000 );
            Thread.currentThread().sleep( iSleep );
        }
        catch ( InterruptedException ie )
        {
            LOG.warn( TUtils.class.getName() + ".sleep caught InterruptedException=" + ie.getMessage(), ie );
        }
    }


    /**
     *
     * @param len
     */
    public static void sleep( int len )
    {
        try
        {
            int iSleep = len * 1000;
            Thread.currentThread().sleep( iSleep );
        }
        catch ( InterruptedException ie )
        {
            LOG.warn( TUtils.class.getName() + ".sleep caught InterruptedException=" + ie.getMessage(), ie );
        }
    }


    /**
     *
     * @param len
     */
    public static void sleep( long len )
    {
        try
        {
            long iSleep = len * 1000;
             Thread.currentThread().sleep( iSleep );
        }
        catch ( InterruptedException ie )
        {
            LOG.warn( TUtils.class.getName() + ".sleep caught InterruptedException=" + ie.getMessage(), ie );
        }
    }
}