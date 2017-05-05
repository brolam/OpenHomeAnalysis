package br.com.brolam.oha.supervisory.ui.helpers;


import android.content.Context;

import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.EnergyUseLogEntry.FilterWatts;


/**
 * Disponibilizar funcionalidades para converter e formatar texto, número, data e etc específicas
 * para o aplicativo Supervisory
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaHelper extends br.com.brolam.library.helpers.OhaHelper {

    /**
     * Recuperar a descrição dos campos por Fase
     */
    public static String getEnergyUsePhaseDescription(Context context, FilterWatts filterWatts){
        switch (filterWatts){
            case NONE: return context.getString(R.string.energy_Use_Column_filter_none);
            case PHASE1: return context.getString(R.string.energy_Use_Column_filter_phase1);
            case PHASE2: return context.getString(R.string.energy_Use_Column_filter_phase2);
            case PHASE3: return context.getString(R.string.energy_Use_Column_filter_phase3);
            case TOTAL: return context.getString(R.string.energy_Use_Column_filter_total);
            default: return filterWatts.name();
        }
    }

}
