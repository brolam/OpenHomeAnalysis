package br.com.brolam.oha.supervisory.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.ui.adapters.holders.OhaEnergyUseWattsHolder;

/**
 * Adaptador para exibir as linhas com a utilização de energia em watts.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseWattsAdapter extends RecyclerView.Adapter<OhaEnergyUseWattsHolder>  {

    private Context context;
    private Cursor cursor = null;

    public OhaEnergyUseWattsAdapter(Context context) {
        this.context = context;
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public OhaEnergyUseWattsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.holder_energy_use_watts, parent, false);
        return new OhaEnergyUseWattsHolder(view);
    }

    @Override
    public void onBindViewHolder(OhaEnergyUseWattsHolder holder, int position) {
        this.cursor.moveToPosition(position);
        holder.bindView(this.cursor);
    }

    @Override
    public int getItemCount() {
        return this.cursor == null? 0 : this.cursor.getCount();
    }
}
