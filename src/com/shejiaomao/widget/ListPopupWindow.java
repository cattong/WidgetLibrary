package com.shejiaomao.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

public class ListPopupWindow {
    private static final String TAG = "ListPopupWindow";
    
    private Context mContext;
    private PopupWindow mPopup;
    private ListAdapter mAdapter;
    private DropDownListView mDropDownList;

    private int mDropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mDropDownWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mDropDownHorizontalOffset;
    private int mDropDownVerticalOffset;
    private boolean mDropDownVerticalOffsetSet;

    private boolean mDropDownAlwaysVisible = false;
    private boolean mForceIgnoreOutsideTouch = false;
    int mListItemExpandMaximum = Integer.MAX_VALUE;

    private int mPromptPosition = POSITION_PROMPT_ABOVE;

    private DataSetObserver mObserver;

    private View mDropDownAnchorView;

    private Drawable mDropDownListHighlight;

    private AdapterView.OnItemClickListener mItemClickListener;
    private AdapterView.OnItemSelectedListener mItemSelectedListener;

    private Runnable mShowDropDownRunnable;

    private Handler mHandler = new Handler();

    private Rect mTempRect = new Rect();

    private boolean mModal;

    /**
     * The provided prompt view should appear above list content.
     * 
     * @see #setPromptPosition(int)
     * @see #getPromptPosition()
     * @see #setPromptView(View)
     */
    public static final int POSITION_PROMPT_ABOVE = 0;

    /**
     * The provided prompt view should appear below list content.
     * 
     * @see #setPromptPosition(int)
     * @see #getPromptPosition()
     * @see #setPromptView(View)
     */
    public static final int POSITION_PROMPT_BELOW = 1;

    /**
     * Alias for {@link ViewGroup.LayoutParams#MATCH_PARENT}.
     * If used to specify a popup width, the popup will match the width of the anchor view.
     * If used to specify a popup height, the popup will fill available space.
     */
    public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    
    /**
     * Alias for {@link ViewGroup.LayoutParams#WRAP_CONTENT}.
     * If used to specify a popup width, the popup will use the width of its content.
     */
    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    
    /**
     * Mode for {@link #setInputMethodMode(int)}: the requirements for the
     * input method should be based on the focusability of the popup.  That is
     * if it is focusable than it needs to work with the input method, else
     * it doesn't.
     */
    public static final int INPUT_METHOD_FROM_FOCUSABLE = PopupWindow.INPUT_METHOD_FROM_FOCUSABLE;
    
    /**
     * Mode for {@link #setInputMethodMode(int)}: this popup always needs to
     * work with an input method, regardless of whether it is focusable.  This
     * means that it will always be displayed so that the user can also operate
     * the input method while it is shown.
     */
    public static final int INPUT_METHOD_NEEDED = PopupWindow.INPUT_METHOD_NEEDED;
    
