package br.com.brolam.oha.supervisory.ui.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;

import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.data.helpers.OhaSQLHelper;

/**
 * Exibir um caixa de dialogo com uma lista de backups do banco de dados para o usuário selecionar e acionar o restore.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaRestoreDatabaseFragment extends DialogFragment implements   Toolbar.OnMenuItemClickListener, AdapterView.OnItemClickListener {
    public static String TAG = OhaRestoreDatabaseFragment.class.getName();

    public interface IOhaRestoreDatabaseFragment{
        void onRequestRestoreDatabase(File backup);
    }

    ListView listView;
    Toolbar toolbar;
    //Lista de backups disponível.
    File[] backups;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.backups = OhaSQLHelper.getBackups();
    }

    public static OhaRestoreDatabaseFragment getInstance(Activity activity){
        OhaRestoreDatabaseFragment ohaEnergyUseBillFragment = (OhaRestoreDatabaseFragment) activity.getFragmentManager().findFragmentByTag(TAG);
        return (ohaEnergyUseBillFragment == null)? new OhaRestoreDatabaseFragment(): ohaEnergyUseBillFragment;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        //Somente exibir o botão de restore quando um backup for selecionado:
        parseActionRestoreDatabase();

    }

    public static void show(Activity activity) {
        OhaRestoreDatabaseFragment ohaEnergyUseBillFragment = getInstance(activity);
        Bundle bundle = new Bundle();
        ohaEnergyUseBillFragment.setArguments(bundle);
        ohaEnergyUseBillFragment.show(activity.getFragmentManager(), TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restore_database, container, false);
        this.listView = (ListView) view.findViewById(R.id.listView);
        int amountBackups = this.backups != null?this.backups.length:0;
        String[] backupsName = new String[amountBackups];
        for(int index = 0; index <amountBackups; index++ ){
            backupsName[index] = this.backups[index].getName();
        }
        this.listView.setAdapter(new ArrayAdapter<String>(this.getActivity(),  android.R.layout.simple_list_item_checked, backupsName));
        this.toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        this.toolbar.inflateMenu(R.menu.fragment_restore_database);
        this.toolbar.setOnMenuItemClickListener(this);
        this.listView.setOnItemClickListener(this);
        //Somente exibir o botão de restore quando um backup for selecionado:
        parseActionRestoreDatabase();

        return view;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_restore_database:
                IOhaRestoreDatabaseFragment iOhaRestoreDatabaseFragment = getActivity() instanceof IOhaRestoreDatabaseFragment? (IOhaRestoreDatabaseFragment) getActivity(): null;
                if ( iOhaRestoreDatabaseFragment != null){
                    iOhaRestoreDatabaseFragment.onRequestRestoreDatabase(this.backups[this.listView.getCheckedItemPosition()]);
                }
                this.dismiss();
                return false;
            case R.id.action_cancel:
                this.dismiss();
                return false;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //Somente exibir o botão de restore quando um backup for selecionado:
        parseActionRestoreDatabase();
    }

    /**
     * Somente exibir o botão de restore quando um backup for selecionado
     */
    private void parseActionRestoreDatabase() {
        MenuItem menuItem = this.toolbar.getMenu().findItem(R.id.action_restore_database);
        if (menuItem != null)
            menuItem.setVisible(this.listView.getCheckedItemPosition() >= 0);
    }

}
