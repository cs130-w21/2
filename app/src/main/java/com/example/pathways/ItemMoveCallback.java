package com.example.pathways;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pathways.PlaylistAdapter;

// From https://www.journaldev.com/23208/android-recyclerview-drag-and-drop
public class ItemMoveCallback extends ItemTouchHelper.Callback {
    private final ItemTouchHelperContract _adapter;

    public ItemMoveCallback(ItemTouchHelperContract adapter) {
        _adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }



    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        _adapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {


        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof PlaylistAdapter.ViewHolder) {
                PlaylistAdapter.ViewHolder myViewHolder=
                        (PlaylistAdapter.ViewHolder) viewHolder;
                _adapter.onRowSelected(myViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof PlaylistAdapter.ViewHolder) {
            PlaylistAdapter.ViewHolder myViewHolder=
                    (PlaylistAdapter.ViewHolder) viewHolder;
            _adapter.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(PlaylistAdapter.ViewHolder viewHolder);
        void onRowClear(PlaylistAdapter.ViewHolder viewHolder);

    }

}