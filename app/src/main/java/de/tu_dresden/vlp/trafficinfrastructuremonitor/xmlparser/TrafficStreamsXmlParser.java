package de.tu_dresden.vlp.trafficinfrastructuremonitor.xmlparser;

import android.util.Xml;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.StopLinePoint;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStream;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStreamElement;
import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.xmlpull.v1.XmlPullParser.TYPES;

/**
 * Created by Markus Wutzler on 25.01.18.
 */
public class TrafficStreamsXmlParser {
    // We don't use namespaces
    private static final String ns = null;

    private final InputStream inputStream;

    public TrafficStreamsXmlParser(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List<TrafficStream> parse() throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readTrafficStreams(parser);
        } finally {
            inputStream.close();
        }
    }

    private List<TrafficStream> readTrafficStreams(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<TrafficStream> trafficStreams = new ArrayList<>();

        if (parser.getEventType() != XmlPullParser.START_TAG || (!"trafficstreams".equalsIgnoreCase(parser.getName())))
            throw new XmlPullParserException("expected " + TYPES[parser.getEventType()] + parser.getPositionDescription());

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equalsIgnoreCase("trafficstream")) {
                trafficStreams.add(readTrafficStream(parser));
            } else {
                skip(parser);
            }
        }
        return trafficStreams;

    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private TrafficStream readTrafficStream(XmlPullParser parser) throws IOException, XmlPullParserException {
        if (parser.getEventType() != XmlPullParser.START_TAG || (!"trafficstream".equalsIgnoreCase(parser.getName())))
            throw new XmlPullParserException("expected " + TYPES[parser.getEventType()] + parser.getPositionDescription());

        String id = null;
        String wkt = null;
        List<TrafficStreamElement> containments = new ArrayList<>();

        id = parser.getAttributeValue(ns, "id");
        wkt = parser.getAttributeValue(ns, "wkt");

        TrafficStream trafficStream = new TrafficStream(id, wkt);

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equalsIgnoreCase("stopLinePoint")) {
                trafficStream.getContainments().add(readStopLinePoint(parser));
            } else {
                skip(parser);
            }
        }
        return trafficStream;
    }

    private TrafficStreamElement readStopLinePoint(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG || (!"stopLinePoint".equalsIgnoreCase(parser.getName())))
            throw new XmlPullParserException("expected " + TYPES[parser.getEventType()] + parser.getPositionDescription());

        List<GeoPoint> pointCoordinates = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equalsIgnoreCase("pointCoordinates")) {
                pointCoordinates.add(readCoordinate(parser));
            } else {
                skip(parser);
            }
        }
        return new StopLinePoint(pointCoordinates);
    }

    private GeoPoint readCoordinate(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG || (!"pointCoordinates".equalsIgnoreCase(parser.getName())))
            throw new XmlPullParserException("expected " + TYPES[parser.getEventType()] + parser.getPositionDescription());

        String latitude = null, longitude = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equalsIgnoreCase("longitude")) {
                longitude = parser.nextText();
            } else if (name.equalsIgnoreCase("latitude")) {
                latitude = parser.nextText();
            } else {
                skip(parser);
            }
        }

        if (latitude != null && longitude != null) {
            return new GeoPoint(Double.valueOf(latitude), Double.valueOf(longitude));
        }
        return null;
    }


}
