package de.tu_dresden.vlp.trafficinfrastructuremonitor.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.Comment;

@Database(entities = {Comment.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CommentDao commentDao();
}
