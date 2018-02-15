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

/**
 * The {@link DataManager} is responsible for managing both the {@link TrafficStream}s and the corresponding {@link Comment}s.
 *
 * @author Markus Wutzler
 */
public class DataManager {

    /**
     * target xml file name
     */
    private static final String TRAFFIC_STREAMS_FILE_NAME = "traffic-streams.xml";
    /**
     * target file object
     */
    private final File trafficStreamsFile;
    /**
     * Comment Database
     *
     * @see AppDatabase
     * @see Room
     */
    private final AppDatabase appDatabase;
    /**
     * in-memory list of {@link TrafficStream}s
     */
    private List<TrafficStream> myTrafficStreams = new LinkedList<>();
    /**
     * set of {@link DataManagerListener}s which respond to changes in the data sets.
     */
    private Set<DataManagerListener> listeners = new HashSet<>();

    /**
     * Initializes the data manager. Should actually be a Singleton.
     *
     * @param applicationContext required to retrieve storage
     */
    public DataManager(Context applicationContext) {
        trafficStreamsFile = new File(applicationContext.getFilesDir(), DataManager.TRAFFIC_STREAMS_FILE_NAME);
        appDatabase = Room.databaseBuilder(applicationContext, AppDatabase.class, "tim-db").build();
    }

    /**
     * Returns the current list of {@link TrafficStream}s, which are loaded/parsed upon first request.
     *
     * @return List of {@link TrafficStream}s
     */
    public List<TrafficStream> getTrafficStreams() {
        if (this.myTrafficStreams == null || this.myTrafficStreams.isEmpty()) {
            parseTrafficStreams();
        }
        return myTrafficStreams;
    }

    /**
     * Private method to handle loading/parsing the {@link TrafficStream}s from the trafficStreamsFile.
     */
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

    /**
     * re-reads the {@link TrafficStream}s and notifies the listeners.
     */
    public void invalidate() {
        myTrafficStreams.clear();
        parseTrafficStreams();
        for (DataManagerListener listener : listeners) {
            listener.onDataChanged();
        }
    }

    /**
     * Reads the {@param file} and copies the content to the local target file.
     * Additionally, the database is truncated and the list of traffic streams in invalidated.
     *
     * @param file original file
     *
     * @throws IOException if file doesn't exist.
     */
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
