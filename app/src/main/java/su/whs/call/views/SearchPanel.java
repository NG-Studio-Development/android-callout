package su.whs.call.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import su.whs.call.R;

public class SearchPanel extends RelativeLayout implements OnItemClickListener, OnClickListener {

    public static boolean isColorTitle = false;
    //public static boolean isVisibleView = false;



    private Button mFindBtn;
    private EditText mQuery;
    private ListView mResults;
    private ImageView imgDelete;
    private LinearLayout searchPanel;
    private boolean mResultFolds = false;

    private SearchPanelListener mListener = null;

    public ListView getSearchResultsList() {
        return mResults;
    }

    public interface SearchPanelListener {
        void onQueryChanged(String query);

        void onFindButtonClicked();

        void onSearchResultClicked(int position);
    }

    public SearchPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        isColorTitle = false;
        LayoutInflater.from(context).inflate(R.layout.search_panel, this, true);
        mFindBtn = (Button) findViewById(R.id.buttonSearch);
        imgDelete = (ImageView) findViewById(R.id.imgDelete);
        imgDelete.setOnClickListener(this);
        imgDelete.setVisibility(View.GONE);
        mQuery = (EditText) findViewById(R.id.editSearch);
        mResults = (ListView) findViewById(R.id.searchResults);
        mResults.setOnItemClickListener(this);

        searchPanel = (LinearLayout) findViewById(R.id.searchPanel);
        searchPanel.setOnClickListener(this);

        mQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                if(arg0.length() == 0) {
                    imgDelete.setVisibility(View.GONE);
                } else {
                    imgDelete.setVisibility(View.VISIBLE);
                }
                String query = arg0 == null ? "" : arg0.toString();
                if (mListener != null)
                    mListener.onQueryChanged(query);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        mQuery.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    unfoldResults();
                } else if (!hasFocus) {
                    foldResults();
                }
            }
        });
        mQuery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                unfoldResults();
            }
        });
        mFindBtn.setOnClickListener(this);
    }

    public void setQuery(String query) {
        mQuery.setText(query);
        mQuery.setSelection(mQuery.getText().length());
    }

    public void setListener(SearchPanelListener l) {
        if (mListener != null) {
            throw new IllegalStateException();
        }
        mListener = l;
    }

    public String getQuery() {
        return mQuery.getText().toString();
    }

    public void setFindButtonText(String text) {
        mFindBtn.setText(text);
    }

    public void setFindButtonText(int resid) {
        mFindBtn.setText(resid);
    }

    public void setAdapter(BaseAdapter adapter) {
        mResults.setAdapter(adapter);
        if (adapter.getCount() > 0)
             unfoldResults();
    }

    public ListAdapter getAdapter() {
        if (mResults != null)
            return mResults.getAdapter();
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null)
            mListener.onSearchResultClicked(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSearch:
                if (mListener != null) {
                    mListener.onFindButtonClicked();
                    isColorTitle = true;
                }
                break;
            case R.id.imgDelete:

                mQuery.setText("");
                mResults.setVisibility(View.GONE);
                break;
            case R.id.searchPanel:
                Log.d("TAG", "CharSequence = ");
                break;
        }
    }

    public void foldResults() {
        mResults.setVisibility(View.GONE);
        mResultFolds = true;
    }

    private void unfoldResults() {
        mResults.setVisibility(View.VISIBLE);
        mResultFolds = false;
    }
}
