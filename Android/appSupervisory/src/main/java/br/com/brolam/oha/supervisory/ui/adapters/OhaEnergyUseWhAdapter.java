package br.com.brolam.oha.supervisory.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.data.helpers.OhaEnergyUseLogHelper;
import br.com.brolam.oha.supervisory.ui.adapters.holders.OhaEnergyUseWhHolder;

/**
 * Adaptador para exibir as linhas com a utilização de energia em watts por hora.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseWhAdapter extends RecyclerView.Adapter<OhaEnergyUseWhHolder>  {

    private Context context;
    private ArrayList<OhaEnergyUseLogHelper.EnergyUseWh> energyUseWhs;
    private double costKwh;

    public OhaEnergyUseWhAdapter(Context context, double costKwh) {
        this.context = context;
        this.costKwh = costKwh;
        this.energyUseWhs = new ArrayList<>();
    }

    public void swapCursor(ArrayList<OhaEnergyUseLogHelper.EnergyUseWh> energyUseWhs) {
        this.energyUseWhs = energyUseWhs;
        notifyDataSetChanged();
    }

    @Override
    public OhaEnergyUseWhHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.holder_energy_use_wh, parent, false);
        return new OhaEnergyUseWhHolder(view);
    }

    @Override
    public void onBindViewHolder(OhaEnergyUseWhHolder holder, int position) {
        holder.bindView(this.energyUseWhs.get(position), this.costKwh);
    }

    @Override
    public int getItemCount() {
        return energyUseWhs.size();
    }
}
