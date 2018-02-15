package de.tu_dresden.vlp.trafficinfrastructuremonitor.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.google.common.base.MoreObjects;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.R;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.backend.DataManager;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.Comment;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStream;

/**
 * Fragment class to manage the editing window of a {@link TrafficStream}.
 *
 * @author Markus Wutzler
 */
public class TrafficStreamInfoFragment extends Fragment {
    public static final String TAG = TrafficStreamInfoFragment.class.getName();
    private TrafficStream myTrafficStream;
    private View fragmentRootView;
    private TextView trafficStreamIdLabel;
    private EditText commentField;
    private DataManager dataManager;
    private Comment currentComment;

    public TrafficStreamInfoFragment() {
    }

    public static TrafficStreamInfoFragment newInstance(TrafficStream trafficStream) {
        TrafficStreamInfoFragment fragment = new TrafficStreamInfoFragment();
        fragment.myTrafficStream = trafficStream;
        return fragment;
    }

    public TrafficStream getTrafficStream() {
        return myTrafficStream;
    }

    public void setTrafficStream(TrafficStream myTrafficStream) {
        this.myTrafficStream = myTrafficStream;
        updateUI();
    }

    private void updateUI() {
        if (fragmentRootView != null) {
            if (trafficStreamIdLabel != null)
                trafficStreamIdLabel.setText(myTrafficStream.getId());
            if (dataManager != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        currentComment = dataManager.getCommentForTrafficStream(getTrafficStream());
                        TrafficStreamInfoFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (currentComment != null) {
                                    commentField.setText(currentComment.getText());
                                } else {
                                    commentField.setText(null);
                                }
                                fragmentRootView.requestFocus();
                            }
                        });
                    }
                }).start();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() instanceof MainActivity) {
            dataManager = ((MainActivity) getActivity()).getDataManager();
        }
        fragmentRootView = inflater.inflate(R.layout.fragment_traffic_stream_info, container, false);
        trafficStreamIdLabel = fragmentRootView.findViewById(R.id.tsiw_traffic_stream_id_label);
        trafficStreamIdLabel.setText(myTrafficStream.getId());
        commentField = fragmentRootView.findViewById(R.id.tsiw_comments);
        SaveButtonClickListener saveButtonClickListener = new SaveButtonClickListener();
        fragmentRootView.findViewById(R.id.tsiw_save_btn).setOnClickListener(saveButtonClickListener);
        CancelButtonClickListener cancelButtonClickListener = new CancelButtonClickListener();
        fragmentRootView.findViewById(R.id.tsiw_cancel_btn).setOnClickListener(cancelButtonClickListener);

        updateUI();

        return fragmentRootView;
    }

    private class SaveButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (currentComment == null)
                currentComment = new Comment();
            String dbText = MoreObjects.firstNonNull(currentComment.getText(), "");
            String viewText = MoreObjects.firstNonNull(commentField.getText().toString(), "");
            if (dbText.equals(viewText)) return;

            // use hashCode as Id due to non-unique traffic stream ids
            currentComment.setId(String.valueOf(myTrafficStream.hashCode()));
            currentComment.setText(commentField.getText().toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dataManager.createOrUpdateCommentForTrafficStream(myTrafficStream, currentComment);
                }
            }).start();
            fragmentRootView.requestFocus();
        }
    }

    private class CancelButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            commentField.setText(currentComment == null ? "" : currentComment.getText());
            fragmentRootView.requestFocus();
        }
    }
}
