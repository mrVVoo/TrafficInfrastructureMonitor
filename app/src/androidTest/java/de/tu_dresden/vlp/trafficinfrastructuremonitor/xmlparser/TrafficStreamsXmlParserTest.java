package de.tu_dresden.vlp.trafficinfrastructuremonitor.xmlparser;

import android.support.test.runner.AndroidJUnit4;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.StopLinePoint;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStream;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStreamElement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Markus Wutzler on 26.01.18.
 */
@RunWith(AndroidJUnit4.class)
public class TrafficStreamsXmlParserTest {

    @Test
    public void parse() throws IOException, XmlPullParserException {
        List<TrafficStream> trafficStreams = new TrafficStreamsXmlParser(getClass().getResourceAsStream("/demo.xml")).parse();
        Assert.assertNotNull("Traffic Stream List is null",trafficStreams);
        Assert.assertFalse("Traffic Stream List is empty",trafficStreams.isEmpty());
        Assert.assertEquals(30,trafficStreams.size());
        for (TrafficStream trafficStream : trafficStreams) {
            Assert.assertNotNull(trafficStream.getId());
            Assert.assertNotNull(trafficStream.getContainments());
            for (TrafficStreamElement element : trafficStream.getContainments()) {
                Assert.assertTrue(element instanceof StopLinePoint);
            }
            Assert.assertNotNull(trafficStream.getCoordinates());
        }
    }
}