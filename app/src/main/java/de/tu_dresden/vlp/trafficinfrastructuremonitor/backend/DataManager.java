package de.tu_dresden.vlp.trafficinfrastructuremonitor.backend;

import android.arch.persistence.room.Room;
import android.content.Context;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.database.AppDatabase;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.Comment;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStream;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DataManager {

    private static final String TRAFFIC_STREAMS_FILE_NAME = "traffic-streams.xml";
    private final File trafficStreamsFile;
    private final AppDatabase appDatabase;
    private List<TrafficStream> myTrafficStreams = new LinkedList<>();
    private Set<DataManagerListener> listeners = new HashSet<>();

    public DataManager(Context applicationContext) {
        trafficStreamsFile = new File(applicationContext.getFilesDir(), DataManager.TRAFFIC_STREAMS_FILE_NAME);
        appDatabase = Room.databaseBuilder(applicationContext, AppDatabase.class, "tim-db").build();
    }

    public List<TrafficStream> getTrafficStreams() {
        if (this.myTrafficStreams == null || this.myTrafficStreams.isEmpty()) {
            parseTrafficStreams();
        }
        return myTrafficStreams;
    }

    private void parseTrafficStreams() {
        if (myTrafficStreams == null) {
            myTrafficStreams = new LinkedList<>();
        }
        if (trafficStreamsFile.exists() && trafficStreamsFile.canRead()) {
            try {
                myTrafficStreams.clear();
                myTrafficStreams.addAll(new TrafficStreamsXmlParser(new FileInputStream(trafficStreamsFile)).parse());
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void invalidate() {
        myTrafficStreams.clear();
        parseTrafficStreams();
        for (DataManagerListener listener : listeners) {
            listener.onDataChanged();
        }
    }

    public void load(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            if (trafficStreamsFile.exists()) {
                trafficStreamsFile.delete();
            }
            OutputStream out = new FileOutputStream(trafficStreamsFile, false);
            try {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
        resetCommentsDatabase();
        invalidate();
    }

    private void resetCommentsDatabase() {
        appDatabase.commentDao().delete(appDatabase.commentDao().all());
    }

    public AppDatabase getDatabase() {
        return appDatabase;
    }

    public Comment getCommentForTrafficStream(TrafficStream trafficStream) {
        return getDatabase().commentDao().get(String.valueOf(trafficStream.hashCode()));
    }

    public void createOrUpdateCommentForTrafficStream(TrafficStream trafficStream, Comment comment) {
        comment.setId(String.valueOf(trafficStream.hashCode()));
        getDatabase().commentDao().insert(comment);
    }

    public void addListener(DataManagerListener listener) {
        this.listeners.add(listener);
    }

    public boolean removeListener(DataManagerListener o) {
        return listeners.remove(o);
    }

    public interface DataManagerListener {
        void onDataChanged();
    }
}
