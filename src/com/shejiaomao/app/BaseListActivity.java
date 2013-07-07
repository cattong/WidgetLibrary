package com.shejiaomao.app;

import com.shejiaomao.widget.XListView;
import com.shejiaomao.widget.XListView.IXListViewListener;
import com.shejiaomao.widget.XListViewFooter;

public abstract class BaseListActivity extends BaseActivity {
    public static final int STATE_LOADING = 1;
    public static final int STATE_LOAD_MORE = 2;
    public static final int STATE_NO_MORE = 3;
    
    protected abstract XListView getListView();
	
	protected abstract void startRefreshTask();
	
	protected abstract void startLoadMoreTask();
	
	public void stopRefresh() {
		XListView listView = getListView();
		if (listView != null) {
			listView.stopRefresh();
		}
	}
	
	public void stopLoadMore(int footerState) {
		XListView listView = getListView();
		if (listView == null) {
            return;
		}
		
		if (footerState == STATE_LOAD_MORE) {
		    listView.stopLoadMore(XListViewFooter.STATE_NORMAL);
		} else if (footerState == STATE_NO_MORE) {
			listView.stopLoadMore(XListViewFooter.STATE_END);
		}
	}
	
	@Override
	protected void initComponents() {
		final XListView listView = getListView();
		if (listView == null) {
			return;
		}
		
		listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				startRefreshTask();
			}
			
			@Override
			public void onLoadMore() {
				startLoadMoreTask();				
			}			
		});
	}
	
}
