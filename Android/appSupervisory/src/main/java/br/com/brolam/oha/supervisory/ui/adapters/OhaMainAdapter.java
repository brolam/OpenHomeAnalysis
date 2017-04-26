package br.com.brolam.oha.supervisory.ui.adapters;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.ui.adapters.holders.OhaEnergyUseBillHolder;
import br.com.brolam.oha.supervisory.ui.adapters.holders.OhaEnergyUseDayHolder;
import br.com.brolam.oha.supervisory.ui.adapters.holders.OhaMainHolder;
import br.com.brolam.oha.supervisory.ui.adapters.holders.OhaMainHolder.IOhaMainHolder;

/**
 * Adaptador para exibir os cartões na tela principal conforme o NavigationView selecionado.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaMainAdapter extends RecyclerView.Adapter<OhaMainHolder>  {

    IOhaMainHolder iOhaMainHolder;
    private Cursor cursor;
    //Informar o id do NavigationView MenuItem selecionado.
    private int navId;

    public OhaMainAdapter(IOhaMainHolder iOhaMainHolder) {
        this.cursor = null;
        this.iOhaMainHolder  = iOhaMainHolder;
        this.navId = -1;
    }

    public void swapCursor(Cursor newCursor,  int navId ) {
        if (( this.cursor != null) && ( this.cursor != newCursor)){
            this.cursor.close();
            this.cursor = newCursor;
        }
        this.navId = navId;
        cursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public OhaMainHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OhaMainHolder viewHolder = null;
        if ( this.navId == R.id.nav_energy_use_day){
            viewHolder = new OhaEnergyUseDayHolder(LayoutInflater.from(this.iOhaMainHolder.getContext()).inflate(R.layout.holder_energy_use_day, parent, false));
        } else if ( this.navId == R.id.nav_energy_use_bill){
            viewHolder = new OhaEnergyUseBillHolder( LayoutInflater.from(this.iOhaMainHolder.getContext()).inflate(R.layout.holder_energy_use_bill, parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OhaMainHolder ohaMainHolder, int position) {
        this.cursor.moveToPosition(position);
        ohaMainHolder.bindView(cursor, iOhaMainHolder);
    }

    @Override
    public int getItemCount() {
        return this.cursor == null? 0 : this.cursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return this.navId;
    }
}
