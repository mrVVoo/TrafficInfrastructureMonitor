package de.tu_dresden.vlp.trafficinfrastructuremonitor.database;

import android.arch.persistence.room.*;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.Comment;

import java.util.List;

/**
 * Dao Class required for Android {@link Room}
 */
@Dao
public interface CommentDao {
    @Query("SELECT * FROM comment")
    List<Comment> all();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Comment... comment);

    @Delete
    void delete(Comment... comment);

    @Delete
    void delete(List<Comment> all);

    @Query("SELECT * FROM comment WHERE id == :trafficStreamId LIMIT 1")
    Comment get(String trafficStreamId);
}
