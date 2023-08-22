package andrades.isabelly.appgaleriapublica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    static int RESULT_REQUEST_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);

        bottomNavigationView = findViewById(R.id.btNav);
        // define a função da nav
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            // define ação quando for selecionado algum botão da nav bar
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // botão selecionado
                vm.setNavigationOpSelected(item.getItemId());
                switch (item.getItemId()) {
                    // caso o botão selecionado seja grid, o fragment mudará para grid
                    case R.id.gridViewOp:
                        GridViewFragment gridViewFragment = GridViewFragment.newInstance();
                        setFragment(gridViewFragment);
                        break;
                    // caso o botão selecionado seja list, o fragment mudará para list
                    case R.id.listViewOp:
                        ListViewFragment listViewFragment = ListViewFragment.newInstance();
                        setFragment(listViewFragment);
                        break;
                }
                return true;
            }
        });
    }

    // coloca o fragmento no container
    void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // lista de permissões da aplicação
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        // verifica as permissões
        checkForPermissions(permissions);
    }

    private boolean hasPermission(String permission) {
        // verifica se a permissão já foi aceita
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void checkForPermissions(List<String> permissions) {
        // permissões não aceitas
        List<String> permissionsNotGranted = new ArrayList<>();

        // roda a lista de permissões
        for (String permission : permissions) {
            // se a permissão não foi aceita, ela é adicionada a lista de permissões não aceitas
            if (!hasPermission(permission)) {
                permissionsNotGranted.add(permission);
            }
        }

        // as permissões não aceitas pelo usuário são solicitadas a ele novamente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // verifica se existem permissões não aceitas
            if (permissionsNotGranted.size() > 0) {
                requestPermissions((permissionsNotGranted.toArray(new String[permissionsNotGranted.size()])),
                        RESULT_REQUEST_PERMISSION);
            } else {
                MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
                int navigationOpSelected = vm.getNavigationOpSelected();
                bottomNavigationView.setSelectedItemId(navigationOpSelected);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        final List<String> permissionsRejected = new ArrayList<>();
        // verifica se as permissões solicitadas foram aceitas
        if(requestCode == RESULT_REQUEST_PERMISSION) {
            for(String permission : permissions) {
                // se não foram aceitas elas são adicionadas a lista de permissões rejeitadas
                if (!hasPermission(permission)) {
                    permissionsRejected.add(permission);
                }
            }
        }

        // condição para verificar se a lista de permissões rejeitadas possuir algum item
        if(permissionsRejected.size() > 0) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                    // se a permissão for necessária para o uso do aplicativo um alerta aparece
                    // para o usuário aceitar a permissão
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app" +
                            "é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(permissionsRejected.toArray(new String[
                                    permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();
                }
            }
        } else {
            MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
            int navigationOpSelected = vm.getNavigationOpSelected();
            bottomNavigationView.getSelectedItemId(navigationOpSelected);
        }
    }

}