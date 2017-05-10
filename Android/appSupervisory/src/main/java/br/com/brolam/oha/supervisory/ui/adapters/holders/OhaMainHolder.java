package br.com.brolam.oha.supervisory.ui.adapters.holders;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Suporte base para os cartões que serão exibidos na tela principal
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public abstract class OhaMainHolder extends RecyclerView.ViewHolder {

    public OhaMainHolder(View itemView) {
        super(itemView);
    }

    public interface IOhaMainHolder {
        Context getContext();
        void onEnergyUseDaySelect(long beginDate, double kwhCost, int menuItemId);
        void onEnergyUseBillSelect(int id, long fromDate, long toDate, double kwhCost, int menuItemId);
    }

    public void bindView(final Cursor cursor, final OhaMainHolder.IOhaMainHolder iOhaMainHolder) {

    }


}
