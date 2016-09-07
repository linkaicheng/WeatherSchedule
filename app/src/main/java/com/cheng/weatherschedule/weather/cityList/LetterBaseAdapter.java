package com.cheng.weatherschedule.weather.cityList;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

public class LetterBaseAdapter extends BaseAdapter implements WrapperListAdapter{
	protected ListAdapter listAdapter;
	public LetterBaseAdapter(ListAdapter listAdapter){
		this.listAdapter = listAdapter;
	}
	@Override
	public ListAdapter getWrappedAdapter() {
		return listAdapter;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return listAdapter.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int i) {
		return listAdapter.isEnabled(i);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver dataSetObserver) {
		listAdapter.registerDataSetObserver(dataSetObserver);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
		listAdapter.unregisterDataSetObserver(dataSetObserver);
	}

	@Override
	public int getCount() {
		return listAdapter.getCount();
	}

	@Override
	public Object getItem(int i) {
		return listAdapter.getItem(i);
	}

	@Override
	public long getItemId(int i) {
		return listAdapter.getItemId(i);
	}

	@Override
	public boolean hasStableIds() {
		return listAdapter.hasStableIds();
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		return listAdapter.getView(position, view, viewGroup);
	}

	@Override
	public int getItemViewType(int i) {
		return listAdapter.getItemViewType(i);
	}

	@Override
	public int getViewTypeCount() {
		return listAdapter.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return listAdapter.isEmpty();
	}

	
}
