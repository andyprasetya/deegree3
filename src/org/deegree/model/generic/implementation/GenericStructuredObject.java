//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2008 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 Andreas Poth  
 lat/lon GmbH 
 Aennchenstr. 19
 53115 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de


 ---------------------------------------------------------------------------*/
package org.deegree.model.generic.implementation;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.deegree.model.generic.AttributeNode;
import org.deegree.model.generic.ObjectNode;
import org.deegree.model.generic.StructuredObject;
import org.deegree.model.generic.ValueNode;
import org.deegree.model.generic.schema.StructuredObjectType;

/**
 * TODO add documentation here
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author:$
 *
 * @version $Revision:$, $Date:$
 */
public class GenericStructuredObject extends GenericNode implements StructuredObject {

    private QName name;
    
    private List<AttributeNode> attributes;

    private List<ObjectNode> contents;

    private StructuredObjectType type;

    GenericStructuredObject(QName name, StructuredObject parent, StructuredObjectType type) {
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.attributes = new LinkedList<AttributeNode>();
        this.contents = new LinkedList<ObjectNode>();
        
        // add to parent if it exists
        if (parent != null) {
            parent.addContent( this );
        }
    }    
    
    public StructuredObjectType getType() {
        return type;
    }

    public QName getName() {
        return name;
    }    
    
    public List<AttributeNode> getAttributes() {
        return attributes;
    }
    
    public void addAttribute(AttributeNode attribute) {
        this.attributes.add (attribute);
    }

    public List<ObjectNode> getContents() {
        return contents;
    }

    public void addContent (StructuredObject content) {
        this.contents.add( content );
    }

    public void addContent (ValueNode content) {
        this.contents.add( content );
    }    
}
