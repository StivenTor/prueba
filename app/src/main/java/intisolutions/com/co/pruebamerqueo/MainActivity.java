package intisolutions.com.co.pruebamerqueo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.GridView;

import java.util.List;
import java.util.concurrent.Callable;

import intisolutions.com.co.pruebamerqueo.Adapters.AdapterExample;
import intisolutions.com.co.pruebamerqueo.Model.Company;
import intisolutions.com.co.pruebamerqueo.Model.RestClient;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    RecyclerView mainRecyclerView;
    Button updateButton;
    CompositeDisposable disposable = new CompositeDisposable();
    private AdapterExample companiesAdapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();


        updateButton = (Button) findViewById(R.id.update_bt);
        mainRecyclerView = (RecyclerView) findViewById(R.id.list_rcv);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButton.setEnabled(false);
                Observable<List<RestClient.Companies>> titleObservable = Observable.fromCallable(new Callable<List<RestClient.Companies>>() {
                    @Override
                    public List<RestClient.Companies> call() throws Exception {
                        return new RestClient().getCompanies();
                    }
                });
                Disposable subscriber = titleObservable.
                        subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Consumer<List<RestClient.Companies>>() {
                            @Override
                            public void accept(@NonNull List<RestClient.Companies> strings) throws Exception {
                                reloadRecyclerView();
                                updateButton.setEnabled(true);
                            }
                        });
                disposable.add(subscriber);
            }
        });

    }

    private void reloadRecyclerView() {
        RealmResults<Company> companies = realm.where(Company.class).findAll();
        companiesAdapter = new AdapterExample(this, companies);
        mainRecyclerView.setAdapter(companiesAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }


}
