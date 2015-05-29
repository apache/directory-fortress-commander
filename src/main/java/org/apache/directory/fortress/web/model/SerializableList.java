/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.fortress.web.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SerializableList<E> implements List<E>, Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private List<E> list;
        
    public SerializableList( List<E> list )
    {
        this.list = list;
    }

    @Override
    public int size()
    {
        return list.size();
    }

    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    @Override
    public boolean contains( Object o )
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<E> iterator()
    {
        return list.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray( T[] a )
    {
        return list.toArray( a );
    }

    @Override
    public boolean add( E e )
    {
        return list.add( e );
    }

    @Override
    public boolean remove( Object o )
    {
        return list.remove( o );
    }

    @Override
    public boolean containsAll( Collection<?> c )
    {
        return list.containsAll( c );
    }

    @Override
    public boolean addAll( Collection<? extends E> c )
    {
        return list.addAll( c );
    }

    @Override
    public boolean addAll( int index, Collection<? extends E> c )
    {
        return list.addAll( index, c );
    }

    @Override
    public boolean removeAll( Collection<?> c )
    {
        return list.removeAll( c );
    }

    @Override
    public boolean retainAll( Collection<?> c )
    {
        return list.retainAll( c );
    }

    @Override
    public void clear()
    {
        list.clear();
    }

    @Override
    public E get( int index )
    {
        return list.get( index );
    }

    @Override
    public E set( int index, E element )
    {
        return list.set( index, element );
    }

    @Override
    public void add( int index, E element )
    {
        list.add( index, element );
    }

    @Override
    public E remove( int index )
    {
        return list.remove( index );
    }

    @Override
    public int indexOf( Object o )
    {
        return list.indexOf( o );
    }

    @Override
    public int lastIndexOf( Object o )
    {
        return list.lastIndexOf( o );
    }

    @Override
    public ListIterator<E> listIterator()
    {
        return list.listIterator();
    }

    @Override
    public ListIterator<E> listIterator( int index )
    {
        return list.listIterator( index );
    }

    @Override
    public List<E> subList( int fromIndex, int toIndex )
    {
        return list.subList( fromIndex, toIndex );
    }
}
