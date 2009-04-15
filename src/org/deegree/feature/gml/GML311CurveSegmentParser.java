//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2009 by:
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
package org.deegree.feature.gml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.deegree.commons.xml.CommonNamespaces.GMLNS;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.deegree.commons.types.Length;
import org.deegree.commons.xml.XMLParsingException;
import org.deegree.commons.xml.stax.XMLStreamReaderWrapper;
import org.deegree.crs.CRS;
import org.deegree.crs.exceptions.UnknownCRSException;
import org.deegree.geometry.GeometryFactory;
import org.deegree.geometry.primitive.Curve;
import org.deegree.geometry.primitive.Point;
import org.deegree.geometry.primitive.curvesegments.Arc;
import org.deegree.geometry.primitive.curvesegments.ArcByBulge;
import org.deegree.geometry.primitive.curvesegments.ArcByCenterPoint;
import org.deegree.geometry.primitive.curvesegments.ArcString;
import org.deegree.geometry.primitive.curvesegments.ArcStringByBulge;
import org.deegree.geometry.primitive.curvesegments.BSpline;
import org.deegree.geometry.primitive.curvesegments.Bezier;
import org.deegree.geometry.primitive.curvesegments.Circle;
import org.deegree.geometry.primitive.curvesegments.CircleByCenterPoint;
import org.deegree.geometry.primitive.curvesegments.Clothoid;
import org.deegree.geometry.primitive.curvesegments.CubicSpline;
import org.deegree.geometry.primitive.curvesegments.CurveSegment;
import org.deegree.geometry.primitive.curvesegments.Geodesic;
import org.deegree.geometry.primitive.curvesegments.GeodesicString;
import org.deegree.geometry.primitive.curvesegments.Knot;
import org.deegree.geometry.primitive.curvesegments.LineStringSegment;
import org.deegree.geometry.primitive.curvesegments.OffsetCurve;
import org.deegree.geometry.primitive.curvesegments.CurveSegment.Interpolation;
import org.deegree.geometry.standard.curvesegments.AffinePlacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the parsing of <code>gml:_CurveSegment</code> elements, i.e concrete element declarations that are in the
 * substitution group <code>gml:_CurveSegment</code>.
 * <p>
 * This class handles all 15 concrete substitutions for <code>gml:_CurveSegment</code> that are defined in GML 3.1.1:
 * <ul>
 * <li><code>Arc</code></li>
 * <li><code>ArcByBulge</code></li>
 * <li><code>ArcByCenterPoint</code></li>
 * <li><code>ArcString</code></li>
 * <li><code>ArcStringByBulge</code></li>
 * <li><code>Bezier</code></li>
 * <li><code>BSpline</code></li>
 * <li><code>Circle</code></li>
 * <li><code>CircleByCenterPoint</code></li>
 * <li><code>Clothoid</code></li>
 * <li><code>CubicSpline</code></li>
 * <li><code>Geodesic</code></li>
 * <li><code>GeodesicString</code></li>
 * <li><code>LineStringSegment</code></li>
 * <li><code>OffsetCurve</code></li>
 * </p>
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 */
class GML311CurveSegmentParser extends GML311BaseParser {

    private static Logger LOG = LoggerFactory.getLogger( GML311CurveSegmentParser.class );

    private GML311GeometryParser geometryParser;

    /**
     * @param geometryParser
     * @param geomFac
     * @param xmlStream
     */
    GML311CurveSegmentParser( GML311GeometryParser geometryParser, GeometryFactory geomFac, GMLIdContext idContext ) {
        super( geomFac, idContext );
        this.geometryParser = geometryParser;
    }

