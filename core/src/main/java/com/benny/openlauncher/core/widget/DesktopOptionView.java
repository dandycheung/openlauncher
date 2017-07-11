package com.benny.openlauncher.core.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.benny.openlauncher.core.R;
import com.benny.openlauncher.core.interfaces.FastItem;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.IconLabelItem;
import com.benny.openlauncher.core.util.Tool;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class DesktopOptionView extends FrameLayout {

    private RecyclerView[] actionRecyclerViews = new RecyclerView[2];
    private FastItemAdapter<FastItem.DesktopOptionsItem>[] actionAdapters = new FastItemAdapter[2];
    private DesktopOptionViewListener desktopOptionViewListener;

    public DesktopOptionView(@NonNull Context context) {
        super(context);
        init();
    }

    public DesktopOptionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DesktopOptionView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setDesktopOptionViewListener(DesktopOptionViewListener desktopOptionViewListener) {
        this.desktopOptionViewListener = desktopOptionViewListener;
    }

    public void updateHomeIcon(boolean home) {
        if (home) {
            actionAdapters[0].getAdapterItem(1).setIcon(R.drawable.ic_star_white_36dp);
        } else {
            actionAdapters[0].getAdapterItem(1).setIcon(R.drawable.ic_star_border_white_36dp);
        }
        actionAdapters[0].notifyAdapterItemChanged(1);
    }

    public void updateLockIcon(boolean lock) {
        if (lock) {
            actionAdapters[0].getAdapterItem(2).setIcon(R.drawable.ic_lock_white_36dp);
        } else {
            actionAdapters[0].getAdapterItem(2).setIcon(R.drawable.ic_lock_open_white_36dp);
        }
        actionAdapters[0].notifyAdapterItemChanged(2);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
            return insets;
        }
        return insets;
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "RobotoCondensed-Regular.ttf");

        actionAdapters[0] = new FastItemAdapter<>();
        actionAdapters[1] = new FastItemAdapter<>();

        actionRecyclerViews[0] = createRecyclerView(actionAdapters[0], Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        actionRecyclerViews[1] = createRecyclerView(actionAdapters[1], Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        FastAdapter.OnClickListener<FastItem.DesktopOptionsItem> clickListener = new FastAdapter.OnClickListener<FastItem.DesktopOptionsItem>() {
            @Override
            public boolean onClick(View v, IAdapter<FastItem.DesktopOptionsItem> adapter, FastItem.DesktopOptionsItem item, int position) {
                if (desktopOptionViewListener != null) {
                    final int id = (int) item.getIdentifier();
                    if (id == R.string.home) {
                        updateHomeIcon(true);
                        desktopOptionViewListener.onSetPageAsHome();
                    } else if (id == R.string.remove) {
                        if (!Setup.appSettings().isDesktopLock()) {
                            desktopOptionViewListener.onRemovePage();
                        } else {
                            Tool.toast(getContext(), "Desktop is locked.");
                        }
                    } else if (id == R.string.widget) {
                        if (!Setup.appSettings().isDesktopLock()) {
                            desktopOptionViewListener.onPickWidget();
                        } else {
                            Tool.toast(getContext(), "Desktop is locked.");
                        }
                    } else if (id == R.string.action) {
                        if (!Setup.appSettings().isDesktopLock()) {
                            desktopOptionViewListener.onPickDesktopAction();
                        } else {
                            Tool.toast(getContext(), "Desktop is locked.");
                        }
                    } else if (id == R.string.lock) {
                        Setup.appSettings().setDesktopLock(!Setup.appSettings().isDesktopLock());
                        //LauncherSettings.getInstance(getContext()).generalSettings.desktopLock = !LauncherSettings.getInstance(getContext()).generalSettings.desktopLock;
                        updateLockIcon(Setup.appSettings().isDesktopLock());
                    } else if (id == R.string.settings) {
                        desktopOptionViewListener.onLaunchSettings();
                    } else {
                        return false;
                    }
                    return true;
                }
                return false;
            }
        };

        List<FastItem.DesktopOptionsItem> itemsTop = new ArrayList<>();
        itemsTop.add(createItem(R.drawable.ic_delete_white_36dp, R.string.remove, typeface));
        itemsTop.add(createItem(R.drawable.ic_star_white_36dp, R.string.home, typeface));
        itemsTop.add(createItem(R.drawable.ic_lock_open_white_36dp, R.string.lock, typeface));
        actionAdapters[0].set(itemsTop);
        actionAdapters[0].withOnClickListener(clickListener);

        List<FastItem.DesktopOptionsItem> itemsBottom = new ArrayList<>();
        itemsBottom.add(createItem(R.drawable.ic_dashboard_white_36dp, R.string.widget, typeface));
        itemsBottom.add(createItem(R.drawable.ic_launch_white_36dp, R.string.action, typeface));
        itemsBottom.add(createItem(R.drawable.ic_settings_launcher_white_36dp, R.string.settings, typeface));
        actionAdapters[1].set(itemsBottom);
        actionAdapters[1].withOnClickListener(clickListener);
    }

    private RecyclerView createRecyclerView(FastAdapter adapter, int gravity){
        RecyclerView actionRecyclerView = new RecyclerView(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        actionRecyclerView.setClipToPadding(false);
        int paddingHorizontal = Tool.dp2px(42, getContext());
        actionRecyclerView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        actionRecyclerView.setLayoutManager(linearLayoutManager);
        actionRecyclerView.setAdapter(adapter);
        actionRecyclerView.setOverScrollMode(OVER_SCROLL_ALWAYS);
        LayoutParams actionRecyclerViewLP = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        actionRecyclerViewLP.gravity = gravity;
        addView(actionRecyclerView, actionRecyclerViewLP);
        return actionRecyclerView;
    }

    private FastItem.DesktopOptionsItem createItem(int icon, int label, Typeface typeface) {
        return new IconLabelItem(getContext(), icon, getContext().getString(label), -1)
                .withIdentifier(label)
                .withOnClickListener(null)
                .withTextColor(Color.WHITE)
                .withDrawablePadding(getContext(), 8)
                .withIconGravity(Gravity.TOP)
                .withGravity(Gravity.CENTER)
                .withMatchParent(false)
                .withTypeface(typeface)
                .withDrawablePadding(getContext(), 0)
                .withTextGravity(Gravity.CENTER);
    }

    public interface DesktopOptionViewListener {
        void onRemovePage();

        void onSetPageAsHome();

        void onLaunchSettings();

        void onPickDesktopAction();

        void onPickWidget();
    }
}
