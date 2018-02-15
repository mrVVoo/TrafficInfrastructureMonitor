package de.tu_dresden.vlp.trafficinfrastructuremonitor.backend;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.R;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.database.AppDatabase;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.Comment;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStream;

/**
 * {@link AsyncTask} which is used to create a summary of the comments.
 */
public class ExportGenerator extends AsyncTask<TrafficStream, Integer, String> {

    private final Context context;
    private final AppDatabase database;

    public ExportGenerator(Context context, AppDatabase database) {
        this.context = context;
        this.database = database;
    }

    @Override
    protected String doInBackground(TrafficStream... trafficStreams) {
        StringBuilder sb = new StringBuilder("Export von Traffic Streams mit Kommentaren").append("\n\n");
        int count = 0, items=0, total = trafficStreams.length;
        for (TrafficStream stream : trafficStreams) {
            count++;
            // use hashCode as Id due to non-unique traffic stream ids
            Comment comment = database.commentDao().get(String.valueOf(stream.hashCode()));
            if (comment == null || comment.getText() == null || comment.getText().isEmpty()) continue;
            items++;
            for (int i = 0; i < 25; i++) {
                sb.append("#");
            }
            sb.append("\n").append(stream).append("\n");
            for (int i = 0; i < 25; i++) {
                sb.append("-");
            }
            sb.append("\n").append("Kommentar:").append("\n").append(comment.getText()).append("\n");
            sb.append("\n").append(stream).append("\n");
            for (int i = 0; i < 25; i++) {
                sb.append("=");
            }
            sb.append("\n");
            publishProgress(count, total);
        }
        if (items==0)
            return null;
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        if (s == null) {
            Toast.makeText(context,context.getString(R.string.no_comments_found),Toast.LENGTH_LONG).show();
        } else {
            // TODO: ask user to choose between file and plain text or other export options.
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, s);
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.export_comments)));
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Toast.makeText(context, values[0] + " von " + values[1] + " geladen", Toast.LENGTH_SHORT).show();
    }
}
