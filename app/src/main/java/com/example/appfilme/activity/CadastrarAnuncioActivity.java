package com.example.appfilme.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;


import com.example.appfilme.R;
import com.example.appfilme.helper.ConfiguracaoFirebase;
import com.example.appfilme.helper.Permissoes;
import com.example.appfilme.model.Publicacao;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class CadastrarAnuncioActivity<grantResults> extends AppCompatActivity
            implements View.OnClickListener{
    private EditText  campoTitulo, campoDescricao;
    private Spinner campoCategoria;
    private ImageView imagem1, imagem2, imagem3;
    private Publicacao publicacao;
    private StorageReference storage;
    private AlertDialog dialog;


    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        Permissoes.validarPermissoes(permissoes,this,1);

        inicializarComponentes();
        carregarDadosSpinner();
    }
    private void inicializarComponentes(){
        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao = findViewById(R.id.editDescricao);
        campoCategoria = findViewById(R.id.spinnerCategoria);
        imagem1=findViewById(R.id.imageCadastro1);
        imagem2=findViewById(R.id.imageCadastro2);
        imagem3 =findViewById(R.id.imageCadastro3);

       imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);

    }
    private Publicacao configurarPublicacao(){
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String descricao = campoDescricao.getText().toString();

       Publicacao publicacao = new Publicacao();
        publicacao.setCategoria(categoria);
        publicacao.setDescricao(descricao);
        publicacao.setTitulo(titulo);

        return publicacao;
    }
    public void salvarPublicacao(){
           LoadingAlert loadingAlert = new LoadingAlert(CadastrarAnuncioActivity.this);
           loadingAlert.startAlertDialog();
        for(int i = 0; i < listaFotosRecuperadas.size(); i++){
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i);
        }
    }
   private void salvarFotoStorage(String urlString, final int totalFotos, int contador) {

       //Criar nó no Storage
       final StorageReference imagemAnuncio = storage.child("imagens")
               .child("publicacoes")
               .child( publicacao.getIdPublicacao() )

               .child("imagem" + contador);

       //Fazer upload do arquivo
       UploadTask uploadTask = imagemAnuncio.putFile( Uri.parse( urlString ) );
       uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

               //Uri firebaseUrl = imagemAnuncio.getDownloadUrl();

               //===================================================
               imagemAnuncio.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       String urlConvertida = uri.toString();      //Esta url funciona!!!
                       LoadingAlert loadingAlert = new LoadingAlert(CadastrarAnuncioActivity.this);
                       listaUrlFotos.add( urlConvertida );

                       //Testa finalização de upload das imagens
                       if ( listaUrlFotos.size() == totalFotos  ){ //todas as fotos salvas
                           publicacao.setFotos( listaUrlFotos );
                           publicacao.salvar();
                           loadingAlert.closeAlertDialog();
                         finish();

                       }
                   }
               });
               //===================================================
           }
       }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            exibirMensagemErro("Falha ao fazer upload");
            Log.e("INFO","Falha ao fazer upload "+ e.getMessage());
            }
        });
    }


    public void validarDadosPublicacao(View view){
       publicacao = configurarPublicacao();

        if(listaFotosRecuperadas.size() !=0){
            if(!publicacao.getCategoria().isEmpty()){
                if(!publicacao.getTitulo().isEmpty()){
                    if(!publicacao.getDescricao().isEmpty()){
                        salvarPublicacao();
                    }else{
                        exibirMensagemErro("Preencha o campo avaliação");
                    }
                }else{
                    exibirMensagemErro("Preencha o campo título");
                }
            }else{
                exibirMensagemErro("Preencha o campo categoria");
            }
        }else{
            exibirMensagemErro("Selecione ao menos uma foto!");
        }
    }
    private void exibirMensagemErro(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
    private void carregarDadosSpinner(){
        String[] categorias = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
          this, android.R.layout.simple_spinner_item, categorias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;

            case R.id.imageCadastro2:
                escolherImagem(2);
                break;

            case R.id.imageCadastro3:
                escolherImagem(3);
                break;
        }
    }
    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //configura imagem no ImageView
            if (requestCode == 1){
                imagem1.setImageURI(imagemSelecionada);

            }else if (requestCode == 2){
                imagem2.setImageURI(imagemSelecionada);
            }else if (requestCode == 3){
                imagem3.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }
    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}