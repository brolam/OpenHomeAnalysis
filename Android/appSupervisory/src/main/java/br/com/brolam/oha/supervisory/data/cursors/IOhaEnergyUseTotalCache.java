package br.com.brolam.oha.supervisory.data.cursors;

import java.util.HashMap;

/**
 * Recuperar o total de utilização de energia no cache do {@link br.com.brolam.oha.supervisory.data.OhaEnergyUseProvider}
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public interface IOhaEnergyUseTotalCache {
    HashMap<Integer,String> getEnergyUseTotalOnCache(long beginDate, long endDate);
}