    /**
     * Mode for {@link #setInputMethodMode(int)}: this popup never needs to
     * work with an input method, regardless of whether it is focusable.  This
     * means that it will always be displayed to use as much space on the
     * screen as needed, regardless of whether this covers the input method.
     */
    public static final int INPUT_METHOD_NOT_NEEDED = PopupWindow.INPUT_METHOD_NOT_NEEDED;

    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter.
     * Backgrounds should be set using {@link #setBackgroundDrawable(Drawable)}.
     * 
     * @param context Context used for contained views.
     */
    public ListPopupWindow(Context context) {
        this(context, null, 0, 0);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter.
     * Backgrounds should be set using {@link #setBackgroundDrawable(Drawable)}.
     * 
     * @param context Context used for contained views.
     * @param attrs Attributes from inflating parent views used to style the popup.
     */
    public ListPopupWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter.
     * Backgrounds should be set using {@link #setBackgroundDrawable(Drawable)}.
     * 
     * @param context Context used for contained views.
     * @param attrs Attributes from inflating parent views used to style the popup.
     * @param defStyleAttr Default style attribute to use for popup content.
     */
    public ListPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter.
     * Backgrounds should be set using {@link #setBackgroundDrawable(Drawable)}.
     * 
     * @param context Context used for contained views.
     * @param attrs Attributes from inflating parent views used to style the popup.
     * @param defStyleAttr Style attribute to read for default styling of popup content.
     * @param defStyleRes Style resource ID to use for default styling of popup content.
     */
    public ListPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContext = context;
        mPopup = new PopupWindow(context);
        mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
    }

    /**
     * Sets the adapter that provides the data and the views to represent the data
     * in this popup window.
     *
     * @param adapter The adapter to use to create this window's content.
     */
    public void setAdapter(ListAdapter adapter) {
        if (mObserver == null) {
            mObserver = new PopupDataSetObserver();
        } else if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            adapter.registerDataSetObserver(mObserver);
        }
        
        if (mDropDownList != null) {
            mDropDownList.setAdapter(mAdapter);
        }
    }

    /**
     * Set where the optional prompt view should appear. The default is
     * {@link #POSITION_PROMPT_ABOVE}.
     * 
     * @param position A position constant declaring where the prompt should be displayed.
     * 
     * @see #POSITION_PROMPT_ABOVE
     * @see #POSITION_PROMPT_BELOW
     */
    public void setPromptPosition(int position) {
        mPromptPosition = position;
    }

    /**
     * @return Where the optional prompt view should appear.
     * 
     * @see #POSITION_PROMPT_ABOVE
     * @see #POSITION_PROMPT_BELOW
     */
    public int getPromptPosition() {
        return mPromptPosition;
    }

    /**
     * Set whether this window should be modal when shown.
     * 
     * <p>If a popup window is modal, it will receive all touch and key input.
     * If the user touches outside the popup window's content area the popup window
     * will be dismissed.
     * 
     * @param modal {@code true} if the popup window should be modal, {@code false} otherwise.
     */
    public void setModal(boolean modal) {
        mModal = true;
        mPopup.setFocusable(modal);
    }

    /**
     * Returns whether the popup window will be modal when shown.
     * 
     * @return {@code true} if the popup window will be modal, {@code false} otherwise.
     */
    public boolean isModal() {
        return mModal;
    }

    /**
     * Forces outside touches to be ignored. Normally if {@link #isDropDownAlwaysVisible()} is
     * false, we allow outside touch to dismiss the dropdown. If this is set to true, then we
     * ignore outside touch even when the drop down is not set to always visible.
     * 
     * @hide Used only by AutoCompleteTextView to handle some internal special cases.
     */
    public void setForceIgnoreOutsideTouch(boolean forceIgnoreOutsideTouch) {
        mForceIgnoreOutsideTouch = forceIgnoreOutsideTouch;
    }

    /**
     * Sets a drawable to use as the list item selector.
     * 
     * @param selector List selector drawable to use in the popup.
     */
    public void setListSelector(Drawable selector) {
        mDropDownListHighlight = selector;
    }

    /**
     * @return The background drawable for the popup window.
     */
    public Drawable getBackground() {
        return mPopup.getBackground();
    }

    /**
     * Sets a drawable to be the background for the popup window.
     * 
     * @param d A drawable to set as the background.
     */
    public void setBackgroundDrawable(Drawable d) {
        mPopup.setBackgroundDrawable(d);
    }

    /**
     * Set an animation style to use when the popup window is shown or dismissed.
     * 
     * @param animationStyle Animation style to use.
     */
    public void setAnimationStyle(int animationStyle) {
        mPopup.setAnimationStyle(animationStyle);
    }

    /**
     * Returns the animation style that will be used when the popup window is
     * shown or dismissed.
     * 
     * @return Animation style that will be used.
     */
    public int getAnimationStyle() {
        return mPopup.getAnimationStyle();
    }

    /**
     * Returns the view that will be used to anchor this popup.
     * 
     * @return The popup's anchor view
     */
    public View getAnchorView() {
        return mDropDownAnchorView;
    }

    /**
     * Sets the popup's anchor view. This popup will always be positioned relative to
     * the anchor view when shown.
     * 
     * @param anchor The view to use as an anchor.
     */
    public void setAnchorView(View anchor) {
        mDropDownAnchorView = anchor;
    }

    /**
     * @return The horizontal offset of the popup from its anchor in pixels.
     */
    public int getHorizontalOffset() {
        return mDropDownHorizontalOffset;
    }

    /**
     * Set the horizontal offset of this popup from its anchor view in pixels.
     * 
     * @param offset The horizontal offset of the popup from its anchor.
     */
    public void setHorizontalOffset(int offset) {
        mDropDownHorizontalOffset = offset;
    }

    /**
     * @return The vertical offset of the popup from its anchor in pixels.
     */
    public int getVerticalOffset() {
        if (!mDropDownVerticalOffsetSet) {
            return 0;
        }
        return mDropDownVerticalOffset;
    }

    /**
     * Set the vertical offset of this popup from its anchor view in pixels.
     * 
     * @param offset The vertical offset of the popup from its anchor.
     */
    public void setVerticalOffset(int offset) {
        mDropDownVerticalOffset = offset;
        mDropDownVerticalOffsetSet = true;
    }

    /**
     * @return The width of the popup window in pixels.
     */
    public int getWidth() {
        return mDropDownWidth;
    }

    /**
     * Sets the width of the popup window in pixels. Can also be {@link #MATCH_PARENT}
     * or {@link #WRAP_CONTENT}.
     * 
     * @param width Width of the popup window.
     */
    public void setWidth(int width) {
        mDropDownWidth = width;
    }

    /**
     * Sets the width of the popup window by the size of its content. The final width may be
     * larger to accommodate styled window dressing.
     *
     * @param width Desired width of content in pixels.
     */
    public void setContentWidth(int width) {
        Drawable popupBackground = mPopup.getBackground();
        if (popupBackground != null) {
            popupBackground.getPadding(mTempRect);
            mDropDownWidth = mTempRect.left + mTempRect.right + width;
        } else {
            setWidth(width);
        }
    }

    /**
     * @return The height of the popup window in pixels.
     */
    public int getHeight() {
        return mDropDownHeight;
    }

    /**
     * Sets the height of the popup window in pixels. Can also be {@link #MATCH_PARENT}.
     * 
     * @param height Height of the popup window.
     */
    public void setHeight(int height) {
        mDropDownHeight = height;
    }

    /**
     * Sets a listener to receive events when a list item is clicked.
     * 
     * @param clickListener Listener to register
     * 
     * @see ListView#setOnItemClickListener(android.widget.AdapterView.OnItemClickListener)
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener clickListener) {
        mItemClickListener = clickListener;
    }

    /**
     * Sets a listener to receive events when a list item is selected.
     * 
     * @param selectedListener Listener to register.
     * 
     * @see ListView#setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)
     */
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener selectedListener) {
        mItemSelectedListener = selectedListener;
    }


    /**
     * Post a {@link #show()} call to the UI thread.
     */
    public void postShow() {
        mHandler.post(mShowDropDownRunnable);
    }

    /**
     * Show the popup list. If the list is already showing, this method
     * will recalculate the popup's size and position.
     */
    public void show() {
        int height = buildDropDown();

        int widthSpec = 0;
        int heightSpec = 0;

        if (mPopup.isShowing()) {
            if (mDropDownWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                // The call to PopupWindow's update method below can accept -1 for any
                // value you do not want to update.
                widthSpec = -1;
            } else if (mDropDownWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
                widthSpec = getAnchorView().getWidth();
            } else {
                widthSpec = mDropDownWidth;
            }

            if (mDropDownHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                // The call to PopupWindow's update method below can accept -1 for any
                // value you do not want to update.
                heightSpec = height;
               
                    mPopup.setWindowLayoutMode(
                            mDropDownWidth == ViewGroup.LayoutParams.MATCH_PARENT ?
                                    ViewGroup.LayoutParams.MATCH_PARENT : 0,
                            ViewGroup.LayoutParams.MATCH_PARENT);
           
            } else if (mDropDownHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                heightSpec = height;
            } else {
                heightSpec = mDropDownHeight;
            }

            mPopup.setOutsideTouchable(!mForceIgnoreOutsideTouch && !mDropDownAlwaysVisible);

            mPopup.update(getAnchorView(), mDropDownHorizontalOffset,
                    mDropDownVerticalOffset, widthSpec, heightSpec);
        } else {
            if (mDropDownWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                widthSpec = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                if (mDropDownWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    mPopup.setWidth(getAnchorView().getWidth());
                } else {
                    mPopup.setWidth(mDropDownWidth);
                }
            }

            if (mDropDownHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                heightSpec = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                if (mDropDownHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    mPopup.setHeight(height);
                } else {
                    mPopup.setHeight(mDropDownHeight);
                }
            }

            mPopup.setWindowLayoutMode(widthSpec, heightSpec);
            //mPopup.setClipToScreenEnabled(true);
            
            // use outside touchable to dismiss drop down when touching outside of it, so
            // only set this if the dropdown is not always visible
            mPopup.setOutsideTouchable(!mForceIgnoreOutsideTouch && !mDropDownAlwaysVisible);
          
            mPopup.showAsDropDown(getAnchorView(),
                    mDropDownHorizontalOffset, mDropDownVerticalOffset);
            mDropDownList.setSelection(ListView.INVALID_POSITION);
            
            if (!mModal || mDropDownList.isInTouchMode()) {
                clearListSelection();
            }
            
        }
    }

    /**
     * Dismiss the popup window.
     */
    public void dismiss() {
        mPopup.dismiss();
        mPopup.setContentView(null);
        mDropDownList = null;
    }

    /**
     * Set a listener to receive a callback when the popup is dismissed.
     *
     * @param listener Listener that will be notified when the popup is dismissed.
     */
    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mPopup.setOnDismissListener(listener);
    }

    /**
     * Control how the popup operates with an input method: one of
     * {@link #INPUT_METHOD_FROM_FOCUSABLE}, {@link #INPUT_METHOD_NEEDED},
     * or {@link #INPUT_METHOD_NOT_NEEDED}.
     * 
     * <p>If the popup is showing, calling this method will take effect only
     * the next time the popup is shown or through a manual call to the {@link #show()}
     * method.</p>
     * 
     * @see #getInputMethodMode()
     * @see #show()
     */
    public void setInputMethodMode(int mode) {
        mPopup.setInputMethodMode(mode);
    }

    /**
     * Return the current value in {@link #setInputMethodMode(int)}.
     * 
     * @see #setInputMethodMode(int)
     */
    public int getInputMethodMode() {
        return mPopup.getInputMethodMode();
    }

    /**
     * Set the selected position of the list.
     * Only valid when {@link #isShowing()} == {@code true}.
     * 
     * @param position List position to set as selected.
     */
    public void setSelection(int position) {
        DropDownListView list = mDropDownList;
        if (isShowing() && list != null) {
            list.mListSelectionHidden = false;
            list.setSelection(position);
            if (list.getChoiceMode() != ListView.CHOICE_MODE_NONE) {
                list.setItemChecked(position, true);
            }
        }
    }

    /**
     * Clear any current list selection.
     * Only valid when {@link #isShowing()} == {@code true}.
     */
    public void clearListSelection() {
        final DropDownListView list = mDropDownList;
        if (list != null) {
            // WARNING: Please read the comment where mListSelectionHidden is declared
            list.mListSelectionHidden = true;
            //list.hideSelector();
            list.requestLayout();
        }
    }

    /**
     * @return {@code true} if the popup is currently showing, {@code false} otherwise.
     */
    public boolean isShowing() {
        return mPopup.isShowing();
    }

    /**
     * Perform an item click operation on the specified list adapter position.
     * 
     * @param position Adapter position for performing the click
     * @return true if the click action could be performed, false if not.
     *         (e.g. if the popup was not showing, this method would return false.)
     */
    public boolean performItemClick(int position) {
        if (isShowing()) {
            if (mItemClickListener != null) {
                final DropDownListView list = mDropDownList;
                final View child = list.getChildAt(position - list.getFirstVisiblePosition());
                final ListAdapter adapter = list.getAdapter();
                mItemClickListener.onItemClick(list, child, position, adapter.getItemId(position));
            }
            return true;
        }
        return false;
    }

    /**
     * @return The currently selected item or null if the popup is not showing.
     */
    public Object getSelectedItem() {
        if (!isShowing()) {
            return null;
        }
        return mDropDownList.getSelectedItem();
    }

    /**
     * @return The position of the currently selected item or {@link ListView#INVALID_POSITION}
     * if {@link #isShowing()} == {@code false}.
     * 
     * @see ListView#getSelectedItemPosition()
     */
    public int getSelectedItemPosition() {
        if (!isShowing()) {
            return ListView.INVALID_POSITION;
        }
        return mDropDownList.getSelectedItemPosition();
    }

    /**
     * @return The ID of the currently selected item or {@link ListView#INVALID_ROW_ID}
     * if {@link #isShowing()} == {@code false}.
     * 
     * @see ListView#getSelectedItemId()
     */
    public long getSelectedItemId() {
        if (!isShowing()) {
            return ListView.INVALID_ROW_ID;
        }
        return mDropDownList.getSelectedItemId();
    }

    /**
     * @return The View for the currently selected item or null if
     * {@link #isShowing()} == {@code false}.
     * 
     * @see ListView#getSelectedView()
     */
    public View getSelectedView() {
        if (!isShowing()) {
            return null;
        }
        return mDropDownList.getSelectedView();
    }

    /**
     * @return The {@link ListView} displayed within the popup window.
     * Only valid when {@link #isShowing()} == {@code true}.
     */
    public ListView getListView() {
        return mDropDownList;
    }

    /**
     * The maximum number of list items that can be visible and still have
     * the list expand when touched.
     *
     * @param max Max number of items that can be visible and still allow the list to expand.
     */
    void setListItemExpandMax(int max) {
        mListItemExpandMaximum = max;
    }

    /**
     * <p>Builds the popup window's content and returns the height the popup
     * should have. Returns -1 when the content already exists.</p>
     *
     * @return the content's height or -1 if content already exists
     */
    private int buildDropDown() {
        ViewGroup dropDownView;
        int otherHeights = 0;

        if (mDropDownList == null) {
            Context context = mContext;

            /**
             * This Runnable exists for the sole purpose of checking if the view layout has got
             * completed and if so call showDropDown to display the drop down. This is used to show
             * the drop down as soon as possible after user opens up the search dialog, without
             * waiting for the normal UI pipeline to do it's job which is slower than this method.
             */
            mShowDropDownRunnable = new Runnable() {
                public void run() {
                    // View layout should be all done before displaying the drop down.
                    View view = getAnchorView();
                    if (view != null && view.getWindowToken() != null) {
                        show();
                    }
                }
            };

            mDropDownList = new DropDownListView(context, !mModal);
            if (mDropDownListHighlight != null) {
                mDropDownList.setSelector(mDropDownListHighlight);
            }
            mDropDownList.setAdapter(mAdapter);
            mDropDownList.setOnItemClickListener(mItemClickListener);
            mDropDownList.setFocusable(true);
            mDropDownList.setFocusableInTouchMode(true);
            mDropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view,
                        int position, long id) {

                    if (position != -1) {
                        DropDownListView dropDownList = mDropDownList;

                        if (dropDownList != null) {
                            dropDownList.mListSelectionHidden = false;
                        }
                    }
                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
          

            if (mItemSelectedListener != null) {
                mDropDownList.setOnItemSelectedListener(mItemSelectedListener);
            }

            dropDownView = mDropDownList;
            mPopup.setContentView(dropDownView);
        } else {
            dropDownView = (ViewGroup) mPopup.getContentView();
        }

        // getMaxAvailableHeight() subtracts the padding, so we put it back
        // to get the available height for the whole window
        int padding = 0;
        Drawable background = mPopup.getBackground();
        if (background != null) {
            background.getPadding(mTempRect);
            padding = mTempRect.top + mTempRect.bottom;

            // If we don't have an explicit vertical offset, determine one from the window
            // background so that content will line up.
            if (!mDropDownVerticalOffsetSet) {
                mDropDownVerticalOffset = -mTempRect.top;
            }
        }

        final int maxHeight = mPopup.getMaxAvailableHeight(
                getAnchorView(), mDropDownVerticalOffset);

        if (mDropDownAlwaysVisible || mDropDownHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            return maxHeight + padding;
        }

       

        return otherHeights;
    }

    /**
     * <p>Wrapper class for a ListView. This wrapper can hijack the focus to
     * make sure the list uses the appropriate drawables and states when
     * displayed on screen within a drop down. The focus is never actually
     * passed to the drop down in this mode; the list only looks focused.</p>
     */
    private static class DropDownListView extends ListView {
        private static final String TAG = ListPopupWindow.TAG + ".DropDownListView";
        /*
         * WARNING: This is a workaround for a touch mode issue.
         *
         * Touch mode is propagated lazily to windows. This causes problems in
         * the following scenario:
         * - Type something in the AutoCompleteTextView and get some results
         * - Move down with the d-pad to select an item in the list
         * - Move up with the d-pad until the selection disappears
         * - Type more text in the AutoCompleteTextView *using the soft keyboard*
         *   and get new results; you are now in touch mode
         * - The selection comes back on the first item in the list, even though
         *   the list is supposed to be in touch mode
         *
         * Using the soft keyboard triggers the touch mode change but that change
         * is propagated to our window only after the first list layout, therefore
         * after the list attempts to resurrect the selection.
         *
         * The trick to work around this issue is to pretend the list is in touch
         * mode when we know that the selection should not appear, that is when
         * we know the user moved the selection away from the list.
         *
         * This boolean is set to true whenever we explicitly hide the list's
         * selection and reset to false whenever we know the user moved the
         * selection back to the list.
         *
         * When this boolean is true, isInTouchMode() returns true, otherwise it
         * returns super.isInTouchMode().
         */
        private boolean mListSelectionHidden;
        
        /**
         * True if this wrapper should fake focus.
         */
        private boolean mHijackFocus;

        /**
         * <p>Creates a new list view wrapper.</p>
         *
         * @param context this view's context
         */
        public DropDownListView(Context context, boolean hijackFocus) {
            super(context, null, 0);
            mHijackFocus = hijackFocus;
            // TODO: Add an API to control this
            setCacheColorHint(0); // Transparent, since the background drawable could be anything.
        }

        @Override
        public boolean isInTouchMode() {
            // WARNING: Please read the comment where mListSelectionHidden is declared
            return (mHijackFocus && mListSelectionHidden) || super.isInTouchMode();
        }

        /**
         * <p>Returns the focus state in the drop down.</p>
         *
         * @return true always if hijacking focus
         */
        @Override
        public boolean hasWindowFocus() {
            return mHijackFocus || super.hasWindowFocus();
        }

        /**
         * <p>Returns the focus state in the drop down.</p>
         *
         * @return true always if hijacking focus
         */
        @Override
        public boolean isFocused() {
            return mHijackFocus || super.isFocused();
        }

        /**
         * <p>Returns the focus state in the drop down.</p>
         *
         * @return true always if hijacking focus
         */
        @Override
        public boolean hasFocus() {
            return mHijackFocus || super.hasFocus();
        }
    }

    private class PopupDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            if (isShowing()) {
                // Resize the popup to fit new content
                show();
            }
        }
        
        @Override
        public void onInvalidated() {
            dismiss();
        }
    }
}
