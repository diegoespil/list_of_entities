package entities.diego.com.entities;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by diego on 15/02/18.
 */

public class EntityTouchHelper extends ItemTouchHelper.SimpleCallback {

    private EntityAdapter mAdapter;
    private RecyclerView mRecyclerView;


    public EntityTouchHelper(EntityAdapter adapter, RecyclerView rView){
        super(0,ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        mRecyclerView = rView;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.removeItem(viewHolder.getAdapterPosition());
    }
}
