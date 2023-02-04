package com.example.appfilme.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.appfilme.R;
import com.example.appfilme.adapter.AdapterPublicacoes;
import com.example.appfilme.helper.ConfiguracaoFirebase;
import com.example.appfilme.helper.RecyclerItemClickListener;
import com.example.appfilme.model.Publicacao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PublicacoesActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private RecyclerView recyclerPublicacoesPublicas;
    private Button buttonCategoria;
    private AdapterPublicacoes adapterAnuncios;
    private List<Publicacao> listaPublicacoes = new ArrayList<>();
    private DatabaseReference publicacoesPublicasRef;
    private String filtroCategoria = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacoes);

        inicializarComponentes();

        //Aplicar evento de click
        recyclerPublicacoesPublicas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerPublicacoesPublicas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Publicacao publicacaoSelecionada = listaPublicacoes.get(position);
                                Intent i = new Intent(PublicacoesActivity.this,DetalhesFilmeActivity.class);
                                i.putExtra("publicacaoSelecionada",publicacaoSelecionada);
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );



        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        publicacoesPublicasRef = ConfiguracaoFirebase.getFirebase().child("publicacoes");
        //Configurar RecyclerView
        recyclerPublicacoesPublicas.setLayoutManager(new LinearLayoutManager(this));
        recyclerPublicacoesPublicas.setHasFixedSize(true);

        adapterAnuncios = new AdapterPublicacoes(listaPublicacoes, this);

        recyclerPublicacoesPublicas.setAdapter(adapterAnuncios);
        recuperarPublicacoesPublicas();
        //autenticacao.signOut();
    }
    public void filtrarPorCategoria(View view){
        AlertDialog.Builder dialogoCategoria = new AlertDialog.Builder(this);
        dialogoCategoria.setTitle("Selecione a categoria desejada");
        //Configurar spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        //Configurar spinner de categorias
        Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);
        String[] categorias = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, categorias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

        dialogoCategoria.setView(viewSpinner);

        dialogoCategoria.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                recuperarPublicacoesPorCategoria();
            }
        });
        dialogoCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = dialogoCategoria.create();
        dialog.show();
    }
    public void recuperarPublicacoesPorCategoria(){
        LoadingAlert loadingAlert = new LoadingAlert(PublicacoesActivity.this);
        loadingAlert.startAlertDialog();
        publicacoesPublicasRef = ConfiguracaoFirebase.getFirebase().child("publicacoes")
                .child(filtroCategoria);
        publicacoesPublicasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               listaPublicacoes.clear();
                for(DataSnapshot publicacoes: dataSnapshot.getChildren()){
                    Publicacao publicacao = publicacoes.getValue(Publicacao.class);
                    listaPublicacoes.add(publicacao);

                }
                Collections.reverse(listaPublicacoes);
                adapterAnuncios.notifyDataSetChanged();
                loadingAlert.closeAlertDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void recuperarPublicacoesPublicas(){
        //correcao
        LoadingAlert loadingAlert = new LoadingAlert(PublicacoesActivity.this);
        loadingAlert.startAlertDialog();
        listaPublicacoes.clear();
        publicacoesPublicasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot categorias: dataSnapshot.getChildren()){
                    for(DataSnapshot publicacoes: categorias.getChildren()){
                        Publicacao publicacao = publicacoes.getValue(Publicacao.class);
                        listaPublicacoes.add(publicacao);

                    }
                }
                Collections.reverse(listaPublicacoes);
                adapterAnuncios.notifyDataSetChanged();
                loadingAlert.closeAlertDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if( autenticacao.getCurrentUser()==null){//usuario deslogado
            menu.setGroupVisible(R.id.group_deslogado, true);
        }else{//usuario logado
            menu.setGroupVisible(R.id.group_logado,true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_cadastrar:
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                break;
            case R.id.menu_sair:
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;

            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(), MinhasPublicacoesActivity.class));
                break;


        }

        return super.onOptionsItemSelected(item);
    }
    public void inicializarComponentes(){
        recyclerPublicacoesPublicas = findViewById(R.id.recyclerPublicacoesPublicas);
    }
}