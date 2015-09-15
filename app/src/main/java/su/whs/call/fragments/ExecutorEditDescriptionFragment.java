package su.whs.call.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import su.whs.call.R;
import su.whs.call.models.ExecutorSubcategory;

public class ExecutorEditDescriptionFragment extends BaseSearchTabFragment {

    private static final String ARG_SUBCATEGORY = "subcategory";
    ExecutorSubcategory subcategory = null;


    public static ExecutorEditDescriptionFragment newInstance(ExecutorSubcategory subcategory) {
        ExecutorEditDescriptionFragment fragment = new ExecutorEditDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SUBCATEGORY, subcategory);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subcategory = (ExecutorSubcategory ) getArguments().getSerializable(ARG_SUBCATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_executor_edit_description, container, false);

        EditText etDescription = (EditText) view.findViewById(R.id.etDescription);
        Button buttonRedact = (Button) view.findViewById(R.id.buttonRedact);

        if (subcategory != null)
            etDescription.setText(subcategory.getDescription());

        mContentView = view;
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        setContentShown(true);
    }

    @Override
    public String getTitle() {
        return "Edit description";
    }
}
