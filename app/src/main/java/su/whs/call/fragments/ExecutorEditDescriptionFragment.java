package su.whs.call.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import su.whs.call.R;
import su.whs.call.models.ExecutorSubcategory;
import su.whs.call.net.ConnectionHandler;
import su.whs.call.register.User;
import su.whs.call.utils.BackPressed;

public class ExecutorEditDescriptionFragment extends BaseSearchTabFragment implements BackPressed.OnBackPressedListener {

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

        final EditText etDescription = (EditText) view.findViewById(R.id.etDescription);
        Button buttonRedact = (Button) view.findViewById(R.id.buttonRedact);

        if (subcategory != null)
            etDescription.setText(subcategory.getDescription());


        buttonRedact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = User.create(getActivity());
                ConnectionHandler handler = ConnectionHandler.getInstance(getActivity());
                handler.queryRedactDescription(user.getToken(), subcategory.getId(), etDescription.getText().toString(), new ConnectionHandler.OnRedactDescription() {
                    @Override
                    public void onSuccess() {
                        subcategory.setPublished(false);
                        Toast.makeText(getActivity(), getString(R.string.service_will_be_published), Toast.LENGTH_LONG).show();
                        onBack();
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(getActivity(), "onFail()", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        mContentView = view;
        BackPressed.setOnBackPressedFragmentListener(this);
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

    @Override
    public boolean onHomeIconClick() {

        return onBack();
    }

    protected boolean onBack() {
        openFragment(ExecutorSubcategoriesFragment.getInstanceFromPool());
        return true;
    }


    @Override
    public void onBackPressed() {
        onBack();
    }
}
