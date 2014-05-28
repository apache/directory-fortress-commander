package org.openldap.commander.panel;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: smckinn
 * Date: 26/05/14
 * Time: 22:05
 * To change this template use File | Settings | File Templates.
 */
public class Member implements Serializable
{
    private String userDn;
    private int index;

    public String getUserDn()
    {
        return userDn;
    }

    public void setUserDn( String userDn )
    {
        this.userDn = userDn;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex( int index )
    {
        this.index = index;
    }
}