    /**
     * Returns the object representation for a <code>gml:_CurveSegment</code> element. Consumes all corresponding events
     * from the associated <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:_CurveSegment&gt;)</li>
     * <li>Postcondition: cursor points at the next event after the <code>END_ELEMENT</code>
     * (&lt;/gml:_CurveSegment&gt;)</li>
     * </ul>
     * <p>
     * This method handles all 15 concrete substitutions for <code>gml:_CurveSegment</code> that are defined in GML
     * 3.1.1:
     * <ul>
     * <li><code>Arc</code></li>
     * <li><code>ArcByBulge</code></li>
     * <li><code>ArcByCenterPoint</code></li>
     * <li><code>ArcString</code></li>
     * <li><code>ArcStringByBulge</code></li>
     * <li><code>Bezier</code></li>
     * <li><code>BSpline</code></li>
     * <li><code>Circle</code></li>
     * <li><code>CircleByCenterPoint</code></li>
     * <li><code>Clothoid</code></li>
     * <li><code>CubicSpline</code></li>
     * <li><code>Geodesic</code></li>
     * <li><code>GeodesicString</code></li>
     * <li><code>LineStringSegment</code></li>
     * <li><code>OffsetCurve</code></li>
     * </ul>
     * 
     * @param defaultCRS
     *            default CRS for the geometry, this is only used if the "gml:_CurveSegment" has no <code>srsName</code>
     *            attribute
     * @return corresponding {@link CurveSegment} object
     * @throws XMLParsingException
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    CurveSegment parseCurveSegment( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        LOG.debug( " - parsing gml:_CurveSegment (begin): " + xmlStream.getCurrentEventInfo() );

        CurveSegment segment = null;

        if ( !GMLNS.equals( xmlStream.getNamespaceURI() ) ) {
            String msg = "Invalid gml:_CurveSegment element: " + xmlStream.getName()
                         + "' is not a GML geometry element. Not in the gml namespace.";
            throw new XMLParsingException( xmlStream, msg );
        }

        String name = xmlStream.getLocalName();
        if ( name.equals( "Arc" ) ) {
            segment = parseArc( xmlStream, defaultCRS );
        } else if ( name.equals( "ArcByCenterPoint" ) ) {
            segment = parseArcByCenterPoint( xmlStream, defaultCRS );
        } else if ( name.equals( "ArcByBulge" ) ) {
            segment = parseArcByBulge( xmlStream, defaultCRS );
        } else if ( name.equals( "ArcString" ) ) {
            segment = parseArcString( xmlStream, defaultCRS );
        } else if ( name.equals( "ArcStringByBulge" ) ) {
            segment = parseArcStringByBulge( xmlStream, defaultCRS );
        } else if ( name.equals( "Bezier" ) ) {
            segment = parseBezier( xmlStream, defaultCRS );
        } else if ( name.equals( "BSpline" ) ) {
            segment = parseBSpline( xmlStream, defaultCRS );
        } else if ( name.equals( "Circle" ) ) {
            segment = parseCircle( xmlStream, defaultCRS );
        } else if ( name.equals( "CircleByCenterPoint" ) ) {
            segment = parseCircleByCenterPoint( xmlStream, defaultCRS );
        } else if ( name.equals( "Clothoid" ) ) {
            segment = parseClothoid( xmlStream, defaultCRS );
        } else if ( name.equals( "CubicSpline" ) ) {
            segment = parseCubicSpline( xmlStream, defaultCRS );
        } else if ( name.equals( "Geodesic" ) ) {
            segment = parseGeodesic( xmlStream, defaultCRS );
        } else if ( name.equals( "GeodesicString" ) ) {
            segment = parseGeodesicString( xmlStream, defaultCRS );
        } else if ( name.equals( "LineStringSegment" ) ) {
            segment = parseLineStringSegment( xmlStream, defaultCRS );
        } else if ( name.equals( "OffsetCurve" ) ) {
            segment = parseOffsetCurve( xmlStream, defaultCRS );
        } else {
            String msg = "Invalid GML geometry: '" + xmlStream.getName()
                         + "' is not a valid substitution for '_CurveSegment'.";
            throw new XMLParsingException( xmlStream, msg );
        }

        LOG.debug( " - parsing gml:_CurveSegment (end): " + xmlStream.getCurrentEventInfo() );
        return segment;
    }

    /**
     * Returns the object representation of a <code>&lt;gml:Arc&gt;</code> element. Consumes all corresponding events
     * from the associated <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:Arc&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event (&lt;/gml:Arc&gt;)</li>
     * </ul>
     * 
     * @param defaultCRS
     *            default CRS for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link Arc} object
     * @throws XMLParsingException
     *             if a syntactical (or semantic) error is detected in the element
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private Arc parseArc( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "circularArc3Points" );
        List<Point> points = parseControlPoints( xmlStream, defaultCRS );
        if ( points.size() != 3 ) {
            String msg = "Error in 'gml:Arc' element. Must specify exactly three control points, but contains "
                         + points.size() + ".";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "Arc" );
        return geomFac.createArc( points.get( 0 ), points.get( 1 ), points.get( 2 ) );
    }

    /**
     * Returns the object representation of a <code>&lt;gml:ArcByBulge&gt;</code> element. Consumes all corresponding
     * events from the associated <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:ArcByBulge&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event (&lt;/gml:ArcByBulge&gt;)</li>
     * </ul>
     * 
     * @param defaultCRS
     *            default CRS for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link ArcByBulge} object
     * @throws XMLParsingException
     *             if a syntactical (or semantic) error is detected in the element
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private ArcByBulge parseArcByBulge( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "circularArc2PointWithBulge" );
        List<Point> points = parseControlPoints( xmlStream, defaultCRS );
        if ( points.size() != 2 ) {
            String msg = "Error in 'gml:ArcByBulge' element. Must contain exactly two control points.";
            throw new XMLParsingException( xmlStream, msg );
        }

        double bulge = xmlStream.getElementTextAsDouble( GMLNS, "bulge" );
        xmlStream.nextTag();

        xmlStream.require( XMLStreamConstants.START_ELEMENT, GMLNS, "normal" );
        CRS normalCRS = determineActiveCRS( xmlStream, defaultCRS );
        double[] coords = parseDoubleList( xmlStream );
        Point normal = geomFac.createPoint( null, coords, normalCRS );
        xmlStream.nextTag();

        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "ArcByBulge" );
        return geomFac.createArcByBulge( points.get( 0 ), points.get( 1 ), bulge, normal );
    }

    /**
     * Returns the object representation of an <code>&lt;gml:ArcByCenterPoint&gt;</code> element. Consumes all
     * corresponding events from the associated <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:ArcByCenterPoint&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event
     * (&lt;/gml:ArcByCenterPoint&gt;)</li>
     * </ul>
     * 
     * @param defaultCRS
     *            default CRS for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link ArcByCenterPoint} object
     * @throws XMLParsingException
     *             if a syntactical (or semantic) error is detected in the element
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private ArcByCenterPoint parseArcByCenterPoint( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "circularArcCenterPointWithRadius" );
        List<Point> points = parseControlPoints( xmlStream, defaultCRS );
        if ( points.size() != 1 ) {
            String msg = "Error in 'gml:ArcByCenterPoint' element. Must contain one control point (the center point), but contains "
                         + points.size() + ".";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.require( XMLStreamConstants.START_ELEMENT, GMLNS, "radius" );
        Length radius = parseLengthType( xmlStream );
        xmlStream.nextTag();

        Angle startAngle = null;
        if ( xmlStream.getEventType() == START_ELEMENT ) {
            String localName = xmlStream.getName().getLocalPart();
            if ( "startAngle".equals( localName ) ) {
                startAngle = parseAngleType( xmlStream );
                xmlStream.nextTag();
            }
        }
        Angle endAngle = null;
        if ( xmlStream.getEventType() == START_ELEMENT ) {
            String localName = xmlStream.getName().getLocalPart();
            if ( "endAngle".equals( localName ) ) {
                endAngle = parseAngleType( xmlStream );
                xmlStream.nextTag();
            }
        }
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "ArcByCenterPoint" );
        return geomFac.createArcByCenterPoint( points.get( 0 ), radius, startAngle, endAngle );
    }

    /**
     * Returns the object representation of an <code>&lt;gml:ArcString&gt;</code> element. Consumes all corresponding
     * events from the associated <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:ArcString&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event (&lt;/gml:ArcString&gt;)</li>
     * </ul>
     * 
     * @param defaultCRS
     *            default CRS for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link ArcString} object
     * @throws XMLParsingException
     *             if a syntactical (or semantic) error is detected in the element
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private ArcString parseArcString( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "circularArc3Points" );
        List<Point> points = parseControlPoints( xmlStream, defaultCRS );
        if ( points.size() < 3 || points.size() % 2 != 1 ) {
            String msg = "Error in 'gml:ArcString' element. Invalid number of points (=" + points.size() + ").";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "ArcString" );
        return geomFac.createArcString( points );
    }

    /**
     * Returns the object representation of an <code>&lt;gml:ArcStringByBulge&gt;</code> element. Consumes all
     * corresponding events from the associated <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:ArcString&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event (&lt;/gml:ArcString&gt;)</li>
     * </ul>
     * 
     * @param defaultCRS
     *            default srs for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link ArcStringByBulge} object
     * @throws XMLParsingException
     *             if a syntactical (or semantic) error is detected in the element
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private ArcStringByBulge parseArcStringByBulge( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "circularArc2PointWithBulge" );
        List<Point> points = parseControlPoints( xmlStream, defaultCRS );
        if ( points.size() < 2 ) {
            String msg = "Error in 'gml:ArcStringByBulge' element. Must contain at least two points.";
            throw new XMLParsingException( xmlStream, msg );
        }

        // The length of the bulge sequence is exactly 1 less than the length of the control
        // point array, since a bulge is needed for each pair of adjacent points in the control point array.
        // (GML 3.1.1 spec, section 10.2.1.9)
        double[] bulges = new double[points.size() - 1];
        for ( int i = 0; i < bulges.length; i++ ) {
            bulges[i] = xmlStream.getElementTextAsDouble( GMLNS, "bulge" );
            xmlStream.nextTag();
        }

        // The length of the normal sequence is exactly the same as for the bulge sequence, 1 less than the control
        // point sequence length. (GML 3.1.1 spec, section 10.2.1.9)
        List<Point> normals = new ArrayList<Point>( points.size() - 1 );
        for ( int i = 0; i < bulges.length; i++ ) {
            xmlStream.require( XMLStreamConstants.START_ELEMENT, GMLNS, "normal" );
            CRS normalCRS = determineActiveCRS( xmlStream, defaultCRS );
            double[] coords = parseDoubleList( xmlStream );
            normals.add( geomFac.createPoint( null, coords, normalCRS ) );
            xmlStream.nextTag();
        }

        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "ArcStringByBulge" );
        return geomFac.createArcStringByBulge( points, bulges, normals );
    }

    /**
     * Returns the object representation of a <code>&lt;gml:Bezier&gt;</code> element. Consumes all corresponding events
     * from the given <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:Bezier&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event (&lt;/gml:Bezier&gt;)</li>
     * </ul>
     * 
     * @param defaultSrsName
     *            default srs for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link Bezier} object
     * @throws XMLParsingException
     *             if a syntactical (or semantic) error is detected in the element
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private Bezier parseBezier( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "polynomialSpline" );
        List<Point> points = parseControlPoints( xmlStream, defaultCRS );

        // TODO are the any more semantic constraints to be considered?

        int degree = xmlStream.getElementTextAsPositiveInteger( GMLNS, "degree" );

        List<Knot> knots = new LinkedList<Knot>();
        while ( xmlStream.nextTag() == XMLStreamConstants.START_ELEMENT ) {
            if ( !"knot".equals( xmlStream.getLocalName() ) ) {
                break;
            }
            xmlStream.nextTag();
            xmlStream.require( XMLStreamConstants.START_ELEMENT, GMLNS, "Knot" );
            xmlStream.nextTag();
            double value = xmlStream.getElementTextAsDouble( GMLNS, "value" );
            xmlStream.nextTag();
            int multiplicity = xmlStream.getElementTextAsPositiveInteger( GMLNS, "multiplicity" );
            xmlStream.nextTag();
            double weight = xmlStream.getElementTextAsDouble( GMLNS, "weight" );
            xmlStream.nextTag();
            xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "Knot" );
            xmlStream.nextTag();
            xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "knot" );
            knots.add( new Knot( value, multiplicity, weight ) );
        }
        if ( knots.size() != 2 ) {
            String msg = "Error in 'gml:Bezier' element. Must specify exactly two knots.";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "Bezier" );
        return geomFac.createBezier( points, degree, knots.get( 0 ), knots.get( 1 ) );
    }

    /**
     * Returns the object representation of a <code>&lt;gml:BSpline&gt;</code> element. Consumes all corresponding
     * events from the given <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:BSpline&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event (&lt;/gml:BSpline&gt;)</li>
     * </ul>
     * 
     * @param defaultSrsName
     *            default srs for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link BSpline} object
     * @throws XMLParsingException
     *             if a syntactical (or semantic) error is detected in the element
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private BSpline parseBSpline( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        Interpolation interpolation = null;
        String interpolationAttrValue = xmlStream.getAttributeValueWDefault( "interpolation", "polynomialSpline" );
        if ( "rationalSpline".equals( interpolationAttrValue ) ) {
            interpolation = Interpolation.rationalSpline;
        } else if ( "polynomialSpline".equals( interpolationAttrValue ) ) {
            interpolation = Interpolation.polynomialSpline;
        } else {
            String msg = "Invalid value ('" + interpolationAttrValue + "') for interpolation attribute in element '"
                         + xmlStream.getName() + "'. Must be 'rationalSpline' or 'polynomialSpline'.";
            throw new XMLParsingException( xmlStream, msg );
        }

        // TODO what about the knotType attribute??

        List<Point> points = parseControlPoints( xmlStream, defaultCRS );

        // TODO are the any more semantic constraints to be considered?

        int degree = xmlStream.getElementTextAsPositiveInteger( GMLNS, "degree" );

        List<Knot> knots = new LinkedList<Knot>();
        while ( xmlStream.nextTag() == XMLStreamConstants.START_ELEMENT ) {
            if ( !"knot".equals( xmlStream.getLocalName() ) ) {
                break;
            }
            xmlStream.nextTag();
            xmlStream.require( XMLStreamConstants.START_ELEMENT, GMLNS, "Knot" );
            xmlStream.nextTag();
            double value = xmlStream.getElementTextAsDouble( GMLNS, "value" );
            xmlStream.nextTag();
            int multiplicity = xmlStream.getElementTextAsPositiveInteger( GMLNS, "multiplicity" );
            xmlStream.nextTag();
            double weight = xmlStream.getElementTextAsDouble( GMLNS, "weight" );
            xmlStream.nextTag();
            xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "Knot" );
            xmlStream.nextTag();
            xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "knot" );
            knots.add( new Knot( value, multiplicity, weight ) );
        }
        if ( knots.size() < 2 ) {
            String msg = "Error in 'gml:BSpline' element. Must specify at least two knots.";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "BSpline" );
        return geomFac.createBSpline( points, degree, knots, interpolation == Interpolation.polynomialSpline );
    }

    /**
     * Returns the object representation of a <code>&lt;gml:Circle&gt;</code> element. Consumes all corresponding events
     * from the associated <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:Circle&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event (&lt;/gml:Circle&gt;)</li>
     * </ul>
     * 
     * @param defaultSrsName
     *            default srs for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link Circle} object
     * @throws XMLParsingException
     *             if a syntactical (or semantic) error is detected in the element
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private Circle parseCircle( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "circularArc3Points" );
        List<Point> points = parseControlPoints( xmlStream, defaultCRS );
        if ( points.size() != 3 ) {
            String msg = "Error in 'gml:Circle' element. Must specify exactly three control points, but contains "
                         + points.size() + ".";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "Circle" );
        return geomFac.createCircle( points.get( 0 ), points.get( 1 ), points.get( 2 ) );
    }

    /**
     * Returns the object representation of a <code>&lt;gml:CircleByCenterPoint&gt;</code> element. Consumes all
     * corresponding events from the associated <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:CircleByCenterPoint&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event
     * (&lt;/gml:CircleByCenterPoint&gt;)</li>
     * </ul>
     * 
     * @param defaultSrsName
     *            default srs for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link Circle} object
     * @throws XMLParsingException
     *             if a syntactical (or semantic) error is detected in the element
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private CircleByCenterPoint parseCircleByCenterPoint( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "circularArcCenterPointWithRadius" );
        List<Point> points = parseControlPoints( xmlStream, defaultCRS );
        if ( points.size() != 1 ) {
            String msg = "Error in 'gml:CircleByCenterPoint' element. Must contain one control point (the center point), but contains "
                         + points.size() + ".";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.require( XMLStreamConstants.START_ELEMENT, GMLNS, "radius" );
        Length radius = parseLengthType( xmlStream );
        xmlStream.nextTag();

        Angle startAngle = null;
        if ( xmlStream.getEventType() == START_ELEMENT ) {
            String localName = xmlStream.getName().getLocalPart();
            if ( "startAngle".equals( localName ) ) {
                startAngle = parseAngleType( xmlStream );
                xmlStream.nextTag();
            }
        }
        Angle endAngle = null;
        if ( xmlStream.getEventType() == START_ELEMENT ) {
            String localName = xmlStream.getName().getLocalPart();
            if ( "endAngle".equals( localName ) ) {
                endAngle = parseAngleType( xmlStream );
                xmlStream.nextTag();
            }
        }

        if ( startAngle != null && endAngle != null ) {
            if ( !startAngle.equals( endAngle ) ) {
                String msg = "Error in 'gml:CircleByCenterPoint' element. The specified values for start angle and end angle differ.";
                throw new XMLParsingException( xmlStream, msg );
            }
        } else if ( endAngle == null ) {
            startAngle = endAngle;
        }

        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "CircleByCenterPoint" );
        return geomFac.createCircleByCenterPoint( points.get( 0 ), radius, startAngle );
    }

    /**
     * Returns the object representation of a <code>&lt;gml:Clothoid&gt;</code> element. Consumes all corresponding
     * events from the associated <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:Clothoid&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event (&lt;/gml:Clothoid&gt;)</li>
     * </ul>
     * 
     * @param defaultSrsName
     *            default srs for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link Clothoid} object
     * @throws XMLParsingException
     *             if a syntactical (or semantic) error is detected in the element
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private Clothoid parseClothoid( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        xmlStream.nextTag();
        xmlStream.require( START_ELEMENT, GMLNS, "refLocation" );
        xmlStream.nextTag();
        xmlStream.require( START_ELEMENT, GMLNS, "AffinePlacement" );
        AffinePlacement referenceLocation = parseAffinePlacement( xmlStream, defaultCRS );
        xmlStream.nextTag();
        xmlStream.require( END_ELEMENT, GMLNS, "refLocation" );
        xmlStream.nextTag();
        double scaleFactor = xmlStream.getElementTextAsDouble( GMLNS, "scaleFactor" );
        xmlStream.nextTag();
        double startParameter = xmlStream.getElementTextAsDouble( GMLNS, "startParameter" );
        xmlStream.nextTag();
        double endParameter = xmlStream.getElementTextAsDouble( GMLNS, "endParameter" );
        xmlStream.nextTag();
        xmlStream.require( END_ELEMENT, GMLNS, "Clothoid" );
        return geomFac.createClothoid( referenceLocation, scaleFactor, startParameter, endParameter );
    }

    /**
     * Returns the object representation of a (&lt;gml:AffinePlacement&gt;) element. Consumes all corresponding events
     * from the given <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:AffinePlacement&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event
     * (&lt;/gml:AffinePlacement&gt;)</li>
     * </ul>
     * 
     * @return corresponding {@link AffinePlacement} object
     * @throws XMLParsingException
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private AffinePlacement parseAffinePlacement( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        xmlStream.nextTag();
        xmlStream.require( START_ELEMENT, GMLNS, "location" );
        Point location = parseDirectPositionType( xmlStream, defaultCRS );
        xmlStream.nextTag();

        List<Point> refDirections = new LinkedList<Point>();
        xmlStream.require( START_ELEMENT, GMLNS, "refDirection" );
        int refDirectionOutDimension = -1;
        while ( xmlStream.getEventType() == START_ELEMENT ) {
            String localName = xmlStream.getName().getLocalPart();
            if ( "refDirection".equals( localName ) ) {
                Point refDirection = parseDirectPositionType( xmlStream, defaultCRS );
                if ( refDirectionOutDimension != -1 ) {
                    if ( refDirectionOutDimension != refDirection.getCoordinateDimension() ) {
                        String msg = "Inconsistent dimensions in 'gml:refDirection' positions.";
                        throw new XMLParsingException( xmlStream, msg );
                    }
                } else {
                    refDirectionOutDimension = refDirection.getCoordinateDimension();
                }
                refDirections.add( refDirection );
                xmlStream.nextTag();
            } else {
                break;
            }
        }

        int inDimension = xmlStream.getElementTextAsPositiveInteger( GMLNS, "inDimension" );
        if ( refDirections.size() != inDimension ) {
            String msg = "The number of target directions ('gml:refDirection') and the in dimension value do not match.";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.nextTag();

        int outDimension = xmlStream.getElementTextAsPositiveInteger( GMLNS, "outDimension" );
        if ( refDirectionOutDimension != outDimension ) {
            String msg = "The dimension of target directions ('gml:refDirection') and the out dimension value do not match.";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.nextTag();

        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "AffinePlacement" );
        return new AffinePlacement( location, refDirections, inDimension, outDimension );
    }

    /**
     * Returns the object representation of a (&lt;gml:CubicSpline&gt;) element. Consumes all corresponding events from
     * the given <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:CubicSpline&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event (&lt;/gml:CubicSpline&gt;)</li>
     * </ul>
     * 
     * @param defaultSrsName
     *            default srs for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link CubicSpline} object
     * @throws XMLParsingException
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private CubicSpline parseCubicSpline( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "cubicSpline" );
        List<Point> points = parseControlPoints( xmlStream, defaultCRS );
        if ( points.size() < 2 ) {
            String msg = "Error in 'gml:CubicSpline' element. Must consist of two points at least.";
            throw new XMLParsingException( xmlStream, msg );
        }

        xmlStream.require( XMLStreamConstants.START_ELEMENT, GMLNS, "vectorAtStart" );
        Point vectorAtStart = parseDirectPositionType( xmlStream, defaultCRS );
        xmlStream.nextTag();
        xmlStream.require( XMLStreamConstants.START_ELEMENT, GMLNS, "vectorAtEnd" );
        Point vectorAtEnd = parseDirectPositionType( xmlStream, defaultCRS );
        xmlStream.nextTag();
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "CubicSpline" );
        return geomFac.createCubicSpline( points, vectorAtStart, vectorAtEnd );
    }

    /**
     * Returns the object representation of a (&lt;gml:Geodesic&gt;) element. Consumes all corresponding events from the
     * given <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:Geodesic&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event (&lt;/gml:Geodesic&gt;)</li>
     * </ul>
     * 
     * @param defaultSrsName
     *            default srs for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link GeodesicString} object
     * @throws XMLParsingException
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private Geodesic parseGeodesic( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "geodesic" );

        // the #parseControlPoints(String) method is not used here, because the GML 3.1.1 schema defines a slightly
        // different model here (no 'gml:coordinates' and no 'gml:coordinates' element)
        List<Point> points = null;
        if ( xmlStream.nextTag() == XMLStreamConstants.START_ELEMENT ) {
            String name = xmlStream.getLocalName();
            if ( "posList".equals( name ) ) {
                points = parsePosList( xmlStream, defaultCRS );
                xmlStream.nextTag();
            } else {
                points = new LinkedList<Point>();
                do {
                    name = xmlStream.getLocalName();
                    if ( "pos".equals( name ) ) {
                        points.add( parseDirectPositionType( xmlStream, defaultCRS ) );
                    } else if ( "pointProperty".equals( name ) ) {
                        points.add( geometryParser.parsePointProperty( xmlStream, defaultCRS ) );
                    } else {
                        break;
                    }
                } while ( xmlStream.nextTag() == XMLStreamConstants.START_ELEMENT );
            }
        }

        if ( points.size() != 2 ) {
            String msg = "Error in 'gml:Geodesic' element. Must consist of exactly two points.";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "Geodesic" );
        return geomFac.createGeodesic( points.get( 0 ), points.get( 1 ) );
    }

    /**
     * Returns the object representation of a (&lt;gml:GeodesicString&gt;) element. Consumes all corresponding events
     * from the given <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:GeodesicString&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event
     * (&lt;/gml:GeodesicString&gt;)</li>
     * </ul>
     * 
     * @param defaultSrsName
     *            default srs for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link GeodesicString} object
     * @throws XMLParsingException
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private GeodesicString parseGeodesicString( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "geodesic" );

        // the #parseControlPoints(String) method is not used here, because the GML 3.1.1 schema defines a slightly
        // different model here (no 'gml:coordinates' and no 'gml:coordinates' element)
        List<Point> points = null;
        if ( xmlStream.nextTag() == XMLStreamConstants.START_ELEMENT ) {
            String name = xmlStream.getLocalName();
            if ( "posList".equals( name ) ) {
                points = parsePosList( xmlStream, defaultCRS );
                xmlStream.nextTag();
            } else {
                points = new LinkedList<Point>();
                do {
                    name = xmlStream.getLocalName();
                    if ( "pos".equals( name ) ) {
                        points.add( parseDirectPositionType( xmlStream, defaultCRS ) );
                    } else if ( "pointProperty".equals( name ) ) {
                        points.add( geometryParser.parsePointProperty( xmlStream, defaultCRS ) );
                    } else {
                        break;
                    }
                } while ( xmlStream.nextTag() == XMLStreamConstants.START_ELEMENT );
            }
        }

        if ( points.size() < 2 ) {
            String msg = "Error in 'gml:GeodesicString' element. Must consist of two points at least.";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "GeodesicString" );
        return geomFac.createGeodesicString( points );
    }

    /**
     * Returns the object representation of a (&lt;gml:LineStringSegment&gt;) element. Consumes all corresponding events
     * from the given <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:LineStringSegment&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event
     * (&lt;/gml:LineStringSegment&gt;)</li>
     * </ul>
     * 
     * @param defaultCRS
     *            default CRS for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link LineStringSegment} object
     * @throws XMLParsingException
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    LineStringSegment parseLineStringSegment( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        validateInterpolationAttribute( xmlStream, "linear" );
        List<Point> points = parseControlPoints( xmlStream, defaultCRS );
        if ( points.size() < 2 ) {
            String msg = "Error in 'gml:LineStringSegment' element. Must consist of two points at least.";
            throw new XMLParsingException( xmlStream, msg );
        }
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "LineStringSegment" );
        return geomFac.createLineStringSegment( points );
    }

    /**
     * Returns the object representation of an (&lt;gml:OffsetCurve&gt;) element. Consumes all corresponding events from
     * the given <code>XMLStream</code>.
     * <ul>
     * <li>Precondition: cursor must point at the <code>START_ELEMENT</code> event (&lt;gml:OffsetCurve&gt;)</li>
     * <li>Postcondition: cursor points at the corresponding <code>END_ELEMENT</code> event (&lt;/gml:OffsetCurve&gt;)</li>
     * </ul>
     * 
     * @param defaultCRS
     *            default CRS for the geometry, this is propagated if no deeper <code>srsName</code> attribute is
     *            specified
     * @return corresponding {@link OffsetCurve} object
     * @throws XMLParsingException
     * @throws XMLStreamException
     * @throws UnknownCRSException
     */
    private OffsetCurve parseOffsetCurve( XMLStreamReaderWrapper xmlStream, CRS defaultCRS )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        xmlStream.nextTag();
        xmlStream.require( XMLStreamConstants.START_ELEMENT, GMLNS, "offsetBase" );
        xmlStream.nextTag();
        Curve baseCurve = geometryParser.parseAbstractCurve( xmlStream, defaultCRS );
        xmlStream.nextTag();
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "offsetBase" );
        xmlStream.nextTag();
        xmlStream.require( XMLStreamConstants.START_ELEMENT, GMLNS, "distance" );
        Length distance = parseLengthType( xmlStream );
        Point direction = null;
        if ( xmlStream.nextTag() == START_ELEMENT ) {
            xmlStream.require( XMLStreamConstants.START_ELEMENT, GMLNS, "refDirection" );
            direction = parseDirectPositionType( xmlStream, defaultCRS );
            xmlStream.nextTag();
        }
        xmlStream.require( XMLStreamConstants.END_ELEMENT, GMLNS, "OffsetCurve" );
        return geomFac.createOffsetCurve( baseCurve, direction, distance );
    }

    /**
     * Parses the control points of a curve segment element.
     * <p>
     * The parsed structure is specified by the following XSD choice:
     * 
     * <pre>
     * &lt;choice&gt;
     *   &lt;choice minOccurs=&quot;0&quot; maxOccurs=&quot;unbounded&quot;&gt;
     *     &lt;element ref=&quot;gml:pos&quot;/&gt;
     *     &lt;element ref=&quot;gml:pointProperty&quot;/&gt;
     *     &lt;element ref=&quot;gml:pointRep&quot;&gt;
     *   &lt;/choice&gt;
     *   &lt;element ref=&quot;gml:posList&quot;/&gt;
     *   &lt;element ref=&quot;gml:coordinates&quot;/&gt;
     *   &lt;element ref=&quot;gml:coord&quot;/&gt;
     * &lt;/choice&gt;
     * </pre>
     * 
     * </p>
     * <p>
     * Precondition: the <b>next</b> tag event is expected to be the first <code>START_ELEMENT</code> of the choice. If
     * this is not the case, an {@link XMLParsingException} is thrown.<br/>
     * Postcondition: the current event is the first tag event after the last <code>END_ELEMENT</code> that belongs to
     * the choice
     * </p>
     * 
     * @param crs
     *            default CRS for the points, this is used if no <code>srsName</code> attribute is specified
     * 
     * @return control points of the curve segment, not null, but size may be zero
     * @throws XMLStreamException
     * @throws XMLParsingException
     * @throws UnknownCRSException
     */
    List<Point> parseControlPoints( XMLStreamReaderWrapper xmlStream, CRS crs )
                            throws XMLParsingException, XMLStreamException, UnknownCRSException {

        List<Point> controlPoints = null;

        if ( xmlStream.nextTag() == XMLStreamConstants.START_ELEMENT ) {
            String name = xmlStream.getLocalName();
            if ( "posList".equals( name ) ) {
                controlPoints = parsePosList( xmlStream, crs );
                xmlStream.nextTag();
            } else if ( "coordinates".equals( name ) ) {
                // deprecated since GML 3.1.0, only included for backward compatibility
                controlPoints = parseCoordinates( xmlStream, crs );
                xmlStream.nextTag();
            } else {
                controlPoints = new LinkedList<Point>();
                do {
                    name = xmlStream.getLocalName();
                    if ( "pos".equals( name ) ) {
                        controlPoints.add( parseDirectPositionType( xmlStream, crs ) );
                    } else if ( "pointProperty".equals( name ) || "pointRep".equals( name ) ) {
                        // pointRep has been deprecated since GML 3.1.0, only included for backward compatibility
                        controlPoints.add( geometryParser.parsePointProperty( xmlStream, crs ) );
                    } else if ( "coord".equals( name ) ) {
                        // deprecated since GML 3.0, only included for backward compatibility
                        double[] coords = parseCoordType( xmlStream );
                        // anonymous point (no registering necessary)
                        controlPoints.add( geomFac.createPoint( null, coords, crs ) );
                    } else {
                        break;
                    }
                } while ( xmlStream.nextTag() == XMLStreamConstants.START_ELEMENT );
            }
        }
        return controlPoints;
    }

    private void validateInterpolationAttribute( XMLStreamReaderWrapper xmlStream, String expected )
                            throws XMLParsingException {
        String actual = xmlStream.getAttributeValue( null, "interpolation" );
        if ( actual != null && !expected.equals( actual ) ) {
            String msg = "Invalid value (='" + actual + "') for interpolation attribute in element '"
                         + xmlStream.getName() + "'. Must be '" + expected + "'.";
            throw new XMLParsingException( xmlStream, msg );
        }
    }
}
