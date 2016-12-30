package me.rain.android.vrvideo.decoration;

/**
 * Created by CBS on 2016/5/13.
 */
public class ItemTouchHelperInterface {
    public interface ItemTouchHelperAdapter {

        void onItemMove(int fromPosition, int toPosition);

        void onItemDismiss(int position);
    }
}
