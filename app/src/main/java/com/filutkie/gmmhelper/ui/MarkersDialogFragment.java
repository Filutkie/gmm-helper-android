package com.filutkie.gmmhelper.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.filutkie.gmmhelper.R;
import com.filutkie.gmmhelper.adapter.MarkerCursorAdapter;
import com.filutkie.gmmhelper.data.FeatureContract;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MarkersDialogFragment extends DialogFragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    @Bind(R.id.listview_markers)
    ListView listView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private CursorAdapter cursorAdapter;
    private SearchView mSearchView;
    private String mCurFilter;

    static MarkersDialogFragment newInstance() {
        return new MarkersDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_markers_list, container, false);
        ButterKnife.bind(this, rootView);
        cursorAdapter = new MarkerCursorAdapter(getActivity(), null, false);

        listView.setAdapter(cursorAdapter);

        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                });
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_close_white_24dp, null));
        toolbar.setTitle(R.string.action_marker_list);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        mSearchView = (SearchView) MenuItemCompat.getActionView(
                toolbar.getMenu().findItem(R.id.action_search));
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setIconifiedByDefault(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: remove hardcode
        int width = getResources().getDimensionPixelSize(R.dimen.dialog_window_width);
        int height = getResources().getDimensionPixelSize(R.dimen.dialog_window_height);
        Window window = getDialog().getWindow();
        window.setLayout(width, height);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(
                    FeatureContract.MarkerEntry.CONTENT_URI,
                    Uri.encode(mCurFilter));
        } else {
            baseUri = FeatureContract.MarkerEntry.CONTENT_URI;
        }
        return new CursorLoader(getActivity(),
                baseUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        if (mCurFilter == null && newFilter == null) {
            return true;
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true;
        }
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

}
